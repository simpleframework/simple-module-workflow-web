<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="net.simpleframework.mvc.component.ComponentParameter"%>
<%@ page import="net.simpleframework.workflow.web.component.workview.DoWorkviewUtils"%>
<%
	final ComponentParameter nCP = DoWorkviewUtils.get(request,
			response);
	final String componentName = nCP.getComponentName();
%>
<div class="workview_select">
  <%=DoWorkviewUtils.toSelectHTML(nCP)%>
</div>
<script type="text/javascript">
	function DoWorkview_init() {
	  $(".workview_select .wv_cc").select(".uitem").invoke("observe",
        "mouseenter", function(evn) {
	    		var act = $(this).down(".act");
	    		if (act)
	    			act.show();
        }).invoke("observe", "mouseleave", function(evn) {
          var act = $(this).down(".act");
          if (act)
          	act.hide();
    });
	}
	
  function DoWorkview_user_selected(selects, params) {
    var act = $Actions['<%=componentName%>_ulist'];
    act.container = $(".workview_select .wv_cc");
    var userIds = selects ? $(selects).inject([], function(r, o) {
      r.push(o.id);
      return r;
    }).join(";") : '';
    act(('<%=DoWorkviewUtils.toParams(nCP)%>&userIds=' + userIds).addParameter(params));
    return true;
  }
  
  $ready(function() {
    var ts = $(".workview_select .wv_cc");
       
    var w = $Actions['<%=componentName%>_win'].window;
    w.content.setStyle("overflow:hidden;");
    var s = function() {
      var h = w.getSize(true).height;
      ts.setStyle('height: ' + (h - 66) + 'px;');
    };
    s();
    w.observe("resize:ended", s);
  });
</script>