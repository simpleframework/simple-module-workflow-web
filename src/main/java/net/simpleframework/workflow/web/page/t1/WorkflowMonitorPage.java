package net.simpleframework.workflow.web.page.t1;

import static net.simpleframework.common.I18n.$m;

import java.io.IOException;
import java.util.Map;

import net.simpleframework.mvc.PageMapping;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.TabButton;
import net.simpleframework.mvc.common.element.TabButtons;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ui.menu.MenuBean;
import net.simpleframework.mvc.component.ui.menu.MenuItem;
import net.simpleframework.mvc.component.ui.menu.MenuItems;
import net.simpleframework.mvc.component.ui.pager.EPagerBarLayout;
import net.simpleframework.mvc.component.ui.pager.TablePagerBean;
import net.simpleframework.mvc.component.ui.pager.TablePagerColumn;
import net.simpleframework.workflow.engine.ActivityBean;
import net.simpleframework.workflow.engine.WorkitemBean;
import net.simpleframework.workflow.web.AbstractWorkflowFormTPage;
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

		addPageComponents(pp);

		final WorkitemBean workitem = AbstractWorkflowFormTPage.getWorkitemBean(pp);
		pp.putParameter("processId", aService.getBean(workitem.getActivityId()).getProcessId())
				.putParameter("tab", 1);
	}

	protected void addPageComponents(final PageParameter pp) {
		final TablePagerBean tablePager = (TablePagerBean) addTablePagerBean(pp,
				"WorkflowMonitorPage_tbl").setPagerBarLayout(EPagerBarLayout.none)
				.setContainerId("idWorkflowMonitorPage_tbl").setHandleClass(_ActivityTbl.class);
		tablePager.addColumn(ActivityMgrPage.TC_TASKNODE()).addColumn(ActivityMgrPage.TC_PREVIOUS())
				.addColumn(ActivityMgrPage.TC_PARTICIPANTS())
				.addColumn(ActivityMgrPage.TC_PARTICIPANTS2())
				.addColumn(ActivityMgrPage.TC_CREATEDATE())
				.addColumn(ActivityMgrPage.TC_COMPLETEDATE()).addColumn(ActivityMgrPage.TC_STATUS())
				.addColumn(TablePagerColumn.OPE().setWidth(90));
	}

	@Override
	protected String toHtml(final PageParameter pp, final Map<String, Object> variables,
			final String currentVariable) throws IOException {
		final StringBuilder sb = new StringBuilder();
		sb.append("<div class='WorkflowMonitorPage'>");
		sb.append(" <div class='tabs'>");
		final WorkitemBean workitem = AbstractWorkflowFormTPage.getWorkitemBean(pp);
		final TabButtons tabs = TabButtons.of(
				new TabButton($m("WorkflowMonitorPage.0")).setHref(getUrlsFactory().getUrl(pp,
						WorkflowMonitorPage.class, workitem)),
				new TabButton($m("WorkflowMonitorPage.1")).setHref(getUrlsFactory().getUrl(pp,
						WorkflowGraphMonitorPage.class, workitem))).setVertical(true);
		sb.append(tabs.toString(pp));
		sb.append("</div>");
		sb.append(" <div class='tb'></div>");
		sb.append(" <div id='idWorkflowMonitorPage_tbl'></div>");
		sb.append("</div>");
		return sb.toString();
	}

	@Override
	public TabButtons getTabButtons(final PageParameter pp) {
		return ((AbstractWorkflowFormPage) singleton(getUrlsFactory().getPageClass(
				WorkflowFormPage.class.getName()))).getTabButtons(pp);
	}

	public static class _ActivityTbl extends ActivityTbl {
		@Override
		protected String getOpe(final ActivityBean activity) {
			return null;
		}

		@Override
		public MenuItems getContextMenu(final ComponentParameter cp, final MenuBean menuBean,
				final MenuItem menuItem) {
			return null;
		}
	}
}