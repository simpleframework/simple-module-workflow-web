package net.simpleframework.workflow.web.page;

import static net.simpleframework.common.I18n.$m;

import java.util.Date;
import java.util.Map;

import net.simpleframework.ado.query.DataQueryUtils;
import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.ado.query.ListDataQuery;
import net.simpleframework.common.Convert;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.mvc.IForward;
import net.simpleframework.mvc.JavascriptForward;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.ButtonElement;
import net.simpleframework.mvc.common.element.ElementList;
import net.simpleframework.mvc.common.element.LinkElement;
import net.simpleframework.mvc.common.element.SpanElement;
import net.simpleframework.mvc.common.element.TabButton;
import net.simpleframework.mvc.common.element.TabButtons;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ui.pager.EPagerBarLayout;
import net.simpleframework.mvc.component.ui.pager.TablePagerBean;
import net.simpleframework.mvc.component.ui.pager.TablePagerColumn;
import net.simpleframework.mvc.component.ui.pager.db.AbstractDbTablePagerHandler;
import net.simpleframework.mvc.template.lets.OneTableTemplatePage;
import net.simpleframework.workflow.engine.EProcessStatus;
import net.simpleframework.workflow.engine.EWorkitemStatus;
import net.simpleframework.workflow.engine.bean.ActivityBean;
import net.simpleframework.workflow.engine.bean.ProcessBean;
import net.simpleframework.workflow.engine.bean.WorkitemBean;
import net.simpleframework.workflow.web.IWorkflowWebContext;
import net.simpleframework.workflow.web.WorkflowUrlsFactory;
import net.simpleframework.workflow.web.WorkflowUtils;
import net.simpleframework.workflow.web.page.t1.AbstractWorkflowMgrPage;
import net.simpleframework.workflow.web.page.t1.WorkflowFormPage;
import net.simpleframework.workflow.web.page.t1.WorkflowMonitorPage;

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

		addTablePagerBean(pp);

		addAjaxRequest(pp, "MyQueryWorksTPage_workitem").setHandlerMethod("doWorkitem");

		// 工作列表窗口
		addAjaxRequest(pp, "MyQueryWorksTPage_workitems_page", ProcessWorkitemsPage.class);
		addWindowBean(pp, "MyQueryWorksTPage_workitems")
				.setContentRef("MyQueryWorksTPage_workitems_page").setWidth(800).setHeight(480)
				.setTitle($m("MyQueryWorksTPage.1"));
	}

	protected TablePagerBean addTablePagerBean(final PageParameter pp) {
		final TablePagerBean tablePager = addTablePagerBean(pp, "MyQueryWorksTPage_tbl",
				MyQueryWorksTbl.class);
		tablePager
				.addColumn(TC_TITLE())
				.addColumn(new TablePagerColumn("userText", $m("ProcessMgrPage.0"), 100))
				.addColumn(AbstractWorkflowMgrPage.TC_CREATEDATE())
				.addColumn(
						AbstractWorkflowMgrPage.TC_STATUS().setColumnAlias("p.status")
								.setPropertyClass(EProcessStatus.class))
				.addColumn(TablePagerColumn.OPE().setWidth(90));
		return tablePager;
	}

	@Override
	protected String getPageCSS(final PageParameter pp) {
		return "MyQueryWorksTPage";
	}

	public IForward doWorkitem(final ComponentParameter cp) {
		final ProcessBean process = WorkflowUtils.getProcessBean(cp);
		WorkitemBean workitem;
		if (process != null && (workitem = getOpenWorkitem(cp, process)) != null) {
			return new JavascriptForward("$Actions.loc('"
					+ uFactory.getUrl(cp, WorkflowFormPage.class, workitem) + "');");
		} else {
			return new JavascriptForward("alert('").append($m("MyQueryWorksTPage.7")).append("');");
		}
	}

	protected WorkitemBean getOpenWorkitem(final PageParameter pp, final ProcessBean process) {
		return wService.getWorkitems(process, pp.getLoginId()).iterator().next();
	}

	@Override
	public ElementList getRightElements(final PageParameter pp) {
		final IWorkflowWebContext ctx = ((IWorkflowWebContext) workflowContext);
		final WorkflowUrlsFactory urlsFactory = ctx.getUrlsFactory();
		final TabButtons tabs = TabButtons.of(new TabButton($m("MyQueryWorksTPage.4"), urlsFactory
				.getUrl(pp, MyQueryWorksTPage.class)));
		if (pp.isLmember(ctx.getDepartmentMgrRole(pp))) {
			tabs.append(new TabButton($m("MyQueryWorksTPage.5"), urlsFactory.getUrl(pp,
					MyQueryWorks_DeptTPage.class)));
		}
		tabs.append(new TabButton($m("MyQueryWorksTPage.6"), urlsFactory.getUrl(pp,
				MyQueryWorks_RoleTPage.class)));
		return ElementList.of(createTabsElement(pp, tabs));
	}

	private static final WorkflowUrlsFactory uFactory = ((IWorkflowWebContext) workflowContext)
			.getUrlsFactory();

	public static class MyQueryWorksTbl extends AbstractDbTablePagerHandler {
		@Override
		public IDataQuery<?> createDataObjectQuery(final ComponentParameter cp) {
			return pService.getProcessList(cp.getLoginId());
		}

		@Override
		protected Map<String, Object> getRowData(final ComponentParameter cp, final Object dataObject) {
			final ProcessBean process = (ProcessBean) dataObject;
			final KVMap row = new KVMap();

			row.add("title", toTitleHTML(cp, process)).add("userText", process.getUserText())
					.add("createDate", process.getCreateDate())
					.add("status", WorkflowUtils.toStatusHTML(cp, process.getStatus()));
			row.add(TablePagerColumn.OPE, toOpeHTML(cp, process));
			return row;
		}

		protected String toTitleHTML(final ComponentParameter cp, final ProcessBean process) {
			final StringBuilder t = new StringBuilder();
			final int c = Convert.toInt(process.getAttr("c"));
			if (c > 0) {
				t.append("[").append(c).append("] ");
			}
			t.append(new LinkElement(WorkflowUtils.getProcessTitle(process)).setOnclick(
					"$Actions['MyQueryWorksTPage_workitem']('processId=" + process.getId() + "');")
					.setColor_gray(!StringUtils.hasText(process.getTitle())));
			return t.toString();
		}

		protected String toOpeHTML(final ComponentParameter cp, final ProcessBean process) {
			final StringBuilder ope = new StringBuilder();
			ope.append(new ButtonElement($m("MyQueryWorksTPage.2"))
					.setOnclick("$Actions['MyQueryWorksTPage_workitems']('processId=" + process.getId()
							+ "');"));
			return ope.toString();
		}
	}

	public static class ProcessWorkitemsPage extends OneTableTemplatePage {
		@Override
		protected void onForward(final PageParameter pp) {
			super.onForward(pp);

			final TablePagerBean tablePager = (TablePagerBean) addTablePagerBean(pp,
					"ProcessWorkitemsPage_tbl", ProcessWorkitemsTbl.class).setShowCheckbox(false)
					.setShowLineNo(false).setPagerBarLayout(EPagerBarLayout.none);
			tablePager
					.addColumn(new TablePagerColumn("taskname", $m("MyQueryWorksTPage.0")))
					.addColumn(
							new TablePagerColumn("userFrom", $m("MyRunningWorklistTPage.0"))
									.setFilter(false))
					.addColumn(
							new TablePagerColumn("createDate", $m("MyRunningWorklistTPage.1"), 115)
									.setPropertyClass(Date.class))
					.addColumn(
							new TablePagerColumn("completeDate", $m("MyFinalWorklistTPage.1"), 115)
									.setPropertyClass(Date.class))
					.addColumn(
							AbstractWorkflowMgrPage.TC_STATUS().setPropertyClass(EWorkitemStatus.class))
					.addColumn(TablePagerColumn.OPE().setWidth(110));
		}

		@Override
		public String getTitle(final PageParameter pp) {
			String t = $m("MyQueryWorksTPage.1");
			final ProcessBean process = WorkflowUtils.getProcessBean(pp);
			if (process != null) {
				t += " - " + process.getTitle();
			}
			return t;
		}
	}

	public static class ProcessWorkitemsTbl extends AbstractDbTablePagerHandler {

		@Override
		public IDataQuery<?> createDataObjectQuery(final ComponentParameter cp) {
			final ProcessBean process = WorkflowUtils.getProcessBean(cp);
			if (process == null) {
				return DataQueryUtils.nullQuery();
			}
			cp.addFormParameter("processId", process.getId());
			return new ListDataQuery<WorkitemBean>(wService.getWorkitems(process, cp.getLoginId()));
		}

		@Override
		protected Map<String, Object> getRowData(final ComponentParameter cp, final Object dataObject) {
			final WorkitemBean workitem = (WorkitemBean) dataObject;
			final KVMap row = new KVMap();

			final ActivityBean activity = wService.getActivity(workitem);
			row.add(
					"taskname",
					new LinkElement(activity).setOnclick("$Actions.loc('"
							+ uFactory.getUrl(cp, WorkflowFormPage.class, workitem) + "');"))
					.add("userFrom", WorkflowUtils.getUserFrom(activity))
					.add("createDate", workitem.getCreateDate())
					.add("completeDate", workitem.getCompleteDate())
					.add("status", WorkflowUtils.toStatusHTML(cp, workitem.getStatus()));

			final StringBuilder ope = new StringBuilder();
			ope.append(new ButtonElement($m("MyQueryWorksTPage.3")).setOnclick("$Actions.loc('"
					+ uFactory.getUrl(cp, WorkflowFormPage.class, workitem) + "');"));
			ope.append(SpanElement.SPACE);
			ope.append(new ButtonElement($m("WorkflowFormPage.1")).setOnclick("$Actions.loc('"
					+ uFactory.getUrl(cp, WorkflowMonitorPage.class, workitem) + "');"));
			row.add(TablePagerColumn.OPE, ope.toString());
			return row;
		}
	}
}