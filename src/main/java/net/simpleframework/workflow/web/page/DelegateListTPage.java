package net.simpleframework.workflow.web.page;

import static net.simpleframework.common.I18n.$m;

import java.util.Map;

import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.ado.query.IteratorDataQuery;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.ctx.trans.Transaction;
import net.simpleframework.mvc.IForward;
import net.simpleframework.mvc.JavascriptForward;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.ButtonElement;
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
import net.simpleframework.workflow.web.IWorkflowWebContext;
import net.simpleframework.workflow.web.WorkflowUrlsFactory;
import net.simpleframework.workflow.web.page.t1.WorkflowFormPage;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class DelegateListTPage extends AbstractWorkitemsTPage implements IWorkflowContextAware {

	@Override
	protected void onForward(final PageParameter pp) {
		super.onForward(pp);

		final TablePagerBean tablePager = addTablePagerBean(pp, "MyWorklistTPage_tbl",
				DelegateTbl.class).setShowFilterBar(false).setShowLineNo(false);
		tablePager.addColumn(TablePagerColumn.ICON().setWidth(16));
		tablePager.addColumn(TITLE()).addColumn(new TablePagerColumn("userId", "委托人", 70))
				.addColumn(new TablePagerColumn("status", "状态", 70))
				.addColumn(new TablePagerColumn("createDate", "委托日期", 115));
		tablePager.addColumn(TablePagerColumn.OPE().setWidth(70));

		// 取消
		addAjaxRequest(pp, "DelegateListTPage_abort").setHandleMethod("doAbort").setConfirmMessage(
				"确认要取消吗？");
	}

	@Transaction(context = IWorkflowContext.class)
	public IForward doAbort(final ComponentParameter cp) {
		dService.doAbort(dService.getBean(cp.getParameter("delegationId")));
		return new JavascriptForward("$Actions['MyWorklistTPage_tbl']();");
	}

	public static class DelegateTbl extends MyWorklistTbl {

		@Override
		public IDataQuery<?> createDataObjectQuery(final ComponentParameter cp) {
			return new IteratorDataQuery<DelegationBean>(context.getDelegationService()
					.queryWorkitems(cp.getLoginId()));
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
			final WorkflowUrlsFactory uFactory = ((IWorkflowWebContext) context).getUrlsFactory();
			final KVMap row = new KVMap().add(
					"title",
					new LinkElement(MyWorklistTbl.getTopic(aService.getProcessBean(wService
							.getActivity(workitem)))).setOnclick("$Actions.loc('"
							+ uFactory.getUrl(cp, WorkflowFormPage.class, workitem, "source=delegation")
							+ "');"));
			row.add("userId", cp.getUser(delegation.getUserId()));
			row.add("createDate", delegation.getCreateDate());
			final EDelegationStatus status = delegation.getStatus();
			row.add("status", WorkflowUtils.createStatusImage(cp, status) + status.toString());

			final StringBuilder sb = new StringBuilder();
			sb.append(new ButtonElement($m("Button.Cancel")).setOnclick(
					"$Actions['DelegateListTPage_abort']('delegationId=" + delegation.getId() + "');")
					.setDisabled(dService.isFinalStatus(delegation)));
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
