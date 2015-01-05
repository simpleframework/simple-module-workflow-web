<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="net.simpleframework.workflow.web.component.complete.WorkitemCompleteUtils"%>
<%@ page import="net.simpleframework.workflow.engine.WorkitemBean"%>
<%@ page import="net.simpleframework.mvc.component.ComponentParameter"%>
<%@ page import="net.simpleframework.workflow.schema.TransitionNode"%>
<%@ page import="net.simpleframework.mvc.common.element.Checkbox"%>
<%@ page import="net.simpleframework.mvc.common.element.ButtonElement"%>
<%
	final ComponentParameter nCP = WorkitemCompleteUtils.get(request,
			response);
	final WorkitemBean workitem = WorkitemCompleteUtils
			.getWorkitemBean(nCP);
%>
<div class="simple_window_tcb transition_select">
  <%=WorkitemCompleteUtils.toTransitionsHTML(nCP, workitem)%>
  <div class="msg"></div>
  <div class="b">
    <input type="button" class="button2" value="#(Button.Ok)" />
    <%=ButtonElement.WINDOW_CLOSE%>
  </div>
</div>
<script type="text/javascript">
  $ready(function() {
    var ts = $(".transition_select");
    
    var PARAMS = "<%=WorkitemCompleteUtils.toParams(nCP, workitem)%>&transitions=";

    ts.down(".button2").observe("click", function(evn) {
      var id = "";
      ts.select("input[id]").each(function(box) {
        if (box.checked) {
          id += ";" + box.value;
        }
      });

      if (id.length > 0) {
        $Actions['TransitionSelectLoaded_ok'](PARAMS + id.substring(1));
      } else {
        $UI.shakeMsg(ts.down(".msg"), "<span>#(transition_select.0)</span>");
      }
    });
  });
</script>