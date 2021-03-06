package net.simpleframework.workflow.web.page.list.delegate;

import static net.simpleframework.common.I18n.$m;

import java.util.Date;
import java.util.Iterator;

import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.ado.query.IteratorDataQuery;
import net.simpleframework.common.StringUtils;
import net.simpleframework.ctx.permission.PermissionUser;
import net.simpleframework.ctx.trans.Transaction;
import net.simpleframework.mvc.IForward;
import net.simpleframework.mvc.JavascriptForward;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.ButtonElement;
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
import net.simpleframework.mvc.component.ext.userselect.DefaultUserSelectHandler;
import net.simpleframework.mvc.component.ext.userselect.UserSelectBean;
import net.simpleframework.mvc.component.ui.autocomplete.AbstractAutocompleteHandler;
import net.simpleframework.mvc.component.ui.autocomplete.AutocompleteData;
import net.simpleframework.mvc.template.lets.FormTableRowTemplatePage;
import net.simpleframework.workflow.WorkflowException;
import net.simpleframework.workflow.engine.EDelegationSource;
import net.simpleframework.workflow.engine.IWorkflowContext;
import net.simpleframework.workflow.engine.bean.DelegationBean;
import net.simpleframework.workflow.engine.bean.WorkitemBean;
import net.simpleframework.workflow.web.IWorkflowWebContext;
import net.simpleframework.workflow.web.WorkflowUtils;
import net.simpleframework.workflow.web.page.IWorkflowPageAware;
import net.simpleframework.workflow.web.page.t1.form.WorkflowFormPage;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public abstract class AbstractDelegateFormPage extends FormTableRowTemplatePage
		implements IWorkflowPageAware {

	@Override
	public String getLabelWidth(final PageParameter pp) {
		return "75px";
	}

	@Override
	protected String getPageCSS(final PageParameter pp) {
		return "AbstractDelegateFormPage";
	}

	protected InputElement createDescElement() {
		return InputElement.textarea("wd_description");
	}

	public static class WorkitemDelegateSetPage extends AbstractDelegateFormPage {

		@Override
		protected void onForward(final PageParameter pp) throws Exception {
			super.onForward(pp);

			addFormValidationBean(pp).addValidators(
					new Validator(EValidatorMethod.required, "#wd_userTxt, #wd_description"));

			addCalendarBean(pp, "WorkitemDelegatePage_cal").setShowTime(true)
					.setDateFormat("yyyy-MM-dd HH:mm");

			// 用户选取
			addComponentBean(pp, "WorkitemDelegatePage_userSelect", UserSelectBean.class)
					.setShowGroupOpt(false).setShowTreeOpt(false).setBindingId("wd_userId")
					.setBindingText("wd_userTxt").setHandlerClass(
							((IWorkflowWebContext) workflowContext).getDelegate_UserSelectHandler(pp));
			// 自动完成
			addUserAutocompleteBean(pp, "WorkitemDelegatePage_autocomplete").setSepChar(null)
					.setInputField("wd_userTxt").setHandlerClass(Delegate_AutocompleteHandler.class);
		}

		@Transaction(context = IWorkflowContext.class)
		@Override
		public JavascriptForward onSave(final ComponentParameter cp) throws Exception {
			final String wd_userId = cp.getParameter("wd_userId");
			PermissionUser user = null;
			if (StringUtils.hasText(wd_userId)) {
				user = cp.getUser(wd_userId);
			} else {
				String txt = cp.getParameter("wd_userTxt");
				if (StringUtils.hasText(txt)) {
					txt = txt.trim();
					final int ps = txt.indexOf("(");
					final int pe = txt.indexOf(")");
					if (ps > -1 && pe > ps) {
						txt = txt.substring(ps + 1, pe);
					}
					user = cp.getUser(txt);
				}
				if (user == null) {
					throw WorkflowException.of($m("AbstractDelegateFormPage.3", txt));
				}
			}

			final Date wd_startDate = cp.getDateParameter("wd_startDate");
			final Date wd_endDate = cp.getDateParameter("wd_endDate");
			final String wd_description = cp.getParameter("wd_description");

			if ("user".equals(cp.getParameter("delegationSource"))) {
				wfdService.doUserDelegation(cp.getLoginId(), user.getId(), wd_startDate, wd_endDate,
						wd_description);
				return JavascriptForward.reloc();
			} else {
				for (final String workitemId : StringUtils.split(cp.getParameter("workitemId"))) {
					wfwService.doWorkitemDelegation(wfwService.getBean(workitemId), cp.getLoginId(),
							user.getId(), wd_startDate, wd_endDate, wd_description);
				}
				return super.onSave(cp).append("$Actions['MyWorklistTPage_tbl']();");
			}
		}

		@Override
		protected TableRows getTableRows(final PageParameter pp) {
			final InputElement delegationSource = InputElement.hidden("delegationSource")
					.setText(pp.getParameter("delegationSource"));
			final InputElement workitemId = InputElement.hidden("workitemId")
					.setText(pp.getParameter("workitemId"));

			final TextButton wd_userTxt = new TextButton("wd_userTxt").setEditable(true)
					.setHiddenField("wd_userId")
					.setOnclick("$Actions['WorkitemDelegatePage_userSelect']();");

			final CalendarInput wd_startDate = new CalendarInput("wd_startDate")
					.setCalendarComponent("WorkitemDelegatePage_cal");
			final CalendarInput wd_endDate = new CalendarInput("wd_endDate")
					.setCalendarComponent("WorkitemDelegatePage_cal");

			final TableRow r1 = new TableRow(new RowField($m("WorkitemDelegateSetPage.0"),
					delegationSource, workitemId, wd_userTxt).setStarMark(true));
			final TableRow r2 = new TableRow(
					new RowField($m("WorkitemDelegateSetPage.1"), wd_startDate),
					new RowField($m("WorkitemDelegateSetPage.2"), wd_endDate));
			final TableRow r3 = new TableRow(
					new RowField($m("WorkitemDelegateSetPage.3"), createDescElement())
							.setStarMark(true));
			return TableRows.of(r1, r2, r3);
		}
	}

	public static class WorkitemDelegateViewPage extends AbstractDelegateFormPage {

		protected DelegationBean _getDelegation(final PageParameter pp) {
			return wfdService.getBean(pp.getParameter("delegationId"));
		}

		protected RowField createUser(final PageParameter pp) {
			final DelegationBean delegation = _getDelegation(pp);
			return new RowField($m("WorkitemDelegateSetPage.0"),
					new InputElement().setText(delegation.getUserText()));
		}

		@Override
		public ElementList getRightElements(final PageParameter pp) {
			return ElementList.of(ButtonElement.closeBtn());
		}

		@Override
		public ElementList getLeftElements(final PageParameter pp) {
			final ElementList el = ElementList.of();
			final DelegationBean delegation = _getDelegation(pp);
			if (delegation.isTimeoutMark()) {
				el.append(new SpanElement($m("AbstractDelegateFormPage.0")).setClassName("status"));
			} else {
				el.append(new SpanElement($m("AbstractDelegateFormPage.1", delegation.getStatus()))
						.setClassName("status"));
			}
			return el;
		}

		@Override
		protected TableRows getTableRows(final PageParameter pp) {
			final DelegationBean delegation = _getDelegation(pp);

			final TableRow r1 = new TableRow(createUser(pp),
					new RowField($m("WorkitemDelegateReceivingPage.0"),
							new InputElement().setText(delegation.getCreateDate()))).setReadonly(true);

			final TableRow r2 = new TableRow(
					new RowField($m("WorkitemDelegateSetPage.1"),
							new InputElement().setText(delegation.getDstartDate())),
					new RowField($m("WorkitemDelegateSetPage.2"),
							new InputElement().setText(delegation.getDcompleteDate()))).setReadonly(true);

			final TableRow r3 = new TableRow(new RowField($m("WorkitemDelegateSetPage.3"),
					InputElement.textarea().setText(delegation.getDescription()))).setReadonly(true);

			final TableRows rows = TableRows.of(r1, r2, r3);
			if (delegation.getDelegationSource() == EDelegationSource.workitem) {
				rows.append(createRow4(delegation));
			}
			return rows;
		}

		protected TableRow createRow4(final DelegationBean delegation) {
			return new TableRow(new RowField($m("WorkitemDelegateSetPage.4"),
					InputElement.textarea().setText(delegation.getDescription2()).setReadonly(true)));
		}
	}

	public static class WorkitemDelegateReceivingPage extends WorkitemDelegateViewPage {
		@Override
		protected void onForward(final PageParameter pp) throws Exception {
			super.onForward(pp);

			addFormValidationBean(pp)
					.addValidators(new Validator(EValidatorMethod.required, "#wd_description"));

			addAjaxRequest(pp, "WorkitemDelegateReceivingPage_refuse")
					.setConfirmMessage($m("WorkitemDelegateReceivingPage.4"))
					.setHandlerMethod("doRefuse").setSelector(getFormSelector());
		}

		@Transaction(context = IWorkflowContext.class)
		@Override
		public JavascriptForward onSave(final ComponentParameter cp) throws Exception {
			final DelegationBean delegation = _getDelegation(cp);
			wfdService.doAccept(delegation, cp.getParameter("wd_description"));
			return super.onSave(cp).append("$Actions.loc('").append(uFactory.getUrl(cp,
					WorkflowFormPage.class, wfwService.getBean(delegation.getSourceId()))).append("');");
		}

		@Transaction(context = IWorkflowContext.class)
		public IForward doRefuse(final ComponentParameter cp) throws Exception {
			final DelegationBean delegation = _getDelegation(cp);
			wfdService.doRefuse(delegation, cp.getParameter("wd_description"));
			return super.onSave(cp).append("$Actions['MyWorklistTPage_tbl']();");
		}

		@Override
		public ElementList getRightElements(final PageParameter pp) {
			return ElementList.of(
					SAVE_BTN().setText($m("WorkitemDelegateReceivingPage.1"))
							.removeClassName("validation"),
					SpanElement.SPACE, VALIDATION_BTN($m("WorkitemDelegateReceivingPage.2"))
							.setOnclick("$Actions['WorkitemDelegateReceivingPage_refuse']();"));
		}

		@Override
		protected DelegationBean _getDelegation(final PageParameter pp) {
			return wfdService.queryRunningDelegation(WorkflowUtils.getWorkitemBean(pp));
		}

		@Override
		protected RowField createUser(final PageParameter pp) {
			final WorkitemBean workitem = WorkflowUtils.getWorkitemBean(pp);
			return new RowField($m("WorkitemDelegateSetPage.0"),
					InputElement.hidden("workitemId").setText(workitem.getId()),
					new InputElement().setText(workitem.getUserText()));
		}

		@Override
		protected TableRow createRow4(final DelegationBean delegation) {
			return new TableRow(
					new RowField($m("WorkitemDelegateReceivingPage.3"), createDescElement()));
		}
	}

	public static class Delegate_AutocompleteHandler extends AbstractAutocompleteHandler {

		@Override
		public Iterator<AutocompleteData> getData(final ComponentParameter cp, final String val,
				final String val2) {
			return newMIterator(cp, cp.getLdept().users(), val, val2);
		}
	}

	public static class Delegate_UserSelectHandler extends DefaultUserSelectHandler {
		@Override
		public IDataQuery<PermissionUser> getUsers(final ComponentParameter cp) {
			return new IteratorDataQuery<>(cp.getLdept().users());
		}
	}
}
