<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="net.simpleframework.mvc.component.ComponentParameter"%>
<%@ page import="net.simpleframework.workflow.web.component.comments.WfCommentUtils"%>
<%
	ComponentParameter cp = WfCommentUtils.get(request, response);
	final String commentName = cp.getComponentName();
%>
<div class="wf_comment_log">
  <div class="cl_tabs">
    <a class="active" onclick="wf_comment_logtab(this, 'lt=collection');">#(wf_comment_log.0)</a><a 
      onclick="wf_comment_logtab(this, 'lt=history');">#(wf_comment_log.1)</a>
  </div>
  <div class="cl_list">
    <%=WfCommentUtils.toLogsHTML(cp)%>
  </div>
</div>
<script type="text/javascript">
  function wf_comment_logtab(o, params) {
    var act = $Actions['<%=commentName%>_logTab'];
    act.jsCompleteCallback = function(req, responseText) {
      o = $(o);
      var tabs = o.up(".cl_tabs");
      tabs.select("a").invoke("removeClassName", "active");
      o.addClassName("active");
      
      tabs.next(".cl_list").update(responseText);
    };
    act(params);
  }
  
  function wf_comment_itemclick(o) {
    var act = $Actions['<%=commentName%>_log_popup'];
    var ta = $(act.trigger).up(".wf_comment").down("textarea");
    $Actions.setValue(ta, $(o).down(".l1").innerHTML);
    act.close();
  }
</script>