package net.simpleframework.workflow.web.page.t1;

import net.simpleframework.mvc.PageParameter;
import net.simpleframework.workflow.engine.WorkviewBean;
import net.simpleframework.workflow.web.IWorkflowWebView;
import net.simpleframework.workflow.web.WorkflowUtils;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class AbstractWorkflowViewPage extends AbstractFormTemplatePage {

	protected IWorkflowWebView getWorkflowView(final PageParameter pp) {
		final WorkviewBean workview = WorkflowUtils.getWorkviewBean(pp);
		return (IWorkflowWebView) pService.getWorkflowView(pService.getBean(workview.getProcessId()));
	}

	@Override
	protected void onForward(final PageParameter pp) {
		super.onForward(pp);

		pp.addImportCSS(AbstractWorkflowViewPage.class, "/form.css");
	}
}
