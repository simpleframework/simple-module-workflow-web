package net.simpleframework.workflow.web.component.comments;

import static net.simpleframework.common.I18n.$m;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.common.DateUtils;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.coll.ArrayUtils;
import net.simpleframework.common.web.html.HtmlUtils;
import net.simpleframework.ctx.permission.Dept;
import net.simpleframework.ctx.permission.IPermissionHandler;
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
import net.simpleframework.workflow.web.component.comments.WfCommentBean.EGroupBy;

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

	protected Map<String, String[]> getTasknames(final ComponentParameter cp,
			final WorkitemBean workitem) {
		final Map<String, String[]> data = new LinkedHashMap<String, String[]>();
		return data;
	}

	private Map<String, List<WfComment>> comments_map(final ComponentParameter cp,
			final WorkitemBean workitem, final EGroupBy groupBy) {
		final Map<String, List<WfComment>> data = new LinkedHashMap<String, List<WfComment>>();
		Map<String, String[]> tasknames = null;
		final IDataQuery<WfComment> dq = comments(cp, workitem);
		final IPermissionHandler phdl = cp.getPermission();
		WfComment comment;
		while ((comment = dq.next()) != null) {
			String key = null;
			if (groupBy == EGroupBy.dept) {
				final Dept dept = phdl.getDept(comment.getDeptId());
				if (dept == null) {
					continue;
				}
				key = dept.toString();
			} else if (groupBy == EGroupBy.taskname) {
				if (tasknames == null) {
					tasknames = getTasknames(cp, workitem);
				}
				if (tasknames != null) {
					for (final Map.Entry<String, String[]> e : tasknames.entrySet()) {
						if (ArrayUtils.contains(e.getValue(), comment.getTaskname())) {
							key = e.getKey();
							break;
						}
					}
				}
			}

			if (key != null) {
				List<WfComment> comments = data.get(key);
				if (comments == null) {
					data.put(key, comments = new ArrayList<WfComment>());
				}
				comments.add(comment);
			}
		}
		return data;
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

		final EGroupBy groupBy = (EGroupBy) cp.getBeanProperty("groupBy");
		final WfComment comment2 = workflowContext.getCommentService().getCurComment(workitem);

		final StringBuilder sb2 = new StringBuilder();
		if (groupBy == EGroupBy.none) {
			int i = 0;
			final IDataQuery<WfComment> dq = comments(cp, workitem);
			WfComment comment;
			while ((comment = dq.next()) != null) {
				if (editable && comment2 != null && comment2.equals(comment)) {
					continue;
				}
				sb2.append(toCommentItemHTML(cp, comment, i++ == 0, groupBy));
			}
		} else {
			for (final Map.Entry<String, List<WfComment>> e : comments_map(cp, workitem, groupBy)
					.entrySet()) {
				sb2.append("<div class='comment-group-item'>").append(e.getKey()).append("</div>");
				int i = 0;
				for (final WfComment comment : e.getValue()) {
					if (editable && comment2 != null && comment2.equals(comment)) {
						continue;
					}
					sb2.append(toCommentItemHTML(cp, comment, i++ == 0, groupBy));
				}
			}
		}

		if (sb2.length() > 0) {
			sb.append("<div class='comment-list");
			if (groupBy != EGroupBy.none) {
				sb.append(" comment-group");
			}
			sb.append("'>");
			sb.append(sb2);
			sb.append("</div>");
		}
		return sb.toString();
	}

	protected String toCommentItemHTML(final ComponentParameter cp, final WfComment comment,
			final boolean first, final EGroupBy groupBy) {
		final StringBuilder sb2 = new StringBuilder();
		sb2.append("<div class='comment-item");
		if (first) {
			sb2.append(" item-first");
		}
		sb2.append("'>");
		sb2.append("<img src='").append(cp.getPhotoUrl()).append("' />");
		sb2.append(" <div class='i1'>").append(HtmlUtils.convertHtmlLines(comment.getCcomment()))
				.append("</div>");
		sb2.append(" <div class='i2 clearfix'>");
		final PermissionUser ouser = cp.getUser(comment.getUserId());
		sb2.append("  <div class='left'>").append(ouser);
		if (groupBy != EGroupBy.dept) {
			sb2.append("@").append(ouser.toDeptText());
		}
		sb2.append(", ").append(DateUtils.getRelativeDate(comment.getCreateDate())).append("</div>");
		sb2.append("  <div class='right'>").append(comment.getTaskname()).append("</div>");
		sb2.append(" </div>");
		sb2.append("</div>");
		return sb2.toString();
	}

	@Override
	public Map<String, Object> getFormParameters(final ComponentParameter cp) {
		final Map<String, Object> data = super.getFormParameters(cp);
		data.put(WfCommentUtils.BEAN_ID, cp.getParameter(WfCommentUtils.BEAN_ID));
		return data;
	}
}
