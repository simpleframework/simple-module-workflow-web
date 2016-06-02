<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="net.simpleframework.mvc.component.ComponentParameter"%>
<%@ page import="net.simpleframework.workflow.web.component.comments.WfCommentUtils"%>
<%
  final ComponentParameter cp = WfCommentUtils.get(request, response);
	final String commentName = cp.getComponentName();
%>
<script type="text/javascript">
  function wf_comment_init() {
    var c = $Actions['<%=commentName%>_log_popup'].window.content;
    
    c.select(".litem").invoke("observe", "mouseenter", function(evn) {
      $(this).down(".act").show();
    }).invoke("observe", "mouseleave", function(evn) {
      $(this).down(".act").hide();
    });
  }
  
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
    $$(".cl_list .litem").invoke("removeClassName", "selected");
    $(o).addClassName("selected");
  }
  
  function wf_comment_okclick() {
    var o = $(".cl_list .litem.selected");
    if (!o) {
      alert('#(wf_comment_log.4)');
    } else {
      wf_comment_itemdblclick(o);
    }
  }
  
  function wf_comment_itemdblclick(o) {
    var act = $Actions['<%=commentName%>_log_popup'];
    var ta = $(act.trigger).up(".wf_comment").down("textarea");
    $Actions.setValue(ta, $(o).down(".l1 textarea").value);
    wf_comment_ta_valchange(ta);
    act.close();
  }
  
  function wf_comment_itemdel(o, params) {
    if (!confirm('#(wf_comment_log.2)')) {
      return;
    }
    var act = $Actions['<%=commentName%>_logDel'];
    act.jsCompleteCallback = function(req, responseText) {
      if (responseText == 'true')
      	$(o).up(".litem").remove();
    };
    act(params);
  }
  
  function wf_comment_itemcopy(o, params) {
    if (!confirm('#(wf_comment_log.3)')) {
      return;
    }
    var act = $Actions['<%=commentName%>_logCopy'];
    act.jsCompleteCallback = function(req, responseText) {
    	alert(responseText);
    };
    act(params);
  }
</script>
<div class="wf_comment_log">
  <%=WfCommentUtils.toCommentLogsHTML(cp)%>  
</div>
<script type="text/javascript">
  $ready(function() {
    var w = $Actions['<%=commentName%>_log_popup'].window;
    var c = w.content;
    c.setStyle("overflow:hidden;");

    var t = c.down(".cl_list");
    var s = function() {
      var h = w.getSize(true).height;
      t.setStyle('height: ' + (h - 28 - 33) + 'px;');
    };
    s();
    w.observe("resize:ended", s);
  });
</script>