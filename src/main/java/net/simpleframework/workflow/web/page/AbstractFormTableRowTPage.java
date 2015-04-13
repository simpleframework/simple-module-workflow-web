package net.simpleframework.workflow.web.page;

import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.template.lets.FormTableRowTemplatePage;
import net.simpleframework.workflow.engine.ActivityBean;
import net.simpleframework.workflow.engine.IWorkflowServiceAware;
import net.simpleframework.workflow.engine.ProcessBean;
import net.simpleframework.workflow.engine.ProcessModelBean;
import net.simpleframework.workflow.engine.WorkitemBean;
import net.simpleframework.workflow.schema.AbstractTaskNode;
import net.simpleframework.workflow.schema.ProcessDocument;
import net.simpleframework.workflow.schema.ProcessNode;
import net.simpleframework.workflow.web.WorkflowUtils;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class AbstractFormTableRowTPage extends FormTableRowTemplatePage implements
		IWorkflowServiceAware {

	public String getForwardUrl(final PageParameter pp) {
		return url(getClass());
	}

	@Override
	public boolean isButtonsOnTop(final PageParameter pp) {
		return true;
	}

	protected WorkitemBean getWorkitemBean(final PageParameter pp) {
		return WorkflowUtils.getWorkitemBean(pp);
	}

	protected ActivityBean getActivityBean(final PageParameter pp) {
		return pp.getRequestCache("$ActivityBean", new IVal<ActivityBean>() {
			@Override
			public ActivityBean get() {
				return wService.getActivity(getWorkitemBean(pp));
			}
		});
	}

	protected ProcessBean getProcessBean(final PageParameter pp) {
		return pp.getRequestCache("$ProcessBean", new IVal<ProcessBean>() {
			@Override
			public ProcessBean get() {
				return wService.getProcessBean(getWorkitemBean(pp));
			}
		});
	}

	protected ProcessModelBean getProcessModel(final PageParameter pp) {
		return pp.getRequestCache("$ProcessModelBean", new IVal<ProcessModelBean>() {
			@Override
			public ProcessModelBean get() {
				return mService.getBean(getProcessBean(pp).getModelId());
			}
		});
	}

	protected AbstractTaskNode getTaskNode(final PageParameter pp) {
		return pp.getRequestCache("$TaskNode", new IVal<AbstractTaskNode>() {
			@Override
			public AbstractTaskNode get() {
				return aService.getTaskNode(getActivityBean(pp));
			}
		});
	}

	protected ProcessNode getProcessNode(final PageParameter pp) {
		return pp.getRequestCache("$ProcessNode", new IVal<ProcessNode>() {
			@Override
			public ProcessNode get() {
				final ProcessDocument doc = pService.getProcessDocument(getProcessBean(pp));
				return doc == null ? null : doc.getProcessNode();
			}
		});
	}

	protected String getProcessNodeProperty(final PageParameter pp, final String key) {
		final ProcessNode node = getProcessNode(pp);
		return node == null ? null : node.getProperty(key);
	}

	protected String getTaskNodeProperty(final PageParameter pp, final String key) {
		final AbstractTaskNode node = getTaskNode(pp);
		return node == null ? null : node.getProperty(key);
	}
}
