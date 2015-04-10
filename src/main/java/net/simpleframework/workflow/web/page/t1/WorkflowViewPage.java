package net.simpleframework.workflow.web.page.t1;

import static net.simpleframework.common.I18n.$m;

import java.io.IOException;
import java.util.Map;

import net.simpleframework.mvc.PageMapping;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.SpanElement;
import net.simpleframework.workflow.web.IWorkflowWebView;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
@PageMapping(url = "/workflow/view")
public class WorkflowViewPage extends AbstractWorkflowViewPage {

	@Override
	protected String toHtml(final PageParameter pp, final Map<String, Object> variables,
			final String currentVariable) throws IOException {
		final StringBuilder sb = new StringBuilder();
		sb.append("<div class='WorkflowViewPage'>");
		final IWorkflowWebView workflowView = getWorkflowView(pp);
		if (workflowView != null) {
			sb.append(pp.includeUrl(workflowView.getForwardUrl(pp)));
		} else {
			sb.append(SpanElement.warnText($m("WorkflowViewPage.0")));
		}
		sb.append("</div>");
		return sb.toString();
	}
}
