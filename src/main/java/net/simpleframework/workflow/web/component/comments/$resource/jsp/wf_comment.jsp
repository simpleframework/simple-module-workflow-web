<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="net.simpleframework.workflow.web.component.comments.WfCommentUtils"%>
<%@ page import="net.simpleframework.mvc.component.ComponentParameter"%>
<%@ page import="net.simpleframework.workflow.web.component.comments.IWfCommentHandler"%>
<%@ page import="net.simpleframework.mvc.component.ComponentRenderUtils"%>
<%@ page import="net.simpleframework.workflow.web.page.WorkflowUtils"%>
<%
	ComponentParameter cp = WfCommentUtils.get(request, response);
	IWfCommentHandler hdl = (IWfCommentHandler) cp
			.getComponentHandler();
%>
<div class="wf_comment">
  <%=ComponentRenderUtils.genParameters(cp)%>
  <%=hdl.toHTML(cp, WorkflowUtils.getWorkitemBean(cp))%>
</div>