package net.simpleframework.workflow.web.page.query;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import net.simpleframework.common.ID;
import net.simpleframework.ctx.permission.PermissionDept;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.ctx.permission.IPagePermissionHandler;
import net.simpleframework.mvc.template.AbstractTemplatePage;
import net.simpleframework.workflow.engine.bean.ActivityBean;
import net.simpleframework.workflow.engine.bean.ProcessBean;
import net.simpleframework.workflow.engine.bean.WorkitemBean;
import net.simpleframework.workflow.schema.AbstractTaskNode;
import net.simpleframework.workflow.web.WorkflowUtils;
import net.simpleframework.workflow.web.page.IWorkflowPageAware;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class ProcessDetailPage extends AbstractTemplatePage implements IWorkflowPageAware {

	@Override
	protected String toHtml(final PageParameter pp, final Map<String, Object> variables,
			final String currentVariable) throws IOException {
		final StringBuilder sb = new StringBuilder();
		final ID loginId = pp.getLoginId();
		final ProcessBean process = WorkflowUtils.getProcessBean(pp);
		sb.append("<div class='ProcessDetailPage'>");
		sb.append("<div class='ptitle'>").append(process).append("</div>");
		sb.append("<table class='form_tbl' cellspacing='1'>");
		sb.append("  <tr>");
		sb.append("    <td class='l'>#(MyQueryWorksTPage.13)</td>");
		sb.append("    <td class='v'>");
		final LinkedHashSet<String> dtags = new LinkedHashSet<String>();
		final LinkedHashMap<ID, Integer> utags = new LinkedHashMap<ID, Integer>();
		List<WorkitemBean> list = wfwService.getWorkitems(process, null);
		final IPagePermissionHandler hdl = pp.getPermission();
		for (int i = list.size() - 1; i >= 0; i--) {
			// 部门
			final WorkitemBean workitem = list.get(i);
			final PermissionDept dept = hdl.getDept(workitem.getDeptId2());
			dtags.add(dept.toString());

			// 用户
			final ID userId = workitem.getUserId2();
			if (userId.equals(loginId)) {
				continue;
			}
			final Integer oj = utags.get(userId);
			if (oj == null) {
				utags.put(userId, 1);
			} else {
				utags.put(userId, oj + 1);
			}
		}
		for (final String e : dtags) {
			sb.append("<span class='ptag'>").append(e).append("</span>");
		}
		sb.append("    </td>");
		sb.append("  </tr>");
		sb.append("  <tr>");
		sb.append("    <td class='l'>#(MyQueryWorksTPage.14)</td>");
		sb.append("    <td class='v'>");
		final LinkedHashMap<AbstractTaskNode, Integer> wtags = new LinkedHashMap<AbstractTaskNode, Integer>();
		list = wfwService.getWorkitems(process, loginId);
		for (int i = list.size() - 1; i >= 0; i--) {
			final ActivityBean activity = wfwService.getActivity(list.get(i));
			final AbstractTaskNode tasknode = wfaService.getTaskNode(activity);
			final Integer oj = wtags.get(tasknode);
			if (oj == null) {
				wtags.put(tasknode, 1);
			} else {
				wtags.put(tasknode, oj + 1);
			}
		}
		for (final Map.Entry<AbstractTaskNode, Integer> e : wtags.entrySet()) {
			sb.append("<span class='ptag'>");
			sb.append(e.getKey()).append(" (").append(e.getValue()).append(")");
			sb.append("</span>");
		}
		sb.append("    </td>");
		sb.append("  </tr>");
		sb.append("  <tr>");
		sb.append("    <td class='l'>#(MyQueryWorksTPage.15)</td>");
		sb.append("    <td class='v'>");
		for (final Map.Entry<ID, Integer> e : utags.entrySet()) {
			sb.append("<span class='ptag'>");
			sb.append(hdl.getUser(e.getKey())).append(" (").append(e.getValue()).append(")");
			sb.append("</span>");
		}
		sb.append("    </td>");
		sb.append("  </tr>");
		sb.append("</table>");
		sb.append("</div>");
		return sb.toString();
	}
}
