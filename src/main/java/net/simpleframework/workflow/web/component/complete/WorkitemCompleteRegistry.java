package net.simpleframework.workflow.web.component.complete;

import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.component.AbstractComponentBean;
import net.simpleframework.mvc.component.AbstractComponentRegistry;
import net.simpleframework.mvc.component.ComponentBean;
import net.simpleframework.mvc.component.ComponentName;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ComponentRender;
import net.simpleframework.mvc.component.ComponentResourceProvider;
import net.simpleframework.mvc.component.base.ajaxrequest.AjaxRequestBean;
import net.simpleframework.mvc.component.ui.window.WindowBean;

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

	@Override
	public AbstractComponentBean createComponentBean(final PageParameter pp, final Object attriData) {
		final WorkitemCompleteBean workitemComplete = (WorkitemCompleteBean) super
				.createComponentBean(pp, attriData);

		final ComponentParameter nCP = ComponentParameter.get(pp, workitemComplete);
		final String componentName = nCP.getComponentName();

		// 手动路由
		AjaxRequestBean ajaxRequest = pp.addComponentBean(componentName + "_transitionSelect_page",
				AjaxRequestBean.class).setUrlForward(
				getComponentResourceProvider().getResourceHomePath() + "/jsp/transition_select.jsp");
		pp.addComponentBean(componentName + "_transitionSelect", WindowBean.class)
				.setContentRef(ajaxRequest.getName()).setTitle("选择路由分支").setHeight(450).setWidth(320);

		// 手动参与者
		ajaxRequest = pp.addComponentBean(componentName + "_participantSelect_page",
				AjaxRequestBean.class).setUrlForward(
				getComponentResourceProvider().getResourceHomePath() + "/jsp/participant_select.jsp");
		pp.addComponentBean(componentName + "_participantSelect", WindowBean.class)
				.setContentRef(ajaxRequest.getName()).setTitle("选择参与人").setHeight(450).setWidth(320);

		return workitemComplete;
	}
}
