package net.simpleframework.workflow.web.page.org2;

import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.TabButton;
import net.simpleframework.mvc.common.element.TabButtons;
import net.simpleframework.mvc.template.lets.Tabs_BlankPage;
import net.simpleframework.workflow.engine.IWorkflowContextAware;
import net.simpleframework.workflow.engine.impl.WorkflowContext;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class AbstractMgrTPage extends Tabs_BlankPage implements IWorkflowContextAware {

	@Override
	public String getRole(final PageParameter pp) {
		return WorkflowContext.ROLE_WORKFLOW_MANAGER;
	}

	@Override
	public TabButtons getTabButtons(final PageParameter pp) {
		return TabButtons.of(new TabButton("流程实例"));
	}
}