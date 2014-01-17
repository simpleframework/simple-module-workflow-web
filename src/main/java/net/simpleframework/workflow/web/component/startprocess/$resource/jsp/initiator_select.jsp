<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="net.simpleframework.workflow.engine.InitiateItem"%>
<%@ page import="net.simpleframework.workflow.web.component.startprocess.StartProcessUtils"%>
<%@ page import="net.simpleframework.mvc.component.ComponentParameter"%>
<%@ page import="net.simpleframework.common.ID"%>
<%@ page import="java.util.Collection"%>
<%@ page import="net.simpleframework.mvc.common.element.LinkButton"%>
<%
	final ComponentParameter nCP = StartProcessUtils.get(request,
			response);
	final InitiateItem initiateItem = StartProcessUtils
			.getInitiateItem(nCP);
%>
<div class="simple_window_tcb initiator_select">
  <%
  	for (ID id : initiateItem.roles()) {
  %>
  <div class="ritem">
    <%=LinkButton.corner(nCP.getPermission().getRole(id))
						.setOnclick(
								"$Actions['InitiatorSelect_ok']('"
										+ StartProcessUtils.toParams(nCP, initiateItem) + "&initiator="
										+ id + "');")%>
  </div>
  <%
  	}
  %>
  <div class="b">
    <input type="button" value="#(Button.Cancel)" onclick="$win(this).close();" />
  </div>
</div>