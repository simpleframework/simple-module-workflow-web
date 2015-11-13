package net.simpleframework.workflow.web.page;

import static net.simpleframework.common.I18n.$m;

import java.util.Map;

import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.ctx.trans.Transaction;
import net.simpleframework.mvc.IForward;
import net.simpleframework.mvc.JavascriptForward;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.AbstractElement;
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
import net.simpleframework.mvc.component.ui.window.WindowBean;
import net.simpleframework.workflow.engine.EDelegationSource;
import net.simpleframework.workflow.engine.EDelegationStatus;
import net.simpleframework.workflow.engine.IWorkflowContext;
import net.simpleframework.workflow.engine.bean.ActivityBean;
import net.simpleframework.workflow.engine.bean.DelegationBean;
import net.simpleframework.workflow.engine.bean.WorkitemBean;
import net.simpleframework.workflow.web.WorkflowUtils;
import net.simpleframework.workflow.web.page.AbstractDelegateFormPage.WorkitemDelegateViewPage;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class MyDelegateListTPage extends AbstractItemsTPage {

	@Override
	protected void onForward(final PageParameter pp) throws Exception {
		super.onForward(pp);
		// 列表
		addTablePagerBean(pp);
		// 取消
		addAjaxRequest(pp, "DelegateListTPage_abort").setHandlerMethod("doAbort").setConfirmMessage(
				$m("MyDelegateListTPage.2"));
		// 删除
		addAjaxRequest(pp, "DelegateListTPage_delete").setHandlerMethod("doDelete")
				.setConfirmMessage($m("Confirm.Delete"));
		// 查看
		addDelegateView(pp);
	}

	protected WindowBean addDelegateView(final PageParameter pp) {
		addAjaxRequest(pp, "DelegateListTPage_view_page", WorkitemDelegateViewPage.class);
		return addWindowBean(pp, "DelegateListTPage_view")
				.setContentRef("DelegateListTPage_view_page").setTitle($m("MyDelegateListTPage.3"))
				.setHeight(360).setWidth(500);
	}

	protected TablePagerBean addTablePagerBean(final PageParameter pp) {
		final TablePagerBean tablePager = addTablePagerBean(pp, "MyWorklistTPage_tbl",
				MyWorkDelegateTbl.class);
		tablePager
				.addColumn(TC_ICON())
				.addColumn(TC_TITLE())
				.addColumn(TC_USERTEXT())
				.addColumn(
						TC_CREATEDATE().setColumnText($m("MyDelegateListTPage.1")).setColumnAlias(
								"d.createdate")).addColumn(TC_STATUS()).addColumn(TablePagerColumn.OPE(70));
		return tablePager;
	}

	protected TablePagerColumn TC_STATUS() {
		return super.TC_STATUS(EDelegationStatus.class).setColumnAlias("d.status");
	}

	protected TablePagerColumn TC_USERTEXT() {
		return new TablePagerColumn("userText", $m("MyDelegateListTPage.0"), 70).setFilterSort(false);
	}

	@Transaction(context = IWorkflowContext.class)
	public IForward doAbort(final ComponentParameter cp) {
		wfdService.doAbort(wfdService.getBean(cp.getParameter("delegationId")));
		return new JavascriptForward("$Actions['MyWorklistTPage_tbl']();");
	}

	@Transaction(context = IWorkflowContext.class)
	public IForward doDelete(final ComponentParameter cp) {
		final Object[] ids = StringUtils.split(cp.getParameter("delegationId"));
		wfdService.delete(ids);
		return new JavascriptForward("$Actions['MyWorklistTPage_tbl']();");
	}

	@Override
	protected String getPageCSS(final PageParameter pp) {
		return "MyDelegateListTPage";
	}

	protected SpanElement getDelegateTabs(final PageParameter pp) {
		return createTabsElement(pp, TabButtons.of(new TabButton($m("MyDelegateListTPage.4"),
				uFactory.getUrl(pp, MyDelegateListTPage.class)), new TabButton(
				$m("MyDelegateListTPage.5"), uFactory.getUrl(pp, UserDelegateListTPage.class))));
	}

	@Override
	public ElementList getRightElements(final PageParameter pp) {
		return ElementList.of(getDelegateTabs(pp));
	}

	public static class MyWorkDelegateTbl extends MyRunningWorklistTbl {

		@Override
		public IDataQuery<?> createDataObjectQuery(final ComponentParameter cp) {
			return wfdService.queryDelegations(cp.getLoginId(), EDelegationSource.workitem);
		}

		@Override
		protected WorkitemBean getWorkitem(final Object dataObject) {
			return wfwService.getBean(((DelegationBean) dataObject).getSourceId());
		}

		protected Object toTitle(final DelegationBean delegation, final Object title) {
			return new LinkElement(title)
					.setOnclick("$Actions['DelegateListTPage_view']('delegationId=" + delegation.getId()
							+ "');");
		}

		protected String toOpeHTML(final ComponentParameter cp, final DelegationBean delegation) {
			final StringBuilder sb = new StringBuilder();
			final Object id = delegation.getId();
			if (wfdService.isFinalStatus(delegation)) {
				sb.append(ButtonElement.viewBtn().setOnclick(
						"$Actions['DelegateListTPage_view']('delegationId=" + id + "');"));
			} else {
				sb.append(ButtonElement.cancelBtn().setOnclick(
						"$Actions['DelegateListTPage_abort']('delegationId=" + id + "');"));
			}

			sb.append(AbstractTablePagerSchema.IMG_DOWNMENU);
			return sb.toString();
		}

		protected AbstractElement<?> createImageMark(final ComponentParameter cp,
				final DelegationBean delegation) {
			AbstractElement<?> img = null;
			if (delegation.isTimeoutMark()) {
				img = AbstractItemsTPage._createImageMark(cp, "status_timeout.png").setTitle(
						$m("MyDelegateListTPage.6"));
			}
			return img;
		}

		@Override
		protected Map<String, Object> getRowData(final ComponentParameter cp, final Object dataObject) {
			final DelegationBean delegation = (DelegationBean) dataObject;
			final WorkitemBean workitem = getWorkitem(delegation);
			final ActivityBean activity = wfwService.getActivity(workitem);
			final KVMap row = new KVMap();
			final AbstractElement<?> img = createImageMark(cp, delegation);
			if (img != null) {
				row.add(TablePagerColumn.ICON, img);
			}
			final StringBuilder title = new StringBuilder();
			appendTaskname(title, cp, activity);
			title.append(toTitle(delegation,
					WorkflowUtils.getProcessTitle(wfaService.getProcessBean(activity))));
			row.add("title", title.toString());
			row.add("userText", delegation.getUserText());
			row.add("createDate", delegation.getCreateDate());
			final EDelegationStatus status = delegation.getStatus();
			row.add("status", WorkflowUtils.toStatusHTML(cp, status));
			row.add(TablePagerColumn.OPE, toOpeHTML(cp, delegation));
			return row;
		}

		@Override
		public MenuItems getContextMenu(final ComponentParameter cp, final MenuBean menuBean,
				final MenuItem menuItem) {
			final MenuItems items = MenuItems.of();
			items.add(MenuItem.itemDelete().setOnclick_act("DelegateListTPage_delete", "delegationId"));
			return items;
		}
	}
}
