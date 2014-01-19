package net.simpleframework.workflow.web.component.complete;

import net.simpleframework.mvc.component.AbstractComponentRender.ComponentJavascriptRender;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ComponentRenderUtils;
import net.simpleframework.mvc.component.ComponentUtils;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class WorkitemCompleteRender extends ComponentJavascriptRender {

	@Override
	public String getJavascriptCode(final ComponentParameter cp) {
		final StringBuilder sb = new StringBuilder();
		sb.append("var dc = function() { $Loading.hide(); };");
		sb.append("$Loading.show();");
		final StringBuilder params = new StringBuilder();
		params.append(WorkitemCompleteUtils.BEAN_ID).append("=").append(cp.hashId());
		sb.append("var params=\"").append(params).append("\";");
		ComponentRenderUtils.appendParameters(sb, cp, "params");
		sb.append("params = params.addParameter(arguments[0]);");
		sb.append("new Ajax.Request('")
				.append(ComponentUtils.getResourceHomePath(WorkitemCompleteBean.class))
				.append("/jsp/workitem_complete.jsp', {");
		sb.append("postBody: params,");
		sb.append("onComplete: function(req) {");
		sb.append("try { $call(req.responseText); } finally { dc(); }");
		sb.append("}, onException: dc, onFailure: dc");
		sb.append("});");
		return ComponentRenderUtils.genActionWrapper(cp, sb.toString());
	}
}
