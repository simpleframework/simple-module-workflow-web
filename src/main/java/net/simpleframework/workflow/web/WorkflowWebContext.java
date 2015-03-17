package net.simpleframework.workflow.web;

import static net.simpleframework.common.I18n.$m;
import net.simpleframework.ctx.IApplicationContext;
import net.simpleframework.ctx.IModuleRef;
import net.simpleframework.ctx.ModuleFunctions;
import net.simpleframework.ctx.permission.PermissionConst;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.ctx.WebModuleFunction;
import net.simpleframework.organization.ERoleType;
import net.simpleframework.organization.RolenameW;
import net.simpleframework.workflow.engine.impl.WorkflowContext;
import net.simpleframework.workflow.engine.participant.IWorkflowPermissionHandler;
import net.simpleframework.workflow.web.page.t1.ProcessModelMgrPage;
import net.simpleframework.workflow.web.page.t2.AbstractWorkPage.MyRunningWorklistPage;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class WorkflowWebContext extends WorkflowContext implements IWorkflowWebContext {

	@Override
	public void onInit(final IApplicationContext application) throws Exception {
		super.onInit(application);

		ROLE_WORKFLOW_MANAGER = RolenameW.toUniqueRolename(RolenameW.ROLECHART_ORG_DEFAULT,
				"workflowmgr");
		RolenameW.registRole("workflowmgr", $m("WorkflowWebContext.2"), null, ERoleType.normal);
	}

	@Override
	public String getDepartmentMgrRole(final PageParameter pp) {
		return null;
	}

	@Override
	public WorkflowUrlsFactory getUrlsFactory() {
		return singleton(WorkflowUrlsFactory.class);
	}

	@Override
	public IWorkflowPermissionHandler getParticipantService() {
		final IWorkflowPermissionHandler handler = super.getParticipantService();
		return handler == null ? singleton(WorkflowPermissionHandler.class) : handler;
	}

	@Override
	public IModuleRef getLogRef() {
		return getRef("net.simpleframework.workflow.web.WorkflowLogRef");
	}

	@Override
	protected ModuleFunctions getFunctions() {
		return ModuleFunctions.of(
				new WebModuleFunction(this, MyRunningWorklistPage.class).setName(
						MODULE_NAME + "-MyRunningWorklistTPage").setText($m("WorkflowWebContext.0")),
				new WebModuleFunction(this, ProcessModelMgrPage.class)
						.setManagerRole(PermissionConst.ROLE_MANAGER)
						.setName(MODULE_NAME + "-ProcessModelMgrPage")
						.setText($m("WorkflowWebContext.1")));
	}
}
