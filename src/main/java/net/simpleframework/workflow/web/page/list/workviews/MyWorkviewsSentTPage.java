package net.simpleframework.workflow.web.page.list.workviews;

import static net.simpleframework.common.I18n.$m;

import java.util.Map;

import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.ImageElement;
import net.simpleframework.mvc.common.element.SpanElement;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ui.pager.TablePagerBean;
import net.simpleframework.mvc.component.ui.pager.TablePagerColumn;
import net.simpleframework.mvc.component.ui.pager.db.AbstractDbTablePagerHandler;
import net.simpleframework.workflow.engine.bean.ProcessBean;
import net.simpleframework.workflow.engine.bean.WorkviewSentBean;
import net.simpleframework.workflow.web.WorkflowUtils;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class MyWorkviewsSentTPage extends MyWorkviewsTPage {

	@Override
	protected TablePagerBean addTablePagerBean(final PageParameter pp) {
		final TablePagerBean tablePager = addTablePagerBean(pp, "MyWorkviewsTPage_tbl",
				MyWorkviewsSentTbl.class);
		tablePager
				.addColumn(TC_ICON())
				.addColumn(TC_TITLE())
				.addColumn(
						new TablePagerColumn("rev", $m("MyFinalWorklistTPage.0"), 200)
								.setFilterSort(false))
				.addColumn(TC_CREATEDATE().setColumnText($m("MyWorkviewsSentTPage.0")))
				.addColumn(TablePagerColumn.OPE(70));
		return tablePager;
	}

	public static class MyWorkviewsSentTbl extends AbstractDbTablePagerHandler {

		@Override
		public IDataQuery<?> createDataObjectQuery(final ComponentParameter cp) {
			return wfvsService.getWorkviewsSentList(cp.getLoginId());
		}

		protected ImageElement createImageMark(final ComponentParameter cp,
				final WorkviewSentBean sent) {
			return null;
		}

		@Override
		protected Map<String, Object> getRowData(final ComponentParameter cp, final Object dataObject) {
			final WorkviewSentBean sent = (WorkviewSentBean) dataObject;
			final KVMap row = new KVMap();
			final ImageElement img = createImageMark(cp, sent);
			if (img != null) {
				row.add(TablePagerColumn.ICON, img);
			}
			row.add("title", toTopicHTML(cp, sent)).add("createDate", sent.getCreateDate());
			return row;
		}

		protected String toTopicHTML(final ComponentParameter cp, final WorkviewSentBean sent) {
			final ProcessBean process = wfpService.getBean(sent.getProcessId());
			return new SpanElement(WorkflowUtils.getProcessTitle(process)).toString();
		}
	}
}