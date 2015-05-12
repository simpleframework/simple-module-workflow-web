package net.simpleframework.workflow.web.page.org2;

import static net.simpleframework.common.I18n.$m;
import net.simpleframework.ctx.IModuleRef;
import net.simpleframework.ctx.permission.PermissionDept;
import net.simpleframework.module.common.web.page.AbstractMgrTPage;
import net.simpleframework.mvc.AbstractMVCPage;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.ElementList;
import net.simpleframework.mvc.common.element.SpanElement;
import net.simpleframework.mvc.component.base.ajaxrequest.AjaxRequestBean;
import net.simpleframework.mvc.component.ui.window.WindowBean;
import net.simpleframework.workflow.engine.IWorkflowServiceAware;
import net.simpleframework.workflow.web.IWorkflowWebContext;
import net.simpleframework.workflow.web.WorkflowUrlsFactory;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class AbstractWorkflowMgrTPage extends AbstractMgrTPage implements IWorkflowServiceAware {

	@Override
	protected void onForward(final PageParameter pp) {
		super.onForward(pp);
		pp.addImportCSS(AbstractWorkflowMgrTPage.class, "/wfmgrt.css");

		// 查看日志
		addLogWindowBean(pp);
		// status
		addStatusWindowBean(pp);
	}

	protected WindowBean addLogWindowBean(final PageParameter pp) {
		final IModuleRef ref = ((IWorkflowWebContext) workflowContext).getLogRef();
		Class<? extends AbstractMVCPage> lPage;
		if (ref != null && (lPage = getUpdateLogPage()) != null) {
			final AjaxRequestBean ajaxRequest = addAjaxRequest(pp,
					"AbstractWorkflowMgrTPage_update_logPage", lPage);
			return addWindowBean(pp, "AbstractWorkflowMgrTPage_update_log", ajaxRequest)
					.setHeight(540).setWidth(864);
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
	public String getRole(final PageParameter pp) {
		return workflowContext.getModule().getManagerRole();
	}

	protected SpanElement createOrgElement(final PageParameter pp) {
		SpanElement oele;
		final PermissionDept org = getPermissionOrg(pp);
		if (org != null) {
			oele = new SpanElement(org.getText());
		} else {
			oele = new SpanElement($m("AbstractMgrTPage.0"));
		}
		return oele;
	}

	@Override
	public ElementList getLeftElements(final PageParameter pp) {
		final ElementList el = ElementList.of(createOrgElement(pp).setClassName("org_txt"));
		// if (pp.getLogin().isManager()) {
		// el.append(SpanElement.SPACE).append(
		// new LinkElement($m("AbstractOrgMgrTPage.4"))
		// .setOnclick("$Actions['AbstractMgrTPage_orgSelect']();"));
		// }
		return el;
	}

	protected static WorkflowUrlsFactory uFactory = ((IWorkflowWebContext) workflowContext)
			.getUrlsFactory();
}