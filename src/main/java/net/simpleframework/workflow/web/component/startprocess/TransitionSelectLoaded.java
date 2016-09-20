package net.simpleframework.workflow.web.component.startprocess;

import net.simpleframework.common.StringUtils;
import net.simpleframework.ctx.trans.Transaction;
import net.simpleframework.mvc.DefaultPageHandler;
import net.simpleframework.mvc.IForward;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.base.ajaxrequest.AjaxRequestBean;
import net.simpleframework.mvc.component.base.ajaxrequest.DefaultAjaxRequestHandler;
import net.simpleframework.workflow.engine.IWorkflowContext;
import net.simpleframework.workflow.engine.InitiateItem;

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

		pp.addComponentBean("TransitionSelectLoaded_ok", AjaxRequestBean.class)
				.setHandlerClass(TransitionSelectAction.class);
	}

	public static class TransitionSelectAction extends DefaultAjaxRequestHandler {

		@Transaction(context = IWorkflowContext.class)
		@Override
		public IForward ajaxProcess(final ComponentParameter cp) throws Exception {
			final ComponentParameter nCP = StartProcessUtils.get(cp);
			final InitiateItem initiateItem = StartProcessUtils.getInitiateItem(nCP);

			initiateItem.resetTransitions(StringUtils.split(cp.getParameter("transitions")));

			return StartProcessUtils.doStartProcess(nCP, initiateItem);
		}
	}
}