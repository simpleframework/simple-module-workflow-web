package net.simpleframework.workflow.web.component.modellist;

import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.workflow.engine.IWorkflowContextAware;
import net.simpleframework.workflow.engine.ProcessBean;
import net.simpleframework.workflow.engine.WorkitemBean;
import net.simpleframework.workflow.web.component.action.startprocess.DefaultStartProcessHandler;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class MyStartProcessHandler extends DefaultStartProcessHandler implements
		IWorkflowContextAware {

	@Override
	public String jsStartProcessCallback(final ComponentParameter cp, final ProcessBean process) {
		final WorkitemBean workitem = context.getProcessService().getFirstWorkitem(process);
		if (workitem != null) {
			final StringBuilder sb = new StringBuilder();
			// sb.append("$Actions.loc('").append(AbstractMVCPage.uriFor(MyWorklistPage.class))
			// .append("');");
			cp.getSession().setAttribute(WorkitemBean.workitemId, workitem.getId());
			return sb.toString();
		} else {
			return null;
		}
	}
}
