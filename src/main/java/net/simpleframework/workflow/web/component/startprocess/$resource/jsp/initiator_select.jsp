<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="net.simpleframework.workflow.engine.InitiateItem"%>
<%@ page import="net.simpleframework.workflow.engine.participant.IParticipantModel"%>
<%@ page import="net.simpleframework.workflow.engine.IWorkflowContextAware"%>
<%@ page import="net.simpleframework.mvc.component.ComponentParameter"%>
<%@ page import="net.simpleframework.workflow.web.component.startprocess.StartProcessUtils"%>
<%@ page import="net.simpleframework.common.StringUtils"%>
<%@ page import="net.simpleframework.common.ID"%>
<%@ page import="java.util.Enumeration"%>
<%
	final ComponentParameter cParameter = StartProcessUtils.get(
			request, response);
	final InitiateItem initiateItem = StartProcessUtils
			.getInitiateItem(cParameter);
	final IParticipantModel service = IWorkflowContextAware.context
			.getParticipantService();
%>
<div class="initiator_select">
  <div class="rtitle">#(initiator_select.0)</div>
  <%
  	Enumeration<ID> roles = initiateItem.roles();
  	ID id;
  	while (roles.hasMoreElements()) {
  		id = roles.nextElement();
  %>
  <div class="ritem">
    <a onclick="doStartProcessByInitiator('initiator=<%=id%>');"><%=service.getRole(id)%></a>
  </div>
  <%
  	}
  %>
  <div class="rbottom">
    <input type="button" value="#(Button.Cancel)" onclick="$win(this).close();" />
  </div>
</div>
<script type="text/javascript">
	function doStartProcessByInitiator(params) {
<%final String confirmMessage = (String) cParameter.getBeanProperty("confirmMessage");
			if (StringUtils.hasText(confirmMessage)) {
				out.write("if (!confirm('");
				out.write(confirmMessage);
				out.write("')) { return; }");
			}%>
	$Actions["ajaxStartProcessByInitiator"](params);
	}
</script>

