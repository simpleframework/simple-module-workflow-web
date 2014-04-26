package net.simpleframework.workflow.web.page;

import static net.simpleframework.common.I18n.$m;
import net.simpleframework.ctx.trans.Transaction;
import net.simpleframework.mvc.JavascriptForward;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.ElementList;
import net.simpleframework.mvc.common.element.InputElement;
import net.simpleframework.mvc.common.element.RowField;
import net.simpleframework.mvc.common.element.SpanElement;
import net.simpleframework.mvc.common.element.TableRow;
import net.simpleframework.mvc.common.element.TableRows;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.base.validation.EValidatorMethod;
import net.simpleframework.mvc.component.base.validation.ValidationBean;
import net.simpleframework.mvc.component.base.validation.Validator;
import net.simpleframework.mvc.template.lets.FormTableRowTemplatePage;
import net.simpleframework.workflow.engine.EWorkitemStatus;
import net.simpleframework.workflow.engine.IWorkflowContext;
import net.simpleframework.workflow.engine.IWorkflowServiceAware;
import net.simpleframework.workflow.engine.ProcessBean;
import net.simpleframework.workflow.engine.WorkitemBean;
import net.simpleframework.workflow.engine.WorkitemComplete;
import net.simpleframework.workflow.web.IWorkflowWebContext;
import net.simpleframework.workflow.web.IWorkflowWebForm;
import net.simpleframework.workflow.web.component.complete.WorkitemCompleteBean;
import net.simpleframework.workflow.web.page.t1.WorkflowCompleteInfoPage;
import net.simpleframework.workflow.web.page.t1.WorkflowFormPage;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public abstract class AbstractWorkflowFormTPage extends FormTableRowTemplatePage implements
		IWorkflowWebForm, IWorkflowServiceAware {

	@Override
	protected boolean isPage404(final PageParameter pp) {
		return getWorkitemBean(pp) == null;
	}

	@Override
	protected void onForward(final PageParameter pp) {
		super.onForward(pp);

		// 完成
		addComponentBean(pp, "AbstractWorkflowFormPage_completeAction", WorkitemCompleteBean.class)
				.setSelector(getFormSelector());

		// 验证
		addFormValidationBean(pp);

		final WorkitemBean workitem = getWorkitemBean(pp);
		if (workitem != null && !workitem.isReadMark()) {
			wService.doReadMark(workitem);
		}
	}

	@Override
	protected ValidationBean addFormValidationBean(final PageParameter pp) {
		return super.addFormValidationBean(pp).addValidators(
				new Validator(EValidatorMethod.required, "#wf_topic"));
	}

	@Transaction(context = IWorkflowContext.class)
	protected void onSaveForm(final PageParameter pp, final WorkitemBean workitem) {
		final ProcessBean process = getProcess(workitem);
		pService.doUpdateTitle(process, pp.getParameter("wf_topic"));
	}

	@Override
	public JavascriptForward onComplete(final PageParameter pp,
			final WorkitemComplete workitemComplete) {
		final WorkitemBean workitem = getWorkitemBean(pp);
		onSaveForm(pp, workitem);
		return new JavascriptForward("$Actions.loc('").append(
				((IWorkflowWebContext) workflowContext).getUrlsFactory().getUrl(pp,
						WorkflowCompleteInfoPage.class, workitem)).append("');");
	}

	@Override
	public JavascriptForward onSave(final ComponentParameter cp) throws Exception {
		final WorkitemBean workitem = getWorkitemBean(cp);
		onSaveForm(cp, workitem);
		return new JavascriptForward("$Actions.loc('").append(
				((IWorkflowWebContext) workflowContext).getUrlsFactory().getUrl(cp,
						WorkflowFormPage.class, workitem)).append("');");
	}

	protected ProcessBean getProcess(final PageParameter pp) {
		return getProcess(getWorkitemBean(pp));
	}

	protected ProcessBean getProcess(final WorkitemBean workitem) {
		return aService.getProcessBean(wService.getActivity(workitem));
	}

	@Override
	public String getFormForward(final PageParameter pp) {
		return url(getClass());
	}

	@Override
	public ElementList getLeftElements(final PageParameter pp) {
		return ElementList.of(InputElement.hidden().setName("workitemId").setValue(pp));
	}

	@Override
	public ElementList getRightElements(final PageParameter pp) {
		final WorkitemBean workitem = getWorkitemBean(pp);
		final ElementList el = ElementList.of();
		if (!isReadonly(workitem)) {
			el.append(SAVE_BTN().setText($m("AbstractWorkflowFormPage.0")).setHighlight(false));
			el.append(SpanElement.SPACE);
			el.append(VALIDATION_BTN().setText($m("AbstractWorkflowFormPage.1")).setHighlight(true)
					.setOnclick("$Actions['AbstractWorkflowFormPage_completeAction']();"));
		}
		return el;
	}

	@Override
	public boolean isButtonsOnTop(final PageParameter pp) {
		return true;
	}

	protected InputElement wf_topic = new InputElement("wf_topic");

	protected InputElement wf_description = InputElement.textarea("wf_description").setRows(5);

	@Override
	protected TableRows getTableRows(final PageParameter pp) {
		final ProcessBean process = getProcess(pp);
		wf_topic.setText(process.getTitle());

		final TableRow r1 = new TableRow(new RowField($m("AbstractWorkflowFormPage.2"), wf_topic));
		final TableRow r2 = new TableRow(new RowField($m("AbstractWorkflowFormPage.3"),
				wf_description));
		return TableRows.of(r1, r2);
	}

	@Override
	public String toTableRowsString(final PageParameter pp) {
		final WorkitemBean workitem = getWorkitemBean(pp);
		final TableRows tableRows = getTableRows(pp).setReadonly(isReadonly(workitem));
		return tableRows != null ? tableRows.toString() : "";
	}

	private boolean isReadonly(final WorkitemBean workitem) {
		final EWorkitemStatus status = workitem.getStatus();
		return status != EWorkitemStatus.running && status != EWorkitemStatus.suspended
				&& status != EWorkitemStatus.delegate;
	}

	protected WorkitemBean getWorkitemBean(final PageParameter pp) {
		return WorkflowUtils.getWorkitemBean(pp);
	}
}