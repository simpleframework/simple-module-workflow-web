package net.simpleframework.workflow.web.component.action.complete;

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
@ComponentName(WorkitemCompleteRegistry.WORKITEMCOMPLETE)
@ComponentBean(WorkitemCompleteBean.class)
@ComponentRender(WorkitemCompleteRender.class)
@ComponentResourceProvider(WorkitemCompleteResourceProvider.class)
public class WorkitemCompleteRegistry extends AbstractComponentRegistry {
	public static final String WORKITEMCOMPLETE = "wf_workitem_complete";

}
