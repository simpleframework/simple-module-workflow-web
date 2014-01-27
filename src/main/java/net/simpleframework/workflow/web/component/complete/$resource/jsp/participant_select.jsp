<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="net.simpleframework.mvc.common.element.ButtonElement"%>
<%@ page import="net.simpleframework.workflow.web.component.complete.WorkitemCompleteUtils"%>
<%@ page import="net.simpleframework.mvc.component.ComponentParameter"%>
<%
	final ComponentParameter nCP = WorkitemCompleteUtils.get(request,
			response);
%>
<div class="simple_window_tcb participant_select">
  <%=WorkitemCompleteUtils.toParticipantsHTML(nCP)%>
  <div class="b">
    <input type="button" class="button2" value="#(Button.Ok)" />
    <%=ButtonElement.WINDOW_CLOSE%>
  </div>
</div>
<script type="text/javascript">
  $ready(function() {
    var ts = $(".participant_select");
	
    var PARAMS = "<%=WorkitemCompleteUtils.toParams(nCP,
					WorkitemCompleteUtils.getWorkitemBean(nCP))%>";

    ts.down(".button2").observe("click", function(evn) {
      var data = ts.select(".transition").inject([], function(r, p) {
        var o = {
          "transition" : p.readAttribute("transition")
        };
        r.push(o);
        o.participant_obj = p.next();
        var id = "";
        o.participant_obj.select("input[type=checkbox], input[type=radio]").each(function(box) {
          if (box.checked) {
            id += ";" + box.value;
          }
        });
        if (id.length > 0) {
          o.participant = id.substring(1);
        }
        return r;
      });

      if (data.any(function(o) {
        if (!o.participant) {
          $UI.shakeMsg(o.participant_obj.down(".msg"), "#(participant_select.0)");
          return true;
        }
        delete o.participant_obj;
      })) {
        return;
      }

      $Actions["ParticipantSelectLoaded_ok"](PARAMS + '&json='
          + encodeURIComponent(Object.toJSON(data)));
    });
  });
</script>