package net.simpleframework.workflow.web.page;

import static net.simpleframework.common.I18n.$m;

import java.util.Date;

import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.web.HttpUtils;
import net.simpleframework.common.web.JavascriptUtils;
import net.simpleframework.ctx.trans.Transaction;
import net.simpleframework.mvc.AbstractMVCPage;
import net.simpleframework.mvc.IForward;
import net.simpleframework.mvc.JavascriptForward;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.BlockElement;
import net.simpleframework.mvc.common.element.ETextAlign;
import net.simpleframework.mvc.common.element.ElementList;
import net.simpleframework.mvc.common.element.LinkButton;
import net.simpleframework.mvc.common.element.LinkElement;
import net.simpleframework.mvc.common.element.Option;
import net.simpleframework.mvc.common.element.SpanElement;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ui.menu.EMenuEvent;
import net.simpleframework.mvc.component.ui.menu.MenuBean;
import net.simpleframework.mvc.component.ui.menu.MenuItem;
import net.simpleframework.mvc.component.ui.pager.TablePagerBean;
import net.simpleframework.mvc.component.ui.pager.TablePagerColumn;
import net.simpleframework.workflow.engine.EWorkitemStatus;
import net.simpleframework.workflow.engine.IWorkflowContext;
import net.simpleframework.workflow.engine.bean.DelegationBean;
import net.simpleframework.workflow.engine.bean.WorkitemBean;
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
		setGroupParam(pp);

		addComponents(pp);
	}

	protected void setGroupParam(final PageParameter pp) {
		String g = pp.getParameter("g");
		if (g == null) {
			g = pp.getCookie("group_worklist_running");
		}
		if ("modelname".equals(g) || "taskname".equals(g) || "none".equals(g)) {
			pp.putParameter("g", g);
			pp.addCookie("group_worklist_running", g, 365 * 60 * 60 * 24);
		} else {
			pp.putParameter("g", "modelname");
		}
	}

	protected void addComponents(final PageParameter pp) {
		// 添加表格
		addTablePagerBean(pp);

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

		final String url = getWorklistPageUrl(pp);
		mb.addItem(MyRunningWorklistTbl.MENU_VIEW_ALL().setOnclick("$Actions.loc('" + url + "');"))
				.addItem(
						MyRunningWorklistTbl.MENU_MARK_UNREAD().setOnclick(
								"$Actions.loc('" + HttpUtils.addParameters(url, "v=unread") + "');"))
				.addItem(MenuItem.sep());
		addGroupMenuItems(pp, mb, url);
		mb.addItem(MenuItem.sep()).addItem(
				MenuItem.of($m("AbstractItemsTPage.4")).setOnclick(
						"$Actions.loc('" + uFactory.getUrl(pp, MyQueryWorksTPage.class) + "');"));

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

	protected void addGroupMenuItems(final PageParameter pp, final MenuBean mb, final String url) {
		final MenuItem g0 = MyRunningWorklistTbl.MENU_VIEW_GROUP0().setOnclick(
				"$Actions.loc('" + HttpUtils.addParameters(url, "g=none") + "');");
		final MenuItem g1 = MyRunningWorklistTbl.MENU_VIEW_GROUP1().setOnclick(
				"$Actions.loc('" + HttpUtils.addParameters(url, "g=modelname") + "');");
		final MenuItem g2 = MyRunningWorklistTbl.MENU_VIEW_GROUP2().setOnclick(
				"$Actions.loc('" + HttpUtils.addParameters(url, "g=taskname") + "');");
		final String g = pp.getParameter("g");
		if ("none".equals(g)) {
			g0.setIconClass(MenuItem.ICON_SELECTED);
		} else if ("modelname".equals(g)) {
			g1.setIconClass(MenuItem.ICON_SELECTED);
		} else if ("taskname".equals(g)) {
			g2.setIconClass(MenuItem.ICON_SELECTED);
		}
		mb.addItem(g0).addItem(g1).addItem(g2);
	}

	protected TablePagerBean addTablePagerBean(final PageParameter pp) {
		final TablePagerBean tablePager = addTablePagerBean(pp, "MyWorklistTPage_tbl",
				MyRunningWorklistTbl.class).setShowCheckbox(true);
		tablePager
				.addColumn(TablePagerColumn.ICON().setWidth(18))
				.addColumn(TC_TITLE())
				.addColumn(
						new TablePagerColumn("userFrom", $m("MyRunningWorklistTPage.0"), 85)
								.setFilterSort(false).setNowrap(false))
				.addColumn(
						new TablePagerColumn("pstat", $m("MyRunningWorklistTPage.13"), 65).setTextAlign(
								ETextAlign.center).setFilterSort(false))
				.addColumn(
						new TablePagerColumn("createDate", $m("MyRunningWorklistTPage.1"), 65)
								.setTextAlign(ETextAlign.center).setPropertyClass(Date.class))
				.addColumn(new TablePagerColumn("status", $m("AbstractWorkitemsTPage.3"), 55) {
					@Override
					protected Option[] getFilterOptions() {
						return Option.from(EWorkitemStatus.running, EWorkitemStatus.delegate,
								EWorkitemStatus.suspended);
					};
				}.setSort(false).setPropertyClass(EWorkitemStatus.class))
				.addColumn(TablePagerColumn.OPE().setWidth(70));
		return tablePager;
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
			txt.append(new LinkElement($m("MyRunningWorklistTPage.10")).setHref(uFactory.getUrl(pp,
					UserDelegateListTPage.class)));
			txt.append(SpanElement.SPACE);
			txt.append(new LinkElement($m("MyRunningWorklistTPage.9"))
					.setOnclick("$Actions['MyRunningWorklistTPage_user_undelegate']();"));
			sb.append(new BlockElement().setClassName("worklist_tip").setText(txt.toString()));
		}
		sb.append(super.toToolbarHTML(pp));
		final String url = getWorklistPageUrl(pp);
		final StringBuilder js = new StringBuilder();
		js.append("var s = $('idAbstractWorkitemsTPage_search');");
		js.append("$UI.addBackgroundTitle(s, '").append($m("AbstractWorkitemsTPage.4")).append("');");
		js.append("var Func = function() {");
		js.append(" var v = $F(s).trim();");
		js.append(" if (v == '')");
		js.append("   $Actions.loc('").append(url).append("');");
		js.append(" else");
		js.append("	  $Actions.loc('").append(HttpUtils.addParameters(url, "t="))
				.append("' + encodeURIComponent(v));");
		js.append("};");
		js.append("$Actions.observeSubmit(s, Func);");
		js.append("s.next().observe('click', Func);");
		sb.append(JavascriptUtils.wrapScriptTag(js.toString(), true));
		return sb.toString();
	}

	protected String getWorklistPageUrl(final PageParameter pp) {
		return uFactory.getUrl(pp, MyRunningWorklistTPage.class);
	}

	@Override
	public ElementList getLeftElements(final PageParameter pp) {
		return ElementList.of(
				LinkButton.of($m("MyRunningWorklistTPage.12")).setHref(
						uFactory.getUrl(pp, MyInitiateItemsTPage.class)), SpanElement.SPACE, LinkButton
						.menu($m("MyRunningWorklistTbl.6")).setId("idMyWorklistTPage_markMenu"),
				LinkButton.menu($m("MyRunningWorklistTbl.14")).setId("idMyWorklistTPage_viewMenu"),
				SpanElement.SPACE,
				LinkButton.menu($m("MyRunningWorklistTbl.5")).setId("idMyWorklistTPage_delegateMenu"),
				LinkButton.menu($m("MyRunningWorklistTbl.17")).setId("idMyWorklistTPage_opeMenu"));
	}

	protected BlockElement createStatElement(final PageParameter pp) {
		return new BlockElement();
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