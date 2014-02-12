package net.simpleframework.workflow.web;

import net.simpleframework.ctx.service.ado.db.IDbBeanService;
import net.simpleframework.module.log.LogRef;
import net.simpleframework.module.log.web.page.EntityUpdateLogPage;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.workflow.engine.IWorkflowContextAware;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class WorkflowLogRef extends LogRef implements IWorkflowContextAware {

	public static class ProcessModelUpdateLogPage extends EntityUpdateLogPage {
		@Override
		protected IDbBeanService<?> getBeanService() {
			return mService;
		}

		@Override
		public String getBeanIdParameter(final PageParameter pp) {
			return "modelId";
		}
	}

	public static class ProcessUpdateLogPage extends EntityUpdateLogPage {
		@Override
		protected IDbBeanService<?> getBeanService() {
			return pService;
		}

		@Override
		public String getBeanIdParameter(final PageParameter pp) {
			return "processId";
		}
	}

	public static class ActivityUpdateLogPage extends EntityUpdateLogPage {
		@Override
		protected IDbBeanService<?> getBeanService() {
			return aService;
		}

		@Override
		public String getBeanIdParameter(final PageParameter pp) {
			return "activityId";
		}
	}

	public static class WorkitemUpdateLogPage extends EntityUpdateLogPage {
		@Override
		protected IDbBeanService<?> getBeanService() {
			return wService;
		}

		@Override
		public String getBeanIdParameter(final PageParameter pp) {
			return "workitemId";
		}
	}
}
