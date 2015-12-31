package net.simpleframework.workflow.web.component.abort;

import static net.simpleframework.common.I18n.$m;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.component.AbstractComponentBean;
import net.simpleframework.mvc.component.AbstractComponentRegistry;
import net.simpleframework.mvc.component.ComponentBean;
import net.simpleframework.mvc.component.ComponentName;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ComponentRender;
import net.simpleframework.mvc.component.ComponentResourceProvider;
import net.simpleframework.mvc.component.base.ajaxrequest.AjaxRequestBean;
import net.simpleframework.mvc.component.ui.window.WindowBean;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
@ComponentName(ActivityAbortRegistry.ACTIVITY_ABORT)
@ComponentBean(ActivityAbortBean.class)
@ComponentRender(ActivityAbortRender.class)
@ComponentResourceProvider(ActivityAbortResourceProvider.class)
public class ActivityAbortRegistry extends AbstractComponentRegistry {

	public static final String ACTIVITY_ABORT = "wf_activity_abort";

	@Override
	public AbstractComponentBean createComponentBean(final PageParameter pp, final Object attriData) {
		final ActivityAbortBean activityAbort = (ActivityAbortBean) super.createComponentBean(pp,
				attriData);

		final ComponentParameter nCP = ComponentParameter.get(pp, activityAbort);
		final String componentName = nCP.getComponentName();

		final AjaxRequestBean ajaxRequest = pp.addComponentBean(componentName + "_win_page",
				AjaxRequestBean.class).setUrlForward(
				getComponentResourceProvider().getResourceHomePath() + "/jsp/activity_select.jsp");
		pp.addComponentBean(componentName + "_win", WindowBean.class)
				.setContentRef(ajaxRequest.getName()).setWidth(300).setHeight(400)
				.setTitle($m("ActivityAbortRegistry.0"));

		return activityAbort;
	}
}
