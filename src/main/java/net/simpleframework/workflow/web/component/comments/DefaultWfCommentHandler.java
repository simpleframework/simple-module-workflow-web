package net.simpleframework.workflow.web.component.comments;

import static net.simpleframework.common.I18n.$m;

import java.util.Date;
import java.util.Map;

import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.common.DateUtils;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.web.html.HtmlUtils;
import net.simpleframework.ctx.permission.PermissionUser;
import net.simpleframework.mvc.common.element.Checkbox;
import net.simpleframework.mvc.common.element.InputElement;
import net.simpleframework.mvc.component.ComponentHandlerEx;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.workflow.engine.IWorkflowContextAware;
import net.simpleframework.workflow.engine.WorkitemBean;
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
public class DefaultWfCommentHandler extends ComponentHandlerEx implements IWfCommentHandler,
		IWorkflowContextAware {

	@Override
	public IDataQuery<WfComment> comments(final ComponentParameter cp, final WorkitemBean workitem) {
		return workflowContext.getCommentService().queryComments(workitem.getProcessId());
	}

	@Override
	public void onSave(final ComponentParameter cp, final WorkitemBean workitem) {
		final String ccomment = cp.getParameter("ta_wfcomment");
		if (!StringUtils.hasText(ccomment)) {
			return;
		}

		final IWfCommentService cService = workflowContext.getCommentService();
		WfComment comment = cService.getCurComment(workitem);
		if (comment == null) {
			comment = cService.createBean();
			comment.setCreateDate(new Date());
			comment.setUserId(workitem.getUserId2());
			comment.setContentId(workitem.getProcessId());
			comment.setWorkitemId(workitem.getId());
			comment.setDeptId(workitem.getDeptId());
			comment.setTaskname(workflowContext.getWorkitemService().getActivity(workitem)
					.getTasknodeText());
			comment.setCcomment(ccomment);
			cService.insert(comment);
		} else {
			comment.setCcomment(ccomment);
			cService.update(new String[] { "ccomment" }, comment);
		}

		if (cp.getBoolParameter("cb_wfcomment")) {
			final IWfCommentLogService lService = workflowContext.getCommentLogService();
			final WfCommentLog log = lService.getLog(comment.getUserId(), comment.getCcomment(),
					ELogType.collection);
			if (log == null) {
				lService.insertLog(comment, ELogType.collection);
			}
		}
	}

	protected InputElement createCommentTa(final WorkitemBean workitem) {
		final InputElement ele = InputElement.textarea().setRows(4).setName("ta_wfcomment");
		final WfComment bean = workflowContext.getCommentService().getCurComment(workitem);
		if (bean != null) {
			ele.setValue(bean.getCcomment());
		}
		return ele;
	}

	@Override
	public String toHTML(final ComponentParameter cp, final WorkitemBean workitem) {
		final String commentName = cp.getComponentName();
		final StringBuilder sb = new StringBuilder();
		final boolean editable = (Boolean) cp.getBeanProperty("editable");
		if (editable) {
			sb.append("<div class='ta'>");
			sb.append(createCommentTa(workitem));
			sb.append("</div>");
			sb.append("<div class='btns'>");
			sb.append(" <div class='left'>").append("<a onclick=\"$Actions['").append(commentName)
					.append("_log_popup']();\">#(DefaultWfCommentHandler.0)</a>");
			sb.append(" </div>");
			sb.append(" <div class='right'>").append(
					new Checkbox("id" + commentName + "_addCheck", $m("DefaultWfCommentHandler.1"))
							.setName("cb_wfcomment").setValue("true"));
			sb.append(" </div>");
			sb.append(" <div class='clearfix'></div>");
			sb.append("</div>");
		}
		sb.append("<div class='comment-list'>");
		final IDataQuery<WfComment> dq = comments(cp, workitem);
		final WfComment comment2 = workflowContext.getCommentService().getCurComment(workitem);
		WfComment comment;
		int i = 0;
		while ((comment = dq.next()) != null) {
			if (editable && comment2 != null && comment2.equals(comment)) {
				continue;
			}
			sb.append("<div class='comment-item");
			if (i++ == 0) {
				sb.append(" item-first");
			}
			sb.append("'>");
			sb.append("<img src='").append(cp.getPhotoUrl()).append("' />");
			sb.append(" <div class='i1'>").append(HtmlUtils.convertHtmlLines(comment.getCcomment()))
					.append("</div>");
			sb.append(" <div class='i2'>");
			final PermissionUser ouser = cp.getUser(comment.getUserId());
			sb.append("  <div class='left'>").append(ouser).append("@").append(ouser.toDeptText())
					.append(", ").append(DateUtils.getRelativeDate(comment.getCreateDate()))
					.append("</div>");
			sb.append("  <div class='right'>").append(comment.getTaskname()).append("</div>");
			sb.append("  <div class='clearfix'></div>");
			sb.append(" </div>");
			sb.append("</div>");
		}
		sb.append("</div>");
		return sb.toString();
	}

	@Override
	public Map<String, Object> getFormParameters(final ComponentParameter cp) {
		final Map<String, Object> data = super.getFormParameters(cp);
		data.put(WfCommentUtils.BEAN_ID, cp.getParameter(WfCommentUtils.BEAN_ID));
		return data;
	}
}
