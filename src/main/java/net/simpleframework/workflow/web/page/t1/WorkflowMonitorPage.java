package net.simpleframework.workflow.web.page.t1;

import static net.simpleframework.common.I18n.$m;

import java.io.IOException;
import java.util.Map;

import net.simpleframework.common.ID;
import net.simpleframework.mvc.PageMapping;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.ElementList;
import net.simpleframework.mvc.common.element.LinkElement;
import net.simpleframework.mvc.common.element.SpanElement;
import net.simpleframework.mvc.common.element.TabButton;
import net.simpleframework.mvc.common.element.TabButtons;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ui.menu.MenuBean;
import net.simpleframework.mvc.component.ui.menu.MenuItem;
import net.simpleframework.mvc.component.ui.menu.MenuItems;
import net.simpleframework.mvc.component.ui.pager.EPagerBarLayout;
import net.simpleframework.mvc.component.ui.pager.TablePagerBean;
import net.simpleframework.workflow.engine.ActivityBean;
import net.simpleframework.workflow.engine.EActivityStatus;
import net.simpleframework.workflow.engine.ProcessBean;
import net.simpleframework.workflow.engine.WorkitemBean;
import net.simpleframework.workflow.web.IWorkflowWebContext;
import net.simpleframework.workflow.web.WorkflowUrlsFactory;
import net.simpleframework.workflow.web.WorkflowUtils;
import net.simpleframework.workflow.web.page.t1.ActivityMgrPage.ActivityTbl;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
@PageMapping(url = "/workflow/monitor")
public class WorkflowMonitorPage extends AbstractWorkflowFormPage {

	@Override
	protected void onForward(final PageParameter pp) {
		super.onForward(pp);

		addTablePagerBean(pp);

		// workitems
		addAjaxRequest(pp, "WorkflowMonitorPage_workitems_page",
				WorkitemsMgrPage.class);
		addWindowBean(pp, "WorkflowMonitorPage_workitems")
				.setContentRef("WorkflowMonitorPage_workitems_page")
				.setWidth(800).setHeight(480);

		pp.putParameter("processId", getProcessId(pp)).putParameter("tab", 1);
	}

	protected ID getProcessId(final PageParameter pp) {
		final WorkitemBean workitem = WorkflowUtils.getWorkitemBean(pp);
		return aService.getBean(workitem.getActivityId()).getProcessId();
	}

	protected TablePagerBean addTablePagerBean(final PageParameter pp) {
		final TablePagerBean tablePager = (TablePagerBean) addTablePagerBean(
				pp, "WorkflowMonitorPage_tbl")
				.setPagerBarLayout(EPagerBarLayout.none)
				.setContainerId("idWorkflowMonitorPage_tbl")
				.setHandlerClass(_ActivityTbl.class);
		tablePager
				.addColumn(ActivityMgrPage.TC_TASKNODE())
				.addColumn(
						AbstractWorkflowMgrPage.TC_STATUS().setPropertyClass(
								EActivityStatus.class))
				.addColumn(ActivityMgrPage.TC_PARTICIPANTS())
				.addColumn(ActivityMgrPage.TC_PARTICIPANTS2())
				.addColumn(AbstractWorkflowMgrPage.TC_CREATEDATE())
				.addColumn(AbstractWorkflowMgrPage.TC_COMPLETEDATE())
				.addColumn(ActivityMgrPage.TC_RELATIVEDATE())
				.addColumn(ActivityMgrPage.TC_TIMEOUT());
		return tablePager;
	}

	@Override
	public ElementList getLeftElements(final PageParameter pp) {
		final ElementList el = super.getLeftElements(pp);
		final ProcessBean process = getProcessBean(pp);
		el.append(
				SpanElement.SPACE15,
				SpanElement.strongText(WorkflowUtils.getTitle(process) + " ["
						+ process.getStatus() + "]"));
		return el;
	}

	@Override
	protected String toHtml(final PageParameter pp,
			final Map<String, Object> variables, final String currentVariable)
			throws IOException {
		final StringBuilder sb = new StringBuilder();
		sb.append("<div class='WorkflowMonitorPage'>");
		sb.append(" <div class='ltabs'>");
		final WorkitemBean workitem = WorkflowUtils.getWorkitemBean(pp);
		final WorkflowUrlsFactory uFactory = ((IWorkflowWebContext) workflowContext)
				.getUrlsFactory();
		final TabButtons tabs = TabButtons.of(
				new TabButton($m("WorkflowMonitorPage.0")).setHref(uFactory
						.getUrl(pp, WorkflowMonitorPage.class, workitem)),
				new TabButton($m("WorkflowMonitorPage.1")).setHref(uFactory
						.getUrl(pp, WorkflowGraphMonitorPage.class, workitem)))
				.setVertical(true);
		sb.append(tabs.toString(pp));
		sb.append(" </div>");
		sb.append(toMonitorHTML(pp));
		sb.append("</div>");
		return sb.toString();
	}

	protected String toMonitorHTML(final PageParameter pp) {
		final StringBuilder sb = new StringBuilder();
		sb.append("<div class='tb'>").append("</div>");
		sb.append("<div id='idWorkflowMonitorPage_tbl'></div>");
		return sb.toString();
	}

	@Override
	public TabButtons getTabButtons(final PageParameter pp) {
		return ((AbstractWorkflowFormPage) singleton(((IWorkflowWebContext) workflowContext)
				.getUrlsFactory()
				.getPageClass(WorkflowFormPage.class.getName())))
				.getTabButtons(pp);
	}

	public static class _ActivityTbl extends ActivityTbl {

		@Override
		protected LinkElement createUserNodeLE(final ActivityBean activity) {
			return new LinkElement(activity)
					.setOnclick("$Actions['WorkflowMonitorPage_workitems']('activityId="
							+ activity.getId() + "');");
		}

		@Override
		protected String getOpe(final ActivityBean activity) {
			return null;
		}

		@Override
		public MenuItems getContextMenu(final ComponentParameter cp,
				final MenuBean menuBean, final MenuItem menuItem) {
			return null;
		}
	}

	protected static ProcessBean getProcessBean(final PageParameter pp) {
		return getCacheBean(pp, pService, "processId");
	}
}