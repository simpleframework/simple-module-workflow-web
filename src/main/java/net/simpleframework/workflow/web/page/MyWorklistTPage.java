package net.simpleframework.workflow.web.page;

import java.util.Date;
import java.util.Map;

import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.common.ID;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.PageRequestResponse;
import net.simpleframework.mvc.common.element.ETextAlign;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ui.pager.TablePagerBean;
import net.simpleframework.mvc.component.ui.pager.TablePagerColumn;
import net.simpleframework.mvc.component.ui.pager.db.AbstractDbTablePagerHandler;
import net.simpleframework.workflow.engine.ActivityBean;
import net.simpleframework.workflow.engine.EWorkitemStatus;
import net.simpleframework.workflow.engine.IWorkitemService;
import net.simpleframework.workflow.engine.WorkitemBean;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class MyWorklistTPage extends AbstractWorkTPage {

	@Override
	protected void onForward(final PageParameter pp) {
		super.onForward(pp);

		final TablePagerBean tablePager = addTablePagerBean(pp, MyWorklistTbl.class);
		tablePager
				.addColumn(new TablePagerColumn("title", "流程主题").setTextAlign(ETextAlign.left))
				// .addColumn(new TablePagerColumn("activity", "环节", 120))
				.addColumn(new TablePagerColumn("userFrom", "发送人", 120))
				// .addColumn(new TablePagerColumn("userTo", "接收人", 120))
				.addColumn(new TablePagerColumn("createDate", "创建日期", 115).setPropertyClass(Date.class))
				// .addColumn(
				// new TablePagerColumn("completeDate", "完成日期",
				// 115).setPropertyClass(Date.class))
				.addColumn(TablePagerColumn.OPE().setWidth(80));
	}

	public static class MyWorklistTbl extends AbstractDbTablePagerHandler {

		@Override
		public IDataQuery<?> createDataObjectQuery(final ComponentParameter cp) {
			final EWorkitemStatus status = getWorkitemStatus(cp);
			final ID userId = cp.getLoginId();
			final IWorkitemService service = context.getWorkitemService();
			if (status != null) {
				cp.addFormParameter("status", status.name());
				return service.getWorkitemList(userId, status);
			} else {
				return service.getWorkitemList(userId);
			}
		}

		@Override
		protected Map<String, Object> getRowData(final ComponentParameter cp, final Object dataObject) {
			final WorkitemBean workitem = (WorkitemBean) dataObject;
			final ActivityBean activity = context.getWorkitemService().getActivity(workitem);
			final String title = StringUtils.text(context.getActivityService()
					.getProcessBean(activity).getTitle(), "未设置主题");
			final KVMap row = new KVMap();
			row.add("title", title);
			return row;
		}
	}

	private static EWorkitemStatus getWorkitemStatus(final PageRequestResponse rRequest) {
		final String status = rRequest.getParameter("status");
		if (!"false".equals(status)) {
			return StringUtils.hasText(status) ? EWorkitemStatus.valueOf(status)
					: EWorkitemStatus.running;
		} else {
			return null;
		}
	}
}