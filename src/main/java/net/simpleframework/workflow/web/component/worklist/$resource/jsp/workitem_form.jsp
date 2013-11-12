<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="net.simpleframework.workflow.web.component.worklist.WorklistUtils"%>
<%@ page import="net.simpleframework.mvc.PageRequestResponse"%>
<%
	out.write(WorklistUtils.getFormResponseText(PageRequestResponse
			.get(request, response)));
	out.flush();
%>