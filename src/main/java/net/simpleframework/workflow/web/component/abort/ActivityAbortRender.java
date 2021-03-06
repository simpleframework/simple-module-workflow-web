package net.simpleframework.workflow.web.component.abort;

import net.simpleframework.mvc.component.AbstractComponentRender.ComponentBaseActionJavascriptRender;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ComponentUtils;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class ActivityAbortRender extends ComponentBaseActionJavascriptRender {

	@Override
	protected String getParams(final ComponentParameter cp) {
		return ActivityAbortUtils.toParams(cp);
	}

	@Override
	protected String getActionPath(final ComponentParameter cp) {
		return ComponentUtils.getResourceHomePath(ActivityAbortBean.class)
				+ "/jsp/activity_abort.jsp";
	}
}
