package net.simpleframework.workflow.web.component.comments;

import java.util.Date;
import java.util.Map;

import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.common.StringUtils;
import net.simpleframework.mvc.common.element.InputElement;
import net.simpleframework.mvc.component.ComponentHandlerEx;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.workflow.engine.IWorkflowContextAware;
import net.simpleframework.workflow.engine.WorkitemBean;
import net.simpleframework.workflow.engine.ext.IWfCommentService;
import net.simpleframework.workflow.engine.ext.WfComment;

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
		WfComment bean = cService.getCurComment(workitem);
		if (bean == null) {
			bean = cService.createBean();
			bean.setCreateDate(new Date());
			bean.setUserId(workitem.getUserId2());
			bean.setContentId(workitem.getProcessId());
			bean.setWorkitemId(workitem.getId());
			bean.setDeptId(workitem.getDeptId());
			bean.setTaskname(workflowContext.getWorkitemService().getActivity(workitem)
					.getTasknodeText());
			bean.setCcomment(ccomment);
			cService.insert(bean);
		} else {
			bean.setCcomment(ccomment);
			cService.update(new String[] { "ccomment" }, bean);
		}
	}

	protected InputElement createCommentTa(final WorkitemBean workitem) {
		final InputElement ele = InputElement.textarea().setRows(3).setName("ta_wfcomment");
		final WfComment bean = workflowContext.getCommentService().getCurComment(workitem);
		if (bean != null) {
			ele.setValue(bean.getCcomment());
		}
		return ele;
	}

	@Override
	public String toHTML(final ComponentParameter cp, final WorkitemBean workitem) {
		final StringBuilder sb = new StringBuilder();
		sb.append(createCommentTa(workitem));
		sb.append("<div class='btns'>");
		sb.append("	<a>常用意见</a>");
		sb.append("</div>");
		sb.append("<div>");
		final IDataQuery<WfComment> dq = comments(cp, workitem);
		WfComment comment;
		while ((comment = dq.next()) != null) {
			sb.append("<div>");
			sb.append(comment.getCcomment());
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
