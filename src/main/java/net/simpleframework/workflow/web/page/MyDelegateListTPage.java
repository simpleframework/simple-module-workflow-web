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
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ui.menu.MenuBean;
import net.simpleframework.mvc.component.ui.menu.MenuItem;
import net.simpleframework.mvc.component.ui.menu.MenuItems;
import net.simpleframework.mvc.component.ui.pager.AbstractTablePagerSchema;
import net.simpleframework.mvc.component.ui.pager.TablePagerBean;
import net.simpleframework.mvc.component.ui.pager.TablePagerColumn;
import net.simpleframework.workflow.engine.DelegationBean;
import net.simpleframework.workflow.engine.EDelegationStatus;
import net.simpleframework.workflow.engine.IWorkflowContext;
import net.simpleframework.workflow.engine.IWorkflowContextAware;
import net.simpleframework.workflow.engine.WorkitemBean;
import net.simpleframework.workflow.web.WorkflowLogRef.DelegateUpdateLogPage;
import net.simpleframework.workflow.web.page.AbstractDelegateFormPage.WorkitemDelegateViewPage;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class MyDelegateListTPage extends AbstractWorkitemsTPage implements IWorkflowContextAware {

	@Override
	protected void onForward(final PageParameter pp) {
		super.onForward(pp);

		final TablePagerBean tablePager = addTablePagerBean(pp, "MyWorklistTPage_tbl",
				DelegateTbl.class);
		tablePager.addColumn(TablePagerColumn.ICON().setWidth(16));
		tablePager
				.addColumn(TC_TITLE())
				.addColumn(new TablePagerColumn("userText", $m("MyDelegateListTPage.0"), 70))
				.addColumn(
						new TablePagerColumn("createDate", $m("MyDelegateListTPage.1"), 115)
								.setPropertyClass(Date.class))
				.addColumn(TC_STATUS().setPropertyClass(EDelegationStatus.class));
		tablePager.addColumn(TablePagerColumn.OPE().setWidth(70));

		// 取消
		addAjaxRequest(pp, "DelegateListTPage_abort").setHandlerMethod("doAbort").setConfirmMessage(
				$m("MyDelegateListTPage.2"));

		// 查看
		addAjaxRequest(pp, "DelegateListTPage_view_page", WorkitemDelegateViewPage.class);
		addWindowBean(pp, "DelegateListTPage_view").setContentRef("DelegateListTPage_view_page")
				.setTitle($m("MyDelegateListTPage.3")).setHeight(300).setWidth(500);
	}

	@Override
	public ElementList getRightElements(final PageParameter pp) {
		return ElementList.of();
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

	public static class DelegateTbl extends MyRunningWorklistTbl {

		@Override
		public IDataQuery<?> createDataObjectQuery(final ComponentParameter cp) {
			return dService.queryDelegations(cp.getLoginId());
		}

		@Override
		public Object getGroupValue(final ComponentParameter cp, final Object bean,
				final String groupColumn) {
			return null;
		}

		@Override
		protected Map<String, Object> getRowData(final ComponentParameter cp, final Object dataObject) {
			final DelegationBean delegation = (DelegationBean) dataObject;
			final WorkitemBean workitem = wService.getBean(delegation.getSourceId());

			final Object id = delegation.getId();
			final KVMap row = new KVMap().add(
					"title",
					new LinkElement(WorkflowUtils.getTitle(aService.getProcessBean(wService
							.getActivity(workitem))))
							.setOnclick("$Actions['DelegateListTPage_view']('delegationId=" + id + "');"));
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
