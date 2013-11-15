package net.simpleframework.workflow.web.component.activitylist;

import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.PageRequestResponse;
import net.simpleframework.mvc.component.ComponentUtils;
import net.simpleframework.workflow.engine.EActivityStatus;
import net.simpleframework.workflow.engine.IWorkflowContextAware;
import net.simpleframework.workflow.engine.WorkitemBean;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public abstract class ActivityListUtils implements IWorkflowContextAware {

	static String getStatusIcon(final PageParameter pp, final EActivityStatus status) {
		final StringBuilder sb = new StringBuilder();
		sb.append("<img style='height: 9px; width: 9px; margin-right: 5px;' src='");
		sb.append(ComponentUtils.getCssResourceHomePath(pp, ActivityListBean.class));
		sb.append("/images/dot_").append(status.name()).append(".png' />");
		return sb.toString();
	}

	public static String getWorkitemDetail(final PageRequestResponse rRequest) {
		final StringBuilder sb = new StringBuilder();
		final WorkitemBean workitem = context.getWorkitemService().getBean(
				rRequest.getParameter(WorkitemBean.workitemId));
		sb.append(workitem);
		return sb.toString();
	}
}
