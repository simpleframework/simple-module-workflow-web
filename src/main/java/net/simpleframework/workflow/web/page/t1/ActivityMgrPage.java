package net.simpleframework.workflow.web.page.t1;

import static net.simpleframework.common.I18n.$m;

import java.io.IOException;
import java.util.Map;

import net.simpleframework.common.Convert;
import net.simpleframework.common.StringUtils;
import net.simpleframework.ctx.trans.Transaction;
import net.simpleframework.mvc.AbstractMVCPage;
import net.simpleframework.mvc.IForward;
import net.simpleframework.mvc.JavascriptForward;
import net.simpleframework.mvc.PageMapping;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.ElementList;
import net.simpleframework.mvc.common.element.InputElement;
import net.simpleframework.mvc.common.element.LinkButton;
import net.simpleframework.mvc.common.element.SpanElement;
import net.simpleframework.mvc.common.element.TabButton;
import net.simpleframework.mvc.common.element.TabButtons;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.base.ajaxrequest.AjaxRequestBean;
import net.simpleframework.mvc.component.ui.pager.EPagerBarLayout;
import net.simpleframework.mvc.component.ui.pager.TablePagerBean;
import net.simpleframework.mvc.component.ui.pager.TablePagerColumn;
import net.simpleframework.mvc.template.struct.NavigationButtons;
import net.simpleframework.workflow.engine.EActivityAbortPolicy;
import net.simpleframework.workflow.engine.EActivityStatus;
import net.simpleframework.workflow.engine.IWorkflowContext;
import net.simpleframework.workflow.engine.bean.ActivityBean;
import net.simpleframework.workflow.engine.bean.ProcessBean;
import net.simpleframework.workflow.web.WorkflowLogRef.ActivityUpdateLogPage;
import net.simpleframework.workflow.web.WorkflowUtils;
import net.simpleframework.workflow.web.component.abort.ActivityAbortBean;
import net.simpleframework.workflow.web.page.ActivityTbl;
import net.simpleframework.workflow.web.page.WorkitemsPage;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
@PageMapping(url = "/workflow/mgr/activity")
public class ActivityMgrPage extends AbstractWorkflowMgrPage {

	@Override
	protected void onForward(final PageParameter pp) throws Exception {
		super.onForward(pp);

		// 表格
		addTablePagerBean(pp);

		// 放弃
		AjaxRequestBean ajaxRequest = addAjaxRequest(pp, "ActivityMgrPage_abortPage",
				ActivityAbortPage.class);
		addWindowBean(pp, "ActivityMgrPage_abort", ajaxRequest).setResizable(false)
				.setTitle(EActivityStatus.abort.toString()).setWidth(420).setHeight(240);

		// workitems
		ajaxRequest = addAjaxRequest(pp, "ActivityMgrPage_workitems_page", WorkitemsPage.class);
		addWindowBean(pp, "ActivityMgrPage_workitems", ajaxRequest).setWidth(800).setHeight(480);

		addComponentBean(pp, "ActivityMgrPage_abort2", ActivityAbortBean.class);
	}

	protected TablePagerBean addTablePagerBean(final PageParameter pp) {
		final TablePagerBean tablePager = (TablePagerBean) addTablePagerBean(pp,
				"ActivityMgrPage_tbl", ActivityTbl.class).setPagerBarLayout(EPagerBarLayout.none)
						.setContainerId("idActivityMgrPage_tbl");
		tablePager.addColumn(TablePagerColumn.ICON()).addColumn(ActivityTbl.TC_TASKNODE())
				.addColumn(ActivityTbl.TC_PARTICIPANTS()).addColumn(ActivityTbl.TC_PARTICIPANTS2())
				.addColumn(TC_CREATEDATE()).addColumn(ActivityTbl.TC_TIMEOUT())
				.addColumn(TC_COMPLETEDATE()).addColumn(ActivityTbl.TC_PREVIOUS())
				.addColumn(TablePagerColumn.OPE(70));
		return tablePager;
	}

	@Override
	public ElementList getLeftElements(final PageParameter pp) {
		final ProcessBean process = WorkflowUtils.getProcessBean(pp);
		return ElementList.of(
				createBackButton()
						.setHref(url(ProcessMgrPage.class, "modelId=" + process.getModelId())),
				SpanElement.SPACE,
				LinkButton.of(EActivityStatus.abort).setOnclick(
						"$Actions['ActivityMgrPage_abort2']('processId=" + process.getId() + "');"),
				SpanElement.SPACE15, SpanElement.strongText(WorkflowUtils.getProcessTitle(process)));
	}

	@Override
	public ElementList getRightElements(final PageParameter pp) {
		final ProcessBean process = WorkflowUtils.getProcessBean(pp);
		final Object id = process.getId();
		return ElementList.of(createTabsElement(pp,
				TabButtons.of(
						new TabButton($m("ActivityMgrPage.1"),
								url(ActivityMgrPage.class, "processId=" + id)),
						new TabButton($m("ActivityMgrPage.2"),
								url(ActivityGraphMgrPage.class, "processId=" + id)))));
	}

	@Override
	public NavigationButtons getNavigationBar(final PageParameter pp) {
		return super.getNavigationBar(pp).append(new SpanElement($m("ActivityMgrPage.0")));
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
		return ActivityStatusDescPage.class;
	}

	@Override
	protected Class<? extends AbstractMVCPage> getUpdateLogPage() {
		return ActivityUpdateLogPage.class;
	}

	public static class ActivityStatusDescPage extends AbstractStatusDescPage {
		@Override
		public String getIDParameterKey() {
			return "activityId";
		}

		@Transaction(context = IWorkflowContext.class)
		@Override
		public JavascriptForward onSave(final ComponentParameter cp) throws Exception {
			final EActivityStatus op = cp.getEnumParameter(EActivityStatus.class, "op");
			for (final String aId : StringUtils.split(cp.getParameter("activityId"), ";")) {
				final ActivityBean activity = wfaService.getBean(aId);
				setLogDescription(cp, activity);
				if (op == EActivityStatus.suspended) {
					wfaService.doSuspend(activity);
				} else if (op == EActivityStatus.running) {
					wfaService.doResume(activity);
				}
			}
			return super.onSave(cp).append("$Actions['ActivityMgrPage_tbl']();");
		}
	}

	public static class ActivityAbortPage extends AbstractAbortPage {

		@Transaction(context = IWorkflowContext.class)
		public IForward doOk(final ComponentParameter cp) {
			final ActivityBean activity = wfaService.getBean(cp.getParameter("activityId"));
			wfaService.doAbort(activity,
					Convert.toEnum(EActivityAbortPolicy.class, cp.getParameter("abort_policy")));
			return doJavascriptForward(cp);
		}

		protected IForward doJavascriptForward(final ComponentParameter cp) {
			return new JavascriptForward(
					"$Actions['ActivityMgrPage_abort'].close(); $Actions['ActivityMgrPage_tbl']();");
		}

		@Override
		protected Enum<?>[] getEnumConstants() {
			return EActivityAbortPolicy.values();
		}

		@Override
		protected InputElement getIdInput(final PageParameter pp) {
			return InputElement.hidden("activityId").setValue(pp);
		}
	}
}
