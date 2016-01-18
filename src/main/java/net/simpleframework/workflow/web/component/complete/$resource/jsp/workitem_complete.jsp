<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="net.simpleframework.workflow.web.component.complete.WorkitemCompleteUtils"%>
<%@ page import="net.simpleframework.common.th.ThrowableUtils"%>
<%
	try {
		WorkitemCompleteUtils.doForword(WorkitemCompleteUtils.get(
				request, response));
	} catch (Throwable th) {
		out.write("alert(\""
				+ ThrowableUtils.getThrowableMessage(th, null, true)
				+ "\");");
	}
%>