package net.simpleframework.workflow.web.participant;

import static net.simpleframework.common.I18n.$m;
import static net.simpleframework.workflow.engine.impl.WorkflowContext.ROLE_WORKFLOW_MANAGER;
import net.simpleframework.ctx.IContextBase;
import net.simpleframework.organization.OrganizationRef;
import net.simpleframework.organization.Role.ERoleType;
import net.simpleframework.organization.role.RolenameW;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class WfOrganizationRef extends OrganizationRef {

	@Override
	public void onInit(final IContextBase context) throws Exception {
		super.onInit(context);

		ROLE_WORKFLOW_MANAGER = RolenameW.toUniqueRolename(RolenameW.ROLECHART_ORG_DEFAULT,
				"workflowmgr");
		RolenameW.registRole("workflowmgr", $m("WorkflowWebContext.2"), null, ERoleType.normal);
	}
}
