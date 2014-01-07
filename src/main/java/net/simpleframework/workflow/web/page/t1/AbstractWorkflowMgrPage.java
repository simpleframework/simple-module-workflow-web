package net.simpleframework.workflow.web.page.t1;

import static net.simpleframework.common.I18n.$m;
import net.simpleframework.ctx.IModuleRef;
import net.simpleframework.mvc.AbstractMVCPage;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.ButtonElement;
import net.simpleframework.mvc.component.base.ajaxrequest.AjaxRequestBean;
import net.simpleframework.mvc.component.ui.window.WindowBean;
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
		if (ref != null) {
			pp.addComponentBean("AbstractWorkflowMgrPage_update_logPage", AjaxRequestBean.class)
					.setUrlForward(AbstractMVCPage.url(getUpdateLogPage()));
			pp.addComponentBean("AbstractWorkflowMgrPage_update_log", WindowBean.class)
					.setContentRef("AbstractWorkflowMgrPage_update_logPage").setHeight(540)
					.setWidth(864);
		}
	}

	protected WindowBean addStatusWindowBean(PageParameter pp) {
		addAjaxRequest(pp, "AbstractWorkflowMgrPage_status_page", getStatusDescPage());
		return addWindowBean(pp, "AbstractWorkflowMgrPage_status")
				.setContentRef("AbstractWorkflowMgrPage_status_page").setWidth(420).setHeight(240);
	}

	protected AjaxRequestBean addDeleteAjaxRequest(PageParameter pp) {
		return addAjaxRequest(pp, "AbstractWorkflowMgrPage_del").setHandleMethod("doDelete")
				.setConfirmMessage($m("Confirm.Delete"));
	}

	protected abstract Class<? extends AbstractMVCPage> getStatusDescPage();

	protected abstract Class<? extends AbstractMVCPage> getUpdateLogPage();

	protected static ButtonElement createLogButton(String params) {
		return ButtonElement.logBtn()
				.setDisabled(((IWorkflowWebContext) context).getLogRef() == null)
				.setOnclick("$Actions['AbstractWorkflowMgrPage_update_log']('" + params + "');");
	}
}
