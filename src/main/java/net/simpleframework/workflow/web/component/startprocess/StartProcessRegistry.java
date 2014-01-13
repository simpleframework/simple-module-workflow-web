package net.simpleframework.workflow.web.component.startprocess;

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
@ComponentName(StartProcessRegistry.STARTPROCESS)
@ComponentBean(StartProcessBean.class)
@ComponentRender(StartProcessRender.class)
@ComponentResourceProvider(StartProcessResourceProvider.class)
public class StartProcessRegistry extends AbstractComponentRegistry {

	public static final String STARTPROCESS = "wf_start_process";

}
