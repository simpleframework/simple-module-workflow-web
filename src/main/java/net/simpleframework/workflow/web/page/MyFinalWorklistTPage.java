package net.simpleframework.workflow.web.page;

import static net.simpleframework.common.I18n.$m;

import java.util.Date;

import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.ctx.trans.Transaction;
import net.simpleframework.mvc.IForward;
import net.simpleframework.mvc.JavascriptForward;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.ETextAlign;
import net.simpleframework.mvc.common.element.ElementList;
import net.simpleframework.mvc.common.element.ImageElement;
import net.simpleframework.mvc.common.element.LinkButton;
import net.simpleframework.mvc.common.element.Option;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ui.menu.MenuBean;
import net.simpleframework.mvc.component.ui.menu.MenuItem;
import net.simpleframework.mvc.component.ui.menu.MenuItems;
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
public class MyFinalWorklistTPage extends MyRunningWorklistTPage {

	@Override
	protected void addComponents(final PageParameter pp) {
		final TablePagerBean tablePager = addTablePagerBean(pp, MyCompleteWorklistTbl.class);
		tablePager.addColumn(TablePagerColumn.ICON().setWidth(18));
		tablePager.addColumn(TC_TITLE());
		tablePager.addColumn(new TablePagerColumn("userTo", $m("MyFinalWorklistTPage.0"), 115)
				.setSort(false).setFilter(false).setNowrap(false));
		tablePager.addColumn(new TablePagerColumn("completeDate", $m("MyFinalWorklistTPage.1"), 115)
				.setPropertyClass(Date.class));
		tablePager.addColumn(new TablePagerColumn("status", $m("AbstractWorkitemsTPage.3"), 55) {
			@Override
			protected Option[] getFilterOptions() {
				return Option.from(EWorkitemStatus.complete, EWorkitemStatus.retake,
						EWorkitemStatus.abort);
			};
		}.setPropertyClass(EWorkitemStatus.class).setTextAlign(ETextAlign.left)).addColumn(
				TablePagerColumn.OPE().setWidth(70));

		// 取回
		addAjaxRequest(pp, "MyWorklistTPage_retake").setHandlerMethod("doRetake").setConfirmMessage(
				$m("MyFinalWorklistTPage.2"));

		// 标记置顶
		addAjaxRequest(pp, "MyWorklistTPage_topMark").setHandlerMethod("doTopMark");

		final MenuBean mb = createViewMenuComponent(pp);
		mb.addItem(MyRunningWorklistTbl.MENU_VIEW_ALL());
	}

	@Transaction(context = IWorkflowContext.class)
	public IForward doRetake(final ComponentParameter cp) {
		wService.doRetake(WorkflowUtils.getWorkitemBean(cp));
		return new JavascriptForward("$Actions['MyWorklistTPage_tbl']();");
	}

	@Override
	public ElementList getLeftElements(final PageParameter pp) {
		return ElementList.of(LinkButton.menu($m("MyRunningWorklistTbl.14")).setId(
				"idMyWorklistTPage_viewMenu"));
	}

	public static class MyCompleteWorklistTbl extends MyRunningWorklistTbl {

		@Override
		public IDataQuery<?> createDataObjectQuery(final ComponentParameter cp) {
			return wService.getWorklist(cp.getLoginId(), EWorkitemStatus.complete,
					EWorkitemStatus.abort, EWorkitemStatus.retake);
		}

		@Override
		protected ImageElement createImageMark(final ComponentParameter cp,
				final WorkitemBean workitem) {
			ImageElement img = null;
			if (workitem.isTopMark()) {
				img = MARK_TOP(cp);
			}
			return img;
		}

		@Override
		public MenuItems getContextMenu(final ComponentParameter cp, final MenuBean menuBean,
				final MenuItem menuItem) {
			final MenuItems items = MenuItems.of();
			items.append(MENU_MONITOR(cp));
			items.append(MenuItem.sep());
			items.append(MenuItem.of($m("MyFinalWorklistTPage.3")).setIconClass("menu_retake")
					.setOnclick_act("MyWorklistTPage_retake", "workitemId"));
			items.append(MenuItem.sep());
			final MenuItem mItems = MenuItem.of($m("MyRunningWorklistTbl.6"));
			mItems.addChild(
					MENU_MARK_TOP().setOnclick_act("MyWorklistTPage_topMark", "workitemId", "op=top"))
					.addChild(
							MENU_MARK_UNTOP().setOnclick_act("MyWorklistTPage_topMark", "workitemId",
									"op=untop"));
			items.append(mItems);
			items.append(MenuItem.sep()).append(MENU_LOG());
			return items;
		}
	}
}