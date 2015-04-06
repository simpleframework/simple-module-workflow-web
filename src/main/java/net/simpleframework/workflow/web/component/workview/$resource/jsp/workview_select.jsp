<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="net.simpleframework.mvc.component.ComponentParameter"%>
<%@ page import="net.simpleframework.workflow.web.component.workview.DoWorkviewUtils"%>
<%
	final ComponentParameter nCP = DoWorkviewUtils.get(request,
			response);
	final String componentName = nCP.getComponentName();
	final String params = DoWorkviewUtils.BEAN_ID + "=" + nCP.hashId();
%>
<div class="workview_select">
  <%=DoWorkviewUtils.toSelectHTML(nCP)%>
</div>
<script type="text/javascript">
	function DoWorkview_init() {
	  $(".workview_select .wv_cc").select(".uitem").invoke("observe",
        "mouseenter", function(evn) {
          $(this).down(".act").show();
        }).invoke("observe", "mouseleave", function(evn) {
      $(this).down(".act").hide();
    });
	}
	
  function DoWorkview_user_selected(selects) {
    var act = $Actions['<%=componentName%>_ulist'];
    act.container = $(".workview_select .wv_cc");
    act('<%=params%>&userIds=' + $(selects).inject([], function(r, o) {
      r.push(o.id);
      return r;
    }).join(";"));
    return true;
  }
  
  $ready(function() {
    var ts = $(".workview_select .wv_cc");
       
    var w = $Actions['<%=componentName%>_win'].window;
    w.content.setStyle("overflow:hidden;");
    var s = function() {
      var h = w.getSize(true).height;
      ts.setStyle('height: ' + (h - 67) + 'px;');
    };
    s();
    w.observe("resize:ended", s);
  });
</script>