package net.simpleframework.workflow.web.component.fallback;

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
@ComponentName(ActivityFallbackRegistry.ACTIVITY_FALLBACK)
@ComponentBean(ActivityFallbackBean.class)
@ComponentRender(ActivityFallbackRender.class)
@ComponentResourceProvider(ActivityFallbackResourceProvider.class)
public class ActivityFallbackRegistry extends AbstractComponentRegistry {

	public static final String ACTIVITY_FALLBACK = "wf_activity_fallback";
}
