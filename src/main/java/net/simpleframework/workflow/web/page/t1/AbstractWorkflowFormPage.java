package net.simpleframework.workflow.web.page.t1;

import net.simpleframework.mvc.PageParameter;
import net.simpleframework.workflow.engine.bean.WorkitemBean;
import net.simpleframework.workflow.web.IWorkflowWebForm;
import net.simpleframework.workflow.web.WorkflowUtils;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public abstract class AbstractWorkflowFormPage extends AbstractFormTemplatePage {

	@Override
	protected boolean isPage404(final PageParameter pp) {
		return WorkflowUtils.getWorkitemBean(pp) == null;
	}

	@Override
	protected void onForward(final PageParameter pp) throws Exception {
		super.onForward(pp);

		pp.addImportCSS(AbstractWorkflowFormPage.class, "/form.css");
	}

	protected IWorkflowWebForm getWorkflowForm(final PageParameter pp) {
		final WorkitemBean workitem = WorkflowUtils.getWorkitemBean(pp);
		return (IWorkflowWebForm) wfaService.getWorkflowForm(wfwService.getActivity(workitem));
	}
}
