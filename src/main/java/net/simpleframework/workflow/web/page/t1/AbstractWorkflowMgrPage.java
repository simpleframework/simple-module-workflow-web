package net.simpleframework.workflow.web.page.t1;

import static net.simpleframework.common.I18n.$m;

import java.io.IOException;
import java.util.Map;

import net.simpleframework.ctx.IModuleRef;
import net.simpleframework.ctx.permission.PermissionConst;
import net.simpleframework.module.common.web.page.AbstractDescPage;
import net.simpleframework.mvc.AbstractMVCPage;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.ButtonElement;
import net.simpleframework.mvc.common.element.Icon;
import net.simpleframework.mvc.common.element.InputElement;
import net.simpleframework.mvc.common.element.LinkButton;
import net.simpleframework.mvc.common.element.Radio;
import net.simpleframework.mvc.common.element.SpanElement;
import net.simpleframework.mvc.component.base.ajaxrequest.AjaxRequestBean;
import net.simpleframework.mvc.component.ui.pager.TablePagerColumn;
import net.simpleframework.mvc.component.ui.window.WindowBean;
import net.simpleframework.mvc.template.AbstractTemplatePage;
import net.simpleframework.mvc.template.t1.T1ResizedTemplatePage;
import net.simpleframework.workflow.web.IWorkflowWebContext;
import net.simpleframework.workflow.web.page.IWorkflowPageAware;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public abstract class AbstractWorkflowMgrPage extends T1ResizedTemplatePage implements
		IWorkflowPageAware {

	@Override
	protected void onForward(final PageParameter pp) throws Exception {
		super.onForward(pp);
		pp.addImportCSS(ProcessModelMgrPage.class, "/pmgr.css");

		// status
		addStatusWindowBean(pp);
		// 查看日志
		addLogWindowBean(pp);
	}

	@Override
	public String getPageRole(final PageParameter pp) {
		return PermissionConst.ROLE_MANAGER;
	}

	protected WindowBean addLogWindowBean(final PageParameter pp) {
		final IModuleRef ref = ((IWorkflowWebContext) workflowContext).getLogRef();
		Class<? extends AbstractMVCPage> lPage;
		if (ref != null && (lPage = getUpdateLogPage()) != null) {
			final AjaxRequestBean ajaxRequest = addAjaxRequest(pp,
					"AbstractWorkflowMgrPage_update_logPage", lPage);
			return addWindowBean(pp, "AbstractWorkflowMgrPage_update_log", ajaxRequest).setHeight(540)
					.setWidth(864);
		}
		return null;
	}

	protected WindowBean addStatusWindowBean(final PageParameter pp) {
		final Class<? extends AbstractMVCPage> sPage = getStatusDescPage();
		if (sPage != null) {
			final AjaxRequestBean ajaxRequest = addAjaxRequest(pp,
					"AbstractWorkflowMgrPage_status_page", sPage);
			return addWindowBean(pp, "AbstractWorkflowMgrPage_status", ajaxRequest).setWidth(420)
					.setHeight(240);
		}
		return null;
	}

	protected AjaxRequestBean addDeleteAjaxRequest(final PageParameter pp) {
		return addAjaxRequest(pp, "AbstractWorkflowMgrPage_del").setHandlerMethod("doDelete")
				.setConfirmMessage($m("Confirm.Delete"));
	}

	protected LinkButton createBackButton() {
		return new LinkButton($m("AbstractWorkflowMgrPage.7")).setIconClass(Icon.share_alt);
	}

	protected abstract Class<? extends AbstractMVCPage> getUpdateLogPage();

	protected abstract Class<? extends AbstractMVCPage> getStatusDescPage();

	public static abstract class AbstractStatusDescPage extends AbstractDescPage {

		@Override
		public String getTitle(final PageParameter pp) {
			final String op = pp.getParameter("op");
			if ("suspended".equals(op)) {
				return $m("AbstractWorkflowMgrPage.0");
			} else if ("running".equals(op)) {
				return $m("AbstractWorkflowMgrPage.1");
			}
			return null;
		}
	}

	public static abstract class AbstractAbortPage extends AbstractTemplatePage {
		@Override
		protected void onForward(final PageParameter pp) throws Exception {
			super.onForward(pp);

			addAjaxRequest(pp, "AbstractAbortPage_ok").setConfirmMessage($m("Comfirm.Save"))
					.setHandlerMethod("doOk").setSelector(".AbstractAbortPage");
		}

		protected abstract Enum<?>[] getEnumConstants();

		protected abstract InputElement getIdInput(PageParameter pp);

		@Override
		protected String toHtml(final PageParameter pp, final Map<String, Object> variables,
				final String currentVariable) throws IOException {
			final StringBuilder sb = new StringBuilder();
			sb.append("<div class='AbstractAbortPage simple_window_tcb'>");
			sb.append(" <div class='t'>").append($m("AbstractWorkflowMgrPage.2")).append("</div>");
			sb.append(" <div class='c'>");
			sb.append(getIdInput(pp));
			int i = 0;
			for (final Enum<?> e : getEnumConstants()) {
				if (i > 0) {
					sb.append("<br>");
				}
				sb.append(new Radio("abort_policy" + i, e).setName("abort_policy").setValue(e.name())
						.setChecked(i++ == 0));
			}
			sb.append(" </div>");
			sb.append(" <div class='b'>");
			sb.append(
					ButtonElement.okBtn().setHighlight(true)
							.setOnclick("$Actions['AbstractAbortPage_ok']();")).append(SpanElement.SPACE)
					.append(ButtonElement.closeBtn());
			sb.append(" </div>");
			sb.append("</div>");
			return sb.toString();
		}
	}

	public static TablePagerColumn TC_TITLE() {
		return new TablePagerColumn("title", $m("AbstractWorkflowMgrPage.3"));
	}

	public static TablePagerColumn TC_CREATEDATE() {
		return TablePagerColumn.DATE("createDate", $m("AbstractWorkflowMgrPage.4"));
	}

	public static TablePagerColumn TC_COMPLETEDATE() {
		return TablePagerColumn.DATE("completeDate", $m("AbstractWorkflowMgrPage.5"));
	}

	public static <T extends Enum<T>> TablePagerColumn TC_STATUS(final Class<T> e) {
		final TablePagerColumn col = new TablePagerColumn("status", $m("AbstractWorkflowMgrPage.6"),
				60).setSort(false);
		if (e != null) {
			col.setPropertyClass(e);
		}
		return col;
	}
}
