package net.simpleframework.workflow.web.page;

import static net.simpleframework.common.I18n.$m;

import java.util.Date;
import java.util.Map;

import net.simpleframework.ado.query.DataQueryUtils;
import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.ado.query.ListDataQuery;
import net.simpleframework.common.Convert;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ui.pager.TablePagerBean;
import net.simpleframework.mvc.component.ui.pager.TablePagerColumn;
import net.simpleframework.mvc.component.ui.pager.db.AbstractDbTablePagerHandler;
import net.simpleframework.mvc.template.lets.OneTableTemplatePage;
import net.simpleframework.workflow.engine.EProcessStatus;
import net.simpleframework.workflow.engine.EWorkitemStatus;
import net.simpleframework.workflow.engine.ProcessBean;
import net.simpleframework.workflow.engine.WorkitemBean;
import net.simpleframework.workflow.web.page.t1.AbstractWorkflowMgrPage;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class MyQueryWorksTPage extends AbstractItemsTPage {

	@Override
	protected void onForward(final PageParameter pp) {
		super.onForward(pp);

		final TablePagerBean tablePager = addTablePagerBean(pp, "MyQueryWorksTPage_tbl",
				MyQueryWorksTbl.class);
		tablePager.addColumn(AbstractWorkflowMgrPage.TC_TITLE())
				.addColumn(new TablePagerColumn("userText", $m("ProcessMgrPage.0"), 100))
				.addColumn(AbstractWorkflowMgrPage.TC_CREATEDATE())
				.addColumn(AbstractWorkflowMgrPage.TC_STATUS().setPropertyClass(EProcessStatus.class));
		tablePager.addColumn(TablePagerColumn.OPE().setWidth(70));

		// 工作列表窗口
		addAjaxRequest(pp, "MyQueryWorksTPage_workitems_page", ProcessWorkitemsPage.class);
		addWindowBean(pp, "MyQueryWorksTPage_workitems")
				.setContentRef("MyQueryWorksTPage_workitems_page").setWidth(800).setHeight(480)
				.setTitle($m("MyQueryWorksTPage.1"));
	}

	public static class MyQueryWorksTbl extends AbstractDbTablePagerHandler {
		@Override
		public IDataQuery<?> createDataObjectQuery(final ComponentParameter cp) {
			return pService.getProcessList(cp.getLoginId());
		}

		@Override
		protected Map<String, Object> getRowData(final ComponentParameter cp, final Object dataObject) {
			final ProcessBean process = (ProcessBean) dataObject;
			final KVMap row = new KVMap();
			final StringBuilder t = new StringBuilder();
			final int c = Convert.toInt(process.getAttr("c"));
			if (c > 0) {
				t.append("[").append(c).append("] ");
			}
			t.append("<a onclick=\"$Actions['MyQueryWorksTPage_workitems']('processId=")
					.append(process.getId()).append("');\">").append(WorkflowUtils.getTitle(process))
					.append("</a>");
			row.add("title", t.toString()).add("userText", process.getUserText())
					.add("createDate", process.getCreateDate())
					.add("status", WorkflowUtils.toStatusHTML(cp, process.getStatus()));
			return row;
		}
	}

	public static class ProcessWorkitemsPage extends OneTableTemplatePage {
		@Override
		protected void onForward(final PageParameter pp) {
			super.onForward(pp);

			final TablePagerBean tablePager = addTablePagerBean(pp, "ProcessWorkitemsPage_tbl",
					ProcessWorkitemsTbl.class).setShowLineNo(false);
			tablePager
					.addColumn(new TablePagerColumn("taskname", $m("MyQueryWorksTPage.0")))
					.addColumn(new TablePagerColumn("userTo", $m("MyRunningWorklistTPage.0")))
					.addColumn(
							new TablePagerColumn("createDate", $m("MyRunningWorklistTPage.1"), 115)
									.setPropertyClass(Date.class))
					.addColumn(
							new TablePagerColumn("completeDate", $m("MyFinalWorklistTPage.1"), 115)
									.setPropertyClass(Date.class))
					.addColumn(
							AbstractWorkflowMgrPage.TC_STATUS().setPropertyClass(EWorkitemStatus.class));
		}

		@Override
		public String getTitle(final PageParameter pp) {
			final ProcessBean process = pService.getBean(pp.getParameter("processId"));
			return process != null ? process.getTitle() : null;
		}
	}

	public static class ProcessWorkitemsTbl extends AbstractDbTablePagerHandler {

		@Override
		public IDataQuery<?> createDataObjectQuery(final ComponentParameter cp) {
			final ProcessBean process = pService.getBean(cp.getParameter("processId"));
			if (process == null) {
				return DataQueryUtils.nullQuery();
			}
			return new ListDataQuery<WorkitemBean>(wService.getWorkitems(process, cp.getLoginId()));
		}

		@Override
		protected Map<String, Object> getRowData(final ComponentParameter cp, final Object dataObject) {
			return super.getRowData(cp, dataObject);
		}
	}
}