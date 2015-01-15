package net.simpleframework.workflow.web.component.abort;

import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.component.AbstractComponentBean;
import net.simpleframework.mvc.component.AbstractComponentRegistry;
import net.simpleframework.mvc.component.ComponentBean;
import net.simpleframework.mvc.component.ComponentName;
import net.simpleframework.mvc.component.ComponentRender;
import net.simpleframework.mvc.component.ComponentResourceProvider;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
@ComponentName(ActivityAbortRegistry.ACTIVITYABORT)
@ComponentBean(ActivityAbortBean.class)
@ComponentRender(ActivityAbortRender.class)
@ComponentResourceProvider(ActivityAbortResourceProvider.class)
public class ActivityAbortRegistry extends AbstractComponentRegistry {

	public static final String ACTIVITYABORT = "wf_activity_abort";

	@Override
	public AbstractComponentBean createComponentBean(final PageParameter pp, final Object attriData) {
		final ActivityAbortBean activityAbort = (ActivityAbortBean) super.createComponentBean(pp,
				attriData);
		return activityAbort;
	}
}
