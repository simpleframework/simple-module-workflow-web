package net.simpleframework.workflow.web.page.t1.form;

import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.LinkButton;
import net.simpleframework.workflow.engine.bean.WorkviewBean;
import net.simpleframework.workflow.web.IWorkflowWebView;
import net.simpleframework.workflow.web.WorkflowUtils;
import net.simpleframework.workflow.web.page.MyWorkviewsTPage;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class AbstractWorkflowViewPage extends AbstractFormTemplatePage {

	protected IWorkflowWebView getWorkflowView(final PageParameter pp) {
		final WorkviewBean workview = WorkflowUtils.getWorkviewBean(pp);
		return (IWorkflowWebView) wfpService.getWorkflowView(wfpService.getBean(workview
				.getProcessId()));
	}

	@Override
	public LinkButton getBackBtn(final PageParameter pp) {
		return super.getBackBtn(pp).setHref(uFactory.getUrl(pp, MyWorkviewsTPage.class));
	}

	@Override
	protected void onForward(final PageParameter pp) throws Exception {
		super.onForward(pp);

		pp.addImportCSS(AbstractWorkflowViewPage.class, "/form.css");
	}
}
