package net.simpleframework.workflow.web.page.mgr2;

import static net.simpleframework.common.I18n.$m;

import java.io.IOException;
import java.util.Map;

import net.simpleframework.mvc.AbstractMVCPage;
import net.simpleframework.mvc.IForward;
import net.simpleframework.mvc.JavascriptForward;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.ButtonElement;
import net.simpleframework.mvc.common.element.ElementList;
import net.simpleframework.mvc.common.element.JS;
import net.simpleframework.mvc.common.element.LinkButton;
import net.simpleframework.mvc.common.element.LinkElement;
import net.simpleframework.mvc.common.element.SpanElement;
import net.simpleframework.mvc.common.element.TabButton;
import net.simpleframework.mvc.common.element.TabButtons;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.base.ajaxrequest.AjaxRequestBean;
import net.simpleframework.mvc.component.ui.menu.MenuItem;
import net.simpleframework.mvc.component.ui.pager.EPagerBarLayout;
import net.simpleframework.mvc.component.ui.pager.TablePagerBean;
import net.simpleframework.mvc.component.ui.pager.TablePagerColumn;
import net.simpleframework.workflow.engine.EActivityStatus;
import net.simpleframework.workflow.engine.bean.ActivityBean;
import net.simpleframework.workflow.engine.bean.ProcessBean;
import net.simpleframework.workflow.web.WorkflowLogRef.ActivityUpdateLogPage;
import net.simpleframework.workflow.web.WorkflowUtils;
import net.simpleframework.workflow.web.page.ActivityTbl;
import net.simpleframework.workflow.web.page.WorkitemsPage;
import net.simpleframework.workflow.web.page.t1.AbstractWorkflowMgrPage;
import net.simpleframework.workflow.web.page.t1.ActivityMgrPage.ActivityAbortPage;
import net.simpleframework.workflow.web.page.t1.ActivityMgrPage.ActivityStatusDescPage;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class ActivityMgrTPage extends AbstractWorkflowMgrTPage {

	@Override
	protected void onForward(final PageParameter pp) throws Exception {
		super.onForward(pp);

		addTablePagerBean(pp);

		// workitems
		AjaxRequestBean ajaxRequest = addAjaxRequest(pp, "ActivityMgrTPage_workitemsPage",
				WorkitemsPage.class);
		addWindowBean(pp, "ActivityMgrTPage_workitems", ajaxRequest).setWidth(800).setHeight(480);

		// 放弃
		ajaxRequest = addAjaxRequest(pp, "ActivityMgrTPage_abortPage", _ActivityAbortPage.class);
		addWindowBean(pp, "ActivityMgrTPage_abort", ajaxRequest).setResizable(false)
				.setTitle(EActivityStatus.abort.toString()).setWidth(420).setHeight(240);

		// addComponentBean(pp, "ActivityMgrTPage_abort2",
		// ActivityAbortBean.class);
	}

	protected TablePagerBean addTablePagerBean(final PageParameter pp) {
		final TablePagerBean tablePager = (TablePagerBean) addTablePagerBean(pp,
				"ActivityMgrTPage_tbl", _ActivityTbl.class).setShowCheckbox(false)
						.setPagerBarLayout(EPagerBarLayout.none).setContainerId("idActivityMgrTPage_tbl");
		tablePager.addColumn(TablePagerColumn.ICON()).addColumn(ActivityTbl.TC_TASKNODE())
				.addColumn(ActivityTbl.TC_PARTICIPANTS()).addColumn(ActivityTbl.TC_PARTICIPANTS2())
				.addColumn(AbstractWorkflowMgrPage.TC_CREATEDATE()).addColumn(ActivityTbl.TC_TIMEOUT())
				.addColumn(AbstractWorkflowMgrPage.TC_COMPLETEDATE())
				// .addColumn(ActivityMgrPage.TC_PREVIOUS())
				.addColumn(TablePagerColumn.OPE(70));
		return tablePager;
	}

	@Override
	protected Class<? extends AbstractMVCPage> getUpdateLogPage() {
		return ActivityUpdateLogPage.class;
	}

	@Override
	protected Class<? extends AbstractMVCPage> getStatusDescPage() {
		return _ActivityStatusDescPage.class;
	}

	@Override
	public ElementList getLeftElements(final PageParameter pp) {
		final ProcessBean process = WorkflowUtils.getProcessBean(pp);
		final ElementList el = ElementList.of(
				LinkButton.backBtn()
						.setOnclick(JS.loc(uFactory.getUrl(pp, ProcessMgrTPage.class,
								"modelId=" + (process != null ? process.getModelId() : "")))),
				// SpanElement.SPACE,
				// LinkButton.of(EActivityStatus.abort).setOnclick(
				// "$Actions['ActivityMgrTPage_abort2']('processId=" +
				// process.getId() + "');"),
				SpanElement.SPACE15);
		return el.appendAll(super.getLeftElements(pp));
	}

	@Override
	protected SpanElement createOrgElement(final PageParameter pp) {
		final SpanElement oele = super.createOrgElement(pp);
		final ProcessBean process = WorkflowUtils.getProcessBean(pp);
		if (process != null) {
			oele.setText(oele.getText() + " - " + WorkflowUtils.getProcessTitle(process));
		}
		return oele;
	}

	@Override
	protected String toHtml(final PageParameter pp, final Map<String, Object> variables,
			final String currentVariable) throws IOException {
		final StringBuilder sb = new StringBuilder();
		sb.append(toMonitorHTML(pp));
		return sb.toString();
	}

	protected String toMonitorHTML(final PageParameter pp) {
		final StringBuilder sb = new StringBuilder();
		sb.append("<div id='idActivityMgrTPage_tbl'></div>");
		return sb.toString();
	}

	@Override
	public TabButtons getTabButtons(final PageParameter pp) {
		final ProcessBean process = WorkflowUtils.getProcessBean(pp);
		final String params = "processId=" + (process != null ? process.getId() : "");
		return TabButtons.of(
				new TabButton($m("ActivityMgrPage.1"),
						uFactory.getUrl(pp, ActivityMgrTPage.class, params)),
				new TabButton($m("ActivityMgrPage.2"),
						uFactory.getUrl(pp, ActivityGraphMgrTPage.class, params)));
	}

	public static class _ActivityAbortPage extends ActivityAbortPage {

		@Override
		protected IForward doJavascriptForward(final ComponentParameter cp) {
			return new JavascriptForward(
					"$Actions['ActivityMgrTPage_abort'].close(); $Actions['ActivityMgrTPage_tbl']();");
		}
	}

	public static class _ActivityStatusDescPage extends ActivityStatusDescPage {
	}

	public static class _ActivityTbl extends ActivityTbl {
		@Override
		protected ButtonElement createLogButton(final ComponentParameter cp,
				final ActivityBean activity) {
			return super.createLogButton(cp, activity)
					.setOnclick("$Actions['AbstractWorkflowMgrTPage_update_log']('activityId="
							+ activity.getId() + "');");
		}

		@Override
		protected LinkElement createUsernodeElement(final ActivityBean activity) {
			return new LinkElement(activity.getTasknodeText()).setOnclick(
					"$Actions['ActivityMgrTPage_workitems']('activityId=" + activity.getId() + "');");
		}

		@Override
		protected MenuItem MI_STATUS_RUNNING() {
			return super.MI_STATUS_RUNNING().setOnclick_act("AbstractWorkflowMgrTPage_status",
					"activityId", "op=running");
		}

		@Override
		protected MenuItem MI_STATUS_SUSPENDED() {
			return super.MI_STATUS_SUSPENDED().setOnclick_act("AbstractWorkflowMgrTPage_status",
					"activityId", "op=suspended");
		}

		@Override
		protected MenuItem MI_STATUS_DO_ABORT() {
			return super.MI_STATUS_DO_ABORT().setOnclick_act("ActivityMgrTPage_abort", "activityId");
		}
	}
}
