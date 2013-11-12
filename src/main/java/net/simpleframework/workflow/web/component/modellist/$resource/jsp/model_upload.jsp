<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html>
<body>
  <div align="center" style="padding: 10px 0;">
    <form id="model_upload_form" style="width: 90%;" class="simple_toolbar">
      <div align="left">#(model_upload.0)</div>
      <div align="left" style="padding: 8px 0;"></div>
      <div style="text-align: right;">
        <input id="ml_submit" type="submit" value="#(model_upload.1)"
          onclick="$Actions['model_upload_submit']();" />
      </div>
    </form>
  </div>
  <script type="text/javascript">
			(function() {
				var ele = $UI.createFileInputField();
				ele.file.writeAttribute("id", "ml_upload");
				ele.file.writeAttribute("name", "ml_upload");
				$("model_upload_form").down().next().insert(ele);
			})();
		</script>
</body>
</html>