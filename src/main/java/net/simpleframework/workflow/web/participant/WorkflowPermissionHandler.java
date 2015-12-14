package net.simpleframework.workflow.web.participant;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.common.BeanUtils;
import net.simpleframework.common.ID;
import net.simpleframework.common.StringUtils;
import net.simpleframework.organization.Department;
import net.simpleframework.organization.Role;
import net.simpleframework.organization.User;
import net.simpleframework.organization.role.RolenameW;
import net.simpleframework.organization.web.OrganizationPermissionHandler;
import net.simpleframework.workflow.engine.EDelegationSource;
import net.simpleframework.workflow.engine.bean.AbstractWorkflowBean;
import net.simpleframework.workflow.engine.bean.ProcessModelBean;
import net.simpleframework.workflow.engine.participant.IWorkflowPermissionHandler;
import net.simpleframework.workflow.engine.participant.Participant;
import net.simpleframework.workflow.schema.UserNode;
import net.simpleframework.workflow.web.participant.PRelativeRoleHandler.Level;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class WorkflowPermissionHandler extends OrganizationPermissionHandler implements
		IWorkflowPermissionHandler {

	@Override
	public Collection<Participant> getRelativeParticipants(final AbstractWorkflowBean workflowBean,
			final UserNode.RelativeRole rRole, final Map<String, Object> variables) {
		final ArrayList<Participant> participants = new ArrayList<Participant>();
		final Role r = getRoleObject(BeanUtils.getProperty(workflowBean, "roleId"), variables);
		if (r != null) {
			// 获取相对角色，部门
			final Role rr = _roleService.getRoleByName(_roleService.getRoleChart(r),
					rRole.getRelative());
			if (rr != null) {
				final ID deptId = rRole.isIndept() ? (ID) BeanUtils.getProperty(workflowBean, "deptId")
						: null;
				final Iterator<ID> users = users(rr.getId(), deptId, variables);
				while (users.hasNext()) {
					participants.add(new Participant(users.next(), rr.getId(), deptId));
				}
			}
		}
		return participants;
	}

	public Collection<Participant> getRelativeParticipantsOfLevel(final Object user,
			final Object role, final ID deptId, final Map<String, Object> variables,
			String relativeRole, final Level level) {
		final ArrayList<Participant> participants = new ArrayList<Participant>();
		Role oRole = getRoleObject(role, variables);
		if (oRole != null) {
			if (StringUtils.hasText(relativeRole)) {
				// 获取相对角色，部门
				final String[] arr = RolenameW.split(relativeRole);
				if (arr.length > 1) {
					if (arr.length == 2) {
						relativeRole = _deptService.getBean(oRole.getOrgId()).getName() + ":"
								+ relativeRole;
					}
					oRole = _roleService.getRoleByName(relativeRole);
				} else if (arr.length == 1) {
					oRole = _roleService.getRoleByName(_roleService.getRoleChart(oRole), relativeRole);
				}
				if (oRole != null) {
					final ID roleId = oRole.getId();
					if (level.equals(Level.internal)) {// 本部门
						final Iterator<ID> users = users(roleId, deptId, variables);
						while (users.hasNext()) {
							participants.add(new Participant(users.next(), roleId, deptId));
						}
					} else if (level.equals(Level.all)) {// 指定角色
						final Iterator<ID> users = users(roleId, null, variables);
						while (users.hasNext()) {
							participants.add(new Participant(users.next(), roleId, null));
						}
					} else {
						Department dept = _deptService.getBean(deptId);
						if (level.equals(Level.level)) {// 平级
							dept = _deptService.getBean(dept.getParentId());
							final IDataQuery<Department> depts = _deptService.queryChildren(dept);
							if (null != depts) {
								while ((dept = depts.next()) != null) {
									final Iterator<ID> users = users(roleId, dept.getId(), variables);
									while (users.hasNext()) {
										participants.add(new Participant(users.next(), roleId, dept.getId()));
									}
								}
							}
						} else if (level.equals(Level.lower)) {// 下级
							final IDataQuery<Department> depts = _deptService.queryChildren(dept);
							if (null != depts) {
								while ((dept = depts.next()) != null) {
									final Iterator<ID> users = users(roleId, dept.getId(), variables);
									while (users.hasNext()) {
										participants.add(new Participant(users.next(), roleId, dept.getId()));
									}
								}
							}
						} else if (level.equals(Level.higher)) {// 上级
							final Iterator<ID> users = users(roleId, dept.getParentId(), variables);
							while (users.hasNext()) {
								participants.add(new Participant(users.next(), roleId, dept.getParentId()));
							}
						}
					}
				}
			} else {
				// 部门的所有人员
				Department dept = _deptService.getBean(deptId);
				if (level.equals(Level.internal)) {// 本部门
					participants.addAll(queryDept(dept));
				} else {
					if (level.equals(Level.level)) {// 平级
						dept = _deptService.getBean(dept.getParentId());
						final IDataQuery<Department> depts = _deptService.queryChildren(dept);
						if (null != depts) {
							while ((dept = depts.next()) != null) {
								participants.addAll(queryDept(dept));
							}
						}
					} else if (level.equals(Level.lower)) {// 下级
						final IDataQuery<Department> depts = _deptService.queryChildren(dept);
						if (null != depts) {
							while ((dept = depts.next()) != null) {
								participants.addAll(queryDept(dept));
							}
						}
					} else if (level.equals(Level.higher)) {// 上级
						dept = _deptService.getBean(dept.getParentId());
						participants.addAll(queryDept(dept));
					}
				}
			}
		}
		return participants;
	}

	private ArrayList<Participant> queryDept(final Department dept) {
		final ArrayList<Participant> participants = new ArrayList<Participant>();
		if (dept == null) {
			return participants;
		}
		final IDataQuery<User> users = _userService.queryUsers(dept);
		User user = null;
		if (null != users) {
			while ((user = users.next()) != null) {
				participants.add(new Participant(user.getId()));
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
