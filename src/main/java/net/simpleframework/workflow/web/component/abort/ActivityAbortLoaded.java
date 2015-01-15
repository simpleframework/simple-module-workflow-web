package net.simpleframework.workflow.web.component.abort;

import net.simpleframework.mvc.DefaultPageHandler;
import net.simpleframework.mvc.IForward;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.base.ajaxrequest.AjaxRequestBean;
import net.simpleframework.mvc.component.base.ajaxrequest.DefaultAjaxRequestHandler;
import net.simpleframework.mvc.component.ui.window.WindowBean;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class ActivityAbortLoaded extends DefaultPageHandler {

	@Override
	public void onBeforeComponentRender(final PageParameter pp) {
		super.onBeforeComponentRender(pp);

		final ComponentParameter nCP = ActivityAbortUtils.get(pp);
		final String componentName = nCP.getComponentName();

		final AjaxRequestBean ajaxRequest = (AjaxRequestBean) pp.addComponentBean(
				componentName + "_win_page", AjaxRequestBean.class).setHandlerClass(
				ActivityAbortPage.class);
		pp.addComponentBean(componentName + "_win", WindowBean.class).setContentRef(
				ajaxRequest.getName());
	}

	public static class ActivityAbortPage extends DefaultAjaxRequestHandler {
		@Override
		public IForward ajaxProcess(final ComponentParameter cp) throws Exception {
			return super.ajaxProcess(cp);
		}
	}
}
