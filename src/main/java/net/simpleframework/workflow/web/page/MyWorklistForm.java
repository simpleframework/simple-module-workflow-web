<<<<<<< HEAD
package net.simpleframework.workflow.web.page;

import static net.simpleframework.common.I18n.$m;

import java.util.Map;

import net.simpleframework.common.web.HttpUtils;
import net.simpleframework.mvc.IForward;
import net.simpleframework.mvc.JavascriptForward;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.InputElement;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.workflow.engine.WorkitemBean;
import net.simpleframework.workflow.engine.WorkitemComplete;
import net.simpleframework.workflow.web.AbstractWorkflowFormPage;
import net.simpleframework.workflow.web.component.action.complete.WorkitemCompleteBean;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public abstract class MyWorklistForm extends AbstractWorkflowFormPage {

	@Override
	protected void onForward(final PageParameter pp) {
		super.onForward(pp);
		addHtmlViewVariable(pp, getClass(), "worklist_form");
	}

	@Override
	protected void addComponents(final PageParameter pp) {
		super.addComponents(pp);

		addComponentBean(pp, "wf_completeAction", WorkitemCompleteBean.class).setSelector(
				"#idWorklistForm");
		addAjaxRequest(pp, "wf_saveAction").setHandleMethod("doSaveAction").setSelector(
				"#idWorklistForm");
	}

	@Override
	public void onComplete(final Map<String, String> parameters,
			final WorkitemComplete workitemComplete) {
		onSave(parameters, workitemComplete.getWorkitem());
	}

	protected void onSave(final Map<String, String> parameters, final WorkitemBean workitem) {
	}

	public IForward doSaveAction(final ComponentParameter cp) {
		onSave(HttpUtils.map(cp.request), getWorkitem(getWorkitemId(cp)));
		return new JavascriptForward("$Actions['myWorklist'].refresh(); alert('").append(
				$m("MyWorklistForm.0")).append("');");
	}

	public String buildForm(final PageParameter pp) {
		final StringBuilder sb = new StringBuilder();
		sb.append(InputElement.hidden().setName(WorkitemBean.workitemId)
				.setText(pp.getParameter(WorkitemBean.workitemId)));
		return sb.toString();
	}
}
=======
package net.simpleframework.workflow.web.page;

import static net.simpleframework.common.I18n.$m;

import java.util.Map;

import net.simpleframework.common.web.HttpUtils;
import net.simpleframework.mvc.IForward;
import net.simpleframework.mvc.JavascriptForward;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.InputElement;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.workflow.engine.WorkitemBean;
import net.simpleframework.workflow.engine.WorkitemComplete;
import net.simpleframework.workflow.web.AbstractWorkflowFormPage;
import net.simpleframework.workflow.web.component.action.complete.WorkitemCompleteBean;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public abstract class MyWorklistForm extends AbstractWorkflowFormPage {

	@Override
	protected void onForward(final PageParameter pp) {
		super.onForward(pp);
		addHtmlViewVariable(getClass(), "worklist_form");
	}

	@Override
	protected void addComponents(final PageParameter pp) {
		super.addComponents(pp);

		addComponentBean(pp, "wf_completeAction", WorkitemCompleteBean.class).setSelector(
				"#idWorklistForm");
		addAjaxRequest(pp, "wf_saveAction").setHandleMethod("doSaveAction").setSelector(
				"#idWorklistForm");
	}

	@Override
	public void onComplete(final Map<String, String> parameters,
			final WorkitemComplete workitemComplete) {
		onSave(parameters, workitemComplete.getWorkitem());
	}

	protected void onSave(final Map<String, String> parameters, final WorkitemBean workitem) {
	}

	public IForward doSaveAction(final ComponentParameter cp) {
		onSave(HttpUtils.map(cp.request), getWorkitem(getWorkitemId(cp)));
		return new JavascriptForward("$Actions['myWorklist'].refresh(); alert('").append(
				$m("MyWorklistForm.0")).append("');");
	}

	public String buildForm(final PageParameter pp) {
		final StringBuilder sb = new StringBuilder();
		sb.append(InputElement.hidden().setName(WorkitemBean.workitemId)
				.setText(pp.getParameter(WorkitemBean.workitemId)));
		return sb.toString();
	}
}
>>>>>>> b408820dc36437d4cfc711a4e18caf2c220d66a8
