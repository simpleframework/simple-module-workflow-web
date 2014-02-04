package net.simpleframework.workflow.web.page;

import static net.simpleframework.common.I18n.$m;

import java.util.Map;

import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.ButtonElement;
import net.simpleframework.mvc.common.element.SpanElement;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ui.pager.AbstractTablePagerSchema;
import net.simpleframework.mvc.component.ui.pager.TablePagerBean;
import net.simpleframework.mvc.component.ui.pager.TablePagerColumn;
import net.simpleframework.workflow.engine.ActivityBean;
import net.simpleframework.workflow.engine.DelegationBean;
import net.simpleframework.workflow.engine.IWorkflowContextAware;

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
	}

	public static class DelegateTbl extends MyWorklistTbl {

		@Override
		public IDataQuery<?> createDataObjectQuery(final ComponentParameter cp) {
			return context.getDelegationService().queryWorkitems(cp.getLoginId());
		}

		@Override
		public Object getGroupValue(final ComponentParameter cp, final Object bean,
				final String groupColumn) {
			return null;
		}

		@Override
		protected Map<String, Object> getRowData(final ComponentParameter cp, final Object dataObject) {
			final DelegationBean delegation = (DelegationBean) dataObject;
			final ActivityBean activity = wService.getActivity(wService.getBean(delegation
					.getSourceId()));
			final KVMap row = new KVMap().add("title",
					MyWorklistTbl.getTopic(aService.getProcessBean(activity))).add("userId",
					cp.getUser(delegation.getUserId()));
			row.add("createDate", delegation.getCreateDate()).add("status", delegation.getStatus());

			final StringBuilder sb = new StringBuilder();
			sb.append(new ButtonElement($m("Button.Cancel")));
			sb.append(SpanElement.SPACE).append(AbstractTablePagerSchema.IMG_DOWNMENU);
			row.add(TablePagerColumn.OPE, sb.toString());
			return row;
		}
	}
}
