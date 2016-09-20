package net.simpleframework.workflow.web.component.startprocess;

import static net.simpleframework.common.I18n.$m;

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
@ComponentName(StartProcessRegistry.STARTPROCESS)
@ComponentBean(StartProcessBean.class)
@ComponentRender(StartProcessRender.class)
@ComponentResourceProvider(StartProcessResourceProvider.class)
public class StartProcessRegistry extends AbstractComponentRegistry {

	public static final String STARTPROCESS = "wf_start_process";

	@Override
	public AbstractComponentBean createComponentBean(final PageParameter pp,
			final Object attriData) {
		final StartProcessBean startProcess = (StartProcessBean) super.createComponentBean(pp,
				attriData);

		final ComponentParameter nCP = ComponentParameter.get(pp, startProcess);
		final String componentName = nCP.getComponentName();

		// 启动流程
		pp.addComponentBean(componentName + "_startProcess", AjaxRequestBean.class)
				.setHandlerClass(StartProcessAction.class);

		// 路由选择
		AjaxRequestBean ajaxRequest = pp
				.addComponentBean(componentName + "__TransitionSelect_page", AjaxRequestBean.class)
				.setUrlForward(getComponentResourceProvider().getResourceHomePath()
						+ "/jsp/transition_select.jsp");
		pp.addComponentBean(componentName + "_TransitionSelect", WindowBean.class)
				.setContentRef(ajaxRequest.getName()).setTitle($m("StartProcessRegistry.0"))
				.setWidth(320).setHeight(400);

		// 角色选择
		ajaxRequest = pp
				.addComponentBean(componentName + "__initiatorSelect_page", AjaxRequestBean.class)
				.setUrlForward(getComponentResourceProvider().getResourceHomePath()
						+ "/jsp/initiator_select.jsp");
		pp.addComponentBean(componentName + "_initiatorSelect", WindowBean.class)
				.setContentRef(ajaxRequest.getName()).setTitle($m("StartProcessRegistry.1"))
				.setWidth(320).setHeight(400);

		return startProcess;
	}
}
