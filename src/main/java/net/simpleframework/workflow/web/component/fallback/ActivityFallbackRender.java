package net.simpleframework.workflow.web.component.fallback;

import net.simpleframework.mvc.component.AbstractComponentRender.ComponentBaseActionJavascriptRender;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ComponentUtils;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class ActivityFallbackRender extends ComponentBaseActionJavascriptRender {
	@Override
	protected String getParams(final ComponentParameter cp) {
		return ActivityFallbackUtils.toParams(cp);
	}

	@Override
	protected String getActionPath(final ComponentParameter cp) {
		return ComponentUtils.getResourceHomePath(ActivityFallbackBean.class)
				+ "/jsp/activity_fallback.jsp";
	}
}
