package net.simpleframework.workflow.web.component.worklist;

import net.simpleframework.mvc.component.ComponentBean;
import net.simpleframework.mvc.component.ComponentName;
import net.simpleframework.mvc.component.ComponentResourceProvider;
import net.simpleframework.mvc.component.ui.pager.TablePagerRegistry;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
@ComponentName(WorklistRegistry.WORKLIST)
@ComponentBean(WorklistBean.class)
@ComponentResourceProvider(WorklistResourceProvider.class)
public class WorklistRegistry extends TablePagerRegistry {
	public static final String WORKLIST = "wf_worklist";
}
