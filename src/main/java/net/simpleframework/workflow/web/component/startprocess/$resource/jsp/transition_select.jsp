<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="net.simpleframework.workflow.engine.InitiateItem"%>
<%@ page import="net.simpleframework.workflow.web.component.startprocess.StartProcessUtils"%>
<%@ page import="net.simpleframework.mvc.component.ComponentParameter"%>
<%@ page import="net.simpleframework.workflow.schema.TransitionNode"%>
<%@ page import="net.simpleframework.mvc.common.element.Checkbox"%>
<%@ page import="net.simpleframework.mvc.common.element.ButtonElement"%>
<%
	final ComponentParameter nCP = StartProcessUtils.get(request,
			response);
	final InitiateItem initiateItem = StartProcessUtils
			.getInitiateItem(nCP);
%>
<div class="simple_window_tcb transition_select">
  <%
  	for (TransitionNode transition : initiateItem.getTransitions()) {
  		String id = transition.getId();
  %>
  <div class="ritem">
    <%=new Checkbox(id, transition.to()).setValue(id)%>
  </div>
  <%
  	}
  %>
  <div class="msg"></div>
  <div class="b">
    <input type="button" class="button2" value="#(Button.Ok)" />
    <%=ButtonElement.closeBtn()%>
  </div>
</div>
<script type="text/javascript">
  $ready(function() {
    var ts = $(".transition_select");
      
    var PARAMS = "<%=StartProcessUtils.toParams(nCP, initiateItem)%>&transitions=";
    
    ts.down(".button2").observe("click", function(evn) {
      var id = "";
      ts.select("input[type=checkbox]").each(function(box) {
        if (box.checked) {
          id += ";" + box.value;
        }
      });
      
      if (id.length > 0) {
        $Actions["TransitionSelectLoaded_ok"](PARAMS + id.substring(1));
      } else {
        $UI.shakeMsg(ts.down(".msg"), "#(transition_select.0)");
      }
    });
  });
</script>