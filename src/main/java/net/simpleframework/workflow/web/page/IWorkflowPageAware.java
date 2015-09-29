package net.simpleframework.workflow.web.page;

import net.simpleframework.workflow.engine.IWorkflowContextAware;
import net.simpleframework.workflow.web.IWorkflowWebContext;
import net.simpleframework.workflow.web.WorkflowUrlsFactory;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public interface IWorkflowPageAware extends IWorkflowContextAware {

	static final WorkflowUrlsFactory uFactory = ((IWorkflowWebContext) workflowContext)
			.getUrlsFactory();
}
