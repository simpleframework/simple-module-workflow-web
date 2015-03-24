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
import net.simpleframework.organization.IRoleService;
import net.simpleframework.organization.Role;
import net.simpleframework.organization.User;
import net.simpleframework.organization.web.OrganizationPermissionHandler;
import net.simpleframework.workflow.engine.AbstractWorkflowBean;
import net.simpleframework.workflow.engine.EDelegationSource;
import net.simpleframework.workflow.engine.ProcessModelBean;
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
			final IRoleService service = orgContext.getRoleService();
			final Role rr = service.getRoleByName(service.getRoleChart(r), rRole.getRelative());
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
			final String relativeRole, final Level level) {
		final ArrayList<Participant> participants = new ArrayList<Participant>();
		Role oRole = getRoleObject(role, variables);
		if (oRole != null) {
			if (StringUtils.hasText(relativeRole)) {
				// 获取相对角色，部门
				final IRoleService service = orgContext.getRoleService();
				oRole = service.getRoleByName(service.getRoleChart(oRole), relativeRole);
				if (oRole != null) {
					final ID roleId = oRole.getId();
					if (level.equals(Level.internal)) {// 本部门
						final Iterator<ID> users = users(roleId, deptId, variables);
						while (users.hasNext()) {
							participants.add(new Participant(users.next(), roleId, deptId));
						}
					} else {
						Department dept = orgContext.getDepartmentService().getBean(deptId);
						if (level.equals(Level.Level)) {// 平级
							dept = orgContext.getDepartmentService().getBean(dept.getParentId());
							final IDataQuery<Department> depts = orgContext.getDepartmentService()
									.queryChildren(dept);
							if (null != depts) {
								while ((dept = depts.next()) != null) {
									final Iterator<ID> users = users(roleId, dept.getId(), variables);
									while (users.hasNext()) {
										participants.add(new Participant(users.next(), roleId, dept.getId()));
									}
								}
							}
						} else if (level.equals(Level.lower)) {// 下级
							final IDataQuery<Department> depts = orgContext.getDepartmentService()
									.queryChildren(dept);
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
				Department dept = orgContext.getDepartmentService().getBean(deptId);
				if (level.equals(Level.internal)) {// 本部门
					participants.addAll(queryDept(dept));
				} else {
					if (level.equals(Level.Level)) {// 平级
						dept = orgContext.getDepartmentService().getBean(dept.getParentId());
						final IDataQuery<Department> depts = orgContext.getDepartmentService()
								.queryChildren(dept);
						if (null != depts) {
							while ((dept = depts.next()) != null) {
								participants.addAll(queryDept(dept));
							}
						}
					} else if (level.equals(Level.lower)) {// 下级
						final IDataQuery<Department> depts = orgContext.getDepartmentService()
								.queryChildren(dept);
						if (null != depts) {
							while ((dept = depts.next()) != null) {
								participants.addAll(queryDept(dept));
							}
						}
					} else if (level.equals(Level.higher)) {// 上级
						dept = orgContext.getDepartmentService().getBean(dept.getParentId());
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
		final IDataQuery<User> users = orgContext.getUserService().queryUsers(dept);
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