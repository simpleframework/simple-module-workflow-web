package net.simpleframework.workflow.web.component.abort;

import net.simpleframework.mvc.DefaultPageHandler;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.base.ajaxrequest.AjaxRequestBean;
import net.simpleframework.mvc.component.base.ajaxrequest.DefaultAjaxRequestHandler;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class ActivitySelectLoaded extends DefaultPageHandler {

	@Override
	public void onBeforeComponentRender(final PageParameter pp) {
		super.onBeforeComponentRender(pp);

		final ComponentParameter nCP = ActivityAbortUtils.get(pp);
		pp.addComponentBean(nCP.getComponentName() + "_ActivitySelect_OK", AjaxRequestBean.class)
				.setHandlerClass(ActivitySelectAction.class);
	}

	public static class ActivitySelectAction extends DefaultAjaxRequestHandler {
	}
}
