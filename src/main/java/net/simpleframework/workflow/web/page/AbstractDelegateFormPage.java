package net.simpleframework.workflow.web.page;

import static net.simpleframework.common.I18n.$m;
import net.simpleframework.common.StringUtils;
import net.simpleframework.ctx.permission.PermissionUser;
import net.simpleframework.ctx.trans.Transaction;
import net.simpleframework.module.common.DescriptionLogUtils;
import net.simpleframework.mvc.IForward;
import net.simpleframework.mvc.JavascriptForward;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.CalendarInput;
import net.simpleframework.mvc.common.element.ElementList;
import net.simpleframework.mvc.common.element.InputElement;
import net.simpleframework.mvc.common.element.RowField;
import net.simpleframework.mvc.common.element.SpanElement;
import net.simpleframework.mvc.common.element.TableRow;
import net.simpleframework.mvc.common.element.TableRows;
import net.simpleframework.mvc.common.element.TextButton;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.base.validation.EValidatorMethod;
import net.simpleframework.mvc.component.base.validation.Validator;
import net.simpleframework.mvc.component.ext.userselect.UserSelectBean;
import net.simpleframework.mvc.template.lets.FormTableRowTemplatePage;
import net.simpleframework.workflow.engine.DelegationBean;
import net.simpleframework.workflow.engine.IWorkflowContext;
import net.simpleframework.workflow.engine.IWorkflowContextAware;
import net.simpleframework.workflow.engine.WorkitemBean;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public abstract class AbstractDelegateFormPage extends FormTableRowTemplatePage implements
		IWorkflowContextAware {

	@Override
	public int getLabelWidth(final PageParameter pp) {
		return 75;
	}

	public static class WorkitemDelegateSetPage extends AbstractDelegateFormPage {

		@Override
		protected void onForward(final PageParameter pp) {
			super.onForward(pp);

			addFormValidationBean(pp).addValidators(
					new Validator(EValidatorMethod.required, "#wd_userTxt, #wd_description"));

			addCalendarBean(pp, "WorkitemDelegatePage_cal").setShowTime(true).setDateFormat(
					"yyyy-MM-dd HH:mm");

			// 用户选取
			addComponentBean(pp, "WorkitemDelegatePage_userSelect", UserSelectBean.class)
					.setBindingId("wd_userId").setBindingText("wd_userTxt");
			// 自动完成
			addUserAutocompleteBean(pp, "WorkitemDelegatePage_autocomplete").setInputField(
					"wd_userTxt");
		}

		@Transaction(context = IWorkflowContext.class)
		@Override
		public JavascriptForward onSave(final ComponentParameter cp) throws Exception {
			final WorkitemBean workitem = WorkflowUtils.getWorkitemBean(cp);

			final String wd_userId = cp.getParameter("wd_userId");
			final PermissionUser user = StringUtils.hasText(wd_userId) ? permission.getUser(wd_userId)
					: permission.getUser(cp.getParameter("wd_userTxt"));

			wService.setWorkitemDelegation(workitem, user.getId(),
					cp.getDateParameter("wd_startDate"), cp.getDateParameter("wd_startDate"),
					cp.getParameter("wd_description"));
			return super.onSave(cp).append("$Actions['MyWorklistTPage_tbl']();");
		}

		@Override
		protected TableRows getTableRows(final PageParameter pp) {
			final WorkitemBean workitem = WorkflowUtils.getWorkitemBean(pp);
			final InputElement workitemId = InputElement.hidden("workitemId")
					.setText(workitem.getId());

			final TextButton wd_userTxt = new TextButton("wd_userTxt").setEditable(true)
					.setHiddenField("wd_userId")
					.setOnclick("$Actions['WorkitemDelegatePage_userSelect']();");

			final CalendarInput wd_startDate = new CalendarInput("wd_startDate")
					.setCalendarComponent("WorkitemDelegatePage_cal");
			final CalendarInput wd_endDate = new CalendarInput("wd_endDate")
					.setCalendarComponent("WorkitemDelegatePage_cal");

			final InputElement wd_description = InputElement.textarea("wd_description").setRows(5);

			final TableRow r1 = new TableRow(new RowField($m("WorkitemDelegateSetPage.0"), workitemId,
					wd_userTxt).setStarMark(true));
			final TableRow r2 = new TableRow(new RowField($m("WorkitemDelegateSetPage.1"),
					wd_startDate), new RowField($m("WorkitemDelegateSetPage.2"), wd_endDate));
			final TableRow r3 = new TableRow(new RowField($m("WorkitemDelegateSetPage.3"),
					wd_description).setStarMark(true));
			return TableRows.of(r1, r2, r3);
		}
	}

	public static class WorkitemDelegateReceivingPage extends AbstractDelegateFormPage {

		@Override
		protected void onForward(final PageParameter pp) {
			super.onForward(pp);

			addFormValidationBean(pp).addValidators(
					new Validator(EValidatorMethod.required, "#wd_description"));

			addAjaxRequest(pp, "WorkitemDelegateReceivingPage_refuse")
					.setConfirmMessage($m("WorkitemDelegateReceivingPage.4"))
					.setHandlerMethod("doRefuse").setSelector(getFormSelector());
		}

		@Override
		public JavascriptForward onSave(final ComponentParameter cp) throws Exception {
			final DelegationBean delegation = dService.queryRunningDelegation(WorkflowUtils
					.getWorkitemBean(cp));
			dService.accept(delegation);
			return super.onSave(cp).append("$Actions['MyWorklistTPage_tbl']();");
		}

		public IForward doRefuse(final ComponentParameter cp) throws Exception {
			final DelegationBean delegation = dService.queryRunningDelegation(WorkflowUtils
					.getWorkitemBean(cp));
			DescriptionLogUtils.set(delegation, "");
			dService.abort(delegation);
			return super.onSave(cp).append("$Actions['MyWorklistTPage_tbl']();");
		}

		@Override
		public ElementList getRightElements(final PageParameter pp) {
			return ElementList.of(
					SAVE_BTN().setText($m("WorkitemDelegateReceivingPage.1")).removeClassName(
							"validation"),
					SpanElement.SPACE,
					VALIDATION_BTN().setText($m("WorkitemDelegateReceivingPage.2")).setOnclick(
							"$Actions['WorkitemDelegateReceivingPage_refuse']();"));
		}

		@Override
		protected TableRows getTableRows(final PageParameter pp) {
			final WorkitemBean workitem = WorkflowUtils.getWorkitemBean(pp);
			final DelegationBean delegation = dService.queryRunningDelegation(workitem);

			final TableRow r1 = new TableRow(new RowField($m("WorkitemDelegateSetPage.0"),
					InputElement.hidden("workitemId").setText(workitem.getId()),
					new InputElement().setText(workitem.getUserText())), new RowField(
					$m("WorkitemDelegateReceivingPage.0"), new InputElement().setText(delegation
							.getCreateDate()))).setReadonly(true);

			final TableRow r2 = new TableRow(new RowField($m("WorkitemDelegateSetPage.1"),
					new InputElement().setText(delegation.getDstartDate())), new RowField(
					$m("WorkitemDelegateSetPage.2"), new InputElement().setText(delegation
							.getDcompleteDate()))).setReadonly(true);

			final TableRow r3 = new TableRow(new RowField($m("WorkitemDelegateSetPage.3"),
					InputElement.textarea().setText(delegation.getDescription()))).setReadonly(true);

			final TableRow r4 = new TableRow(new RowField($m("WorkitemDelegateReceivingPage.3"),
					InputElement.textarea("wd_description")));
			return TableRows.of(r1, r2, r3, r4);
		}
	}
}
