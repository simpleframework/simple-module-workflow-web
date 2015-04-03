<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="net.simpleframework.mvc.component.ComponentParameter"%>
<%@ page import="net.simpleframework.workflow.web.component.workview.DoWorkviewUtils"%>
<%
	final ComponentParameter nCP = DoWorkviewUtils.get(request,
			response);
%>
<div class="workview_select">
  <%=DoWorkviewUtils.toSelectHTML(nCP)%>
</div>
<style type="text/css">

</style>