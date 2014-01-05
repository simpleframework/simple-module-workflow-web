package net.simpleframework.workflow.web;

import net.simpleframework.module.log.LogRef;
import net.simpleframework.module.log.web.page.EntityUpdateLogPage;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.workflow.engine.IWorkflowContextAware;
import net.simpleframework.workflow.engine.ProcessModelBean;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class WorkflowLogRef extends LogRef implements IWorkflowContextAware {

	public static class ProcessModelUpdateLogPage extends EntityUpdateLogPage {

		@Override
		protected ProcessModelBean getBean(final PageParameter pp) {
			return getCacheBean(pp, context.getProcessModelService(), getBeanIdParameter());
		}

		@Override
		public String getBeanIdParameter() {
			return "modelId";
		}
	}
}
