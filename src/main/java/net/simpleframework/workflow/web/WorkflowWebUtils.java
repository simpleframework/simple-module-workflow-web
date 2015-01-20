package net.simpleframework.workflow.web;

import net.simpleframework.workflow.engine.ActivityBean;
import net.simpleframework.workflow.engine.IWorkflowServiceAware;
import net.simpleframework.workflow.engine.participant.Participant;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public abstract class WorkflowWebUtils implements IWorkflowServiceAware {

	public static String getParticipants(final ActivityBean activity, final boolean r) {
		final StringBuilder sb = new StringBuilder();
		int i = 0;
		for (final Participant p : (r ? aService.getParticipants2(activity) : aService
				.getParticipants(activity, true))) {
			if (i++ > 0) {
				sb.append(", ");
			}
			sb.append(permission.getUser(p.userId).getText());
		}
		return sb.toString();
	}
}
