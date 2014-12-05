package net.simpleframework.workflow.web.component.complete;

import java.util.HashMap;
import java.util.Map;

import net.simpleframework.common.JsonUtils;
import net.simpleframework.common.StringUtils;
import net.simpleframework.mvc.DefaultPageHandler;
import net.simpleframework.mvc.IForward;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.base.ajaxrequest.AjaxRequestBean;
import net.simpleframework.mvc.component.base.ajaxrequest.DefaultAjaxRequestHandler;
import net.simpleframework.workflow.engine.ActivityComplete;
import net.simpleframework.workflow.engine.WorkitemBean;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class ParticipantSelectLoaded extends DefaultPageHandler {

	@Override
	public void onBeforeComponentRender(final PageParameter pp) {
		super.onBeforeComponentRender(pp);

		pp.addComponentBean("ParticipantSelectLoaded_ok", AjaxRequestBean.class).setHandlerClass(
				ParticipantSelectAction.class);
	}

	public static class ParticipantSelectAction extends DefaultAjaxRequestHandler {

		@Override
		public Object getBeanProperty(final ComponentParameter cp, final String beanProperty) {
			if ("selector".equals(beanProperty)) {
				final ComponentParameter nCP = WorkitemCompleteUtils.get(cp);
				return nCP.getBeanProperty("selector");
			}
			return super.getBeanProperty(cp, beanProperty);
		}

		@Override
		public IForward ajaxProcess(final ComponentParameter cp) throws Exception {
			final ComponentParameter nCP = WorkitemCompleteUtils.get(cp);
			final WorkitemBean workitem = WorkitemCompleteUtils.getWorkitemBean(nCP);

			final Map<String, String[]> participantIds = new HashMap<String, String[]>();
			for (final Object o : JsonUtils.toList(nCP.getParameter("json"))) {
				final Map<?, ?> map = (Map<?, ?>) o;
				participantIds.put((String) map.get("transition"),
						StringUtils.split((String) map.get("participant")));
			}

			final ActivityComplete aComplete = WorkitemCompleteUtils
					.getActivityComplete(nCP, workitem);
			aComplete.resetParticipants(participantIds);
			return ((IWorkitemCompleteHandler) nCP.getComponentHandler()).onComplete(nCP, workitem);
		}
	}
}
