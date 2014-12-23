<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="net.simpleframework.mvc.component.ComponentParameter"%>
<%@ page import="net.simpleframework.workflow.web.component.comments.WfCommentUtils"%>
<%
	ComponentParameter cp = WfCommentUtils.get(request, response);
%>
<div class="wf_comment_log">
  <div class="cl_tabs">
    <a class="active">常用</a><a>历史</a>
  </div>
  <div class="cl_list">
    <%=WfCommentUtils.toLogsHTML(cp)%>
  </div>
</div>