package net.simpleframework.workflow.web.page;

import java.util.Map;

import net.simpleframework.ado.query.DataQueryUtils;
import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.ado.query.ListDataObjectQuery;
import net.simpleframework.common.ID;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.ETextAlign;
import net.simpleframework.mvc.common.element.LinkElement;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ui.pager.TablePagerBean;
import net.simpleframework.mvc.component.ui.pager.TablePagerColumn;
import net.simpleframework.mvc.component.ui.pager.db.AbstractDbTablePagerHandler;
import net.simpleframework.workflow.engine.InitiateItem;
import net.simpleframework.workflow.engine.InitiateItems;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class MyInitiateItemsTPage extends AbstractWorkTPage {

	@Override
	protected void onForward(final PageParameter pp) {
		super.onForward(pp);

		final TablePagerBean tablePager = addTablePagerBean(pp, MyInitiateItemsTbl.class);
		tablePager
				.addColumn(new TablePagerColumn("modelText", "可启动流程").setTextAlign(ETextAlign.left))
				.addColumn(TablePagerColumn.OPE().setWidth(80));
	}

	public static class MyInitiateItemsTbl extends AbstractDbTablePagerHandler {

		@Override
		public IDataQuery<?> createDataObjectQuery(ComponentParameter cp) {
			final ID loginId = cp.getLoginId();
			InitiateItems items;
			if (loginId == null
					|| (items = context.getProcessModelService().getInitiateItems(loginId)) == null) {
				return DataQueryUtils.nullQuery();
			}
			return new ListDataObjectQuery<InitiateItem>(items);
		}

		@Override
		protected Map<String, Object> getRowData(ComponentParameter cp, Object dataObject) {
			final InitiateItem initiateItem = (InitiateItem) dataObject;
			final KVMap row = new KVMap().add("modelText", new LinkElement(initiateItem));
			final StringBuilder sb = new StringBuilder();
			row.put(TablePagerColumn.OPE, sb.toString());
			return row;
		}
	}
}
