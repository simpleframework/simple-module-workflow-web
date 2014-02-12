package net.simpleframework.workflow.web.page.t1;

import static net.simpleframework.common.I18n.$m;

import java.io.IOException;
import java.util.Map;

import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.mvc.PageMapping;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.BlockElement;
import net.simpleframework.workflow.engine.ActivityBean;
import net.simpleframework.workflow.engine.EWorkitemStatus;
import net.simpleframework.workflow.engine.WorkitemBean;
import net.simpleframework.workflow.web.page.WorkflowUtils;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
@PageMapping(url = "/workflow/completeInfo")
public class WorkflowCompleteInfoPage extends AbstractWorkflowFormPage {

	@Override
	protected String toHtml(final PageParameter pp, final Map<String, Object> variables,
			final String currentVariable) throws IOException {
		final StringBuilder sb = new StringBuilder();
		sb.append("<div class='WorkflowCompleteInfoPage'>");
		sb.append(" <div class='l1'>#(WorkflowCompleteInfoPage.0)</div>");
		sb.append(" <div class='l2'>");
		final IDataQuery<ActivityBean> dq = aService.getNextActivities(wService
				.getActivity(WorkflowUtils.getWorkitemBean(pp)));
		if (dq.getCount() > 0) {
			sb.append("<table>");
			ActivityBean next;
			while ((next = dq.next()) != null) {
				sb.append("<tr>");
				sb.append("<td class='task'>").append(aService.getTaskNode(next)).append("</td>");
				sb.append("<td>");
				final IDataQuery<WorkitemBean> dq2 = wService.getWorkitemList(next,
						EWorkitemStatus.running);
				WorkitemBean workitem2;
				int i = 0;
				while ((workitem2 = dq2.next()) != null) {
					if (i++ > 0) {
						sb.append(", ");
					}
					sb.append(pp.getUser(workitem2.getUserId()));
				}
				sb.append("</td></tr>");
			}
			sb.append("</table>");
		} else {
			sb.append(new BlockElement().setText($m("WorkflowCompleteInfoPage.1")).setStyle(
					"margin: 12px;"));
		}
		sb.append(" </div>");
		sb.append("</div>");
		return sb.toString();
	}
}
