package net.simpleframework.workflow.web;

import static net.simpleframework.common.I18n.$m;
import net.simpleframework.ctx.IModuleRef;
import net.simpleframework.ctx.ModuleFunctions;
import net.simpleframework.mvc.ctx.WebModuleFunction;
import net.simpleframework.workflow.engine.impl.WorkflowContext;
import net.simpleframework.workflow.web.page.MyWorklistPage;
import net.simpleframework.workflow.web.page.t1.ProcessModelMgrPage;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class WorkflowWebContext extends WorkflowContext implements IWorkflowWebContext {

	@Override
	public IModuleRef getLogRef() {
		return getRef("net.simpleframework.workflow.web.WorkflowLogRef");
	}

	@Override
	public ModuleFunctions getFunctions() {
		return ModuleFunctions.of(FUNC_MY_WORKLIST, FUNC_PROCESS_MODEL);
	}

	public static final WebModuleFunction FUNC_MY_WORKLIST = (WebModuleFunction) new WebModuleFunction(
			MyWorklistPage.class).setName(MODULE_NAME + "-MyWorklistPage").setText(
			$m("WorkflowWebContext.0"));
	public static final WebModuleFunction FUNC_PROCESS_MODEL = (WebModuleFunction) new WebModuleFunction(
			ProcessModelMgrPage.class).setName(MODULE_NAME + "-ProcessModelMgrPage").setText(
			$m("WorkflowWebContext.1"));
}
