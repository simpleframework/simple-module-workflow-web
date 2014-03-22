package net.simpleframework.workflow.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import net.simpleframework.common.ID;
import net.simpleframework.organization.IRoleService;
import net.simpleframework.organization.Role;
import net.simpleframework.organization.web.OrganizationPermissionHandler;
import net.simpleframework.workflow.engine.EDelegationSource;
import net.simpleframework.workflow.engine.ProcessModelBean;
import net.simpleframework.workflow.engine.participant.IWorkflowPermissionHandler;
import net.simpleframework.workflow.engine.participant.Participant;

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
			final String relative, final Map<String, Object> variables) {
		final ArrayList<Participant> participants = new ArrayList<Participant>();
		Role oRole = getRoleObject(role);
		if (oRole != null) {
			// 获取相对角色，部门
			final IRoleService service = context.getRoleService();
			oRole = service.getRoleByName(service.getRoleChart(oRole), relative);
			if (oRole != null) {
				final ID roleId = oRole.getId();
				final Iterator<ID> users = users(roleId, variables);
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
