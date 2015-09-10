package net.simpleframework.workflow.web.component.comments.mgr2;

import static net.simpleframework.common.I18n.$m;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.common.web.html.HtmlUtils;
import net.simpleframework.ctx.trans.Transaction;
import net.simpleframework.module.common.web.page.AbstractDescPage;
import net.simpleframework.module.common.web.page.AbstractMgrTPage;
import net.simpleframework.mvc.IForward;
import net.simpleframework.mvc.JavascriptForward;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.ButtonElement;
import net.simpleframework.mvc.common.element.ElementList;
import net.simpleframework.mvc.common.element.InputElement;
import net.simpleframework.mvc.common.element.LinkButton;
import net.simpleframework.mvc.common.element.RowField;
import net.simpleframework.mvc.common.element.SpanElement;
import net.simpleframework.mvc.common.element.TabButton;
import net.simpleframework.mvc.common.element.TabButtons;
import net.simpleframework.mvc.common.element.TableRow;
import net.simpleframework.mvc.common.element.TableRows;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.base.ajaxrequest.AjaxRequestBean;
import net.simpleframework.mvc.component.ui.pager.EPagerBarLayout;
import net.simpleframework.mvc.component.ui.pager.TablePagerBean;
import net.simpleframework.mvc.component.ui.pager.TablePagerColumn;
import net.simpleframework.mvc.component.ui.pager.db.AbstractDbTablePagerHandler;
import net.simpleframework.mvc.template.AbstractTemplatePage;
import net.simpleframework.workflow.engine.IWorkflowContext;
import net.simpleframework.workflow.engine.IWorkflowServiceAware;
import net.simpleframework.workflow.engine.comment.WfCommentLog;
import net.simpleframework.workflow.engine.comment.WfCommentLog.ELogType;
import net.simpleframework.workflow.web.component.comments.IWfCommentHandler;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class MyCommentsMgrTPage extends AbstractMgrTPage implements IWorkflowServiceAware {

	@Override
	protected void onForward(final PageParameter pp) throws Exception {
		super.onForward(pp);

		pp.addImportCSS(IWfCommentHandler.class, "/my_comment.css");

		final TablePagerBean tablePager = (TablePagerBean) addTablePagerBean(pp,
				"MyCommentsMgrTPage_tbl").setFilter(false).setSort(false)
				.setPagerBarLayout(EPagerBarLayout.none).setContainerId("idMyCommentsMgrTPage_tbl")
				.setHandlerClass(MyCommentsTbl.class);
		tablePager.addColumn(new TablePagerColumn("comment", $m("MyCommentsMgrTPage.1")))
				.addColumn(TablePagerColumn.DATE("createDate", $m("MyCommentsMgrTPage.2")))
				.addColumn(TablePagerColumn.OPE(105));

		// 删除
		addDeleteAjaxRequest(pp, "MyCommentsMgrTPage_delete");

		// 编辑
		final AjaxRequestBean ajaxRequest = addAjaxRequest(pp, "MyCommentsMgrTPage_editPage",
				CommentEditPage.class);
		addWindowBean(pp, "MyCommentsMgrTPage_edit", ajaxRequest)
				.setTitle($m("MyCommentsMgrTPage.3")).setHeight(280).setWidth(480);

	}

	@Transaction(context = IWorkflowContext.class)
	public IForward doDelete(final ComponentParameter cp) {
		final Object[] ids = StringUtils.split(cp.getParameter("id"));
		wfclService.delete(ids);
		return _jsTableRefresh();
	}

	@Override
	public ElementList getLeftElements(final PageParameter pp) {
		return ElementList.of(LinkButton.addBtn()
				.setOnclick("$Actions['MyCommentsMgrTPage_edit']();"));
	}

	@Override
	public TabButtons getTabButtons(final PageParameter pp) {
		return TabButtons.of(new TabButton($m("MyCommentsMgrTPage.0")));
	}

	@Override
	protected String toHtml(final PageParameter pp, final Map<String, Object> variables,
			final String currentVariable) throws IOException {
		final StringBuilder sb = new StringBuilder();
		sb.append("<div class='MyCommentsMgrTPage'>");
		sb.append(" <div id='idMyCommentsMgrTPage_tbl'></div>");
		sb.append("</div>");
		return sb.toString();
	}

	public static class MyCommentsTbl extends AbstractDbTablePagerHandler {
		@Override
		public IDataQuery<?> createDataObjectQuery(final ComponentParameter cp) {
			return wfclService.queryLogs(cp.getLoginId(), ELogType.collection);
		}

		@Override
		protected Map<String, Object> getRowData(final ComponentParameter cp, final Object dataObject) {
			final WfCommentLog log = (WfCommentLog) dataObject;
			final KVMap kv = new KVMap();
			kv.add("comment",
					HtmlUtils.convertHtmlLines(StringUtils.replace(log.getCcomment(), " ", "&nbsp;")))
					.add("createDate", log.getCreateDate());
			kv.add(TablePagerColumn.OPE, toOpeHTML(cp, log));
			return kv;
		}

		protected String toOpeHTML(final ComponentParameter cp, final WfCommentLog log) {
			final StringBuilder sb = new StringBuilder();
			final Object id = log.getId();
			sb.append(ButtonElement.editBtn().setOnclick(
					"$Actions['MyCommentsMgrTPage_edit']('logId=" + id + "');"));
			sb.append(SpanElement.SPACE);
			sb.append(ButtonElement.deleteBtn().setOnclick(
					"$Actions['MyCommentsMgrTPage_delete']('id=" + id + "');"));
			return sb.toString();
		}
	}

	static JavascriptForward _jsTableRefresh() {
		return new JavascriptForward("$Actions['MyCommentsMgrTPage_tbl']();");
	}

	static WfCommentLog getCommentLog(final PageParameter pp) {
		return AbstractTemplatePage.getCacheBean(pp, wfclService, "logId");
	}

	public static class CommentEditPage extends AbstractDescPage {

		@Override
		public JavascriptForward onSave(final ComponentParameter cp) throws Exception {
			WfCommentLog log = getCommentLog(cp);
			final boolean insert = log == null;
			if (insert) {
				log = wfclService.createBean();
				log.setLogType(ELogType.collection);
				log.setUserId(cp.getLoginId());
				log.setCreateDate(new Date());
			}
			log.setCcomment(cp.getParameter("sl_description"));
			if (insert) {
				wfclService.insert(log);
			} else {
				wfclService.update(new String[] { "ccomment" }, log);
			}
			return _jsTableRefresh().append("$Actions['MyCommentsMgrTPage_edit'].close();");
		}

		@Override
		protected TableRows getTableRows(final PageParameter pp) {
			final InputElement ta = createTextarea(pp);
			final WfCommentLog log = getCommentLog(pp);
			if (log != null) {
				ta.setValue(log.getCcomment());
			}
			return TableRows.of(new TableRow(new RowField($m("MyCommentsMgrTPage.1"), ta)));
		}
	}
}