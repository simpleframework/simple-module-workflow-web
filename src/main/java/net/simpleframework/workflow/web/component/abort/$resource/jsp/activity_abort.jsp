<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="net.simpleframework.mvc.component.ComponentParameter"%>
<%@ page import="net.simpleframework.workflow.web.component.abort.ActivityAbortUtils"%>

<%
	final ComponentParameter nCP = ActivityAbortUtils.get(request,
			response);
%>
<script type="text/javascript">
  $ready(function() {
		$Actions['<%=nCP.getComponentName()%>_win']();
  });
</script>