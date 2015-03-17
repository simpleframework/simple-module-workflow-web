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
public class AbstractWorkflowMgrTPage extends Tabs_BlankPage implements IWorkflowContextAware {

	@Override
	public String getRole(final PageParameter pp) {
		return WorkflowContext.ROLE_WORKFLOW_MANAGER;
	}

	@Override
	public TabButtons getTabButtons(final PageParameter pp) {
		return TabButtons.of(new TabButton("流程实例"));
	}

	// static Department getOrg(final PageParameter pp) {
	// return pp.getCache("@org", new IVal<Department>() {
	// @Override
	// public Department get() {
	// final IDepartmentService dService = orgContext.getDepartmentService();
	// Department org = null;
	// if (pp.getLogin().isManager()) {
	// org = dService.getBean(pp.getParameter("orgId"));
	// }
	// if (org == null) {
	// org = dService.getBean(pp.getLogin().getDept().getDomainId());
	// }
	// return org;
	// }
	// });
	// }
}