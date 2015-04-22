package net.simpleframework.workflow.web.page.t1;

import static net.simpleframework.common.I18n.$m;

import java.io.IOException;
import java.util.Map;

import net.simpleframework.mvc.PageMapping;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.SpanElement;
import net.simpleframework.mvc.common.element.TabButton;
import net.simpleframework.mvc.common.element.TabButtons;
import net.simpleframework.workflow.engine.bean.WorkitemBean;
import net.simpleframework.workflow.web.IWorkflowWebContext;
import net.simpleframework.workflow.web.IWorkflowWebForm;
import net.simpleframework.workflow.web.WorkflowUrlsFactory;
import net.simpleframework.workflow.web.WorkflowUtils;

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
			sb.append(pp.includeUrl(workflowForm.getForwardUrl(pp)));
		} else {
			sb.append(SpanElement.warnText($m("WorkflowFormPage.2")));
		}
		sb.append("</div>");
		return sb.toString();
	}

	@Override
	public TabButtons getTabButtons(final PageParameter pp) {
		final WorkitemBean workitem = WorkflowUtils.getWorkitemBean(pp);
		final WorkflowUrlsFactory uFactory = ((IWorkflowWebContext) workflowContext).getUrlsFactory();
		return TabButtons.of(
				new TabButton($m("WorkflowFormPage.0")).setHref(uFactory.getUrl(pp,
						WorkflowFormPage.class, workitem)),
				new TabButton($m("WorkflowFormPage.1")).setHref(
						uFactory.getUrl(pp, WorkflowMonitorPage.class, workitem)).setTabIndex(1));
	}
}
