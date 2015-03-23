package net.simpleframework.workflow.web.page;

import static net.simpleframework.common.I18n.$m;

import java.util.Date;

import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.common.StringUtils;
import net.simpleframework.ctx.trans.Transaction;
import net.simpleframework.mvc.AbstractMVCPage;
import net.simpleframework.mvc.IForward;
import net.simpleframework.mvc.JavascriptForward;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.BlockElement;
import net.simpleframework.mvc.common.element.ElementList;
import net.simpleframework.mvc.common.element.LinkButton;
import net.simpleframework.mvc.common.element.LinkElement;
import net.simpleframework.mvc.common.element.Option;
import net.simpleframework.mvc.common.element.SpanElement;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ui.menu.EMenuEvent;
import net.simpleframework.mvc.component.ui.menu.MenuBean;
import net.simpleframework.mvc.component.ui.menu.MenuItem;
import net.simpleframework.mvc.component.ui.pager.ITablePagerHandler;
import net.simpleframework.mvc.component.ui.pager.TablePagerBean;
import net.simpleframework.mvc.component.ui.pager.TablePagerColumn;
import net.simpleframework.workflow.engine.DelegationBean;
import net.simpleframework.workflow.engine.EWorkitemStatus;
import net.simpleframework.workflow.engine.IWorkflowContext;
import net.simpleframework.workflow.engine.WorkitemBean;
import net.simpleframework.workflow.web.WorkflowLogRef.WorkitemUpdateLogPage;
import net.simpleframework.workflow.web.WorkflowUtils;
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

	protected Class<? extends ITablePagerHandler> getWorklistTbl(final PageParameter pp) {
		return MyRunningWorklistTbl.class;
	}

	protected void addComponents(final PageParameter pp) {
		final TablePagerBean tablePager = addTablePagerBean(pp, getWorklistTbl(pp)).setShowCheckbox(
				true);
		tablePager.addColumn(TablePagerColumn.ICON().setWidth(18));
		tablePager.addColumn(TC_TITLE());
		tablePager.addColumn(new TablePagerColumn("userFrom", $m("MyRunningWorklistTPage.0"), 115)
				.setFilterSort(false).setNowrap(false));
		tablePager.addColumn(new TablePagerColumn("createDate", $m("MyRunningWorklistTPage.1"), 90)
				.setPropertyClass(Date.class));
		tablePager.addColumn(new TablePagerColumn("status", $m("AbstractWorkitemsTPage.3"), 55) {
			@Override
			protected Option[] getFilterOptions() {
				return Option.from(EWorkitemStatus.running, EWorkitemStatus.delegate,
						EWorkitemStatus.suspended);
			};
		}.setPropertyClass(EWorkitemStatus.class)).addColumn(TablePagerColumn.OPE().setWidth(70));

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
		MenuBean mb = createMarkMenuComponent(pp);
		mb.addItem(MyRunningWorklistTbl.MENU_MARK_READ())
				.addItem(MyRunningWorklistTbl.MENU_MARK_UNREAD())
				.addItem(MyRunningWorklistTbl.MENU_MARK_ALLREAD());
		mb.addItem(MenuItem.sep()).addItem(MyRunningWorklistTbl.MENU_MARK_TOP())
				.addItem(MyRunningWorklistTbl.MENU_MARK_UNTOP());
		// 标记已读
		addAjaxRequest(pp, "MyWorklistTPage_readMark").setHandlerMethod("doReadMark");
		// 标记置顶
		addAjaxRequest(pp, "MyWorklistTPage_topMark").setHandlerMethod("doTopMark");

		// 查看菜单
		mb = createViewMenuComponent(pp);
		mb.addItem(
				MyRunningWorklistTbl.MENU_VIEW_ALL().setOnclick(
						"$Actions['MyWorklistTPage_tbl']('v=');")).addItem(
				MyRunningWorklistTbl.MENU_MARK_UNREAD().setOnclick(
						"$Actions['MyWorklistTPage_tbl']('v=unread');"));

		// 委托菜单
		mb = createDelegateMenuComponent(pp);
		mb.addItem(
				MenuItem
						.of($m("MyRunningWorklistTPage.6"))
						.setOnclick(
								"$Actions['MyWorklistTPage_tbl'].doAct('MyWorklistTPage_delegate', 'workitemId');"))
				.addItem(MenuItem.sep())
				.addItem(
						MenuItem.of($m("MyRunningWorklistTPage.7")).setOnclick(
								"$Actions['MyWorklistTPage_delegate']('delegationSource=user');"));

		// 更多操作
		mb = createOpeMenuComponent(pp);
		mb.addItem(MenuItem.of($m("MyRunningWorklistTbl.16")).setOnclick(
				"$Actions['MyWorklistTPage_tbl'].doAct('MyWorklistTPage_delete', 'workitemId');"));
	}

	protected TablePagerBean addTablePagerBean(final PageParameter pp,
			final Class<? extends ITablePagerHandler> handlerClass) {
		return super.addTablePagerBean(pp, "MyWorklistTPage_tbl", handlerClass);
	}

	@Override
	protected Class<? extends AbstractMVCPage> getUpdateLogPage() {
		return WorkitemUpdateLogPage.class;
	}

	protected MenuBean createMarkMenuComponent(final PageParameter pp) {
		// 标记菜单
		return (MenuBean) addComponentBean(pp, "MyWorklistTPage_markMenu", MenuBean.class)
				.setMenuEvent(EMenuEvent.click).setSelector("#idMyWorklistTPage_markMenu");
	}

	protected MenuBean createViewMenuComponent(final PageParameter pp) {
		return (MenuBean) addComponentBean(pp, "MyWorklistTPage_viewMenu", MenuBean.class)
				.setMenuEvent(EMenuEvent.click).setSelector("#idMyWorklistTPage_viewMenu");
	}

	protected MenuBean createDelegateMenuComponent(final PageParameter pp) {
		return (MenuBean) addComponentBean(pp, "MyWorklistTPage_delegateMenu", MenuBean.class)
				.setMenuEvent(EMenuEvent.click).setSelector("#idMyWorklistTPage_delegateMenu");
	}

	protected MenuBean createOpeMenuComponent(final PageParameter pp) {
		return (MenuBean) addComponentBean(pp, "MyWorklistTPage_opeMenu", MenuBean.class)
				.setMenuEvent(EMenuEvent.click).setSelector("#idMyWorklistTPage_opeMenu");
	}

	@Override
	public String toToolbarHTML(final PageParameter pp) {
		final StringBuilder sb = new StringBuilder();
		final DelegationBean delegation = dService.queryRunningDelegation(pp.getLoginId());
		if (delegation != null) {
			addAjaxRequest(pp, "MyRunningWorklistTPage_user_undelegate").setConfirmMessage(
					$m("MyRunningWorklistTPage.11")).setHandlerMethod("doUserUndelegate");

			final StringBuilder txt = new StringBuilder();
			txt.append($m("MyRunningWorklistTPage.8",
					new SpanElement(pp.getUser(delegation.getUserId())).setStrong(true)));
			txt.append(SpanElement.SPACE15);
			txt.append(new LinkElement($m("MyRunningWorklistTPage.10")).setHref(getUrlsFactory()
					.getUrl(pp, UserDelegateListTPage.class)));
			txt.append(SpanElement.SPACE);
			txt.append(new LinkElement($m("MyRunningWorklistTPage.9"))
					.setOnclick("$Actions['MyRunningWorklistTPage_user_undelegate']();"));
			sb.append(new BlockElement().setClassName("worklist_tip").setText(txt.toString()));
		}
		sb.append(super.toToolbarHTML(pp));
		return sb.toString();
	}

	@Override
	public ElementList getLeftElements(final PageParameter pp) {
		return ElementList.of(
				LinkButton.menu($m("MyRunningWorklistTbl.6")).setId("idMyWorklistTPage_markMenu"),
				LinkButton.menu($m("MyRunningWorklistTbl.14")).setId("idMyWorklistTPage_viewMenu"),
				SpanElement.SPACE,
				LinkButton.menu($m("MyRunningWorklistTbl.5")).setId("idMyWorklistTPage_delegateMenu"),
				LinkButton.menu($m("MyRunningWorklistTbl.17")).setId("idMyWorklistTPage_opeMenu"));
	}

	@Transaction(context = IWorkflowContext.class)
	public IForward doUserUndelegate(final ComponentParameter cp) {
		final DelegationBean delegation = dService.queryRunningDelegation(cp.getLoginId());
		if (delegation != null) {
			dService.doAbort(delegation);
		}
		return JavascriptForward.RELOC;
	}

	@Transaction(context = IWorkflowContext.class)
	public IForward doReadMark(final ComponentParameter cp) {
		final String op = cp.getParameter("op");
		if ("allread".equals(op)) {
			final IDataQuery<WorkitemBean> dq = wService.getRunningWorklist(cp.getLoginId());
			WorkitemBean workitem;
			while ((workitem = dq.next()) != null) {
				if (!workitem.isReadMark()) {
					wService.doReadMark(workitem);
				}
			}
		} else {
			for (final Object id : StringUtils.split(cp.getParameter("workitemId"))) {
				final WorkitemBean workitem = wService.getBean(id);
				if ("unread".equals(op)) {
					wService.doUnReadMark(workitem);
				} else {
					wService.doReadMark(workitem);
				}
			}
		}
		return new JavascriptForward("$Actions['MyWorklistTPage_tbl']();");
	}

	public IForward doTopMark(final ComponentParameter cp) {
		for (final Object id : StringUtils.split(cp.getParameter("workitemId"))) {
			final WorkitemBean workitem = wService.getBean(id);
			if ("untop".equals(cp.getParameter("op"))) {
				wService.doUnTopMark(workitem);
			} else {
				wService.doTopMark(workitem);
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
		for (final String workitemId : StringUtils.split(cp.getParameter("workitemId"))) {
			wService.doDeleteProcess(wService.getBean(workitemId));
		}
		return new JavascriptForward("$Actions['MyWorklistTPage_tbl']();");
	}
}