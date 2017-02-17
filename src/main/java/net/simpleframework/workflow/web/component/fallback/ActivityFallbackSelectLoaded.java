package net.simpleframework.workflow.web.component.fallback;

import net.simpleframework.ctx.trans.Transaction;
import net.simpleframework.mvc.DefaultPageHandler;
import net.simpleframework.mvc.IForward;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.component.AbstractComponentBean;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.base.ajaxrequest.AjaxRequestBean;
import net.simpleframework.mvc.component.base.ajaxrequest.DefaultAjaxRequestHandler;
import net.simpleframework.workflow.engine.IWorkflowContext;
import net.simpleframework.workflow.engine.IWorkflowContextAware;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class ActivityFallbackSelectLoaded extends DefaultPageHandler
		implements IWorkflowContextAware {

	@Override
	public void onBeforeComponentRender(final PageParameter pp) {
		super.onBeforeComponentRender(pp);

		final ComponentParameter nCP = ActivityFallbackUtils.get(pp);
		pp.addComponentBean(nCP.getComponentName() + "_Usernode_Select_OK", AjaxRequestBean.class)
				.setHandlerClass(UsernodeSelectAction.class)
				.setAttr("_ActivityFallback", nCP.componentBean);
	}

	public static class UsernodeSelectAction extends DefaultAjaxRequestHandler {

		@Transaction(context = IWorkflowContext.class)
		@Override
		public IForward ajaxProcess(final ComponentParameter cp) throws Exception {
			final ComponentParameter nCP = ComponentParameter.get(cp,
					(AbstractComponentBean) cp.componentBean.getAttr("_ActivityFallback"));
			return ((IActivityFallbackHandler) nCP.getComponentHandler()).doFallback(nCP,
					cp.getParameter("usernodeId"), cp.getBoolParameter("opt1"));
		}
	}
}
