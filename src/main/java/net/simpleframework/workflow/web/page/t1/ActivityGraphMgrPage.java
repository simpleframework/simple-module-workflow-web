package net.simpleframework.workflow.web.page.t1;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.ado.query.ListDataQuery;
import net.simpleframework.common.StringUtils;
import net.simpleframework.mvc.PageMapping;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ui.pager.TablePagerBean;
import net.simpleframework.workflow.engine.ActivityBean;
import net.simpleframework.workflow.engine.ProcessBean;
import net.simpleframework.workflow.web.WorkflowUtils;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
@PageMapping(url = "/workflow/mgr/activity/graph")
public class ActivityGraphMgrPage extends ActivityMgrPage {

	@Override
	protected void onForward(final PageParameter pp) {
		super.onForward(pp);

		pp.addImportCSS(ActivityGraphMgrPage.class, "/monitor.css");
	}

	@Override
	protected TablePagerBean addTablePagerBean(final PageParameter pp) {
		final TablePagerBean tablePager = (TablePagerBean) super.addTablePagerBean(pp)
				.setShowFilterBar(false).setContainerId("idWorkflowGraphMonitorPage_tbl")
				.setName("WorkflowGraphMonitorPage_tbl").setHandlerClass(ActivityGraphTbl.class);
		return tablePager;
	}

	@Override
	protected String toHtml(final PageParameter pp, final Map<String, Object> variables,
			final String variable) throws IOException {
		final StringBuilder sb = new StringBuilder();
		final String gHTML = WorkflowGraphUtils.toGraphHTML(pp, WorkflowUtils.getProcessBean(pp));
		sb.append("<div align='center' class='ActivityMgrPage'>");
		sb.append(" <div class='tb'>").append(gHTML).append("</div>");
		sb.append("</div>");
		return sb.toString();
	}

	public static class ActivityGraphTbl extends ActivityTbl {

		@Override
		public IDataQuery<?> createDataObjectQuery(final ComponentParameter cp) {
			final ProcessBean process = WorkflowUtils.getProcessBean(cp);
			if (process != null) {
				cp.addFormParameter("processId", process.getId());
			}
			final String taskid = cp.getParameter("taskid");
			cp.addFormParameter("taskid", taskid);
			if (StringUtils.hasText(taskid)) {
				final List<ActivityBean> list = toTreeList(aService.getActivities(process, taskid));
				setRelativeDate(cp, list);
				return new ListDataQuery<ActivityBean>(list);
			}
			return null;
		}
	}
}
