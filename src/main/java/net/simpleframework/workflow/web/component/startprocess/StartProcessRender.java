package net.simpleframework.workflow.web.component.startprocess;

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
public class StartProcessRender extends ComponentJavascriptRender {

	@Override
	public String getJavascriptCode(final ComponentParameter cp) {
		final StringBuilder sb = new StringBuilder();
		sb.append("var dc = function() { $Loading.hide(); };");
		sb.append("$Loading.show();");
		final StringBuilder params = new StringBuilder();
		params.append(StartProcessUtils.BEAN_ID).append("=").append(cp.hashId());
		sb.append("var params=\"").append(params).append("\";");
		ComponentRenderUtils.appendParameters(sb, cp, "params");
		sb.append("params = params.addParameter(arguments[0]);");
		sb.append("new Ajax.Request('")
				.append(ComponentUtils.getResourceHomePath(StartProcessBean.class))
				.append("/jsp/start_process.jsp', {");
		sb.append("postBody: params,");
		sb.append("onComplete: function(req) {");
		sb.append("try { $call(req.responseText); } finally { dc(); }");
		sb.append("}, onException: dc, onFailure: dc");
		sb.append("});");

		final String actionFunc = ComponentRenderUtils.actionFunc(cp);
		final String componentName = cp.getComponentName();
		final StringBuilder sb2 = new StringBuilder();
		sb2.append(actionFunc).append(".initiator_select = function() {");
		sb2.append(" $Actions['").append(componentName).append("_initiatorSelect']();");
		sb2.append("};");
		return ComponentRenderUtils.genActionWrapper(cp, sb.toString(), sb2.toString());
	}
}
