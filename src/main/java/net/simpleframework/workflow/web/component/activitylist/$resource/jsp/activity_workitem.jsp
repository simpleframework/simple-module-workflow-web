<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="net.simpleframework.mvc.PageRequestResponse"%>
<%@ page import="net.simpleframework.workflow.web.component.activitylist.ActivityListUtils"%>

<div>
  <%=ActivityListUtils.getWorkitemDetail(PageRequestResponse
					.get(request, response))%>
</div>