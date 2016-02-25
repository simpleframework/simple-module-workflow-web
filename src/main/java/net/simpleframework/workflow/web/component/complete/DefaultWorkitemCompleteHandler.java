package net.simpleframework.workflow.web.component.complete;

import net.simpleframework.ctx.trans.Transaction;
import net.simpleframework.mvc.JavascriptForward;
import net.simpleframework.mvc.component.AbstractComponentHandler;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.workflow.engine.IWorkflowContext;
import net.simpleframework.workflow.engine.WorkitemComplete;
import net.simpleframework.workflow.engine.bean.WorkitemBean;
import net.simpleframework.workflow.web.IWorkflowWebForm;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class DefaultWorkitemCompleteHandler extends AbstractComponentHandler implements
		IWorkitemCompleteHandler {

	@Transaction(context = IWorkflowContext.class)
	@Override
	public JavascriptForward onComplete(final ComponentParameter cp, final WorkitemBean workitem)
			throws Exception {
		final WorkitemComplete workitemComplete = WorkitemComplete.get(workitem);
		final IWorkflowWebForm form = (IWorkflowWebForm) workitemComplete.getWorkflowForm();
		form.doUpdateProcessKV(cp);
		workitemComplete.complete(cp.map());
		return form.onComplete(cp, workitemComplete);
	}
}
