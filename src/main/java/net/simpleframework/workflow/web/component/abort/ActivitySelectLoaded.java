package net.simpleframework.workflow.web.component.abort;

import java.util.ArrayList;
import java.util.List;

import net.simpleframework.common.StringUtils;
import net.simpleframework.mvc.DefaultPageHandler;
import net.simpleframework.mvc.IForward;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.base.ajaxrequest.AjaxRequestBean;
import net.simpleframework.mvc.component.base.ajaxrequest.DefaultAjaxRequestHandler;
import net.simpleframework.workflow.engine.IWorkflowContextAware;
import net.simpleframework.workflow.engine.bean.ActivityBean;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class ActivitySelectLoaded extends DefaultPageHandler implements IWorkflowContextAware {

	@Override
	public void onBeforeComponentRender(final PageParameter pp) {
		super.onBeforeComponentRender(pp);

		final ComponentParameter nCP = ActivityAbortUtils.get(pp);
		pp.addComponentBean(nCP.getComponentName() + "_ActivitySelect_OK", AjaxRequestBean.class)
				.setHandlerClass(ActivitySelectAction.class);
	}

	public static class ActivitySelectAction extends DefaultAjaxRequestHandler {

		@Override
		public IForward ajaxProcess(final ComponentParameter cp) throws Exception {
			final ComponentParameter nCP = ActivityAbortUtils.get(cp);
			final List<ActivityBean> list = new ArrayList<ActivityBean>();
			final String[] activityIds = StringUtils.split(cp.getParameter("activityIds"), ";");
			if (activityIds != null) {
				for (final String id : activityIds) {
					final ActivityBean activity = wfaService.getBean(id);
					if (activity != null) {
						list.add(activity);
					}
				}
			}
			return ((IActivityAbortHandler) nCP.getComponentHandler()).doAbort(nCP, list);
		}
	}
}
