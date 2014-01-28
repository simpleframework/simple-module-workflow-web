package net.simpleframework.workflow.web.page;

import static net.simpleframework.common.I18n.$m;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.CalendarInput;
import net.simpleframework.mvc.common.element.InputElement;
import net.simpleframework.mvc.common.element.RowField;
import net.simpleframework.mvc.common.element.TableRow;
import net.simpleframework.mvc.common.element.TableRows;
import net.simpleframework.mvc.common.element.TextButton;
import net.simpleframework.mvc.component.base.validation.EValidatorMethod;
import net.simpleframework.mvc.component.base.validation.Validator;
import net.simpleframework.mvc.component.ext.userselect.UserSelectBean;
import net.simpleframework.mvc.template.lets.FormTableRowTemplatePage;
import net.simpleframework.workflow.engine.IWorkflowContextAware;

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

		addCalendarBean(pp, "WorkitemDelegatePage_cal");

		// 用户选取
		addComponentBean(pp, "WorkitemDelegatePage_userSelect", UserSelectBean.class).setBindingId(
				"wd_userId").setBindingText("wd_userTxt");
	}

	@Override
	public int getLabelWidth(final PageParameter pp) {
		return 75;
	}

	@Override
	protected TableRows getTableRows(final PageParameter pp) {
		final TextButton wd_userTxt = new TextButton("wd_userTxt").setHiddenField("wd_userId")
				.setOnclick("$Actions['WorkitemDelegatePage_userSelect']();");

		final CalendarInput wd_startDate = new CalendarInput("wd_startDate")
				.setCalendarComponent("WorkitemDelegatePage_cal");
		final CalendarInput wd_endDate = new CalendarInput("wd_endDate")
				.setCalendarComponent("WorkitemDelegatePage_cal");

		final InputElement wd_description = InputElement.textarea("wd_description").setRows(5);

		final TableRow r1 = new TableRow(
				new RowField($m("WorkitemDelegatePage.0"), wd_userTxt).setStarMark(true));
		final TableRow r2 = new TableRow(new RowField($m("WorkitemDelegatePage.1"), wd_startDate),
				new RowField($m("WorkitemDelegatePage.2"), wd_endDate));
		final TableRow r3 = new TableRow(
				new RowField($m("WorkitemDelegatePage.3"), wd_description).setStarMark(true));
		return TableRows.of(r1, r2, r3);
	}
}
