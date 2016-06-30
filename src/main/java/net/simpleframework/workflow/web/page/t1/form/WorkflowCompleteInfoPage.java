package net.simpleframework.workflow.web.page.t1.form;

import static net.simpleframework.common.I18n.$m;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.simpleframework.ctx.permission.PermissionUser;
import net.simpleframework.mvc.PageMapping;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.BlockElement;
import net.simpleframework.workflow.engine.EWorkitemStatus;
import net.simpleframework.workflow.engine.bean.ActivityBean;
import net.simpleframework.workflow.engine.bean.WorkitemBean;
import net.simpleframework.workflow.schema.AbstractTaskNode;
import net.simpleframework.workflow.web.WorkflowUtils;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
@PageMapping(url = "/workflow/completeInfo")
public class WorkflowCompleteInfoPage extends AbstractWorkflowFormPage {

	private List<ActivityBean> removeMergeNodes(final List<ActivityBean> nextActivities) {
		final List<ActivityBean> l = new ArrayList<ActivityBean>();
		if (nextActivities != null) {
			for (final ActivityBean next : nextActivities) {
				if (next.getTasknodeType() == AbstractTaskNode.TT_MERGE) {
					l.addAll(removeMergeNodes(wfaService.getLastNextActivities(next)));
				} else {
					l.add(next);
				}
			}
		}
		return l;
	}

	@Override
	protected String toHtml(final PageParameter pp, final Map<String, Object> variables,
			final String currentVariable) throws IOException {
		final StringBuilder sb = new StringBuilder();
		sb.append("<div class='WorkflowCompleteInfoPage'>");
		sb.append(" <div class='l1'>#(WorkflowCompleteInfoPage.0)</div>");
		sb.append(" <div class='l2'>");
		final List<ActivityBean> nextActivities = removeMergeNodes(wfaService
				.getLastNextActivities(wfwService.getActivity(WorkflowUtils.getWorkitemBean(pp))));
		if (nextActivities.size() > 0) {
			sb.append("<table>");
			for (final ActivityBean next : nextActivities) {
				if (next.getTasknodeType() == AbstractTaskNode.TT_MERGE) {
				}
				sb.append("<tr>");
				sb.append("<td class='task'>").append(next.getTasknodeText()).append("</td>");
				sb.append("<td>");
				int i = 0;
				for (final WorkitemBean workitem2 : wfwService.getWorkitems(next,
						EWorkitemStatus.running, EWorkitemStatus.delegate)) {
					if (i++ > 0) {
						sb.append(", ");
					}
					sb.append(workitem2.getUserText2());
					final Object uId = workitem2.getUserId2();
					final PermissionUser oUser = pp.getUser(uId);
					if (oUser.exists()) {
						sb.append(" (").append(oUser.getName()).append(")");
					}
					if (!uId.equals(workitem2.getUserId())) {
						sb.append("<br><span class='desc'>")
								.append($m("WorkflowCompleteInfoPage.2", workitem2.getUserText()))
								.append("</span>");
					}
				}
				sb.append("</td></tr>");
			}
			sb.append("</table>");
		} else {
			sb.append(new BlockElement().setClassName("winfo").setText(
					$m("WorkflowCompleteInfoPage.1")));
		}
		sb.append(" </div>");
		sb.append("</div>");
		return sb.toString();
	}
}
