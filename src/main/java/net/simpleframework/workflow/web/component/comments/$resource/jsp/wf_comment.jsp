<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="net.simpleframework.mvc.component.ComponentRenderUtils"%>
<%@ page import="net.simpleframework.workflow.web.component.comments.IWfCommentHandler"%>
<%@ page import="net.simpleframework.workflow.web.component.comments.WfCommentUtils"%>
<%@ page import="net.simpleframework.mvc.component.ComponentParameter"%>
<%
	final ComponentParameter cp = WfCommentUtils.get(request, response);
	final IWfCommentHandler hdl = (IWfCommentHandler) cp
			.getComponentHandler();
	final int maxlength = (Integer) cp.getBeanProperty("maxlength");
%>
<div class="wf_comment">
  <%=ComponentRenderUtils.genParameters(cp)%>
  <%=hdl.toHTML(cp)%>
</div>
<script type="text/javascript">
  function wf_comment_ta_valchange(ta) {
    var vlen = ta.value.length;
    var maxlength = <%=maxlength%>;
    var l = Math.max(maxlength - vlen, 0);
    if (vlen == 0) {
      ta.ltxt.innerHTML = "&nbsp;";
    } else {
      ta.ltxt.innerHTML = "#(wf_comment.0)<label>" + l + "</label>#(wf_comment.1)";
    }
    
    if (vlen > maxlength) {
      ta.value = ta.value.substring(0, maxlength);
    }  
  }
  
  $ready(function() {
    var ta = $(".wf_comment textarea");
    var ltxt = $(".wf_comment .ltxt");
    if (ta && ltxt) {
      ta.ltxt = ltxt;
      ta.observe("keyup", function(evn) {
        wf_comment_ta_valchange(ta);
      });
    }
  });
</script>