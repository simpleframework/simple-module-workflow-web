<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="net.simpleframework.mvc.component.ComponentParameter"%>
<%@ page import="net.simpleframework.workflow.engine.IWorkflowContextAware"%>
<%@ page import="net.simpleframework.workflow.engine.WorkitemBean"%>
<%@ page import="net.simpleframework.workflow.web.component.action.complete.WorkitemCompleteUtils"%>
<%@ page import="net.simpleframework.workflow.schema.TransitionNode"%>
<%
	final ComponentParameter nCP = WorkitemCompleteUtils.get(request,
			response);
	final WorkitemBean workitem = IWorkflowContextAware.context
			.getWorkitemService().getBean(
					request.getParameter(WorkitemBean.workitemId));
%>
<div class="simple_window_tcb transition_manual">
  <div class="t">#(transition_manual.0)</div>
  <div class="c">
    <%
    	for (TransitionNode transition : WorkitemCompleteUtils.getTransitions(nCP,
    			workitem)) {
    %>
    <div class="node" onclick="var c = $(this).down('input'); c.checked = !c.checked;">
      <img class="icon" />
      <div class="txt"><%=transition.to()%></div>
      <input type="checkbox" value="<%=transition.getId()%>" />
    </div>
    <%
    	}
    	final String params = WorkitemBean.workitemId + "=" + workitem.getId() + "&"
    			+ WorkitemCompleteUtils.BEAN_ID + "=" + nCP.hashId();
    %>
    <div class="msg"></div>
  </div>
  <div class="b">
    <input type="button" class="button2" value="#(Button.Ok)"
      onclick="WF_WORKITEM_COMPLETE_ACTION.transitionSave(this, '<%=params%>');" /> <input
      type="button" value="#(Button.Cancel)" onclick="$win(this).close();" />
  </div>
</div>
<script type="text/javascript">
	WF_WORKITEM_COMPLETE_ACTION.TRANSITION_MESSAGE = "#(transition_manual.1)";
	$UI.hackCheckbox($(".transition_manual"));
</script>
