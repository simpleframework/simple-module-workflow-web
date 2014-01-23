package net.simpleframework.workflow.web.page.t1;

import java.io.IOException;
import java.util.Map;

import net.simpleframework.mvc.PageMapping;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.workflow.web.IWorkflowWebForm;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
@PageMapping(url = "/workflow/form")
public class WorkflowFormPage extends AbstractWorkflowFormPage {

	@Override
	protected String toHtml(final PageParameter pp, final Map<String, Object> variables,
			final String currentVariable) throws IOException {
		final StringBuilder sb = new StringBuilder();
		sb.append("<div class='WorkflowFormPage'>");
		final IWorkflowWebForm workflowForm = getWorkflowForm(pp);
		if (workflowForm != null) {
			sb.append(pp.includeUrl(workflowForm.getFormForward(pp)));
		}
		sb.append("</div>");
		return sb.toString();
	}
}
