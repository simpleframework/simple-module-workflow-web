package net.simpleframework.workflow.web.page.t1.form;

import static net.simpleframework.common.I18n.$m;

import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.TabButton;
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

	protected IWorkflowWebForm getWorkflowForm(final PageParameter pp) {
		final WorkitemBean workitem = WorkflowUtils.getWorkitemBean(pp);
		return (IWorkflowWebForm) wfaService.getWorkflowForm(wfwService.getActivity(workitem));
	}

	protected TabButton createFormTab(final PageParameter pp, final WorkitemBean workitem) {
		return new TabButton($m("WorkflowFormPage.0"))
				.setHref(uFactory.getUrl(pp, WorkflowFormPage.class, workitem));
	}

	protected TabButton createMonitorTab(final PageParameter pp, final WorkitemBean workitem) {
		return new TabButton($m("WorkflowFormPage.1")).blank()
				.setHref(uFactory.getUrl(pp, WorkflowMonitorPage.class, workitem));
	}
}
