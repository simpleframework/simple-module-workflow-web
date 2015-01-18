package net.simpleframework.workflow.web.component.abort;

import java.util.List;

import net.simpleframework.mvc.component.AbstractComponentHandler;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.workflow.engine.ActivityBean;
import net.simpleframework.workflow.engine.IWorkflowContextAware;
import net.simpleframework.workflow.engine.ProcessBean;

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
		final ProcessBean process = workflowContext.getProcessService().getBean(
				cp.getParameter("processId"));
		if (process != null) {
			return workflowContext.getActivityService().getActivities(process);
		}
		return null;
	}
}