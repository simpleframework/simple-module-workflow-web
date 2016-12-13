package net.simpleframework.workflow.web.component.comments;

import static net.simpleframework.common.I18n.$m;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.common.Convert;
import net.simpleframework.common.web.JavascriptUtils;
import net.simpleframework.common.web.html.HtmlUtils;
import net.simpleframework.mvc.PageRequestResponse;
import net.simpleframework.mvc.common.element.ButtonElement;
import net.simpleframework.mvc.common.element.InputElement;
import net.simpleframework.mvc.common.element.LinkElement;
import net.simpleframework.mvc.common.element.SpanElement;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.workflow.engine.IWorkflowContextAware;
import net.simpleframework.workflow.engine.comment.IWfCommentLogService;
import net.simpleframework.workflow.engine.comment.WfComment;
import net.simpleframework.workflow.engine.comment.WfCommentLog;
import net.simpleframework.workflow.engine.comment.WfCommentLog.ELogType;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public abstract class WfCommentUtils implements IWorkflowContextAware {
	public static final String BEAN_ID = "wf_comment_@bid";

	public static ComponentParameter get(final HttpServletRequest request,
			final HttpServletResponse response) {
		return ComponentParameter.get(request, response, BEAN_ID);
	}

	public static ComponentParameter get(final PageRequestResponse rRequest) {
		return ComponentParameter.get(rRequest, BEAN_ID);
	}

	public static String toCommentLogsHTML(final ComponentParameter cp) {
		final StringBuilder sb = new StringBuilder();
		sb.append("<div class='cl_tabs'>");
		sb.append(new LinkElement($m("wf_comment_log.0"), "active")
				.setOnclick("wf_comment_logtab(this, 'lt=collection');"));
		sb.append(new LinkElement($m("wf_comment_log.1"))
				.setOnclick("wf_comment_logtab(this, 'lt=history');"));
		sb.append("</div>");
		sb.append("<div class='cl_list'>").append(toCommentLogs_ListHTML(cp)).append("</div>");

		final IWfCommentHandler cHdl = (IWfCommentHandler) cp.getComponentHandler();
		sb.append("<div class='cl_btns clearfix'>");
		sb.append(" <div class='left'>");
		sb.append(
				LinkElement.style2($m("WfCommentUtils.2")).blank().setHref(cHdl.getMycommentsUrl(cp)));
		sb.append(" </div>");
		sb.append(" <div class='right'>");
		sb.append(ButtonElement.okBtn().setOnclick("wf_comment_okclick();"));
		sb.append(SpanElement.SPACE).append(ButtonElement.closeBtn());
		sb.append(" </div>");
		sb.append("</div>");
		return sb.toString();
	}

	static String toCommentLogs_ListHTML(final ComponentParameter cp) {
		ELogType logType = cp.getEnumParameter(ELogType.class, "lt");
		if (logType == null) {
			logType = ELogType.collection;
		}
		final StringBuilder sb = new StringBuilder();
		// final IWfCommentService cService = workflowContext.getCommentService();
		final IWfCommentLogService lService = workflowContext.getCommentLogService();
		final IDataQuery<WfCommentLog> dq = lService.queryLogs(cp.getLoginId(), logType);
		WfCommentLog log;
		while ((log = dq.next()) != null) {
			sb.append(
					"<div class='litem' onclick='wf_comment_itemclick(this);' ondblclick='wf_comment_itemdblclick(this);'>");
			final String ccomment = log.getCcomment();
			sb.append(" <div class='l1'>").append(HtmlUtils.convertHtmlLines(ccomment));
			sb.append(InputElement.textarea().setStyle("display:none;").setValue(ccomment));
			sb.append(" </div>");
			// sb.append(" <div class='l2'>");
			// final WfComment comment = cService.getBean(log.getCommentId());
			// if (comment != null) {
			// sb.append(comment.getTaskname()).append($m("WfCommentUtils.0"));
			// }
			// sb.append(DateUtils.getRelativeDate(log.getCreateDate())).append("</div>");
			sb.append(" <span class='act' style='display:none;'>");
			if (logType == ELogType.history) {
				sb.append(new SpanElement().setClassName("copy")
						.setOnclick("wf_comment_itemcopy(this, 'logid=" + log.getId() + "');")
						.setTitle($m("WfCommentUtils.1")));
			}
			sb.append(new SpanElement().setClassName("del")
					.setOnclick("wf_comment_itemdel(this, 'logid=" + log.getId() + "');"));
			sb.append(" </span>");
			sb.append("</div>");
		}
		sb.append(JavascriptUtils.wrapScriptTag("wf_comment_init();"));
		return sb.toString();
	}

	public static String toCommentEditHTML(final ComponentParameter cp) {
		final StringBuilder sb = new StringBuilder();
		final WfComment comment = wfcService.getBean(cp.getParameter("commentId"));
		sb.append(InputElement.hidden("commentId").setVal(comment.getId()));
		sb.append("<div class='c'>");
		sb.append(" <div class='ta'>")
				.append(InputElement.textarea("ce_ccomment").setRows(7).setText(comment.getCcomment()))
				.append("</div>");
		sb.append(" <br><div class='txt-wrap'>").append(
				new InputElement("ce_cdate").setText(Convert.toDateTimeString(comment.getCreateDate())))
				.append("</div>");
		sb.append("</div>");
		sb.append("<div class='b'>");
		sb.append(
				ButtonElement.saveBtn().setOnclick("$Actions['" + cp.getComponentName() + "_save']();"))
				.append(SpanElement.SPACE).append(ButtonElement.closeBtn());
		sb.append("</div>");
		return sb.toString();
	}
}
