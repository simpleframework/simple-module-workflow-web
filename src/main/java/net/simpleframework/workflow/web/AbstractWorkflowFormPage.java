package net.simpleframework.workflow.web;

import java.awt.Dimension;
import java.util.Map;

import net.simpleframework.common.coll.KVMap;
import net.simpleframework.common.object.ObjectUtils;
import net.simpleframework.mvc.AbstractMVCPage;
import net.simpleframework.mvc.IPageHandler.PageSelector;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.PageRequestResponse;
import net.simpleframework.mvc.template.AbstractTemplatePage;
import net.simpleframework.workflow.engine.IWorkflowContextAware;
import net.simpleframework.workflow.engine.IWorkflowForm;
import net.simpleframework.workflow.engine.ProcessBean;
import net.simpleframework.workflow.engine.WorkitemBean;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public abstract class AbstractWorkflowFormPage extends AbstractTemplatePage implements
		IWorkflowForm, IWorkflowContextAware {

	private String url;

	@Override
	protected void onForward(final PageParameter pp) {
		super.onForward(pp);

		getPageBean().setHandleClass(getClass().getName()).setHandleMethod("doPageLoad");
	}

	public void doPageLoad(final PageParameter pp, final Map<String, Object> dataBinding,
			final PageSelector selector) {
		final WorkitemBean workitem = getWorkitem(getWorkitemId(pp));
		if (!workitem.isReadMark()) {
			context.getWorkitemService().readMark(workitem, false);
		}
		onLoad(pp, dataBinding, selector, workitem);
	}

	protected abstract void onLoad(final PageParameter pp, final Map<String, Object> dataBinding,
			final PageSelector selector, final WorkitemBean workitem);

	@Override
	public String getFormForward() {
		if (url == null) {
			url = AbstractMVCPage.url(getClass());
		}
		return url;
	}

	@Override
	public void bindVariables(final KVMap variables) {
	}

	@Override
	public String getTitle() {
		return null;
	}

	@Override
	public Dimension getSize() {
		return null;
	}

	protected void updateProcessTitle(final WorkitemBean workitem, final String title) {
		final ProcessBean process = getProcess(workitem);
		if (!ObjectUtils.objectEquals(title, process.getTitle())) {
			context.getProcessService().setProcessTitle(process, title);
		}
	}

	protected ProcessBean getProcess(final WorkitemBean workitem) {
		return context.getActivityService().getProcessBean(
				context.getWorkitemService().getActivity(workitem));
	}

	protected Object getWorkitemId(final PageRequestResponse rRequest) {
		return rRequest.getParameter(WorkitemBean.workitemId);
	}

	protected WorkitemBean getWorkitem(final Object id) {
		return context.getWorkitemService().getBean(id);
	}
}