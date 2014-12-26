package net.simpleframework.workflow.web.component.comments;

import static net.simpleframework.common.I18n.$m;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.common.DateUtils;
import net.simpleframework.mvc.PageRequestResponse;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.workflow.engine.IWorkflowContextAware;
import net.simpleframework.workflow.engine.ext.IWfCommentLogService;
import net.simpleframework.workflow.engine.ext.IWfCommentService;
import net.simpleframework.workflow.engine.ext.WfComment;
import net.simpleframework.workflow.engine.ext.WfCommentLog;
import net.simpleframework.workflow.engine.ext.WfCommentLog.ELogType;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
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

	public static String toLogsHTML(final ComponentParameter cp) {
		ELogType logType = cp.getEnumParameter(ELogType.class, "lt");
		if (logType == null) {
			logType = ELogType.collection;
		}
		final StringBuilder sb = new StringBuilder();
		final IWfCommentService cService = workflowContext.getCommentService();
		final IWfCommentLogService lService = workflowContext.getCommentLogService();
		final IDataQuery<WfCommentLog> dq = lService.queryLogs(cp.getLoginId(), logType);
		WfCommentLog log;
		while ((log = dq.next()) != null) {
			sb.append("<div class='litem' ondblclick='wf_comment_itemclick(this);'>");
			sb.append(" <div class='l1'>").append(log.getCcomment()).append("</div>");
			sb.append(" <div class='l2'>");
			final WfComment comment = cService.getBean(log.getCommentId());
			if (comment != null) {
				sb.append(comment.getTaskname()).append($m("WfCommentUtils.0"));
			}
			sb.append(DateUtils.getRelativeDate(log.getCreateDate())).append("</div>");
			sb.append("</div>");
		}
		return sb.toString();
	}
}
