package net.simpleframework.workflow.web.component.startprocess;

import net.simpleframework.ctx.trans.Transaction;
import net.simpleframework.mvc.IForward;
import net.simpleframework.mvc.JavascriptForward;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.base.ajaxrequest.DefaultAjaxRequestHandler;
import net.simpleframework.workflow.engine.IWorkflowContext;
import net.simpleframework.workflow.engine.InitiateItem;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class StartProcessAction extends DefaultAjaxRequestHandler {

	@Transaction(context = IWorkflowContext.class)
	@Override
	public IForward ajaxProcess(final ComponentParameter cp) throws Exception {
		final ComponentParameter nCP = StartProcessUtils.get(cp);
		final InitiateItem initiateItem = StartProcessUtils.getInitiateItem(nCP);

		((IStartProcessHandler) nCP.getComponentHandler()).onInit(nCP, initiateItem);
		// 初始化路由
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