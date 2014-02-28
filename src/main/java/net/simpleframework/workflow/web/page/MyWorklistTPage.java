package net.simpleframework.workflow.web.page;

import static net.simpleframework.common.I18n.$m;

import java.util.Date;

import net.simpleframework.ctx.trans.Transaction;
import net.simpleframework.mvc.IForward;
import net.simpleframework.mvc.JavascriptForward;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ui.pager.TablePagerBean;
import net.simpleframework.mvc.component.ui.pager.TablePagerColumn;
import net.simpleframework.workflow.engine.EWorkitemStatus;
import net.simpleframework.workflow.engine.IWorkflowContext;
import net.simpleframework.workflow.engine.WorkitemBean;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class MyWorklistTPage extends AbstractWorkitemsTPage {

	@Override
	protected void onForward(final PageParameter pp) {
		super.onForward(pp);

		final TablePagerBean tablePager = addTablePagerBean(pp, "MyWorklistTPage_tbl",
				MyWorklistTbl.class).setShowFilterBar(false).setShowLineNo(false);
		tablePager.addColumn(TablePagerColumn.ICON().setWidth(16));

		final EWorkitemStatus status = pp.getEnumParameter(EWorkitemStatus.class, "status");
		tablePager.addColumn(TITLE());
		if (status == EWorkitemStatus.complete) {
			tablePager.addColumn(new TablePagerColumn("userTo", $m("MyWorklistTPage.0"), 115)
					.setSort(false));
			tablePager.addColumn(new TablePagerColumn("completeDate", $m("MyWorklistTPage.1"), 115)
					.setPropertyClass(Date.class));
		} else {
			tablePager.addColumn(new TablePagerColumn("userFrom", $m("MyWorklistTPage.2"), 115)
					.setSort(false));
			tablePager.addColumn(new TablePagerColumn("createDate", $m("MyWorklistTPage.3"), 115)
					.setPropertyClass(Date.class));
		}
		tablePager.addColumn(TablePagerColumn.OPE().setWidth(70));

		// readMark
		addAjaxRequest(pp, "MyWorklistTPage_readMark").setHandlerMethod("doReadMark");
		// retake
		addAjaxRequest(pp, "MyWorklistTPage_retake").setHandlerMethod("doRetake").setConfirmMessage(
				$m("MyWorklistTPage.4"));
		// fallback
		addAjaxRequest(pp, "MyWorklistTPage_fallback").setHandlerMethod("doFallback")
				.setConfirmMessage($m("MyWorklistTPage.5"));
		// delete
		addAjaxRequest(pp, "MyWorklistTPage_delete").setHandlerMethod("doDelete").setConfirmMessage(
				$m("Confirm.Delete"));

		// 委托
		addAjaxRequest(pp, "MyWorklistTPage_delegate_page", WorkitemDelegatePage.class);
		addWindowBean(pp, "MyWorklistTPage_delegate").setContentRef("MyWorklistTPage_delegate_page")
				.setTitle($m("MyWorklistTbl.5")).setHeight(300).setWidth(510);
	}

	@Transaction(context = IWorkflowContext.class)
	public IForward doReadMark(final ComponentParameter cp) {
		final WorkitemBean workitem = WorkflowUtils.getWorkitemBean(cp);
		wService.readMark(workitem, workitem.isReadMark() ? true : false);
		return new JavascriptForward("$Actions['MyWorklistTPage_tbl']();");
	}

	@Transaction(context = IWorkflowContext.class)
	public IForward doRetake(final ComponentParameter cp) {
		wService.retake(WorkflowUtils.getWorkitemBean(cp));
		return new JavascriptForward("$Actions['MyWorklistTPage_tbl']();");
	}

	@Transaction(context = IWorkflowContext.class)
	public IForward doFallback(final ComponentParameter cp) {
		aService.fallback(wService.getActivity(WorkflowUtils.getWorkitemBean(cp)));
		return new JavascriptForward("$Actions['MyWorklistTPage_tbl']();");
	}

	@Transaction(context = IWorkflowContext.class)
	public IForward doDelete(final ComponentParameter cp) {
		wService.deleteProcess(WorkflowUtils.getWorkitemBean(cp));
		return new JavascriptForward("$Actions['MyWorklistTPage_tbl']();");
	}
}