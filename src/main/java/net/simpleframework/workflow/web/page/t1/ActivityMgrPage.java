package net.simpleframework.workflow.web.page.t1;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.mvc.AbstractMVCPage;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ui.pager.AbstractTablePagerSchema;
import net.simpleframework.mvc.component.ui.pager.EPagerBarLayout;
import net.simpleframework.mvc.component.ui.pager.TablePagerBean;
import net.simpleframework.mvc.component.ui.pager.TablePagerColumn;
import net.simpleframework.mvc.component.ui.pager.db.AbstractDbTablePagerHandler;
import net.simpleframework.workflow.engine.ActivityBean;
import net.simpleframework.workflow.engine.EActivityStatus;
import net.simpleframework.workflow.engine.EWorkitemStatus;
import net.simpleframework.workflow.engine.IActivityService;
import net.simpleframework.workflow.engine.ProcessBean;
import net.simpleframework.workflow.engine.WorkitemBean;
import net.simpleframework.workflow.engine.participant.IParticipantModel;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class ActivityMgrPage extends AbstractWorkflowMgrPage {

	@Override
	protected void onForward(final PageParameter pp) {
		super.onForward(pp);

		final TablePagerBean tablePager = (TablePagerBean) addComponentBean(pp,
				"ActivityMgrPage_tbl", TablePagerBean.class).setPagerBarLayout(EPagerBarLayout.bottom)
				.setContainerId("idActivityMgrPage_tbl").setHandleClass(ActivityTbl.class);
		tablePager
				.addColumn(new TablePagerColumn("tasknode", "环节"))
				.addColumn(new TablePagerColumn("previous", "前一环节"))
				.addColumn(new TablePagerColumn("participants", "定义参与者").setNowrap(false))
				.addColumn(new TablePagerColumn("participants2", "实际参与者").setNowrap(false))
				.addColumn(new TablePagerColumn("createDate", "创建日期", 115).setPropertyClass(Date.class))
				.addColumn(
						new TablePagerColumn("completeDate", "完成日期", 115).setPropertyClass(Date.class))
				.addColumn(new TablePagerColumn("status", "状态", 55))
				.addColumn(TablePagerColumn.OPE().setWidth(90));
	}

	@Override
	protected String toHtml(final PageParameter pp, final Map<String, Object> variables,
			final String variable) throws IOException {
		final StringBuilder sb = new StringBuilder();
		sb.append("<div align='center' class='ActivityMgrPage'>");
		sb.append(" <div id='idActivityMgrPage_tbl'></div>");
		sb.append("</div>");
		return sb.toString();
	}

	@Override
	protected Class<? extends AbstractMVCPage> getStatusDescPage() {
		return null;
	}

	@Override
	protected Class<? extends AbstractMVCPage> getUpdateLogPage() {
		return null;
	}

	public static class ActivityTbl extends AbstractDbTablePagerHandler {

		@Override
		public IDataQuery<?> createDataObjectQuery(final ComponentParameter cp) {
			final ProcessBean process = context.getProcessService().getBean(
					cp.getParameter("processId"));
			cp.addFormParameter("processId", process.getId());
			final IDataQuery<?> qs = context.getActivityService().getActivities(process);
			cp.setRequestAttr("pageItems", qs.getCount());
			return qs;
		}

		@Override
		protected Map<String, Object> getRowData(final ComponentParameter cp, final Object dataObject) {
			final ActivityBean activity = (ActivityBean) dataObject;
			final KVMap rowData = new KVMap();
			final IActivityService service = context.getActivityService();
			rowData.add("tasknode", service.taskNode(activity));
			final ActivityBean pre = service.getPreActivity(activity);
			if (pre != null) {
				rowData.add("previous", service.taskNode(pre));
			}
			rowData.add("participants", getParticipants(activity, null));
			rowData.add("participants2", getParticipants(activity, EWorkitemStatus.complete));
			rowData.add("createDate", activity.getCreateDate());
			rowData.add("completeDate", activity.getCompleteDate());

			final EActivityStatus status = activity.getStatus();
			rowData.add("status", status);
			rowData.add("action", AbstractTablePagerSchema.IMG_DOWNMENU);
			return rowData;
		}

		private String getParticipants(final ActivityBean activity, final EWorkitemStatus status) {
			final StringBuilder sb = new StringBuilder();
			final IDataQuery<WorkitemBean> qs = context.getWorkitemService().getWorkitemList(activity);
			final IParticipantModel service = context.getParticipantService();
			WorkitemBean item;
			int i = 0;
			while ((item = qs.next()) != null) {
				if (status != null && status != item.getStatus()) {
					continue;
				}
				if (i > 0) {
					sb.append(", ");
				}
				sb.append("<span class='participants2' params='").append(WorkitemBean.workitemId)
						.append("=").append(item.getId()).append("'>")
						.append(service.getUser(item.getUserId())).append("</span>");
				i++;
			}
			return sb.toString();
		}
	}
}
