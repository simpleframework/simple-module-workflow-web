package net.simpleframework.workflow.web.component.action.complete;

import net.simpleframework.common.StringUtils;
import net.simpleframework.mvc.component.AbstractComponentRender.ComponentJavascriptRender;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ComponentRenderUtils;
import net.simpleframework.mvc.component.ComponentUtils;
import net.simpleframework.workflow.engine.WorkitemBean;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class WorkitemCompleteRender extends ComponentJavascriptRender {

	@Override
	public String getJavascriptCode(final ComponentParameter cp) {
		final String workitemId = cp.getParameter(WorkitemBean.workitemId);
		final StringBuilder sb = new StringBuilder();
		sb.append("var dc = function() { $Loading.hide(); };");
		sb.append("$Loading.show();");
		final StringBuilder params = new StringBuilder();
		params.append(WorkitemCompleteUtils.BEAN_ID).append("=").append(cp.hashId()).append("&")
				.append(WorkitemBean.workitemId).append("=").append(workitemId);
		sb.append("var params=\"").append(params).append("\";");
		ComponentRenderUtils.appendParameters(sb, cp, "params");
		sb.append("params = params.addParameter(arguments[0]);");
		sb.append("new Ajax.Request('")
				.append(ComponentUtils.getResourceHomePath(WorkitemCompleteBean.class))
				.append("/jsp/workitem_complete.jsp', {");
		sb.append("postBody: params,");
		sb.append("onComplete: function(req) {");
		sb.append("try {");
		sb.append("var json = req.responseText.evalJSON();");
		sb.append("var err = json['exception']; if (err) { $error(err); return; }");
		sb.append("var rt = json['responseText'];");
		sb.append("if (rt) { new $UI.AjaxRequest(null, rt, '").append(cp.getComponentName())
				.append("', false); }");
		sb.append("if (json['transitionManual']) { (function() { $Actions['transitionManualWindow']('");
		sb.append(params).append("'); }).defer(); }");
		sb.append("else if (json['participantManual']) { (function() { $Actions['participantManualWindow']('");
		sb.append(params).append("'); }).defer(); }");

		final IWorkitemCompleteHandler hdl = (IWorkitemCompleteHandler) cp.getComponentHandler();
		final String jsCallback = hdl.jsCompleteCallback(cp);
		if (StringUtils.hasText(jsCallback)) {
			sb.append("else {").append(jsCallback).append("}");
		}
		sb.append("} finally { dc(); }");
		sb.append("}, onException: dc, onFailure: dc");
		sb.append("});");
		return ComponentRenderUtils.genActionWrapper(cp, sb.toString());
	}
}
