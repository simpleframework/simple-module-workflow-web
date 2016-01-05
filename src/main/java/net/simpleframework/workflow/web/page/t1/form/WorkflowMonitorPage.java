package net.simpleframework.workflow.web.page.t1.form;

import static net.simpleframework.common.I18n.$m;

import java.io.IOException;
import java.util.Map;

import net.simpleframework.mvc.PageMapping;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.Checkbox;
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
import net.simpleframework.mvc.component.ui.pager.TablePagerColumn;
import net.simpleframework.workflow.engine.EActivityStatus;
import net.simpleframework.workflow.engine.bean.ActivityBean;
import net.simpleframework.workflow.engine.bean.ProcessBean;
import net.simpleframework.workflow.engine.bean.WorkitemBean;
import net.simpleframework.workflow.web.WorkflowUtils;
import net.simpleframework.workflow.web.page.ActivityTbl;
import net.simpleframework.workflow.web.page.WorkitemsPage;
import net.simpleframework.workflow.web.page.t1.AbstractWorkflowMgrPage;
import net.simpleframework.workflow.web.page.t1.WorkflowGraphUtils;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
@PageMapping(url = "/workflow/form/monitor")
public class WorkflowMonitorPage extends AbstractWorkflowFormPage {

	@Override
	protected void onForward(final PageParameter pp) throws Exception {
		super.onForward(pp);
		pp.addImportCSS(WorkflowGraphUtils.class, "/monitor.css");

		addTablePagerBean(pp);

		// workitems
		addAjaxRequest(pp, "WorkflowMonitorPage_workitems_page", WorkitemsPage.class);
		addWindowBean(pp, "WorkflowMonitorPage_workitems")
				.setContentRef("WorkflowMonitorPage_workitems_page").setWidth(800).setHeight(480);

		final WorkitemBean workitem = WorkflowUtils.getWorkitemBean(pp);
		if (workitem != null) {
			pp.putParameter("processId", workitem.getProcessId());
		}
		pp.putParameter("tab", 1);
	}

	protected TablePagerBean addTablePagerBean(final PageParameter pp) {
		final TablePagerBean tablePager = (TablePagerBean) addTablePagerBean(pp,
				"WorkflowMonitorPage_tbl", _ActivityTbl.class).setShowCheckbox(false)
				.setPagerBarLayout(EPagerBarLayout.none).setContainerId("idWorkflowMonitorPage_tbl");
		tablePager.addColumn(TablePagerColumn.ICON()).addColumn(ActivityTbl.TC_TASKNODE())
				.addColumn(ActivityTbl.TC_PRE_PARTICIPANTS()).addColumn(ActivityTbl.TC_PARTICIPANTS())
				.addColumn(ActivityTbl.TC_PARTICIPANTS2())
				.addColumn(AbstractWorkflowMgrPage.TC_CREATEDATE())
				.addColumn(AbstractWorkflowMgrPage.TC_COMPLETEDATE())
				.addColumn(ActivityTbl.TC_RELATIVEDATE()).addColumn(ActivityTbl.TC_TIMEOUT());
		return tablePager;
	}

	@Override
	public ElementList getLeftElements(final PageParameter pp) {
		final ElementList el = super.getLeftElements(pp);
		final ProcessBean process = WorkflowUtils.getProcessBean(pp);
		el.append(
				SpanElement.SPACE15,
				SpanElement.strongText(WorkflowUtils.getProcessTitle(process) + " ["
						+ process.getStatus() + "]"));
		return el;
	}

	@Override
	protected String toHtml(final PageParameter pp, final Map<String, Object> variables,
			final String currentVariable) throws IOException {
		final StringBuilder sb = new StringBuilder();
		sb.append("<div class='WorkflowMonitorPage'>");
		sb.append(" <div class='ltabs'>");
		final TabButtons tabs = getLeftTabButtons(pp);
		sb.append(tabs.toString(pp));
		sb.append(" </div>");
		sb.append(toMonitorHTML(pp));
		sb.append("</div>");
		return sb.toString();
	}

	public TabButtons getLeftTabButtons(final PageParameter pp) {
		final WorkitemBean workitem = WorkflowUtils.getWorkitemBean(pp);
		final TabButtons tabs = TabButtons.of(
				new TabButton($m("WorkflowMonitorPage.0")).setHref(uFactory.getUrl(pp,
						WorkflowMonitorPage.class, workitem)),
				new TabButton($m("WorkflowMonitorPage.1")).setHref(uFactory.getUrl(pp,
						WorkflowGraphMonitorPage.class, workitem))).setVertical(true);
		return tabs;
	}

	protected String toMonitorHTML(final PageParameter pp) {
		final StringBuilder sb = new StringBuilder();
		sb.append("<div class='tb clearfix'>");
		sb.append(" <div class='left'>");
		sb.append(new Checkbox("idWorkflowMonitorPage_hideNulltask", $m("WorkflowMonitorPage.2"))
				.setChecked(ActivityTbl.isNulltask_opt(pp)).setOnclick(
						checkClick("nulltask", ActivityTbl.COOKIE_HIDE_NULLTASK)));
		sb.append(" </div>");
		sb.append(" <div class='right'>");
		for (final EActivityStatus status : EActivityStatus.values()) {
			sb.append("<span class='icon'>");
			sb.append(WorkflowUtils.getStatusIcon(pp, status));
			sb.append(status);
			sb.append("</span>");
		}
		sb.append(" </div>");
		sb.append("</div>");
		sb.append("<div id='idWorkflowMonitorPage_tbl'></div>");
		return sb.toString();
	}

	private String checkClick(final String opt, final String cookie) {
		final StringBuilder sb = new StringBuilder();
		sb.append("var c = this.checked;");
		sb.append("$Actions['WorkflowMonitorPage_tbl']('").append(opt).append("=' + c);");
		sb.append("document.setCookie('").append(cookie).append("', c, 24 * 365);");
		return sb.toString();
	}

	@Override
	public TabButtons getTabButtons(final PageParameter pp) {
		final WorkitemBean workitem = WorkflowUtils.getWorkitemBean(pp);
		return TabButtons.of(createFormTab(pp, workitem),
				createMonitorTab(pp, workitem).setTabIndex(1));
	}

	public static class _ActivityTbl extends ActivityTbl {
		@Override
		protected LinkElement createUsernodeElement(final ActivityBean activity) {
			return new LinkElement(activity.getTasknodeText())
					.setOnclick("$Actions['WorkflowMonitorPage_workitems']('activityId="
							+ activity.getId() + "');");
		}

		@Override
		public MenuItems getContextMenu(final ComponentParameter cp, final MenuBean menuBean,
				final MenuItem menuItem) {
			return null;
		}

		@Override
		protected String toOpeHTML(final ComponentParameter cp, final ActivityBean activity) {
			return null;
		}
	}
}