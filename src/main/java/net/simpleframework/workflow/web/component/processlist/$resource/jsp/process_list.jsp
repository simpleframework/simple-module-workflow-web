<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="net.simpleframework.mvc.component.ui.pager.PagerUtils"%>
<%@ page import="net.simpleframework.mvc.component.ComponentParameter"%>
<%@ page import="net.simpleframework.mvc.component.ui.pager.TablePagerHTML"%>
<%@ page import="net.simpleframework.workflow.engine.ProcessBean"%>
<%@ page import="net.simpleframework.workflow.web.component.processlist.ProcessListBean"%>
<%
	final ComponentParameter nCP = PagerUtils.get(request, response);
	ProcessListBean componentBean = (ProcessListBean) nCP.componentBean;
	out.write(TablePagerHTML.renderTable(nCP));
%>
<script type="text/javascript">
  var pager_init_<%=componentBean.hashId()%> = function(action) {
    $table_pager_addMethods(action);
    
    action.suspend = function(item) {
      var act = $Actions['process_list_suspend'];
      act.selector = action.selector;
      act('<%=ProcessBean.processId%>=' + action.rowId(item));
    };
    
    action.abort = function(item) {
      var act = $Actions['process_list_abort_window'];
      act('<%=ProcessBean.processId%>=' + action.rowId(item));
    };
    
    action.del = function(item) {
      var act = $Actions['process_list_delete'];
      act.selector = action.selector;
      act('<%=ProcessBean.processId%>=' + action.rowId(item));
    };
  };
</script>