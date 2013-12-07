<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="net.simpleframework.mvc.component.ui.pager.PagerUtils"%>
<%@ page import="net.simpleframework.mvc.component.ComponentParameter"%>
<%@ page import="net.simpleframework.mvc.component.ui.pager.TablePagerHTML"%>
<%@ page import="net.simpleframework.mvc.component.IComponentHandler"%>
<%@ page import="net.simpleframework.workflow.engine.ProcessModelBean"%>
<%@ page import="net.simpleframework.workflow.web.component.modellist.DefaultModelListHandler"%>
<%@ page import="net.simpleframework.workflow.web.component.modellist.ModelListBean"%>
<%
	final ComponentParameter nCP = PagerUtils.get(request, response);
	ModelListBean componentBean = (ModelListBean) nCP.componentBean;
	out.write(TablePagerHTML.renderTable(nCP));
%>
<script type="text/javascript">
  var pager_init_<%=componentBean.hashId()%> = function(action) {
    $table_pager_addMethods(action); 
    
    <%final IComponentHandler hdl = nCP
          .getComponentHandler();
      if (hdl instanceof DefaultModelListHandler) {%>      
    action.upload_model = function() {  
      var act = $Actions['ml_upload_window'];
      act.pager = action;
      act();
    };
    
    action.del = function(item) {
      var act = $Actions['ml_delete_model'];
      act.selector = action.selector;
      act('<%=ProcessModelBean.modelId%>=' + action.rowId(item));
    };
    
    action.opt = function(item) {
      var act = $Actions['ml_opt_window'];
      act('<%=ProcessModelBean.modelId%>=' + action.rowId(item));
    };
<%}%>
  };
</script>
