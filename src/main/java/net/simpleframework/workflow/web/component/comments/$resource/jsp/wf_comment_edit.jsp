<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="net.simpleframework.workflow.web.component.comments.WfCommentUtils"%>
<%@ page import="net.simpleframework.mvc.component.ComponentParameter"%>

<%
	final ComponentParameter cp = WfCommentUtils.get(request, response);
%>
<div class="wf_comment_edit simple_window_tcb">
  <%=WfCommentUtils.toCommentEditHTML(cp)%>
</div>