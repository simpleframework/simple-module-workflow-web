package net.simpleframework.workflow.web.page;

import static net.simpleframework.common.I18n.$m;

import java.util.Date;

import net.simpleframework.common.StringUtils;
import net.simpleframework.mvc.IForward;
import net.simpleframework.mvc.JavascriptForward;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.ETextAlign;
import net.simpleframework.mvc.common.element.ElementList;
import net.simpleframework.mvc.common.element.Option;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ui.pager.TablePagerBean;
import net.simpleframework.mvc.component.ui.pager.TablePagerColumn;
import net.simpleframework.mvc.component.ui.pager.db.GroupDbTablePagerHandler;
import net.simpleframework.workflow.engine.EWorkitemStatus;
import net.simpleframework.workflow.engine.WorkitemBean;
import net.simpleframework.workflow.web.AbstractWorkflowFormTPage;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class MyWorklistTPage extends AbstractWorkTPage {

	@Override
	protected void onForward(final PageParameter pp) {
		super.onForward(pp);

		final TablePagerBean tablePager = addTablePagerBean(pp, "MyWorklistTPage_tbl",
				MyWorklistTbl.class).setShowFilterBar(false);

		final EWorkitemStatus status = getWorkitemStatus(pp);
		tablePager.addColumn(new TablePagerColumn("title", $m("MyWorklistTPage.0")).setTextAlign(
				ETextAlign.left).setSort(false));
		if (status == EWorkitemStatus.complete) {
			tablePager.addColumn(new TablePagerColumn("userTo", $m("MyWorklistTPage.1"), 115)
					.setSort(false));
			tablePager.addColumn(new TablePagerColumn("completeDate", $m("MyWorklistTPage.2"), 115)
					.setPropertyClass(Date.class));
		} else {
			tablePager.addColumn(new TablePagerColumn("userFrom", $m("MyWorklistTPage.3"), 115)
					.setSort(false));
			tablePager.addColumn(new TablePagerColumn("createDate", $m("MyWorklistTPage.4"), 115)
					.setPropertyClass(Date.class));
		}
		tablePager.addColumn(TablePagerColumn.OPE().setWidth(70));

		// readMark
		addAjaxRequest(pp, "MyWorklistTPage_readMark").setHandleMethod("doReadMark");
		// retake
		addAjaxRequest(pp, "MyWorklistTPage_retake").setHandleMethod("doRetake").setConfirmMessage(
				$m("MyWorklistTPage.5"));
		// fallback
		addAjaxRequest(pp, "MyWorklistTPage_fallback").setHandleMethod("doFallback")
				.setConfirmMessage($m("MyWorklistTPage.6"));
		// delete
		addAjaxRequest(pp, "MyWorklistTPage_delete").setHandleMethod("doDelete").setConfirmMessage(
				$m("Confirm.Delete"));

		// 委托
		addAjaxRequest(pp, "MyWorklistTPage_delegate_page", WorkitemDelegatePage.class);
		addWindowBean(pp, "MyWorklistTPage_delegate").setContentRef("MyWorklistTPage_delegate_page")
				.setTitle($m("MyWorklistTbl.5")).setHeight(400).setWidth(320);
	}

	public IForward doReadMark(final ComponentParameter cp) {
		final WorkitemBean workitem = AbstractWorkflowFormTPage.getWorkitemBean(cp);
		wService.readMark(workitem, workitem.isReadMark() ? true : false);
		return new JavascriptForward("$Actions['MyWorklistTPage_tbl']();");
	}

	public IForward doRetake(final ComponentParameter cp) {
		wService.retake(AbstractWorkflowFormTPage.getWorkitemBean(cp));
		return new JavascriptForward("$Actions['MyWorklistTPage_tbl']();");
	}

	public IForward doFallback(final ComponentParameter cp) {
		aService.fallback(wService.getActivity(AbstractWorkflowFormTPage.getWorkitemBean(cp)));
		return new JavascriptForward("$Actions['MyWorklistTPage_tbl']();");
	}

	public IForward doDelete(final ComponentParameter cp) {
		wService.deleteProcess(AbstractWorkflowFormTPage.getWorkitemBean(cp));
		return new JavascriptForward("$Actions['MyWorklistTPage_tbl']();");
	}

	@Override
	public ElementList getRightElements(final PageParameter pp) {
		GroupDbTablePagerHandler.setDefaultGroupVal(pp, "MyWorklistTPage_tbl", "modelname");

		return ElementList.of(createGroupElement(pp, "MyWorklistTPage_tbl", new Option("modelname",
				$m("MyWorklistTPage.7")), new Option("taskname", $m("MyWorklistTPage.8"))));
	}

	static EWorkitemStatus getWorkitemStatus(final PageParameter pp) {
		final String status = pp.getParameter("status");
		if (!"false".equals(status)) {
			return StringUtils.hasText(status) ? EWorkitemStatus.valueOf(status)
					: EWorkitemStatus.running;
		}
		return null;
	}
}