<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="net.simpleframework.workflow.web.component.startprocess.StartProcessUtils"%>
<%@ page import="net.simpleframework.mvc.component.ComponentParameter"%>
<%
	final ComponentParameter nCP = StartProcessUtils.get(request,
			response);
%>
<div class="simple_window_tcb initiator_select">
  <%=StartProcessUtils.toInitiatorHTML(nCP)%>
  <div class="b">
    <input type="button" value="#(Button.Cancel)" onclick="$win(this).close();" />
  </div>
</div>