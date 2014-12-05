package net.simpleframework.workflow.web.component.complete;

import net.simpleframework.common.StringUtils;
import net.simpleframework.mvc.DefaultPageHandler;
import net.simpleframework.mvc.IForward;
import net.simpleframework.mvc.JavascriptForward;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.base.ajaxrequest.AjaxRequestBean;
import net.simpleframework.workflow.engine.ActivityComplete;
import net.simpleframework.workflow.engine.WorkitemBean;
import net.simpleframework.workflow.web.component.complete.ParticipantSelectLoaded.ParticipantSelectAction;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class TransitionSelectLoaded extends DefaultPageHandler {

	@Override
	public void onBeforeComponentRender(final PageParameter pp) {
		super.onBeforeComponentRender(pp);

		pp.addComponentBean("TransitionSelectLoaded_ok", AjaxRequestBean.class).setHandlerClass(
				TransitionSelectAction.class);
	}

	public static class TransitionSelectAction extends ParticipantSelectAction {

		@Override
		public IForward ajaxProcess(final ComponentParameter cp) throws Exception {
			final ComponentParameter nCP = WorkitemCompleteUtils.get(cp);
			final WorkitemBean workitem = WorkitemCompleteUtils.getWorkitemBean(nCP);
			final String transitions = nCP.getParameter("transitions");
			final String[] transitionIds = StringUtils.split(transitions);
			final ActivityComplete aComplete = WorkitemCompleteUtils
					.getActivityComplete(nCP, workitem);
			if (aComplete.isParticipantManual(transitionIds)) {
				final JavascriptForward js = new JavascriptForward();
				js.append("$Actions['").append(nCP.getComponentName()).append("_participantSelect']('")
						.append(WorkitemCompleteUtils.toParams(nCP, workitem)).append("&transitions=")
						.append(transitions).append("');");
				return js;
			} else {
				aComplete.resetTransitions(transitionIds);
				return ((IWorkitemCompleteHandler) nCP.getComponentHandler()).onComplete(nCP, workitem);
			}
		}
	}
}
