<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="net.simpleframework.mvc.component.ComponentParameter"%>
<%@ page import="net.simpleframework.workflow.web.component.abort.ActivityAbortUtils"%>
<%@ page import="net.simpleframework.mvc.common.element.ButtonElement"%>
<%
	final ComponentParameter nCP = ActivityAbortUtils.get(request,
			response);
	String componentName = nCP.getComponentName();
%>
<div class="simple_window_tcb activity_select">
  <%=ActivityAbortUtils.toListHTML(nCP)%>
  <div class="msg"></div>
  <div class="b">
    <input type="button" class="button2" value="#(Button.Ok)" />
    <%=ButtonElement.WINDOW_CLOSE%>
  </div>
</div>
<script type="text/javascript">
  $ready(function() {
    var ts = $(".activity_select");
    
    ts.down(".button2").observe("click", function(evn) {
    });
    
    var w = $Actions['<%=componentName%>_win'].window;
    w.content.setStyle("overflow:hidden;");
    var s = function() {
      var h = w.getSize(true).height;
      ts.setStyle('height: ' + (h - 37) + 'px;');
    };
    s();
    w.observe("resize:ended", s);
  });
</script>