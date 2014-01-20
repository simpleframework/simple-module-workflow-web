package net.simpleframework.workflow.web.page.t1;

import java.io.IOException;
import java.util.Map;

import net.simpleframework.common.StringUtils;
import net.simpleframework.ctx.permission.IPermissionConst;
import net.simpleframework.mvc.PageMapping;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.ElementList;
import net.simpleframework.mvc.common.element.LinkButton;
import net.simpleframework.mvc.template.t1.T1FormTemplatePage;
import net.simpleframework.workflow.engine.EWorkitemStatus;
import net.simpleframework.workflow.engine.IWorkflowContextAware;
import net.simpleframework.workflow.engine.IWorkflowForm;
import net.simpleframework.workflow.engine.WorkitemBean;
import net.simpleframework.workflow.web.AbstractWorkflowFormPage;
import net.simpleframework.workflow.web.IWorkflowWebContext;
import net.simpleframework.workflow.web.page.AbstractWorkTPage;
import net.simpleframework.workflow.web.page.MyWorklistTPage;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
@PageMapping(url = "/workflow/form")
public class WorkflowFormPage extends T1FormTemplatePage implements IWorkflowContextAware {

	@Override
	protected void onForward(final PageParameter pp) {
		super.onForward(pp);
		pp.addImportCSS(AbstractWorkTPage.class, "/my_work.css");
	}

	@Override
	protected String toHtml(final PageParameter pp, final Map<String, Object> variables,
			final String currentVariable) throws IOException {
		final StringBuilder sb = new StringBuilder();
		sb.append("<div class='WorkflowFormPage'>");
		final IWorkflowForm workflowForm = getWorkflowForm(pp);
		if (workflowForm != null) {
			sb.append(pp.includeUrl(workflowForm.getFormForward()));
		}
		sb.append("</div>");
		return sb.toString();
	}

	protected IWorkflowForm getWorkflowForm(final PageParameter pp) {
		final WorkitemBean workitem = AbstractWorkflowFormPage.getWorkitemBean(pp);
		return (IWorkflowForm) context.getActivityService().getWorkflowForm(
				context.getWorkitemService().getActivity(workitem));
	}

	@Override
	public ElementList getLeftElements(final PageParameter pp) {
		final LinkButton backBtn = backBtn();
		final String url = pp.getParameter("url");
		if (StringUtils.hasText(url)) {
			backBtn.setHref(url);
		} else {
			final StringBuilder sb = new StringBuilder();
			final WorkitemBean workitem = AbstractWorkflowFormPage.getWorkitemBean(pp);
			final EWorkitemStatus status = workitem.getStatus();
			if (status != EWorkitemStatus.running) {
				sb.append("status=").append(status.name());
			}
			backBtn.setOnclick("$Actions.loc('"
					+ ((IWorkflowWebContext) context).getUrlsFactory().getMyWorkUrl(
							MyWorklistTPage.class, sb.toString()) + "');");
		}
		final ElementList el = ElementList.of(backBtn);
		return el;
	}

	@Override
	public String getRole(final PageParameter pp) {
		return IPermissionConst.ROLE_ALL_ACCOUNT;
	}
}
