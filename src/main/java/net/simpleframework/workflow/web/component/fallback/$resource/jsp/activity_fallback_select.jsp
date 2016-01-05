<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="net.simpleframework.mvc.component.ComponentParameter"%>
<%@ page import="net.simpleframework.workflow.web.component.fallback.ActivityFallbackUtils"%>
<%@ page import="net.simpleframework.mvc.common.element.ButtonElement"%>
<%
	final ComponentParameter nCP = ActivityFallbackUtils.get(request,
			response);
	final String componentName = nCP.getComponentName();
%>
<div class="simple_window_tcb activity_fallback_select">
  <%=ActivityFallbackUtils.toListHTML(nCP)%>
  <div class="b clearfix">
    <%=ActivityFallbackUtils.toBottomHTML(nCP)%>
  </div>
</div>
<script type="text/javascript">
  $ready(function() {
    var ts = $(".activity_fallback_select");
    
    var w = $Actions['<%=componentName%>_win'].window;
    w.content.setStyle("overflow:hidden;");
    var s = function() {
      var h = w.getSize(true).height;
      ts.setStyle('height: ' + (h - 36) + 'px;');
    };
    s();
    w.observe("resize:ended", s);
  });
</script>