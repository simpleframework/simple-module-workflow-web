package net.simpleframework.workflow.web.component.abort;

import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ComponentUtils;
import net.simpleframework.workflow.web.component.complete.WorkitemCompleteRender;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class ActivityAbortRender extends WorkitemCompleteRender {

	@Override
	protected String _params(final ComponentParameter cp) {
		return ActivityAbortUtils.BEAN_ID + "=" + cp.hashId();
	}

	@Override
	protected String _actpath(final ComponentParameter cp) {
		return ComponentUtils.getResourceHomePath(ActivityAbortBean.class)
				+ "/jsp/activity_abort.jsp";
	}
}
