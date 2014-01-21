package net.simpleframework.workflow.web.page.t1;

import static net.simpleframework.common.I18n.$m;

import java.io.IOException;
import java.util.Map;

import net.simpleframework.common.BeanUtils;
import net.simpleframework.ctx.IModuleRef;
import net.simpleframework.ctx.service.ado.db.IDbBeanService;
import net.simpleframework.module.common.web.page.AbstractDescPage;
import net.simpleframework.mvc.AbstractMVCPage;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.ButtonElement;
import net.simpleframework.mvc.common.element.EVerticalAlign;
import net.simpleframework.mvc.common.element.ImageElement;
import net.simpleframework.mvc.common.element.InputElement;
import net.simpleframework.mvc.common.element.Radio;
import net.simpleframework.mvc.common.element.SpanElement;
import net.simpleframework.mvc.component.base.ajaxrequest.AjaxRequestBean;
import net.simpleframework.mvc.component.ui.window.WindowBean;
import net.simpleframework.mvc.template.AbstractTemplatePage;
import net.simpleframework.mvc.template.t1.T1ResizedTemplatePage;
import net.simpleframework.workflow.engine.IWorkflowContextAware;
import net.simpleframework.workflow.web.IWorkflowWebContext;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public abstract class AbstractWorkflowMgrPage extends T1ResizedTemplatePage implements
		IWorkflowContextAware {

	@Override
	protected void onForward(final PageParameter pp) {
		super.onForward(pp);
		pp.addImportCSS(ProcessModelMgrPage.class, "/pm_mgr.css");

		// status
		addStatusWindowBean(pp);

		// 查看日志
		final IModuleRef ref = ((IWorkflowWebContext) context).getLogRef();
		Class<? extends AbstractMVCPage> lPage;
		if (ref != null && (lPage = getUpdateLogPage()) != null) {
			pp.addComponentBean("AbstractWorkflowMgrPage_update_logPage", AjaxRequestBean.class)
					.setUrlForward(AbstractMVCPage.url(lPage));
			pp.addComponentBean("AbstractWorkflowMgrPage_update_log", WindowBean.class)
					.setContentRef("AbstractWorkflowMgrPage_update_logPage").setHeight(540)
					.setWidth(864);
		}
	}

	protected WindowBean addStatusWindowBean(final PageParameter pp) {
		final Class<? extends AbstractMVCPage> sPage = getStatusDescPage();
		if (sPage != null) {
			addAjaxRequest(pp, "AbstractWorkflowMgrPage_status_page", sPage);
			return addWindowBean(pp, "AbstractWorkflowMgrPage_status")
					.setContentRef("AbstractWorkflowMgrPage_status_page").setWidth(420).setHeight(240);
		}
		return null;
	}

	protected AjaxRequestBean addDeleteAjaxRequest(final PageParameter pp) {
		return addAjaxRequest(pp, "AbstractWorkflowMgrPage_del").setHandleMethod("doDelete")
				.setConfirmMessage($m("Confirm.Delete"));
	}

	protected abstract Class<? extends AbstractMVCPage> getUpdateLogPage();

	protected abstract Class<? extends AbstractMVCPage> getStatusDescPage();

	protected static ButtonElement createLogButton(final String params) {
		return ButtonElement.logBtn()
				.setDisabled(((IWorkflowWebContext) context).getLogRef() == null)
				.setOnclick("$Actions['AbstractWorkflowMgrPage_update_log']('" + params + "');");
	}

	protected static ImageElement createStatusImage(final PageParameter pp, final Enum<?> status) {
		return new ImageElement(pp.getCssResourceHomePath(AbstractWorkflowMgrPage.class)
				+ "/images/status_" + status.name() + ".png").setVerticalAlign(EVerticalAlign.bottom)
				.setClassName("icon16");
	}

	public static abstract class AbstractStatusDescPage extends AbstractDescPage {

		@SuppressWarnings("unchecked")
		protected <T> void updateStatus(final PageParameter pp, final IDbBeanService<T> service,
				final String[] idArr, final Enum<?> op) {
			if (idArr == null) {
				return;
			}
			for (final String id : idArr) {
				final T bean = service.getBean(id);
				if (bean != null && op != BeanUtils.getProperty(bean, "status")) {
					setLogDescription(pp, bean);
					BeanUtils.setProperty(bean, "status", op);
					service.update(new String[] { "status" }, bean);
				}
			}
		}

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
		protected void onForward(final PageParameter pp) {
			super.onForward(pp);

			addAjaxRequest(pp, "AbstractAbortPage_ok").setConfirmMessage($m("Comfirm.Save"))
					.setHandleMethod("doOk").setSelector(".AbstractAbortPage");
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
					.append(ButtonElement.WINDOW_CLOSE);
			sb.append(" </div>");
			sb.append("</div>");
			return sb.toString();
		}
	}
}
