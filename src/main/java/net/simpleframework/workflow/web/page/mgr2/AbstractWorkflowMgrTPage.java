package net.simpleframework.workflow.web.page.mgr2;

import net.simpleframework.ctx.IModuleRef;
import net.simpleframework.module.common.web.page.AbstractMgrTPage;
import net.simpleframework.mvc.AbstractMVCPage;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.LinkButton;
import net.simpleframework.mvc.component.base.ajaxrequest.AjaxRequestBean;
import net.simpleframework.mvc.component.ui.window.WindowBean;
import net.simpleframework.workflow.engine.IWorkflowContextAware;
import net.simpleframework.workflow.web.IWorkflowWebContext;
import net.simpleframework.workflow.web.WorkflowUrlsFactory;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class AbstractWorkflowMgrTPage extends AbstractMgrTPage implements IWorkflowContextAware {

	@Override
	protected void onForward(final PageParameter pp) throws Exception {
		super.onForward(pp);
		pp.addImportCSS(AbstractWorkflowMgrTPage.class, "/wfmgrt.css");

		// 查看日志
		addLogWindowBean(pp);
		// status
		addStatusWindowBean(pp);
	}

	@Override
	protected String getPageCSS(final PageParameter pp) {
		return getOriginalClass().getSimpleName();
	}

	@Override
	protected LinkButton createOrgCancelBtn(final PageParameter pp) {
		return null;
	}

	protected WindowBean addLogWindowBean(final PageParameter pp) {
		final IModuleRef ref = ((IWorkflowWebContext) workflowContext).getLogRef();
		Class<? extends AbstractMVCPage> lPage;
		if (ref != null && (lPage = getUpdateLogPage()) != null) {
			final AjaxRequestBean ajaxRequest = addAjaxRequest(pp,
					"AbstractWorkflowMgrTPage_update_logPage", lPage);
			return addWindowBean(pp, "AbstractWorkflowMgrTPage_update_log", ajaxRequest).setHeight(540)
					.setWidth(864);
		}
		return null;
	}

	protected WindowBean addStatusWindowBean(final PageParameter pp) {
		final Class<? extends AbstractMVCPage> sPage = getStatusDescPage();
		if (sPage != null) {
			final AjaxRequestBean ajaxRequest = addAjaxRequest(pp,
					"AbstractWorkflowMgrTPage_status_page", sPage);
			return addWindowBean(pp, "AbstractWorkflowMgrTPage_status", ajaxRequest).setWidth(420)
					.setHeight(240);
		}
		return null;
	}

	protected Class<? extends AbstractMVCPage> getUpdateLogPage() {
		return null;
	}

	protected Class<? extends AbstractMVCPage> getStatusDescPage() {
		return null;
	}

	@Override
	public String getPageRole(final PageParameter pp) {
		return getPageManagerRole(pp);
	}

	protected static WorkflowUrlsFactory uFactory = ((IWorkflowWebContext) workflowContext)
			.getUrlsFactory();
}