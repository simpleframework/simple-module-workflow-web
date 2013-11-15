package net.simpleframework.workflow.web.component.activitylist;

import net.simpleframework.mvc.component.ComponentBean;
import net.simpleframework.mvc.component.ComponentName;
import net.simpleframework.mvc.component.ComponentResourceProvider;
import net.simpleframework.mvc.component.ui.pager.TablePagerRegistry;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
@ComponentName(ActivityListRegistry.ACTIVITYLIST)
@ComponentBean(ActivityListBean.class)
@ComponentResourceProvider(ActivityListResourceProvider.class)
public class ActivityListRegistry extends TablePagerRegistry {
	public static final String ACTIVITYLIST = "wf_activitylist";
}
