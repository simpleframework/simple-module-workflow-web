<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html>
<body>
  <div align="center" style="padding: 10px;">
    <div class="f3 simple_toolbar">#(model_upload_result.0)</div>
    <div align="right">
      <input type="button" value="#(model_upload_result.1)" onclick="history.back();" /> <input
        type="button" value="#(Button.Close)" onclick="parent.$Actions['ml_upload_window'].close();" />
    </div>
  </div>
  <script type="text/javascript">
			(function() {
				parent.$Actions['ml_upload_window'].pager.refresh();
			})();
		</script>
</body>
</html>