<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="net.simpleframework.mvc.component.ComponentParameter"%>
<%@ page import="net.simpleframework.workflow.web.component.fallback.ActivityFallbackUtils"%>
<%
	final ComponentParameter nCP = ActivityFallbackUtils.get(request,
			response);
	final String componentName = nCP.getComponentName();
%>
<%=componentName%>