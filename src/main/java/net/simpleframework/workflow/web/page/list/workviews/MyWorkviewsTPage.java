package net.simpleframework.workflow.web.page.list.workviews;

import static net.simpleframework.common.I18n.$m;

import java.util.Iterator;
import java.util.Map;

import net.simpleframework.ado.FilterItem;
import net.simpleframework.ado.db.DbDataQuery;
import net.simpleframework.ado.db.common.ExpressionValue;
import net.simpleframework.ado.db.common.SQLValue;
import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.ButtonElement;
import net.simpleframework.mvc.common.element.ETextAlign;
import net.simpleframework.mvc.common.element.ElementList;
import net.simpleframework.mvc.common.element.ImageElement;
import net.simpleframework.mvc.common.element.JS;
import net.simpleframework.mvc.common.element.LinkElement;
import net.simpleframework.mvc.common.element.SpanElement;
import net.simpleframework.mvc.common.element.TabButton;
import net.simpleframework.mvc.common.element.TabButtons;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ui.pager.TablePagerBean;
import net.simpleframework.mvc.component.ui.pager.TablePagerColumn;
import net.simpleframework.mvc.component.ui.pager.db.AbstractDbTablePagerHandler;
import net.simpleframework.workflow.engine.bean.ProcessBean;
import net.simpleframework.workflow.engine.bean.WorkitemBean;
import net.simpleframework.workflow.engine.bean.WorkviewBean;
import net.simpleframework.workflow.web.WorkflowUtils;
import net.simpleframework.workflow.web.page.list.AbstractItemsTPage;
import net.simpleframework.workflow.web.page.t1.form.WorkflowViewPage;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class MyWorkviewsTPage extends AbstractItemsTPage {

	@Override
	protected void onForward(final PageParameter pp) throws Exception {
		super.onForward(pp);

		addTablePagerBean(pp);
	}

	protected TablePagerBean addTablePagerBean(final PageParameter pp) {
		final TablePagerBean tablePager = addTablePagerBean(pp, "MyWorkviewsTPage_tbl",
				MyWorkviewsTbl.class);
		tablePager
				.addColumn(TC_ICON())
				.addColumn(TC_TITLE())
				.addColumn(
						new TablePagerColumn("sent", $m("MyRunningWorklistTPage.0"), 120).setTextAlign(
								ETextAlign.center).setFilterSort(false))
				.addColumn(TC_CREATEDATE().setColumnText($m("MyRunningWorklistTPage.1")))
				.addColumn(TablePagerColumn.OPE(70));
		return tablePager;
	}

	static SpanElement getStatTabs(final PageParameter pp) {
		return createTabsElement(pp, TabButtons.of(
				new TabButton($m("AbstractItemsTPage.5"), uFactory.getUrl(pp, MyWorkviewsTPage.class)),
				new TabButton($m("AbstractItemsTPage.13"), uFactory.getUrl(pp,
						MyWorkviewsSentTPage.class))));
	}

	@Override
	public ElementList getRightElements(final PageParameter pp) {
		return ElementList.of(getStatTabs(pp));
	}

	protected static class AMyWorkviewsTbl extends AbstractDbTablePagerHandler {

	}

	public static class MyWorkviewsTbl extends AMyWorkviewsTbl {
		@Override
		public IDataQuery<WorkviewBean> createDataObjectQuery(final ComponentParameter cp) {
			return wfvService.getWorkviewsList(cp.getLoginId());
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
						.append(wfpService.getTablename(ProcessBean.class))
						.append(" p on t.processid=p.id where " + ev.getExpression());
				sv.setSql(sb.toString());
				sv.addValues(ev.getValues());
				return null;
			}
			return super.createFilterExpressionValue(qs, oCol, it);
		}

		protected ImageElement createImageMark(final ComponentParameter cp,
				final WorkviewBean workview) {
			ImageElement img = null;
			if (workview.getParentId() != null) {
				final WorkitemBean workitem = wfwService.getBean(workview.getWorkitemId());
				// img = AbstractItemsTPage.MARK_TOP(cp);
				System.out.println(workitem);
			} else if (workview.isTopMark()) {
				img = AbstractItemsTPage.MARK_TOP(cp);
			} else if (!workview.isReadMark()) {
				img = AbstractItemsTPage.MARK_UNREAD(cp);
			}
			return img;
		}

		@Override
		protected Map<String, Object> getRowData(final ComponentParameter cp, final Object dataObject) {
			final WorkviewBean workview = (WorkviewBean) dataObject;
			final KVMap row = new KVMap();
			final ImageElement img = createImageMark(cp, workview);
			if (img != null) {
				row.add(TablePagerColumn.ICON, img);
			}
			row.add("title", toTopicHTML(cp, workview)).add("createDate", workview.getCreateDate())
					.add("sent", cp.getUser(workview.getUserId()))
					.add(TablePagerColumn.OPE, toOpeHTML(cp, workview));
			return row;
		}

		protected String toTopicHTML(final ComponentParameter cp, final WorkviewBean workview) {
			final ProcessBean process = wfpService.getBean(workview.getProcessId());
			return new LinkElement(WorkflowUtils.getProcessTitle(process))
					.setStrong(!workview.isReadMark()).setHref(getWorkviewUrl(cp, workview)).toString();
		}

		protected String toOpeHTML(final ComponentParameter cp, final WorkviewBean workview) {
			final StringBuilder ope = new StringBuilder();
			ope.append(new ButtonElement($m("MyWorkviewsTPage。0")).setOnclick(JS.loc(getWorkviewUrl(
					cp, workview))));
			return ope.toString();
		}

		private String getWorkviewUrl(final ComponentParameter cp, final WorkviewBean workview) {
			return uFactory.getUrl(cp, WorkflowViewPage.class, "workviewId=" + workview.getId());
		}
	}
}
