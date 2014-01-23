package net.simpleframework.workflow.web.component.complete;

import net.simpleframework.mvc.JavascriptForward;
import net.simpleframework.mvc.component.AbstractComponentHandler;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.workflow.engine.WorkitemComplete;
import net.simpleframework.workflow.web.IWorkflowWebForm;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class DefaultWorkitemCompleteHandler extends AbstractComponentHandler implements
		IWorkitemCompleteHandler {

	@Override
	public JavascriptForward onComplete(final ComponentParameter cp,
			final WorkitemComplete workitemComplete) {
		workitemComplete.complete(cp.map());
		return ((IWorkflowWebForm) workitemComplete.getWorkflowForm()).onComplete(cp,
				workitemComplete);
	}
}
