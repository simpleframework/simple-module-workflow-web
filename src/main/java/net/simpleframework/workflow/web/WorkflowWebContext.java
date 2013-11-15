package net.simpleframework.workflow.web;

import static net.simpleframework.common.I18n.$m;
import net.simpleframework.ctx.ModuleFunctions;
import net.simpleframework.mvc.ctx.WebModuleFunction;
import net.simpleframework.workflow.engine.impl.WorkflowContext;
import net.simpleframework.workflow.web.page.MyWorklistPage;
import net.simpleframework.workflow.web.page.ProcessModelPage;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class WorkflowWebContext extends WorkflowContext {

	@Override
	public ModuleFunctions getFunctions() {
		return ModuleFunctions.of(
				new WebModuleFunction(MyWorklistPage.class).setName(MODULE_NAME + "-MyWorklistPage")
						.setText($m("WorkflowWebContext.0"))).append(
				new WebModuleFunction(ProcessModelPage.class)
						.setName(MODULE_NAME + "-ProcessModelPage").setText($m("WorkflowWebContext.1")));
	}
}
