package net.simpleframework.workflow.web.component.action.startprocess;

import net.simpleframework.mvc.component.AbstractComponentRender.ComponentJavascriptRender;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ComponentRenderUtils;
import net.simpleframework.mvc.component.ComponentUtils;
import net.simpleframework.mvc.component.IComponentRegistry;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public class StartProcessRender extends ComponentJavascriptRender {

	public StartProcessRender(final IComponentRegistry componentRegistry) {
		super(componentRegistry);
	}

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
		sb.append("try {");
		sb.append("var json = req.responseText.evalJSON();");
		sb.append("var err = json['exception']; if (err) { $error(err); return; }");
		sb.append("if (json['jsCallback']) { eval(json['jsCallback']); } else {");
		sb.append("var rt = json['responseText'];");
		sb.append("if (rt) { new $UI.AjaxRequest(null, rt, '").append(cp.getComponentName())
				.append("', false); }");
		sb.append("if (json['confirmMessage']) { ");
		sb.append("if (!confirm(json['confirmMessage'])) { return; }");
		sb.append("$Actions['ajaxStartProcessByInitiator']();");
		sb.append("} else if (json['transitionManual']) { ")
				.append("(function() { $Actions['process_transition_manual_window']('").append(params)
				.append("');}).defer(); } else if (json['initiateRoles']) {")
				.append("(function() { $Actions['initiator_select_window']('").append(params)
				.append("');}).defer(); }");
		sb.append("}");
		sb.append("} finally { dc(); }");
		sb.append("}, onException: dc, onFailure: dc");
		sb.append("});");
		return ComponentRenderUtils.genActionWrapper(cp, sb.toString());
	}
}
