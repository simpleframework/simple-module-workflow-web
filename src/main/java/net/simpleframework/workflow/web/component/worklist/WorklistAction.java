package net.simpleframework.workflow.web.component.worklist;

import net.simpleframework.mvc.IForward;
import net.simpleframework.mvc.JavascriptForward;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.workflow.engine.WorkitemBean;
import net.simpleframework.workflow.web.component.AbstractListAction;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class WorklistAction extends AbstractListAction {

	public IForward doRetake(final ComponentParameter cp) {
		final JavascriptForward js = new JavascriptForward();
		context.getWorkitemService().retake(WorklistUtils.getWorkitem(cp));
		jsRefreshAction(cp, js);
		return js;
	}

	public IForward doReadMark(final ComponentParameter cp) {
		final JavascriptForward js = new JavascriptForward();
		final WorkitemBean workitem = WorklistUtils.getWorkitem(cp);
		context.getWorkitemService().readMark(workitem, workitem.isReadMark() ? true : false);
		jsRefreshAction(cp, js);
		return js;
	}

	public IForward doFallback(final ComponentParameter cp) {
		final JavascriptForward js = new JavascriptForward();
		final WorkitemBean workitem = WorklistUtils.getWorkitem(cp);
		context.getActivityService().fallback(context.getWorkitemService().getActivity(workitem));
		jsRefreshAction(cp, js);
		return js;
	}
}
