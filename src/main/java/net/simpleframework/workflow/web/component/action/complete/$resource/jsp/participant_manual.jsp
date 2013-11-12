<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.Collection"%>
<%@ page import="net.simpleframework.mvc.component.ComponentParameter"%>
<%@ page import="net.simpleframework.workflow.engine.WorkitemComplete"%>
<%@ page import="net.simpleframework.workflow.engine.participant.Participant"%>
<%@ page import="net.simpleframework.workflow.engine.WorkitemBean"%>
<%@ page import="net.simpleframework.mvc.ctx.permission.IPagePermissionHandler"%>
<%@ page import="net.simpleframework.workflow.engine.IWorkflowContextAware"%>
<%@ page import="net.simpleframework.workflow.schema.TransitionNode"%>
<%@ page import="net.simpleframework.workflow.web.component.action.complete.WorkitemCompleteUtils"%>
<%
	final ComponentParameter nCP = WorkitemCompleteUtils
			.get(request, response);
	final WorkitemBean workitem = IWorkflowContextAware.context.getWorkitemService()
			.getBean(request.getParameter(WorkitemBean.workitemId));
	final IPagePermissionHandler service = (IPagePermissionHandler) IWorkflowContextAware.context
			.getParticipantService();
%>
<div class="simple_window_tcb participant_manual">
  <%
  	for (final TransitionNode transition : WorkitemCompleteUtils.getTransitions(
  			nCP, workitem)) {
  %>
  <div class="node" transition="<%=transition.getId()%>"><%=transition.to()%></div>
  <div class="participants">
    <%
    	final Collection<Participant> coll = WorkitemComplete.get(workitem)
    				.getActivityComplete().getParticipants(transition);
    		if (coll == null || coll.size() == 0) {
    			out.write("#(participant_manual.1)");
    		} else {
    			for (Participant participant : coll) {
    %>
    <div class="participant" onclick="var c = $(this).down('input'); c.checked = !c.checked;">
      <img class="icon" src="<%=service.getPhotoUrl(nCP, participant.userId,
								128, 128)%>">
      <div class="txt"><%=service.getUser(participant.userId)%></div>
      <input type="checkbox" value="<%=participant.getId()%>" />
    </div>
    <%
    	}
    		}
    %>
    <div class="msg"></div>
  </div>
  <%
  	}
  	final String params = WorkitemBean.workitemId + "=" + workitem.getId() + "&"
  			+ WorkitemCompleteUtils.BEAN_ID + "=" + nCP.hashId();
  %>
  <div class="b">
    <input type="button" class="button2" value="#(Button.Ok)"
      onclick="WF_WORKITEM_COMPLETE_ACTION.participantSave(this, '<%=params%>');" /> <input
      type="button" value="#(Button.Cancel)" onclick="$win(this).close();" />
  </div>
</div>
<script type="text/javascript">
  WF_WORKITEM_COMPLETE_ACTION.PARTICIPANT_MESSAGE = "#(participant_manual.0)";
  $UI.hackCheckbox($(".participant_manual"));
</script>