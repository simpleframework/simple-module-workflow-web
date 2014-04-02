package net.simpleframework.workflow.web.page;

import static net.simpleframework.common.I18n.$m;

import java.util.Date;
import java.util.Map;

import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.ctx.trans.Transaction;
import net.simpleframework.mvc.AbstractMVCPage;
import net.simpleframework.mvc.IForward;
import net.simpleframework.mvc.JavascriptForward;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.ButtonElement;
import net.simpleframework.mvc.common.element.ElementList;
import net.simpleframework.mvc.common.element.LinkElement;
import net.simpleframework.mvc.common.element.SpanElement;
import net.simpleframework.mvc.common.element.TabButton;
import net.simpleframework.mvc.common.element.TabButtons;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ui.menu.MenuBean;
import net.simpleframework.mvc.component.ui.menu.MenuItem;
import net.simpleframework.mvc.component.ui.menu.MenuItems;
import net.simpleframework.mvc.component.ui.pager.AbstractTablePagerSchema;
import net.simpleframework.mvc.component.ui.pager.TablePagerBean;
import net.simpleframework.mvc.component.ui.pager.TablePagerColumn;
import net.simpleframework.workflow.engine.ActivityBean;
import net.simpleframework.workflow.engine.DelegationBean;
import net.simpleframework.workflow.engine.EDelegationSource;
import net.simpleframework.workflow.engine.EDelegationStatus;
import net.simpleframework.workflow.engine.IWorkflowContext;
import net.simpleframework.workflow.engine.WorkitemBean;
import net.simpleframework.workflow.web.WorkflowLogRef.DelegateUpdateLogPage;
import net.simpleframework.workflow.web.WorkflowUrlsFactory;
import net.simpleframework.workflow.web.page.AbstractDelegateFormPage.WorkitemDelegateViewPage;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class MyWorkDelegateListTPage extends AbstractWorkitemsTPage {

	@Override
	protected void onForward(final PageParameter pp) {
		super.onForward(pp);
		// 列表
		addTablePagerBean(pp);
		// 取消
		addAjaxRequest(pp, "DelegateListTPage_abort").setHandlerMethod("doAbort").setConfirmMessage(
				$m("MyWorkDelegateListTPage.2"));
		// 删除
		addAjaxRequest(pp, "DelegateListTPage_delete").setHandlerMethod("doDelete")
				.setConfirmMessage($m("Confirm.Delete"));
		// 查看
		addAjaxRequest(pp, "DelegateListTPage_view_page", WorkitemDelegateViewPage.class);
		addWindowBean(pp, "DelegateListTPage_view").setContentRef("DelegateListTPage_view_page")
				.setTitle($m("MyWorkDelegateListTPage.3")).setHeight(300).setWidth(500);
	}

	protected TablePagerBean addTablePagerBean(final PageParameter pp) {
		final TablePagerBean tablePager = addTablePagerBean(pp, "MyWorklistTPage_tbl",
				MyWorkDelegateTbl.class);
		tablePager.addColumn(TablePagerColumn.ICON().setWidth(16));
		tablePager
				.addColumn(TC_TITLE())
				.addColumn(new TablePagerColumn("userText", $m("MyWorkDelegateListTPage.0"), 70))
				.addColumn(
						new TablePagerColumn("createDate", $m("MyWorkDelegateListTPage.1"), 115)
								.setPropertyClass(Date.class))
				.addColumn(TC_STATUS().setPropertyClass(EDelegationStatus.class));
		tablePager.addColumn(TablePagerColumn.OPE().setWidth(70));
		return tablePager;
	}

	@Transaction(context = IWorkflowContext.class)
	public IForward doAbort(final ComponentParameter cp) {
		dService.doAbort(dService.getBean(cp.getParameter("delegationId")));
		return new JavascriptForward("$Actions['MyWorklistTPage_tbl']();");
	}

	@Transaction(context = IWorkflowContext.class)
	public IForward doDelete(final ComponentParameter cp) {
		final Object[] ids = StringUtils.split(cp.getParameter("delegationId"));
		dService.delete(ids);
		return new JavascriptForward("$Actions['MyWorklistTPage_tbl']();");
	}

	@Override
	protected String getPageCSS(final PageParameter pp) {
		return "MyWorkDelegateListTPage";
	}

	@Override
	protected Class<? extends AbstractMVCPage> getUpdateLogPage() {
		return DelegateUpdateLogPage.class;
	}

	protected SpanElement getTabButtons(final PageParameter pp) {
		final WorkflowUrlsFactory urlsFactory = getUrlsFactory();
		return new SpanElement().setClassName("tabbtns").addHtml(
				TabButtons.of(
						new TabButton($m("MyWorkDelegateListTPage.4"), urlsFactory.getUrl(pp,
								MyWorkDelegateListTPage.class)),
						new TabButton($m("MyWorkDelegateListTPage.5"), urlsFactory.getUrl(pp,
								UserDelegateListTPage.class))).toString(pp));
	}

	@Override
	public ElementList getRightElements(final PageParameter pp) {
		final ElementList el = super.getRightElements(pp);
		el.add(0, getTabButtons(pp));
		return el;
	}

	public static class MyWorkDelegateTbl extends MyRunningWorklistTbl {

		@Override
		public IDataQuery<?> createDataObjectQuery(final ComponentParameter cp) {
			return dService.queryDelegations(cp.getLoginId(), EDelegationSource.workitem);
		}

		@Override
		protected WorkitemBean getWorkitem(final Object dataObject) {
			return wService.getBean(((DelegationBean) dataObject).getSourceId());
		}

		protected Object toTitle(final DelegationBean delegation, final Object title) {
			return new LinkElement(title)
					.setOnclick("$Actions['DelegateListTPage_view']('delegationId=" + delegation.getId()
							+ "');");
		}

		protected Object toOpe(final DelegationBean delegation) {
			final StringBuilder sb = new StringBuilder();
			final Object id = delegation.getId();
			if (dService.isFinalStatus(delegation)) {
				sb.append(WorkflowUtils.createLogButton().setOnclick(
						"$Actions['AbstractItemsTPage_update_log']('delegationId=" + id + "');"));
			} else {
				sb.append(new ButtonElement($m("Button.Cancel"))
						.setOnclick("$Actions['DelegateListTPage_abort']('delegationId=" + id + "');"));
			}

			sb.append(SpanElement.SPACE).append(AbstractTablePagerSchema.IMG_DOWNMENU);
			return sb;
		}

		@Override
		protected Map<String, Object> getRowData(final ComponentParameter cp, final Object dataObject) {
			final DelegationBean delegation = (DelegationBean) dataObject;
			final WorkitemBean workitem = getWorkitem(delegation);
			final ActivityBean activity = wService.getActivity(workitem);
			final StringBuilder title = new StringBuilder();
			appendTaskname(title, cp, activity);
			title.append(toTitle(delegation, WorkflowUtils.getTitle(aService.getProcessBean(activity))));
			final KVMap row = new KVMap().add("title", title.toString());
			row.add("userText", delegation.getUserText());
			row.add("createDate", delegation.getCreateDate());
			final EDelegationStatus status = delegation.getStatus();
			row.add("status", WorkflowUtils.toStatusHTML(cp, status));
			row.add(TablePagerColumn.OPE, toOpe(delegation));
			return row;
		}

		@Override
		public MenuItems getContextMenu(final ComponentParameter cp, final MenuBean menuBean,
				final MenuItem menuItem) {
			final MenuItems items = MenuItems.of();
			items.add(MenuItem.itemDelete().setOnclick_act("DelegateListTPage_delete", "delegationId"));
			items.append(MenuItem.sep()).append(
					MENU_LOG().setOnclick_act("AbstractItemsTPage_update_log", "delegationId"));
			return items;
		}
	}
}
