<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="net.simpleframework.workflow.schema.TransitionNode"%>
<%@ page import="net.simpleframework.workflow.engine.InitiateItem"%>
<%@ page import="net.simpleframework.workflow.engine.ProcessModelBean"%>
<%@ page import="net.simpleframework.workflow.web.component.startprocess.StartProcessUtils"%>
<%@ page import="net.simpleframework.mvc.component.ComponentParameter"%>
<%
	final ComponentParameter cParameter = StartProcessUtils.get(request, response);
	final InitiateItem initiateItem = StartProcessUtils
			.getInitiateItem(cParameter);
	final String params = ProcessModelBean.modelId + "="
			+ initiateItem.getModelId() + "&" 
			+ StartProcessUtils.BEAN_ID + "="
			+ request.getParameter(StartProcessUtils.BEAN_ID);
%>
<div class="simple_window_tcb process_transition_manual">
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
    <input type="button" class="button2" value="#(Button.Ok)"
      onclick="doProcessTransitionSave(this, '<%=params%>');" /> <input type="button"
      value="#(Button.Cancel)" onclick="$win(this).close();" />
  </div>
</div>
<script type="text/javascript">
	function doProcessTransitionSave(obj, params) {
		var c = obj.up(".process_transition_manual");
		var id = "";
		c.select(".node input[type=checkbox]").each(function(box) {
			if (box.checked) {
				id += ";" + box.value;
			}
		});
		if (id.length > 0) {
			$Actions['ajaxProcessTransitionManualSave'](params + '&transitions='
					+ id.substring(1));
		} else {
			$UI.shakeMsg(c.down(".msg"), "#(transition_manual.1)");
		}
	}

	$UI.hackCheckbox($(".process_transition_manual"));
</script>
