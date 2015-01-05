package net.simpleframework.workflow.web.participant;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.simpleframework.common.ID;
import net.simpleframework.common.StringUtils;
import net.simpleframework.ctx.script.IScriptEval;
import net.simpleframework.workflow.engine.ActivityBean;
import net.simpleframework.workflow.engine.ActivityComplete;
import net.simpleframework.workflow.engine.EWorkitemStatus;
import net.simpleframework.workflow.engine.WorkitemBean;
import net.simpleframework.workflow.engine.participant.IParticipantHandler.AbstractParticipantHandler;
import net.simpleframework.workflow.engine.participant.Participant;
import net.simpleframework.workflow.schema.AbstractTaskNode;
import net.simpleframework.workflow.schema.UserNode;
import net.simpleframework.workflow.web.WorkflowPermissionHandler;

public class PRelativeRoleHandler extends AbstractParticipantHandler {

	// 指定前一任务节点node: (默认为前一节点)
	private final String PARAMS_KEY_NODE = "node";
	// 相对角色名role: (默认为部门所有人员)
	private final String PARAMS_KEY_ROLE = "role";
	// 相对角色的级别level:下级lower,上级higher (默认是本部门)
	private final String PARAMS_KEY_level = "level";

	public enum Level {
		internal {// 本部门

		},
		Level {// 平级

		},
		lower {// 下级

		},
		higher {// 上级

		}
	}

	@Override
	public Collection<Participant> getParticipants(final IScriptEval script,
			final ActivityComplete activityComplete, final Map<String, Object> variables) {
		// TODO Auto-generated method stub
		final ArrayList<Participant> participants = new ArrayList<Participant>();
		// UserNode node = ((UserNode) ((TransitionNode)
		// variables.get("transition")).to());
		ActivityBean preActivity = activityComplete.getActivity();// 前一任务步骤实例
		final UserNode.RuleRole rRole = (UserNode.RuleRole) getParticipantType(variables);
		final Map<String, String> params = getParams(rRole.getParams());
		final String node = params.get(PARAMS_KEY_NODE);
		final String role = params.get(PARAMS_KEY_ROLE);
		if (StringUtils.hasText(node)) {
			// 获取前一指定任务步骤实例
			preActivity = aService.getPreActivity(preActivity, node);
			if (null == preActivity) {
				return null;
			}
		}

		ID userId = null;
		ID roleId = null;
		ID deptId = null;
		WorkitemBean workitem = null;
		if (preActivity.getId().equals(activityComplete.getActivity().getId())) {
			// 如果前一指定节点就是上一节点
			workitem = activityComplete.getWorkitem();
			if (null == workitem) {
				final AbstractTaskNode tasknode = aService.getTaskNode(activityComplete.getActivity());
				if (tasknode instanceof UserNode && ((UserNode) tasknode).isEmpty()) {
					// 处理空节点的执行者
					final List<Participant> mps = aService.getEmptyParticipants(activityComplete
							.getActivity());
					if (null != mps && mps.size() > 0) {
						final Participant mp = mps.get(0);
						userId = mp.userId;
						roleId = mp.roleId;
						deptId = mp.deptId;
					}
				}
			}
		} else {
			final List<WorkitemBean> items = wService.getWorkitems(preActivity,
					EWorkitemStatus.complete);
			if (null != items && items.size() > 0) {
				workitem = items.get(0);
			}
		}
		if (workitem != null) {
			userId = workitem.getUserId();
			roleId = workitem.getRoleId();
			deptId = workitem.getDeptId();
		}
		if (null == userId || null == roleId || null == deptId) {
			return null;
		}

		Level level = Level.internal;
		final String levelStr = params.get(PARAMS_KEY_level);
		if (StringUtils.hasText(levelStr)) {
			level = Level.valueOf(levelStr);
		}

		final WorkflowPermissionHandler wph = (WorkflowPermissionHandler) permission;
		final Collection<Participant> _participants = wph.getRelativeParticipantsOfLevel(userId,
				roleId, deptId, variables, role, level);
		if (_participants != null) {
			participants.addAll(_participants);
		}

		return participants;
	}

	/**
	 * 获取前一指定节点实例
	 * 
	 * @param ab
	 * @param preNodeName
	 * @return
	 */
	// public ActivityBean getPreActivityBean(final ActivityBean ab,
	// final String preNodeName) {
	// if (null == ab) {
	// return null;
	// }
	// if (ab.getTasknodeText().equals(preNodeName)) {
	// return ab;
	// }
	// return getPreActivityBean(aService.getBean(ab.getPreviousId()),
	// preNodeName);
	// }

	private Map<String, String> getParams(final String pas) {
		final Map<String, String> params = new HashMap<String, String>();
		if (StringUtils.hasText(pas)) {
			final String[] ps = pas.split(";");
			if (null != ps) {
				for (final String _p : ps) {
					if (StringUtils.hasText(_p)) {
						final String[] p = _p.split("=");
						if (p.length == 2) {
							params.put(p[0], p[1]);
						}
					}
				}
			}
		}
		return params;
	}

}
