<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="net.simpleframework.workflow.engine.EActivityAbortPolicy"%>
<%@ page import="net.simpleframework.workflow.engine.ActivityBean"%>

<div class="simple_window_tcb activity_abort_form">
  <div class="t">#(activity_abort.0)</div>
  <div class="c">
    <p>
      <input name="activity_abort_policy" id="activity_abort_policy0"
        value="<%=EActivityAbortPolicy.normal.name()%>" type="radio" checked /> <label
        for="activity_abort_policy0" style="cursor: pointer;"><%=EActivityAbortPolicy.normal%></label>
    </p>
    <p>
      <input name="activity_abort_policy" id="activity_abort_policy1"
        value="<%=EActivityAbortPolicy.nextActivities.name()%>" type="radio" /> <label
        for="activity_abort_policy1" style="cursor: pointer;"><%=EActivityAbortPolicy.nextActivities%></label>
    </p>
  </div>
  <div class="b">
    <input type="button" class="button2" value="#(Button.Ok)"
      onclick="$Actions['ajaxActivityAbort']('<%=ActivityBean.activityId%>=<%=request.getParameter(ActivityBean.activityId)%>');" />
    <input type="button" value="#(Button.Cancel)" onclick="$win(this).close();" />
  </div>
</div>