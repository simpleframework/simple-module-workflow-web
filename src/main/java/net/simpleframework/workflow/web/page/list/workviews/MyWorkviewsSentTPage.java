package net.simpleframework.workflow.web.page.list.workviews;

import static net.simpleframework.common.I18n.$m;

import java.io.IOException;
import java.util.Map;

import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.common.Convert;
import net.simpleframework.common.ID;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.ButtonElement;
import net.simpleframework.mvc.common.element.ImageElement;
import net.simpleframework.mvc.common.element.JS;
import net.simpleframework.mvc.common.element.LinkElement;
import net.simpleframework.mvc.common.element.SpanElement;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.base.ajaxrequest.AjaxRequestBean;
import net.simpleframework.mvc.component.ui.pager.TablePagerBean;
import net.simpleframework.mvc.component.ui.pager.TablePagerColumn;
import net.simpleframework.mvc.template.AbstractTemplatePage;
import net.simpleframework.workflow.engine.bean.ProcessBean;
import net.simpleframework.workflow.engine.bean.WorkitemBean;
import net.simpleframework.workflow.engine.bean.WorkviewBean;
import net.simpleframework.workflow.engine.bean.WorkviewSentBean;
import net.simpleframework.workflow.web.WorkflowUtils;
import net.simpleframework.workflow.web.page.t1.form.WorkflowFormPage;
import net.simpleframework.workflow.web.page.t1.form.WorkflowViewPage;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class MyWorkviewsSentTPage extends MyWorkviewsTPage {

	@Override
	protected void onForward(final PageParameter pp) throws Exception {
		super.onForward(pp);

		// 树视图
		final AjaxRequestBean ajaxRequest = addAjaxRequest(pp, "MyWorkviewsSentTPage_tracingPage",
				WorkviewTracing.class);
		addWindowBean(pp, "MyWorkviewsSentTPage_tracing", ajaxRequest)
				.setTitle($m("MyWorkviewsSentTPage.1")).setHeight(480).setWidth(400);
	}

	@Override
	protected TablePagerBean addTablePagerBean(final PageParameter pp) {
		final TablePagerBean tablePager = addTablePagerBean(pp, "MyWorkviewsTPage_tbl",
				MyWorkviewsSentTbl.class);
		tablePager.addColumn(TC_ICON()).addColumn(TC_TITLE())
				.addColumn(new TablePagerColumn("rev", $m("MyFinalWorklistTPage.0"), 200).center()
						.setNowrap(false).setFilterSort(false))
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
			ImageElement img = null;
			if (sent.getWorkviewId() != null) {
				img = MARK_FORWARD(cp);
			}
			return img;
		}

		@Override
		protected Map<String, Object> getRowData(final ComponentParameter cp,
				final Object dataObject) {
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
							final IDataQuery<WorkviewBean> dq = wfvService
									.getWorkviewsListBySent(sent.getId());
							if (dq.getCount() > 0) {
								WorkviewBean workview;
								int i = 0;
								final StringBuilder rev = new StringBuilder();
								while ((workview = dq.next()) != null) {
									if (i++ > 0) {
										rev.append(", ");
									}
									rev.append(workview.getUserText());
								}
								return rev.toString();
							}
							return null;
						}
					})).add(TablePagerColumn.OPE, toOpeHTML(cp, sent));
			return row;
		}

		protected String toTopicHTML(final ComponentParameter cp, final WorkviewSentBean sent) {
			final ProcessBean process = wfpService.getBean(sent.getProcessId());
			return new LinkElement(WorkflowUtils.getProcessTitle(process))
					.setHref(getWorkviewSentUrl(cp, sent)).toString();
		}

		protected String toOpeHTML(final ComponentParameter cp, final WorkviewSentBean sent) {
			final StringBuilder ope = new StringBuilder();
			if (sent.getWorkviewId() != null) {
				ope.append(new ButtonElement($m("MyWorkviewsTPage.0"))
						.setOnclick(JS.loc(getWorkviewSentUrl(cp, sent))));
			} else {
				ope.append(new ButtonElement($m("MyWorkviewsTPage.2"))
						.setOnclick("$Actions['MyWorkviewsSentTPage_tracing']('workitemId="
								+ sent.getWorkitemId() + "');"));
			}
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

	public static class WorkviewTracing extends AbstractTemplatePage {

		@Override
		protected String toHtml(final PageParameter pp, final Map<String, Object> variables,
				final String currentVariable) throws IOException {
			final StringBuilder sb = new StringBuilder();
			final WorkitemBean workitem = WorkflowUtils.getWorkitemBean(pp);
			sb.append("<div class='WorkviewTracing'>");
			sb.append(" <div class='tb'>").append(workitem).append("</div>");
			appendWorkviewsHTML(pp, sb, wfvService.getChildren(workitem, null), 0);
			sb.append("</div>");
			return sb.toString();
		}

		private void appendWorkviewsHTML(final PageParameter pp, final StringBuilder sb,
				final IDataQuery<WorkviewBean> dq, int i) {
			i++;
			final WorkitemBean workitem = WorkflowUtils.getWorkitemBean(pp);
			WorkviewBean workview;
			while ((workview = dq.next()) != null) {
				sb.append("<div class='vitem");
				if (i == 1) {
					sb.append(" r1");
				}
				sb.append("' style='padding-left: ").append((i == 1 ? 15 : 0) + i * 8).append("px;'>");
				sb.append("<table width='100%'><tr>");
				sb.append("<td>");
				if (i > 1) {
					sb.append("|-&nbsp;");
				}
				sb.append(
						SpanElement.colora00(workview.getUserText()).setStrong(!workview.isReadMark()))
						.append(" (").append(pp.getDept(workview.getDeptId())).append(")");
				sb.append("</td><td align='right'>");
				sb.append(SpanElement.color777(Convert.toDateTimeString(workview.getCreateDate())));
				sb.append("</td></tr></table>");
				appendWorkviewsHTML(pp, sb, wfvService.getChildren(workitem, workview.getId()), i);
				sb.append("</div>");
			}
		}
	}
}