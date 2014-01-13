package net.simpleframework.workflow.web;

import java.util.Map;

import net.simpleframework.mvc.JavascriptForward;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.ElementList;
import net.simpleframework.mvc.common.element.InputElement;
import net.simpleframework.mvc.common.element.SpanElement;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.template.lets.FormTableRowTemplatePage;
import net.simpleframework.workflow.engine.IWorkflowContextAware;
import net.simpleframework.workflow.engine.IWorkflowForm;
import net.simpleframework.workflow.engine.ProcessBean;
import net.simpleframework.workflow.engine.WorkitemBean;
import net.simpleframework.workflow.engine.WorkitemComplete;
import net.simpleframework.workflow.web.component.complete.WorkitemCompleteBean;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public abstract class AbstractWorkflowFormPage extends FormTableRowTemplatePage implements
		IWorkflowForm, IWorkflowContextAware {

	@Override
	protected void onForward(final PageParameter pp) {
		super.onForward(pp);

		// 完成
		addComponentBean(pp, "AbstractWorkflowFormPage_completeAction", WorkitemCompleteBean.class)
				.setSelector(getFormSelector());
	}

	@Override
	public void onComplete(final Map<String, String> parameters,
			final WorkitemComplete workitemComplete) {
		onSave(parameters, workitemComplete.getWorkitem());
	}

	protected void onSave(final Map<String, String> parameters, final WorkitemBean workitem) {
	}

	@Override
	public JavascriptForward onSave(final ComponentParameter cp) throws Exception {
		// onSave(HttpUtils.map(cp.request), getWorkitem(getWorkitemId(cp)));
		// return new
		// JavascriptForward("$Actions['myWorklist'].refresh(); alert('").append(
		// $m("MyWorklistForm.0")).append("');");
		return super.onSave(cp);
	}

	protected ProcessBean getProcess(final PageParameter pp) {
		return getProcess(getWorkitemBean(pp));
	}

	protected ProcessBean getProcess(final WorkitemBean workitem) {
		return context.getActivityService().getProcessBean(
				context.getWorkitemService().getActivity(workitem));
	}

	@Override
	public String getFormForward() {
		return url(getClass());
	}

	@Override
	public void bindVariables(final Map<String, Object> variables) {
	}

	@Override
	public ElementList getLeftElements(final PageParameter pp) {
		return ElementList.of(InputElement.hidden().setName("workitemId").setValue(pp));
	}

	@Override
	public ElementList getRightElements(final PageParameter pp) {
		return ElementList.of(
				SAVE_BTN().setText("暂存").setHighlight(false),
				SpanElement.SPACE,
				VALIDATION_BTN().setText("完成").setHighlight(true)
						.setOnclick("$Actions['AbstractWorkflowFormPage_completeAction']();"));
	}

	public static WorkitemBean getWorkitemBean(final PageParameter pp) {
		return getCacheBean(pp, context.getWorkitemService(), "workitemId");
	}
}