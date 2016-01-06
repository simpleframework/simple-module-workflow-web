package net.simpleframework.workflow.web.component.abort;

import java.util.List;

import net.simpleframework.ctx.trans.Transaction;
import net.simpleframework.mvc.JavascriptForward;
import net.simpleframework.mvc.component.AbstractComponentHandler;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.workflow.engine.IWorkflowContext;
import net.simpleframework.workflow.engine.IWorkflowContextAware;
import net.simpleframework.workflow.engine.bean.ActivityBean;
import net.simpleframework.workflow.engine.bean.ProcessBean;
import net.simpleframework.workflow.web.WorkflowUtils;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class DefaultActivityAbortHandler extends AbstractComponentHandler implements
		IActivityAbortHandler, IWorkflowContextAware {

	@Override
	public List<ActivityBean> getActivities(final ComponentParameter cp) {
		final ProcessBean process = WorkflowUtils.getProcessBean(cp);
		return process != null ? workflowContext.getActivityService().getActivities(process) : null;
	}

	@Transaction(context = IWorkflowContext.class)
	@Override
	public JavascriptForward doAbort(final ComponentParameter cp, final List<ActivityBean> list) {
		for (final ActivityBean activity : list) {
			wfaService.doAbort(activity);
		}
		return new JavascriptForward("$Actions['" + cp.getComponentName() + "_win'].close();");
	}
}