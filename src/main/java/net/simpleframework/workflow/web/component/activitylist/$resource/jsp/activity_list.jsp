<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="net.simpleframework.mvc.component.ComponentParameter"%>
<%@ page import="net.simpleframework.mvc.component.ui.pager.TablePagerHTML"%>
<%@ page import="net.simpleframework.mvc.component.ui.pager.PagerUtils"%>
<%@ page import="net.simpleframework.workflow.engine.ActivityBean"%>
<%@ page import="net.simpleframework.workflow.web.component.activitylist.ActivityListBean"%>
<%
	final ComponentParameter nCP = PagerUtils.get(request, response);
	ActivityListBean componentBean = (ActivityListBean) nCP.componentBean;
	out.write(TablePagerHTML.renderTable(nCP));
%>
<script type="text/javascript">
  var pager_init_<%=componentBean.hashId()%> = function(action) {
    $table_pager_addMethods(action);
    
    action.suspend = function(item) {
      var act = $Actions['activity_suspend'];
      act.selector = action.selector;
      act('<%=ActivityBean.activityId%>=' + action.rowId(item));
    };
    
    action.abort = function(item) {
      var act = $Actions['activity_abort_window'];
      act('<%=ActivityBean.activityId%>=' + action.rowId(item));
    };
  };
</script>