package net.simpleframework.workflow.web.component.startprocess;

import net.simpleframework.mvc.IForward;
import net.simpleframework.mvc.JavascriptForward;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.component.AbstractComponentBean;
import net.simpleframework.mvc.component.AbstractComponentRegistry;
import net.simpleframework.mvc.component.ComponentBean;
import net.simpleframework.mvc.component.ComponentName;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ComponentRender;
import net.simpleframework.mvc.component.ComponentResourceProvider;
import net.simpleframework.mvc.component.base.ajaxrequest.AjaxRequestBean;
import net.simpleframework.mvc.component.base.ajaxrequest.DefaultAjaxRequestHandler;
import net.simpleframework.mvc.component.ui.window.WindowBean;
import net.simpleframework.workflow.engine.IWorkflowContextAware;
import net.simpleframework.workflow.engine.InitiateItem;

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
public class StartProcessRegistry extends AbstractComponentRegistry implements
		IWorkflowContextAware {
	public static final String STARTPROCESS = "wf_start_process";

	@Override
	public AbstractComponentBean createComponentBean(final PageParameter pp, final Object attriData) {
		final StartProcessBean startProcess = (StartProcessBean) super.createComponentBean(pp,
				attriData);

		final ComponentParameter nCP = ComponentParameter.get(pp, startProcess);
		final String componentName = nCP.getComponentName();

		// 启动流程
		pp.addComponentBean(componentName + "_startProcess", AjaxRequestBean.class)
				.setHandleClass(StartProcessHandler.class).setAttr("_startProcess", startProcess);

		// 路由选择
		AjaxRequestBean ajaxRequest = pp.addComponentBean(componentName + "__transitionSelect_page",
				AjaxRequestBean.class).setUrlForward(
				getComponentResourceProvider().getResourceHomePath() + "/jsp/transition_select.jsp");
		pp.addComponentBean(componentName + "_transitionSelect", WindowBean.class)
				.setContentRef(ajaxRequest.getName()).setTitle("路由选择").setWidth(480).setHeight(320);

		// 角色选择
		ajaxRequest = pp.addComponentBean(componentName + "__initiatorSelect_page",
				AjaxRequestBean.class).setUrlForward(
				getComponentResourceProvider().getResourceHomePath() + "/jsp/initiator_select.jsp");
		pp.addComponentBean(componentName + "_initiatorSelect", WindowBean.class)
				.setContentRef(ajaxRequest.getName()).setTitle("以其它角色启动").setWidth(320).setHeight(400);

		return startProcess;
	}

	public static class StartProcessHandler extends DefaultAjaxRequestHandler {

		@Override
		public IForward ajaxProcess(final ComponentParameter cp) throws Exception {
			final StartProcessBean startProcess = (StartProcessBean) cp.componentBean
					.getAttr("_startProcess");
			final ComponentParameter nCP = startProcess != null ? ComponentParameter.get(cp,
					startProcess) : StartProcessUtils.get(cp);
			final InitiateItem initiateItem = StartProcessUtils.getInitiateItem(nCP);

			((IStartProcessHandler) nCP.getComponentHandler()).onInit(nCP, initiateItem);
			initiateItem.doTransitions();

			final boolean transitionManual = initiateItem.isTransitionManual();
			if (transitionManual) {
				return new JavascriptForward("$Actions['").append(nCP.getComponentName())
						.append("_transitionSelect']('")
						.append(StartProcessUtils.toParams(nCP, initiateItem)).append("');");
			}

			return StartProcessUtils.doStartProcess(nCP, initiateItem);
		}
	}
}
