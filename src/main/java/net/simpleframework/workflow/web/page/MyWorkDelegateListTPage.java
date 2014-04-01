package net.simpleframework.workflow.web.page;

import static net.simpleframework.common.I18n.$m;

import java.util.Date;
import java.util.Map;

import net.simpleframework.ado.query.IDataQuery;
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
import net.simpleframework.mvc.component.ui.pager.ITablePagerHandler;
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
	protected String getPageCSS(final PageParameter pp) {
		return "MyWorkDelegateListTPage";
	}

	@Override
	protected void onForward(final PageParameter pp) {
		super.onForward(pp);
		addComponents(pp);
	}

	protected void addComponents(final PageParameter pp) {
		final TablePagerBean tablePager = addTablePagerBean(pp, MyWorkDelegateTbl.class);
		tablePager.addColumn(TablePagerColumn.ICON().setWidth(16));
		tablePager
				.addColumn(TC_TITLE())
				.addColumn(new TablePagerColumn("userText", $m("MyWorkDelegateListTPage.0"), 70))
				.addColumn(
						new TablePagerColumn("createDate", $m("MyWorkDelegateListTPage.1"), 115)
								.setPropertyClass(Date.class))
				.addColumn(TC_STATUS().setPropertyClass(EDelegationStatus.class));
		tablePager.addColumn(TablePagerColumn.OPE().setWidth(70));

		// 取消
		addAjaxRequest(pp, "DelegateListTPage_abort").setHandlerMethod("doAbort").setConfirmMessage(
				$m("MyWorkDelegateListTPage.2"));

		// 查看
		addAjaxRequest(pp, "DelegateListTPage_view_page", WorkitemDelegateViewPage.class);
		addWindowBean(pp, "DelegateListTPage_view").setContentRef("DelegateListTPage_view_page")
				.setTitle($m("MyWorkDelegateListTPage.3")).setHeight(300).setWidth(500);
	}

	protected TablePagerBean addTablePagerBean(final PageParameter pp,
			final Class<? extends ITablePagerHandler> handlerClass) {
		return addTablePagerBean(pp, "MyWorklistTPage_tbl", handlerClass);
	}

	@Override
	protected Class<? extends AbstractMVCPage> getUpdateLogPage() {
		return DelegateUpdateLogPage.class;
	}

	@Transaction(context = IWorkflowContext.class)
	public IForward doAbort(final ComponentParameter cp) {
		dService.doAbort(dService.getBean(cp.getParameter("delegationId")));
		return new JavascriptForward("$Actions['MyWorklistTPage_tbl']();");
	}

	protected SpanElement getTabButtons(final PageParameter pp) {
		final WorkflowUrlsFactory urlsFactory = getUrlsFactory();
		return new SpanElement().setClassName("tabbtns").addHtml(
				TabButtons.of(
						new TabButton("工作项委托", urlsFactory.getUrl(pp, MyWorkDelegateListTPage.class)),
						new TabButton("用户委托", urlsFactory.getUrl(pp, UserDelegateListTPage.class)))
						.toString(pp));
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

		@Override
		protected Map<String, Object> getRowData(final ComponentParameter cp, final Object dataObject) {
			final DelegationBean delegation = (DelegationBean) dataObject;
			final WorkitemBean workitem = getWorkitem(delegation);

			final Object id = delegation.getId();

			final ActivityBean activity = wService.getActivity(workitem);
			final StringBuilder title = new StringBuilder();
			appendTaskname(title, cp, activity);
			title.append(new LinkElement(WorkflowUtils.getTitle(aService.getProcessBean(activity)))
					.setOnclick("$Actions['DelegateListTPage_view']('delegationId=" + id + "');"));
			final KVMap row = new KVMap().add("title", title.toString());
			row.add("userText", delegation.getUserText());
			row.add("createDate", delegation.getCreateDate());
			final EDelegationStatus status = delegation.getStatus();
			row.add("status", WorkflowUtils.toStatusHTML(cp, status));

			final StringBuilder sb = new StringBuilder();
			if (dService.isFinalStatus(delegation)) {
				sb.append(WorkflowUtils.createLogButton().setOnclick(
						"$Actions['AbstractItemsTPage_update_log']('delegationId=" + id + "');"));
			} else {
				sb.append(new ButtonElement($m("Button.Cancel"))
						.setOnclick("$Actions['DelegateListTPage_abort']('delegationId=" + id + "');"));
			}

			sb.append(SpanElement.SPACE).append(AbstractTablePagerSchema.IMG_DOWNMENU);
			row.add(TablePagerColumn.OPE, sb.toString());
			return row;
		}

		@Override
		public MenuItems getContextMenu(final ComponentParameter cp, final MenuBean menuBean,
				final MenuItem menuItem) {
			final MenuItems items = MenuItems.of();
			return items;
		}
	}
}
