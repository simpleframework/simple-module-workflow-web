package net.simpleframework.workflow.web.participant;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.simpleframework.common.StringUtils;
import net.simpleframework.ctx.script.IScriptEval;
import net.simpleframework.workflow.engine.ActivityBean;
import net.simpleframework.workflow.engine.ActivityComplete;
import net.simpleframework.workflow.engine.EWorkitemStatus;
import net.simpleframework.workflow.engine.WorkitemBean;
import net.simpleframework.workflow.engine.participant.IParticipantHandler.AbstractParticipantHandler;
import net.simpleframework.workflow.engine.participant.Participant;
import net.simpleframework.workflow.schema.UserNode;
import net.simpleframework.workflow.web.WorkflowPermissionHandler;

public class PRelativeRoleHandler extends AbstractParticipantHandler {

	//指定前一任务节点node: (默认为前一节点)
	private String PARAMS_KEY_NODE="node";
	//相对角色名role:	   (默认为实际执行者)
	private String PARAMS_KEY_ROLE="role";
	//相对角色的级别level:下级lower,上级higher (默认是本部门)
	private String PARAMS_KEY_level="level";
	public enum Level{
		internal{//本部门
			
		},
		Level{//平级
			
		},
		lower{//下级
			
		},
		higher{//上级
			
		}
	}
	
	@Override
	public Collection<Participant> getParticipants(IScriptEval script,
			Map<String, Object> variables) {
		// TODO Auto-generated method stub
		final ArrayList<Participant> participants = new ArrayList<Participant>();
		//UserNode node = ((UserNode) ((TransitionNode) variables.get("transition")).to());
		final ActivityComplete activityComplete = (ActivityComplete) variables
				.get("activityComplete");
		ActivityBean preActivity = activityComplete.getActivity();//前一任务步骤实例
		final UserNode.RuleRole rRole = (UserNode.RuleRole) getParticipantType(variables);
		Map<String, String> params = getParams(rRole.getParams());
		String node = params.get(PARAMS_KEY_NODE);
		String role = params.get(PARAMS_KEY_ROLE);
		if(StringUtils.hasText(node)){
			//获取前一指定任务步骤实例
			preActivity=getPreActivityBean(preActivity,node);
			if(null==preActivity) return null;
		}
		
		if(StringUtils.hasText(role)){
			//指定角色
			WorkitemBean workitem = null;
			if(preActivity.getId().equals(activityComplete.getActivity().getId())){
				//如果前一指定节点就是上一节点
				workitem = activityComplete.getWorkitem();
			}else{
				List<WorkitemBean> items = wService.getWorkitems(preActivity,EWorkitemStatus.complete);
				if(null!=items&&items.size()>0)
					workitem=items.get(0);
				if(workitem==null) return null;
			}
			
			Level level=Level.internal;
			String levelStr = params.get(PARAMS_KEY_level);
			if(StringUtils.hasText(levelStr))
				level=Level.valueOf(levelStr);
			
			WorkflowPermissionHandler wph = (WorkflowPermissionHandler)permission;
			Collection<Participant> _participants = wph
					.getRelativeParticipantsOfLevel(workitem.getUserId(),
							workitem.getRoleId(), workitem.getDeptId(),
							variables, role, level);
			if(level.equals(Level.internal)){

			}else if(level.equals(Level.Level)){
				
			}else if(level.equals(Level.lower)){
				
			}else if(level.equals(Level.higher)){
				
			}
			if (_participants != null) {
				participants.addAll(_participants);
			}
		}else{
			//添加实际执行者
			
			if(preActivity.getId().equals(activityComplete.getActivity().getId())){
				//如果前一指定节点就是上一节点则需要将当前完成的用户加进执行者
				WorkitemBean workitem = activityComplete.getWorkitem();
				participants.add(new Participant(workitem.getUserId(), workitem.getRoleId()));
			}
			//已完成任务项
			for (final WorkitemBean workitem2 : wService.getWorkitems(preActivity,
					EWorkitemStatus.complete)) {
				participants.add(new Participant(workitem2.getUserId(), workitem2.getRoleId()));
			}
		}
		
		
		return participants;
	}
	
	/**
	 * 获取前一指定节点实例
	 * @param ab
	 * @param preNodeName
	 * @return
	 */
	public ActivityBean getPreActivityBean(ActivityBean ab,String preNodeName){
		if(null==ab) return null;
		if(ab.getTasknodeText().equals(preNodeName)) return ab;
		return getPreActivityBean(aService.getBean(ab.getPreviousId()),preNodeName);
	}
	
	private Map<String, String> getParams(String pas){
		Map<String, String> params = new HashMap<String, String>();
		if(StringUtils.hasText(pas)){
			String[] ps = pas.split(";");
			if(null!=ps)
				for(String _p:ps){
					if(StringUtils.hasText(_p)){
						String[] p = _p.split("=");
						if(p.length==2){
							params.put(p[0], p[1]);
						}
					}
				}
		}
		return params;
	}

	
}
