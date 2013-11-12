<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="net.simpleframework.workflow.engine.EProcessAbortPolicy"%>
<%@ page import="net.simpleframework.workflow.engine.ProcessBean"%>

<div class="simple_window_tcb process_abort_form">
  <div class="t">#(activity_abort.0)</div>
  <div class="c">
    <p>
      <input name="process_abort_policy" id="process_abort_policy0"
        value="<%=EProcessAbortPolicy.normal.name()%>" type="radio" checked /> <label
        for="process_abort_policy0" style="cursor: pointer;"><%=EProcessAbortPolicy.normal%></label>
    </p>
    <p>
      <input name="process_abort_policy" id="process_abort_policy1"
        value="<%=EProcessAbortPolicy.allActivities.name()%>" type="radio" /> <label
        for="process_abort_policy1" style="cursor: pointer;"><%=EProcessAbortPolicy.allActivities%></label>
    </p>
  </div>
  <div class="b">
    <input type="button" class="button2" value="#(Button.Ok)"
      onclick="$Actions['ajaxProcessAbort']('<%=ProcessBean.processId%>=<%=request.getParameter(ProcessBean.processId)%>');" />
    <input type="button" value="#(Button.Cancel)" onclick="$win(this).close();" />
  </div>
</div>