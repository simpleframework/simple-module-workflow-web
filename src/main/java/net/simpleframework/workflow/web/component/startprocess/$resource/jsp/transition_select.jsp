<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="net.simpleframework.workflow.engine.InitiateItem"%>
<%@ page import="net.simpleframework.workflow.web.component.startprocess.StartProcessUtils"%>
<%@ page import="net.simpleframework.mvc.component.ComponentParameter"%>
<%@ page import="net.simpleframework.workflow.schema.TransitionNode"%>
<%
	final ComponentParameter nCP = StartProcessUtils.get(request,
			response);
	final InitiateItem initiateItem = StartProcessUtils
			.getInitiateItem(nCP);
%>
<div class="simple_window_tcb transition_select">
  <div class="t">#(transition_manual.0)</div>
  <div class="c">
    <%
    	for (TransitionNode transition : initiateItem.getTransitions()) {
    %>
    <div class="node" onclick="var c = $(this).down('input'); c.checked = !c.checked;">
      <img class="icon" />
      <div class="txt"><%=transition.to()%></div>
      <input type="checkbox" value="<%=transition.getId()%>" />
    </div>
    <%
    	}
    %>
    <div class="msg"></div>
  </div>
  <div class="b">
    <input type="button" class="button2" value="#(Button.Ok)" /> <input type="button"
      value="#(Button.Cancel)" onclick="$win(this).close();" />
  </div>
</div>
<script type="text/javascript">
  $ready(function() {
    var ts = $(".transition_select");
    $UI.hackCheckbox(ts);
		
    var PARAMS = "<%=StartProcessUtils.toParams(nCP, initiateItem)%>&transitions=";
    
    ts.down(".button2").observe("click", function(evn) {
      var id = "";
      ts.select(".node input[type=checkbox]").each(function(box) {
        if (box.checked) {
          id += ";" + box.value;
        }
      });
      
      if (id.length > 0) {
        $Actions["TransitionSelectLoaded_ok"](PARAMS + id.substring(1));
      } else {
        $UI.shakeMsg(ts.down(".msg"), "#(transition_manual.1)");
      }
    });
  });
</script>