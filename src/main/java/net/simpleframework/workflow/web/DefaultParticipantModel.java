package net.simpleframework.workflow.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import net.simpleframework.common.ID;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.organization.IRole;
import net.simpleframework.organization.IRoleService;
import net.simpleframework.organization.web.OrganizationPermissionHandler;
import net.simpleframework.workflow.engine.EDelegationSource;
import net.simpleframework.workflow.engine.ProcessModelBean;
import net.simpleframework.workflow.engine.participant.IParticipantModel;
import net.simpleframework.workflow.engine.participant.Participant;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public class DefaultParticipantModel extends OrganizationPermissionHandler implements
		IParticipantModel {

	@Override
	public Collection<Participant> getRelativeParticipants(final Object user, final Object role,
			final String relative, final KVMap variables) {
		final ArrayList<Participant> participants = new ArrayList<Participant>();
		IRole oRole = getRoleObject(role);
		if (oRole != null) {
			// 获取相对角色，部门
			final IRoleService service = context.getRoleService();
			oRole = service.getRoleByName(service.getRoleChart(oRole), relative);
			if (oRole != null) {
				final ID roleId = oRole.getId();
				for (final ID userId : users(roleId, variables)) {
					participants.add(new Participant(userId, roleId));
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
