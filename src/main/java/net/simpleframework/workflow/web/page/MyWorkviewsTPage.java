package net.simpleframework.workflow.web.page;

import java.util.Map;

import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ui.pager.TablePagerBean;
import net.simpleframework.mvc.component.ui.pager.TablePagerColumn;
import net.simpleframework.mvc.component.ui.pager.db.AbstractDbTablePagerHandler;
import net.simpleframework.workflow.web.page.t1.AbstractWorkflowMgrPage;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class MyWorkviewsTPage extends AbstractItemsTPage {

	@Override
	protected void onForward(final PageParameter pp) {
		super.onForward(pp);

		addTablePagerBean(pp);
	}

	protected TablePagerBean addTablePagerBean(final PageParameter pp) {
		final TablePagerBean tablePager = addTablePagerBean(pp, "MyWorkviewsTPage_tbl",
				MyWorkviewsTbl.class);
		tablePager.addColumn(AbstractWorkflowMgrPage.TC_TITLE()).addColumn(
				TablePagerColumn.OPE().setWidth(90));
		return tablePager;
	}

	public static class MyWorkviewsTbl extends AbstractDbTablePagerHandler {
		@Override
		public IDataQuery<?> createDataObjectQuery(final ComponentParameter cp) {
			return super.createDataObjectQuery(cp);
		}

		@Override
		protected Map<String, Object> getRowData(final ComponentParameter cp, final Object dataObject) {
			return super.getRowData(cp, dataObject);
		}
	}
}
