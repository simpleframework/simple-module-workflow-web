package net.simpleframework.workflow.web.page;

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
import net.simpleframework.workflow.web.AbstractWorkflowFormPage;

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
		tablePager.addColumn(new TablePagerColumn("title", "流程主题").setTextAlign(ETextAlign.left)
				.setSort(false));
		if (status == EWorkitemStatus.complete) {
			tablePager.addColumn(new TablePagerColumn("userTo", "接收人", 115).setSort(false));
			tablePager.addColumn(new TablePagerColumn("completeDate", "完成日期", 115)
					.setPropertyClass(Date.class));
		} else {
			tablePager.addColumn(new TablePagerColumn("userFrom", "发送人", 115).setSort(false));
			tablePager.addColumn(new TablePagerColumn("createDate", "创建日期", 115)
					.setPropertyClass(Date.class));
		}
		tablePager.addColumn(TablePagerColumn.OPE().setWidth(70));

		// readMark
		addAjaxRequest(pp, "MyWorklistTPage_action").setHandleMethod("doAction");

		// 委托
		addAjaxRequest(pp, "MyWorklistTPage_delegate_page", WorkitemDelegatePage.class);
		addWindowBean(pp, "MyWorklistTPage_delegate").setContentRef("MyWorklistTPage_delegate_page")
				.setTitle("委托").setHeight(400).setWidth(320);
	}

	@Override
	public ElementList getRightElements(final PageParameter pp) {
		GroupDbTablePagerHandler.setDefaultGroupVal(pp, "MyWorklistTPage_tbl", "modelname");

		return ElementList.of(createGroupElement(pp, "MyWorklistTPage_tbl", new Option("modelname",
				"按模型分组"), new Option("taskname", "按环节分组")));
	}

	public IForward doAction(final ComponentParameter cp) {
		final WorkitemBean workitem = AbstractWorkflowFormPage.getWorkitemBean(cp);
		final String action = cp.getParameter("action");
		if ("readMark".equals(action)) {
			wService.readMark(workitem, workitem.isReadMark() ? true : false);
		} else if ("retake".equals(action)) {
			wService.retake(workitem);
		} else if ("fallback".equals(action)) {
			aService.fallback(wService.getActivity(workitem));
		} else if ("delete".equals(action)) {
			wService.deleteProcess(workitem);
		}
		return new JavascriptForward("$Actions['MyWorklistTPage_tbl']();");
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