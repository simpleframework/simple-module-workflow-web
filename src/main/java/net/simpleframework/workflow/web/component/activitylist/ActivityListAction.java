package net.simpleframework.workflow.web.component.activitylist;

import net.simpleframework.common.Convert;
import net.simpleframework.mvc.IForward;
import net.simpleframework.mvc.JavascriptForward;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.workflow.engine.ActivityBean;
import net.simpleframework.workflow.engine.EActivityAbortPolicy;
import net.simpleframework.workflow.engine.EActivityStatus;
import net.simpleframework.workflow.engine.IActivityService;
import net.simpleframework.workflow.web.component.AbstractListAction;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public class ActivityListAction extends AbstractListAction {

	public IForward doSuspend(final ComponentParameter cp) {
		final JavascriptForward js = new JavascriptForward();
		final IActivityService service = context.getActivityService();
		final ActivityBean activity = service.getBean(cp.getParameter(ActivityBean.activityId));
		service.suspend(activity, activity.getStatus() == EActivityStatus.suspended);
		jsRefreshAction(cp, js);
		return js;
	}

	public IForward doAbort(final ComponentParameter cp) {
		final JavascriptForward js = new JavascriptForward();
		final IActivityService service = context.getActivityService();
		final ActivityBean activity = service.getBean(cp.getParameter(ActivityBean.activityId));
		service.abort(activity,
				Convert.toEnum(EActivityAbortPolicy.class, cp.getParameter("activity_abort_policy")));
		js.append("$Actions['activity_abort_window'].close();");
		jsRefreshAction(cp, js);
		return js;
	}
}
