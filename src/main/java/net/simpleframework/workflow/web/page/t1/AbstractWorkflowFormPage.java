package net.simpleframework.workflow.web.page.t1;

import net.simpleframework.ctx.permission.IPermissionConst;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.ElementList;
import net.simpleframework.mvc.common.element.LinkButton;
import net.simpleframework.mvc.template.t1.T1FormTemplatePage;
import net.simpleframework.workflow.engine.EWorkitemStatus;
import net.simpleframework.workflow.engine.IWorkflowContextAware;
import net.simpleframework.workflow.engine.WorkitemBean;
import net.simpleframework.workflow.web.IWorkflowWebContext;
import net.simpleframework.workflow.web.IWorkflowWebForm;
import net.simpleframework.workflow.web.WorkflowUrlsFactory;
import net.simpleframework.workflow.web.page.AbstractItemsTPage;
import net.simpleframework.workflow.web.page.DelegateListTPage;
import net.simpleframework.workflow.web.page.MyRunningWorklistTPage;
import net.simpleframework.workflow.web.page.WorkflowUtils;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public abstract class AbstractWorkflowFormPage extends T1FormTemplatePage implements
		IWorkflowContextAware {

	@Override
	protected boolean isPage404(final PageParameter pp) {
		return WorkflowUtils.getWorkitemBean(pp) == null;
	}

	@Override
	protected void onForward(final PageParameter pp) {
		super.onForward(pp);
		pp.addImportCSS(AbstractItemsTPage.class, "/my_work.css");
	}

	protected IWorkflowWebForm getWorkflowForm(final PageParameter pp) {
		final WorkitemBean workitem = WorkflowUtils.getWorkitemBean(pp);
		return (IWorkflowWebForm) aService.getWorkflowForm(wService.getActivity(workitem));
	}

	@Override
	public ElementList getLeftElements(final PageParameter pp) {
		final LinkButton backBtn = backBtn();
		final WorkflowUrlsFactory uFactory = ((IWorkflowWebContext) context).getUrlsFactory();
		final String source = pp.getParameter("source");
		if ("delegation".equals(source)) {
			backBtn.setHref(uFactory.getUrl(pp, DelegateListTPage.class));
		} else {
			final StringBuilder sb = new StringBuilder();
			final WorkitemBean workitem = WorkflowUtils.getWorkitemBean(pp);
			EWorkitemStatus status;
			if (workitem != null
					&& (status = workitem.getStatus()).ordinal() > EWorkitemStatus.delegate.ordinal()) {
				sb.append("status=").append(status.name());
			}
			backBtn.setHref(uFactory.getUrl(pp, MyRunningWorklistTPage.class, sb.toString()));
		}
		final ElementList el = ElementList.of(backBtn);
		return el;
	}

	@Override
	public String getRole(final PageParameter pp) {
		return IPermissionConst.ROLE_ALL_ACCOUNT;
	}
}
