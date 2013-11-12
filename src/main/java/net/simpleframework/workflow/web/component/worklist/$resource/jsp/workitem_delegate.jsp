<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<div class="simple_window_tcb workitem_delegate">
  <div class="t">
    <input type="button" value="选择委托用户" />
  </div>
  <div class="c" style="padding: 0">
    <table class="form_tbl">
      <tr>
        <td class="l">简单描述</td>
        <td class="v"><textarea rows="6" style="width: 100%;"></textarea></td>
      </tr>
      <tr>
        <td class="l">开始时间</td>
        <td class="v"></td>
      </tr>
      <tr>
        <td class="l">结束时间</td>
        <td class="v"></td>
      </tr>
    </table>
  </div>
  <div class="b">
    <input type="button" class="button2" value="#(Button.Ok)" onclick="" /> <input type="button"
      value="#(Button.Cancel)" onclick="$win(this).close();" />
  </div>
</div>
<style type="text/css">
.workitem_delegate .form_tbl .l {
	width: 70px;
}

.workitem_delegate .c {
	margin-top: -1px;
}
</style>