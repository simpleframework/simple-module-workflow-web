<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="net.simpleframework.mvc.component.ComponentParameter"%>
<%@ page import="net.simpleframework.mvc.component.ui.pager.PagerUtils"%>
<%@ page import="net.simpleframework.mvc.component.ui.pager.TablePagerHTML"%>
<%@ page import="net.simpleframework.workflow.web.component.worklist.WorklistUtils"%>
<%@ page import="net.simpleframework.workflow.engine.WorkitemBean"%>
<%@ page import="net.simpleframework.workflow.web.component.worklist.WorklistBean"%>
<%
	final ComponentParameter nCP = PagerUtils.get(
			request, response);
  WorklistBean componentBean = (WorklistBean) nCP.componentBean;
	out.write(TablePagerHTML.renderTable(nCP));
%>
<script type="text/javascript">
  var pager_init_<%=componentBean.hashId()%> = function(action) {
    $table_pager_addMethods(action);
    
    action.retake = function(item) {
      var act = $Actions['ajaxWorkitemRetake'];
      act.selector = action.selector;
      act('<%=WorkitemBean.workitemId%>=' + action.rowId(item));
    };

    action.readMark = function(item) {
      var act = $Actions['ajaxWorkitemReadMark'];
      act.selector = action.selector;
      act('<%=WorkitemBean.workitemId%>=' + action.rowId(item));
    };
    
    action.fallback = function(item) {
      var act = $Actions['ajaxWorkitemFallback'];
      act.selector = action.selector;
      act('<%=WorkitemBean.workitemId%>=' + action.rowId(item));
    };
    
    action.delegate = function(item) {
      var act = $Actions['workitemDelegateWindow'];
      act.selector = action.selector;
      act('<%=WorkitemBean.workitemId%>=' + action.rowId(item));
    };
  };
<%=WorklistUtils.jsWorkflowForm(nCP)%>
</script>