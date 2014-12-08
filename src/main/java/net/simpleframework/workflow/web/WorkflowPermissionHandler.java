package net.simpleframework.workflow.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import net.simpleframework.common.ID;
import net.simpleframework.organization.IRoleService;
import net.simpleframework.organization.Role;
import net.simpleframework.organization.web.OrganizationPermissionHandler;
import net.simpleframework.workflow.engine.ActivityComplete;
import net.simpleframework.workflow.engine.EDelegationSource;
import net.simpleframework.workflow.engine.ProcessModelBean;
import net.simpleframework.workflow.engine.WorkitemBean;
import net.simpleframework.workflow.engine.participant.IWorkflowPermissionHandler;
import net.simpleframework.workflow.engine.participant.Participant;
import net.simpleframework.workflow.schema.UserNode;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class WorkflowPermissionHandler extends OrganizationPermissionHandler implements
		IWorkflowPermissionHandler {

	@Override
	public Collection<Participant> getRelativeParticipants(final Object user, final Object role,
			final UserNode.RelativeRole rRole, final Map<String, Object> variables) {
		final ArrayList<Participant> participants = new ArrayList<Participant>();
		Role oRole = getRoleObject(role);
		if (oRole != null) {
			// 获取相对角色，部门
			final IRoleService service = orgContext.getRoleService();
			oRole = service.getRoleByName(service.getRoleChart(oRole), rRole.getRelative());
			if (oRole != null) {
				ID deptId = null;
				if (rRole.isIndept()) {
					final WorkitemBean workitem = ((ActivityComplete) variables.get("activityComplete"))
							.getWorkitem();
					if (workitem != null) {
						deptId = workitem.getDeptId();
					}
				}
				final ID roleId = oRole.getId();
				final Iterator<ID> users = users(roleId, deptId, variables);
				while (users.hasNext()) {
					participants.add(new Participant(users.next(), roleId));
				}
			}
		}
		return participants;
	}

	@Override
	public Iterator<ID> getUsersOfDelegation(final ProcessModelBean processModel,
			final EDelegationSource source, final Map<String, String> filterMap) {
		return null;
	}
}
