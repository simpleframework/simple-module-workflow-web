package net.simpleframework.workflow.web.page.list.workviews;

import static net.simpleframework.common.I18n.$m;

import java.util.Map;

import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.common.ID;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.ButtonElement;
import net.simpleframework.mvc.common.element.ETextAlign;
import net.simpleframework.mvc.common.element.ImageElement;
import net.simpleframework.mvc.common.element.JS;
import net.simpleframework.mvc.common.element.LinkElement;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ui.pager.TablePagerBean;
import net.simpleframework.mvc.component.ui.pager.TablePagerColumn;
import net.simpleframework.workflow.engine.bean.ProcessBean;
import net.simpleframework.workflow.engine.bean.WorkviewBean;
import net.simpleframework.workflow.engine.bean.WorkviewSentBean;
import net.simpleframework.workflow.web.WorkflowUtils;
import net.simpleframework.workflow.web.page.t1.form.WorkflowFormPage;
import net.simpleframework.workflow.web.page.t1.form.WorkflowViewPage;

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
								.setTextAlign(ETextAlign.center).setNowrap(false).setFilterSort(false))
				.addColumn(TC_CREATEDATE().setColumnText($m("MyWorkviewsSentTPage.0")))
				.addColumn(TablePagerColumn.OPE(70));
		return tablePager;
	}

	public static class MyWorkviewsSentTbl extends AMyWorkviewsTbl {

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
			row.add("title", toTopicHTML(cp, sent)).add("createDate", sent.getCreateDate())
					.add("rev", sent.getAttrCache("_rev", new CacheV<String>() {
						@Override
						public String get() {
							final StringBuilder rev = new StringBuilder();
							final IDataQuery<WorkviewBean> dq = wfvService.getWorkviewsListBySent(sent
									.getId());
							WorkviewBean workview;
							int i = 0;
							while ((workview = dq.next()) != null) {
								if (i++ > 0) {
									rev.append(", ");
								}
								rev.append(workview.getUserText());
							}
							return rev.toString();
						}
					})).add(TablePagerColumn.OPE, toOpeHTML(cp, sent));
			return row;
		}

		protected String toTopicHTML(final ComponentParameter cp, final WorkviewSentBean sent) {
			final ProcessBean process = wfpService.getBean(sent.getProcessId());
			return new LinkElement(WorkflowUtils.getProcessTitle(process)).setHref(
					getWorkviewSentUrl(cp, sent)).toString();
		}

		protected String toOpeHTML(final ComponentParameter cp, final WorkviewSentBean sent) {
			final StringBuilder ope = new StringBuilder();
			ope.append(new ButtonElement($m("MyWorkviewsTPage。0")).setOnclick(JS
					.loc(getWorkviewSentUrl(cp, sent))));
			return ope.toString();
		}

		private String getWorkviewSentUrl(final ComponentParameter cp, final WorkviewSentBean sent) {
			ID workviewId;
			if ((workviewId = sent.getWorkviewId()) != null) {
				return uFactory.getUrl(cp, WorkflowViewPage.class, "workviewId=" + workviewId);
			} else {
				return uFactory.getUrl(cp, WorkflowFormPage.class,
						wfwService.getBean(sent.getWorkitemId()));
			}
		}
	}
}