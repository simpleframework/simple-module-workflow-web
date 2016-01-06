<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="net.simpleframework.mvc.component.ComponentParameter"%>
<%@ page import="net.simpleframework.workflow.web.component.fallback.ActivityFallbackUtils"%>
<%@ page import="net.simpleframework.mvc.common.element.ButtonElement"%>
<%@ page import="net.simpleframework.workflow.web.WorkflowUtils"%>
<%@ page import="net.simpleframework.workflow.engine.bean.ActivityBean"%>
<%
	final ComponentParameter nCP = ActivityFallbackUtils.get(request,
			response);
	final String componentName = nCP.getComponentName();
	final ActivityBean activity = WorkflowUtils.getActivityBean(nCP);
%>
<div class="simple_window_tcb activity_fallback_select">
  <%=ActivityFallbackUtils.toListHTML(nCP)%>
  <div class="b clearfix">
    <%=ActivityFallbackUtils.toBottomHTML(nCP)%>
  </div>
</div>
<script type="text/javascript">
	function _activity_fallback_select_click(obj) {
	  if (!obj) {
	    obj = $(".activity_fallback_select .select");
	  }
	  if (!obj) {
	    alert('#(activity_fallback_select.0)');
	    return;
	  }
	  if (!confirm('#(activity_fallback_select.1)')) {
	    return;
	  }
	  $Actions['<%=componentName%>_Usernode_Select_OK'](
	      'activityId=<%=activity.getId()%>&usernodeId=' + obj.getAttribute('_usernode') + 
	      '&opt1=' + $F('idActivityFallback_opt1'));
	}
	
  $ready(function() {
    var w = $Actions['<%=componentName%>_win'].window;
    
    var ts = $(".activity_fallback_select");
    
    ts.select(".nitem").invoke("observe", "click", function(e) {
      var sel = ts.down(".select");
      if(sel)
        sel.removeClassName("select");	 
     	this.addClassName("select");	
     	w.setHeader("#(ActivityFallbackRegistry.0)" + " (" + this.innerHTML + ")");
    }).invoke("observe", "dblclick", function(e) {
      _activity_fallback_select_click(this);
    });
    
    w.content.setStyle("overflow:hidden;");
    var s = function() {
      var h = w.getSize(true).height;
      ts.setStyle('height: ' + (h - 36) + 'px;');
    };
    s();
    w.observe("resize:ended", s);
  });
</script>