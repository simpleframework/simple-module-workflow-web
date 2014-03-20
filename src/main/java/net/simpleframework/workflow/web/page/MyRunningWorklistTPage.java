package net.simpleframework.workflow.web.page;

import static net.simpleframework.common.I18n.$m;

import java.util.Date;

import net.simpleframework.common.StringUtils;
import net.simpleframework.ctx.trans.Transaction;
import net.simpleframework.mvc.AbstractMVCPage;
import net.simpleframework.mvc.IForward;
import net.simpleframework.mvc.JavascriptForward;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.ETextAlign;
import net.simpleframework.mvc.common.element.ElementList;
import net.simpleframework.mvc.common.element.LinkButton;
import net.simpleframework.mvc.common.element.Option;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ui.menu.EMenuEvent;
import net.simpleframework.mvc.component.ui.menu.MenuBean;
import net.simpleframework.mvc.component.ui.menu.MenuItem;
import net.simpleframework.mvc.component.ui.pager.ITablePagerHandler;
import net.simpleframework.mvc.component.ui.pager.TablePagerBean;
import net.simpleframework.mvc.component.ui.pager.TablePagerColumn;
import net.simpleframework.workflow.engine.EWorkitemStatus;
import net.simpleframework.workflow.engine.IWorkflowContext;
import net.simpleframework.workflow.engine.WorkitemBean;
import net.simpleframework.workflow.web.WorkflowLogRef.WorkitemUpdateLogPage;
import net.simpleframework.workflow.web.page.AbstractDelegateFormPage.WorkitemDelegateReceivingPage;
import net.simpleframework.workflow.web.page.AbstractDelegateFormPage.WorkitemDelegateSetPage;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class MyRunningWorklistTPage extends AbstractWorkitemsTPage {

	@Override
	protected void onForward(final PageParameter pp) {
		super.onForward(pp);

		addComponents(pp);
	}

	protected void addComponents(final PageParameter pp) {
		final TablePagerBean tablePager = addTablePagerBean(pp, MyRunningWorklistTbl.class)
				.setShowCheckbox(true);
		tablePager.addColumn(TablePagerColumn.ICON().setWidth(18));
		tablePager.addColumn(TC_TITLE());
		tablePager.addColumn(new TablePagerColumn("userFrom", $m("MyRunningWorklistTPage.0"), 115)
				.setSort(false).setFilter(false).setNowrap(false));
		tablePager.addColumn(new TablePagerColumn("createDate", $m("MyRunningWorklistTPage.1"), 90)
				.setPropertyClass(Date.class));
		tablePager.addColumn(new TablePagerColumn("status", $m("AbstractWorkitemsTPage.3"), 55) {
			@Override
			protected Option[] getFilterOptions() {
				return Option.from(EWorkitemStatus.running, EWorkitemStatus.delegate,
						EWorkitemStatus.suspended);
			};
		}.setPropertyClass(EWorkitemStatus.class).setTextAlign(ETextAlign.left)).addColumn(
				TablePagerColumn.OPE().setWidth(70));

		// 回退
		addAjaxRequest(pp, "MyWorklistTPage_fallback").setHandlerMethod("doFallback")
				.setConfirmMessage($m("MyRunningWorklistTPage.3"));

		// 删除
		addAjaxRequest(pp, "MyWorklistTPage_delete").setHandlerMethod("doDelete").setConfirmMessage(
				$m("Confirm.Delete"));

		// 委托设置
		addAjaxRequest(pp, "MyWorklistTPage_delegate_page", WorkitemDelegateSetPage.class);
		addWindowBean(pp, "MyWorklistTPage_delegate").setContentRef("MyWorklistTPage_delegate_page")
				.setTitle($m("MyRunningWorklistTPage.4")).setHeight(300).setWidth(500);

		// 委托确认
		addAjaxRequest(pp, "MyWorklistTPage_delegate_receiving_page",
				WorkitemDelegateReceivingPage.class);
		addWindowBean(pp, "MyWorklistTPage_delegate_receiving")
				.setContentRef("MyWorklistTPage_delegate_receiving_page")
				.setTitle($m("MyRunningWorklistTPage.5")).setHeight(400).setWidth(500);

		// 标记菜单
		createMarkMenuComponent(pp);
		// 标记已读
		addAjaxRequest(pp, "MyWorklistTPage_readMark").setHandlerMethod("doReadMark");
		// 标记置顶
		addAjaxRequest(pp, "MyWorklistTPage_topMark").setHandlerMethod("doTopMark");
	}

	protected TablePagerBean addTablePagerBean(final PageParameter pp,
			final Class<? extends ITablePagerHandler> handlerClass) {
		return super.addTablePagerBean(pp, "MyWorklistTPage_tbl", handlerClass)
				.setShowFilterBar(true);
	}

	@Override
	protected Class<? extends AbstractMVCPage> getUpdateLogPage() {
		return WorkitemUpdateLogPage.class;
	}

	protected void createMarkMenuComponent(final PageParameter pp) {
		// 标记菜单
		final MenuBean mb = (MenuBean) addComponentBean(pp, "MyWorklistTPage_markMenu",
				MenuBean.class).setMenuEvent(EMenuEvent.click).setSelector(
				"#idMyWorklistTPage_markMenu");
		mb.addItem(MyRunningWorklistTbl.MENU_MARK_READ())
				.addItem(MyRunningWorklistTbl.MENU_MARK_UNREAD())
				.addItem(MyRunningWorklistTbl.MENU_MARK_ALLREAD());
		mb.addItem(MenuItem.sep()).addItem(MyRunningWorklistTbl.MENU_MARK_TOP())
				.addItem(MyRunningWorklistTbl.MENU_MARK_UNTOP());
	}

	@Override
	public ElementList getLeftElements(final PageParameter pp) {
		return ElementList.of(LinkButton.menu($m("MyRunningWorklistTbl.6")).setId(
				"idMyWorklistTPage_markMenu"));
	}

	@Transaction(context = IWorkflowContext.class)
	public IForward doReadMark(final ComponentParameter cp) {
		final String op = cp.getParameter("op");
		if ("allread".equals(op)) {
			for (final WorkitemBean workitem : wService.getRunningWorklist(cp.getLoginId())) {
				if (!workitem.isReadMark()) {
					wService.doReadMark(workitem);
				}
			}
		} else {
			final Object[] ids = StringUtils.split(cp.getParameter("workitemId"));
			if (ids != null) {
				for (final Object id : ids) {
					final WorkitemBean workitem = wService.getBean(id);
					if ("unread".equals(op)) {
						wService.doUnReadMark(workitem);
					} else {
						wService.doReadMark(workitem);
					}
				}
			}
		}
		return new JavascriptForward("$Actions['MyWorklistTPage_tbl']();");
	}

	public IForward doTopMark(final ComponentParameter cp) {
		final Object[] ids = StringUtils.split(cp.getParameter("workitemId"));
		if (ids != null) {
			for (final Object id : ids) {
				final WorkitemBean workitem = wService.getBean(id);
				if ("untop".equals(cp.getParameter("op"))) {
					wService.doUnTopMark(workitem);
				} else {
					wService.doTopMark(workitem);
				}
			}
		}
		return new JavascriptForward("$Actions['MyWorklistTPage_tbl']();");
	}

	@Transaction(context = IWorkflowContext.class)
	public IForward doFallback(final ComponentParameter cp) {
		aService.doFallback(wService.getActivity(WorkflowUtils.getWorkitemBean(cp)));
		return new JavascriptForward("$Actions['MyWorklistTPage_tbl']();");
	}

	@Transaction(context = IWorkflowContext.class)
	public IForward doDelete(final ComponentParameter cp) {
		wService.doDeleteProcess(WorkflowUtils.getWorkitemBean(cp));
		return new JavascriptForward("$Actions['MyWorklistTPage_tbl']();");
	}
}