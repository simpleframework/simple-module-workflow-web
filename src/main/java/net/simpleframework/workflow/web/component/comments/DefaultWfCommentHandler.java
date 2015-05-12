package net.simpleframework.workflow.web.component.comments;

import static net.simpleframework.common.I18n.$m;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.simpleframework.ado.query.DataQueryUtils;
import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.common.DateUtils;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.coll.ArrayUtils;
import net.simpleframework.common.web.html.HtmlUtils;
import net.simpleframework.ctx.permission.IPermissionHandler;
import net.simpleframework.ctx.permission.PermissionDept;
import net.simpleframework.ctx.permission.PermissionUser;
import net.simpleframework.mvc.common.element.Checkbox;
import net.simpleframework.mvc.common.element.InputElement;
import net.simpleframework.mvc.common.element.Radio;
import net.simpleframework.mvc.common.element.SpanElement;
import net.simpleframework.mvc.component.ComponentHandlerEx;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.workflow.engine.IActivityService;
import net.simpleframework.workflow.engine.IWorkflowContextAware;
import net.simpleframework.workflow.engine.bean.ProcessBean;
import net.simpleframework.workflow.engine.bean.WorkitemBean;
import net.simpleframework.workflow.engine.comment.IWfCommentLogService;
import net.simpleframework.workflow.engine.comment.IWfCommentService;
import net.simpleframework.workflow.engine.comment.WfComment;
import net.simpleframework.workflow.engine.comment.WfCommentLog;
import net.simpleframework.workflow.engine.comment.WfCommentLog.ELogType;
import net.simpleframework.workflow.schema.AbstractTaskNode;
import net.simpleframework.workflow.schema.Node;
import net.simpleframework.workflow.schema.ProcessNode;
import net.simpleframework.workflow.schema.UserNode;
import net.simpleframework.workflow.web.WorkflowUtils;
import net.simpleframework.workflow.web.component.comments.WfCommentBean.EGroupBy;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class DefaultWfCommentHandler extends ComponentHandlerEx implements IWfCommentHandler,
		IWorkflowContextAware {

	protected ProcessBean getProcessBean(final ComponentParameter cp) {
		return WorkflowUtils.getProcessBean(cp);
	}

	@Override
	public IDataQuery<WfComment> comments(final ComponentParameter cp) {
		final ProcessBean processBean = getProcessBean(cp);
		if (processBean == null) {
			return DataQueryUtils.nullQuery();
		}
		return workflowContext.getCommentService().queryComments(processBean.getId());
	}

	@Override
	public void onSave(final ComponentParameter cp) {
		final WorkitemBean workitem = WorkflowUtils.getWorkitemBean(cp);
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

	protected InputElement createCommentTa(final ComponentParameter cp, final WorkitemBean workitem) {
		final InputElement ele = InputElement.textarea().setRows(4).setName("ta_wfcomment")
				.setId("ta_wfcomment").addAttribute("maxlength", cp.getBeanProperty("maxlength"));
		final WfComment bean = workflowContext.getCommentService().getCurComment(workitem);
		if (bean != null) {
			ele.setValue(bean.getCcomment());
		}
		return ele;
	}

	protected Map<String, String[]> getTasknames(final ComponentParameter cp,
			final WorkitemBean workitem) {
		final Map<String, String[]> data = new LinkedHashMap<String, String[]>();
		final IActivityService aService = workflowContext.getActivityService();
		final AbstractTaskNode tasknode = aService.getTaskNode(aService.getBean(workitem
				.getActivityId()));
		for (final Node node : ((ProcessNode) tasknode.getParent()).nodes()) {
			if (node instanceof UserNode) {
				final String name = node.getName();
				if (StringUtils.hasText(name)) {
					data.put(node.getText(), new String[] { name });
				}
			}
		}
		return data;
	}

	private Map<String, List<WfComment>> comments_map(final ComponentParameter cp,
			final WorkitemBean workitem, final EGroupBy groupBy) {
		final Map<String, List<WfComment>> data = new LinkedHashMap<String, List<WfComment>>();
		Map<String, String[]> tasknames = null;
		final IDataQuery<WfComment> dq = comments(cp);
		final IPermissionHandler phdl = cp.getPermission();
		WfComment comment;
		while ((comment = dq.next()) != null) {
			String key = null;
			if (groupBy == EGroupBy.dept) {
				final PermissionDept dept = phdl.getDept(comment.getDeptId());
				if (dept == null) {
					continue;
				}
				key = dept.getText();
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
	public String toHTML(final ComponentParameter cp) {
		final WorkitemBean workitem = WorkflowUtils.getWorkitemBean(cp);

		EGroupBy groupBy = cp.getEnumParameter(EGroupBy.class, "groupBy");
		if (groupBy == null) {
			groupBy = (EGroupBy) cp.getBeanProperty("groupBy");
		}

		final String commentName = cp.getComponentName();
		final StringBuilder sb = new StringBuilder();
		final boolean editable = (Boolean) cp.getBeanProperty("editable");
		if (editable) {
			sb.append("<div class='ta'>");
			sb.append(createCommentTa(cp, workitem));
			sb.append("</div>");
			sb.append("<div class='btns clearfix'>");
			sb.append(" <div class='left'>");
			sb.append("   <a class='simple_btn2' onclick=\"$Actions['").append(commentName)
					.append("_log_popup']();\">#(DefaultWfCommentHandler.0)</a>");
			sb.append("	  <span class='ltxt'></span>");
			sb.append(" </div>");
			sb.append(" <div class='right'>");
			int i = 0;
			for (final EGroupBy g : EGroupBy.values()) {
				final String rn = "comments_groupby";
				sb.append(
						new Radio(rn + i++, g)
								.setChecked(groupBy == g)
								.setOnclick(
										"location.href = location.href.addParameter('groupBy=" + g.name()
												+ "');").setName(rn)).append(SpanElement.SPACE);
			}
			sb.append(SpanElement.SPACE(20)).append(
					new Checkbox("id" + commentName + "_addCheck", $m("DefaultWfCommentHandler.1"))
							.setName("cb_wfcomment").setValue("true"));
			sb.append(" </div>");
			sb.append("</div>");
		}

		final WfComment comment2 = workflowContext.getCommentService().getCurComment(workitem);

		final StringBuilder sb2 = new StringBuilder();
		if (groupBy == EGroupBy.none) {
			int i = 0;
			final IDataQuery<WfComment> dq = comments(cp);
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
				final List<WfComment> list = new ArrayList<WfComment>();
				for (final WfComment comment : e.getValue()) {
					if (editable && comment2 != null && comment2.equals(comment)) {
						continue;
					}
					list.add(comment);
				}
				if (list.size() > 0) {
					sb2.append("<div class='comment-group-item'>").append(e.getKey()).append("</div>");
					int i = 0;
					for (final WfComment comment : list) {
						sb2.append(toCommentItemHTML(cp, comment, i++ == 0, groupBy));
					}
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
			sb2.append("@").append(ouser.getDept().getText());
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
