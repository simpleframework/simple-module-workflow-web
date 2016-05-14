package net.simpleframework.workflow.web.page.t1.form;

import net.simpleframework.mvc.PageParameter;
import net.simpleframework.workflow.engine.bean.WorkviewBean;
import net.simpleframework.workflow.web.IWorkflowWebView;
import net.simpleframework.workflow.web.WorkflowUtils;
import net.simpleframework.workflow.web.page.list.workviews.MyWorkviewsTPage;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class AbstractWorkflowViewPage extends AbstractFormTemplatePage {

	@Override
	protected void onForward(final PageParameter pp) throws Exception {
		super.onForward(pp);

		pp.addImportCSS(AbstractWorkflowViewPage.class, "/form.css");
	}

	@Override
	protected boolean isPage404(final PageParameter pp) {
		return WorkflowUtils.getWorkviewBean(pp) == null;
	}

	protected IWorkflowWebView getWorkflowView(final PageParameter pp) {
		final WorkviewBean workview = WorkflowUtils.getWorkviewBean(pp);
		return (IWorkflowWebView) wfpService.getWorkflowView(wfpService.getBean(workview
				.getProcessId()));
	}

	@Override
	protected String getDefaultBackUrl(final PageParameter pp) {
		return uFactory.getUrl(pp, MyWorkviewsTPage.class);
	}
}
