package net.simpleframework.workflow.web.component.complete;

import static net.simpleframework.common.I18n.$m;
import net.simpleframework.mvc.IForward;
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
import net.simpleframework.workflow.engine.bean.WorkitemBean;
import net.simpleframework.workflow.web.WorkflowUtils;

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
		AjaxRequestBean ajaxRequest = pp.addComponentBean(componentName + "_TransitionSelect_page",
				AjaxRequestBean.class).setUrlForward(
				getComponentResourceProvider().getResourceHomePath() + "/jsp/transition_select.jsp");
		pp.addComponentBean(componentName + "_TransitionSelect", WindowBean.class)
				.setContentRef(ajaxRequest.getName()).setTitle($m("WorkitemCompleteRegistry.0"))
				.setHeight(450).setWidth(320);

		// 手动参与者
		ajaxRequest = pp.addComponentBean(componentName + "_ParticipantSelect_page",
				AjaxRequestBean.class).setUrlForward(
				getComponentResourceProvider().getResourceHomePath() + "/jsp/participant_select.jsp");
		pp.addComponentBean(componentName + "_ParticipantSelect", WindowBean.class)
				.setContentRef(ajaxRequest.getName()).setTitle($m("WorkitemCompleteRegistry.1"))
				.setHeight(450).setWidth(320);

		// 含有确认消息
		pp.addComponentBean(componentName + "_Comfirm", AjaxRequestBean.class)
				.setHandlerClass(ComfirmAction.class).setAttr("_workitemComplete", workitemComplete);
		return workitemComplete;
	}

	public static class ComfirmAction extends DefaultAjaxRequestHandler {
		@Override
		public Object getBeanProperty(final ComponentParameter cp, final String beanProperty) {
			if ("selector".equals(beanProperty)) {
				final ComponentParameter nCP = ComponentParameter.getByAttri(cp, "_workitemComplete");
				return nCP.getBeanProperty("selector");
			}
			return super.getBeanProperty(cp, beanProperty);
		}

		@Override
		public IForward ajaxProcess(final ComponentParameter cp) throws Exception {
			final ComponentParameter nCP = ComponentParameter.getByAttri(cp, "_workitemComplete");
			try {
				final WorkitemBean workitem = WorkflowUtils.getWorkitemBean(nCP);
				return ((IWorkitemCompleteHandler) nCP.getComponentHandler()).onComplete(nCP, workitem);
			} catch (final Throwable th) {
				return WorkitemCompleteUtils.createErrorForward(cp, th);
			}
		}
	}
}
