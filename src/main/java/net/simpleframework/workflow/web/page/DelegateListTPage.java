package net.simpleframework.workflow.web.page;

import java.util.Map;

import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ui.pager.TablePagerBean;
import net.simpleframework.mvc.component.ui.pager.TablePagerColumn;
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
		tablePager.addColumn(TITLE()).addColumn(new TablePagerColumn("userId", "委托人", 115))
				.addColumn(new TablePagerColumn("status", "状态", 70));
		tablePager.addColumn(TablePagerColumn.OPE().setWidth(70));
	}

	public static class DelegateTbl extends MyWorklistTbl {

		@Override
		public IDataQuery<?> createDataObjectQuery(final ComponentParameter cp) {
			return context.getDelegationService().queryWorkitems(cp.getLoginId());
		}

		@Override
		protected Map<String, Object> getRowData(final ComponentParameter cp, final Object dataObject) {
			final DelegationBean delegation = (DelegationBean) dataObject;
			final KVMap row = new KVMap().add("userId", cp.getUser(delegation.getUserId()));
			return row;
		}
	}
}
