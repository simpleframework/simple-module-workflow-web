package net.simpleframework.workflow.web.component.action.complete;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.simpleframework.common.JsonUtils;
import net.simpleframework.common.StringUtils;
import net.simpleframework.mvc.IForward;
import net.simpleframework.mvc.JavascriptForward;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.base.ajaxrequest.DefaultAjaxRequestHandler;
import net.simpleframework.workflow.engine.ActivityComplete;
import net.simpleframework.workflow.engine.IWorkflowContextAware;
import net.simpleframework.workflow.engine.WorkitemBean;
import net.simpleframework.workflow.engine.WorkitemComplete;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public class WorkitemCompleteAction extends DefaultAjaxRequestHandler implements
		IWorkflowContextAware {

	@Override
	public Object getBeanProperty(final ComponentParameter cp, final String beanProperty) {
		if ("selector".equals(beanProperty)) {
			final ComponentParameter nCP = WorkitemCompleteUtils.get(cp);
			return nCP.getBeanProperty("selector");
		}
		return super.getBeanProperty(cp, beanProperty);
	}

	public IForward doTransitionSave(final ComponentParameter cp) {
		final WorkitemBean workitem = context.getWorkitemService().getBean(
				cp.getParameter(WorkitemBean.workitemId));
		final String transitions = cp.getParameter("transitions");
		final String[] transitionIds = StringUtils.split(transitions);
		final WorkitemComplete workitemComplete = WorkitemComplete.get(workitem);
		final ActivityComplete activityComplete = workitemComplete.getActivityComplete();
		final JavascriptForward js = new JavascriptForward();
		final ComponentParameter nCP = WorkitemCompleteUtils.get(cp);
		if (activityComplete.isParticipantManual(transitionIds)) {
			js.append("$Actions['participantManualWindow']('").append(WorkitemCompleteUtils.BEAN_ID)
					.append("=").append(nCP.hashId()).append("&").append(WorkitemBean.workitemId)
					.append("=").append(workitem.getId()).append("&transitions=").append(transitions)
					.append("');");
		} else {
			activityComplete.resetTransitions(transitionIds);
			final IWorkitemCompleteHandler hdl = (IWorkitemCompleteHandler) nCP.getComponentHandler();
			hdl.complete(nCP, workitemComplete);

			js.append("$Actions['transitionManualWindow'].close();");
			final String jsCallback = hdl.jsCompleteCallback(nCP);
			if (StringUtils.hasText(jsCallback)) {
				js.append(jsCallback);
			}
		}
		return js;
	}

	public IForward doParticipantSave(final ComponentParameter cp) {
		final WorkitemBean workitem = context.getWorkitemService().getBean(
				cp.getParameter(WorkitemBean.workitemId));
		final WorkitemComplete workitemComplete = WorkitemComplete.get(workitem);
		final ActivityComplete activityComplete = workitemComplete.getActivityComplete();
		final Map<String, String[]> participantIds = new HashMap<String, String[]>();
		for (final Object o : JsonUtils.toObject(cp.getParameter("json"), List.class)) {
			final Map<?, ?> map = (Map<?, ?>) o;
			participantIds.put((String) map.get("transition"),
					StringUtils.split((String) map.get("participant")));
		}
		activityComplete.resetParticipants(participantIds);

		final ComponentParameter nCP = WorkitemCompleteUtils.get(cp);
		final IWorkitemCompleteHandler hdl = (IWorkitemCompleteHandler) nCP.getComponentHandler();
		hdl.complete(nCP, workitemComplete);

		final JavascriptForward js = new JavascriptForward();
		js.append("$Actions['participantManualWindow'].close();");
		js.append("$Actions['transitionManualWindow'].close();");
		final String jsCallback = hdl.jsCompleteCallback(nCP);
		if (StringUtils.hasText(jsCallback)) {
			js.append(jsCallback);
		}
		return js;
	}
}
