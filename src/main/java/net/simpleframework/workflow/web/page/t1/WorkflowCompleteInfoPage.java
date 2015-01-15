package net.simpleframework.workflow.web.page.t1;

import static net.simpleframework.common.I18n.$m;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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
		final List<ActivityBean> nextActivities = aService.getLastNextActivities(wService
				.getActivity(WorkflowUtils.getWorkitemBean(pp)));
		if (nextActivities.size() > 0) {
			sb.append("<table>");
			for (final ActivityBean next : nextActivities) {
				sb.append("<tr>");
				sb.append("<td class='task'>").append(next).append("</td>");
				sb.append("<td>");
				int i = 0;
				for (final WorkitemBean workitem2 : wService
						.getWorkitems(next, EWorkitemStatus.running)) {
					if (i++ > 0) {
						sb.append(", ");
					}
					sb.append(workitem2.getUserText());
				}
				sb.append("</td></tr>");
			}
			sb.append("</table>");
		} else {
			sb.append(new BlockElement().setText($m("WorkflowCompleteInfoPage.1")).addStyle(
					"margin: 12px;"));
		}
		sb.append(" </div>");
		sb.append("</div>");
		return sb.toString();
	}
}
