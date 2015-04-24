package net.simpleframework.workflow.web.page;

import java.util.Iterator;
import java.util.Map;

import net.simpleframework.ado.FilterItem;
import net.simpleframework.ado.db.DbDataQuery;
import net.simpleframework.ado.db.common.ExpressionValue;
import net.simpleframework.ado.db.common.SQLValue;
import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.AbstractElement;
import net.simpleframework.mvc.common.element.LinkElement;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ui.pager.TablePagerBean;
import net.simpleframework.mvc.component.ui.pager.TablePagerColumn;
import net.simpleframework.mvc.component.ui.pager.db.AbstractDbTablePagerHandler;
import net.simpleframework.workflow.engine.bean.ProcessBean;
import net.simpleframework.workflow.engine.bean.WorkviewBean;
import net.simpleframework.workflow.web.WorkflowUtils;
import net.simpleframework.workflow.web.page.t1.WorkflowViewPage;

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
		tablePager.addColumn(TablePagerColumn.ICON().setWidth(18)).addColumn(TC_TITLE())
				.addColumn(TablePagerColumn.OPE().setWidth(90));
		return tablePager;
	}

	public static class MyWorkviewsTbl extends AbstractDbTablePagerHandler {
		@Override
		public IDataQuery<WorkviewBean> createDataObjectQuery(final ComponentParameter cp) {
			return vService.getWorkviewsList(cp.getLoginId());
		}

		@Override
		protected ExpressionValue createFilterExpressionValue(final DbDataQuery<?> qs,
				final TablePagerColumn oCol, final Iterator<FilterItem> it) {
			if ("title".equals(oCol.getColumnName())) {
				final TablePagerColumn oCol2 = (TablePagerColumn) oCol.clone();
				oCol2.setColumnAlias("p.title");
				final ExpressionValue ev = super.createFilterExpressionValue(qs, oCol2, it);
				final SQLValue sv = qs.getSqlValue();
				final StringBuilder sb = new StringBuilder();
				sb.append("select * from (").append(sv.getSql()).append(") t left join ")
						.append(pService.getTablename(ProcessBean.class))
						.append(" p on t.processid=p.id where " + ev.getExpression());
				sv.setSql(sb.toString());
				sv.addValues(ev.getValues());
				return null;
			}
			return super.createFilterExpressionValue(qs, oCol, it);
		}

		protected AbstractElement<?> createImageMark(final ComponentParameter cp,
				final WorkviewBean workview) {
			AbstractElement<?> img = null;
			if (workview.isTopMark()) {
				img = AbstractItemsTPage.MARK_TOP(cp);
			} else if (!workview.isReadMark()) {
				img = AbstractItemsTPage.MARK_UNREAD(cp);
			}
			return img;
		}

		@Override
		protected Map<String, Object> getRowData(final ComponentParameter cp, final Object dataObject) {
			final WorkviewBean workview = (WorkviewBean) dataObject;
			final ProcessBean process = pService.getBean(workview.getProcessId());
			final KVMap row = new KVMap();
			final AbstractElement<?> img = createImageMark(cp, workview);
			if (img != null) {
				row.add(TablePagerColumn.ICON, img);
			}

			final LinkElement le = new LinkElement(WorkflowUtils.getProcessTitle(process)).setStrong(
					!workview.isReadMark()).setOnclick(
					"$Actions.loc('"
							+ uFactory.getUrl(cp, WorkflowViewPage.class,
									workview != null ? ("workviewId=" + workview.getId()) : null) + "');");
			row.add("title", le);
			return row;
		}
	}
}
