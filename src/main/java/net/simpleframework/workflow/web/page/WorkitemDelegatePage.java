package net.simpleframework.workflow.web.page;

import static net.simpleframework.common.I18n.$m;
import net.simpleframework.common.StringUtils;
import net.simpleframework.ctx.permission.PermissionUser;
import net.simpleframework.ctx.trans.Transaction;
import net.simpleframework.mvc.JavascriptForward;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.CalendarInput;
import net.simpleframework.mvc.common.element.InputElement;
import net.simpleframework.mvc.common.element.RowField;
import net.simpleframework.mvc.common.element.TableRow;
import net.simpleframework.mvc.common.element.TableRows;
import net.simpleframework.mvc.common.element.TextButton;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.base.validation.EValidatorMethod;
import net.simpleframework.mvc.component.base.validation.Validator;
import net.simpleframework.mvc.component.ext.userselect.UserSelectBean;
import net.simpleframework.mvc.template.lets.FormTableRowTemplatePage;
import net.simpleframework.workflow.engine.IWorkflowContext;
import net.simpleframework.workflow.engine.IWorkflowContextAware;
import net.simpleframework.workflow.engine.WorkitemBean;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class WorkitemDelegatePage extends FormTableRowTemplatePage implements IWorkflowContextAware {

	@Override
	protected void onForward(final PageParameter pp) {
		super.onForward(pp);

		addFormValidationBean(pp).addValidators(
				new Validator(EValidatorMethod.required, "#wd_userTxt, #wd_description"));

		addCalendarBean(pp, "WorkitemDelegatePage_cal").setShowTime(true).setDateFormat(
				"yyyy-MM-dd HH:mm");

		addUserAutocompleteBean(pp, "WorkitemDelegatePage_autocomplete").setInputField("wd_userTxt");

		// 用户选取
		addComponentBean(pp, "WorkitemDelegatePage_userSelect", UserSelectBean.class).setBindingId(
				"wd_userId").setBindingText("wd_userTxt");
	}

	@Transaction(context = IWorkflowContext.class)
	@Override
	public JavascriptForward onSave(final ComponentParameter cp) throws Exception {
		final WorkitemBean workitem = WorkflowUtils.getWorkitemBean(cp);

		final String wd_userId = cp.getParameter("wd_userId");
		final PermissionUser user = StringUtils.hasText(wd_userId) ? permission.getUser(wd_userId)
				: permission.getUser(cp.getParameter("wd_userTxt"));

		wService.setWorkitemDelegation(workitem, user.getId(), cp.getDateParameter("wd_startDate"),
				cp.getDateParameter("wd_startDate"), cp.getParameter("wd_description"));
		return super.onSave(cp).append("$Actions['MyWorklistTPage_tbl']();");
	}

	@Override
	public int getLabelWidth(final PageParameter pp) {
		return 75;
	}

	@Override
	protected TableRows getTableRows(final PageParameter pp) {
		final WorkitemBean workitem = WorkflowUtils.getWorkitemBean(pp);
		final InputElement workitemId = InputElement.hidden("workitemId").setText(workitem.getId());

		final TextButton wd_userTxt = new TextButton("wd_userTxt").setEditable(true)
				.setHiddenField("wd_userId")
				.setOnclick("$Actions['WorkitemDelegatePage_userSelect']();");

		final CalendarInput wd_startDate = new CalendarInput("wd_startDate")
				.setCalendarComponent("WorkitemDelegatePage_cal");
		final CalendarInput wd_endDate = new CalendarInput("wd_endDate")
				.setCalendarComponent("WorkitemDelegatePage_cal");

		final InputElement wd_description = InputElement.textarea("wd_description").setRows(5);

		final TableRow r1 = new TableRow(new RowField($m("WorkitemDelegatePage.0"), workitemId,
				wd_userTxt).setStarMark(true));
		final TableRow r2 = new TableRow(new RowField($m("WorkitemDelegatePage.1"), wd_startDate),
				new RowField($m("WorkitemDelegatePage.2"), wd_endDate));
		final TableRow r3 = new TableRow(
				new RowField($m("WorkitemDelegatePage.3"), wd_description).setStarMark(true));
		return TableRows.of(r1, r2, r3);
	}
}
