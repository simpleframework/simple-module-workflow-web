package net.simpleframework.workflow.web.page.t1;

import java.util.List;

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
@PageMapping(url = "/workflow/monitor/graph")
public class WorkflowGraphMonitorPage extends WorkflowMonitorPage {

	@Override
	protected void onForward(final PageParameter pp) {
		super.onForward(pp);

		pp.addImportCSS(WorkflowGraphMonitorPage.class, "/monitor.css");
	}

	@Override
	protected TablePagerBean addTablePagerBean(final PageParameter pp) {
		// pp.putParameter(G, "tasknode");
		final TablePagerBean tablePager = (TablePagerBean) super.addTablePagerBean(pp)
				.setShowFilterBar(false).setContainerId("idWorkflowGraphMonitorPage_tbl")
				.setName("WorkflowGraphMonitorPage_tbl").setHandlerClass(ActivityGraphTbl2.class);
		return tablePager;
	}

	@Override
	public void onHtmlNormalise(final PageParameter pp,
			final net.simpleframework.lib.org.jsoup.nodes.Element element) {
		super.onHtmlNormalise(pp, element);
		if (element.tagName().equalsIgnoreCase("html") && WorkflowGraphUtils.isVML(pp)) {
			element.attr("xmlns:v", "urn:schemas-microsoft-com:vml");
		}
	}

	@Override
	protected String toMonitorHTML(final PageParameter pp) {
		final ProcessBean process = wService.getProcessBean(WorkflowUtils.getWorkitemBean(pp));
		return WorkflowGraphUtils.toGraphHTML(pp, process);
	}

	public static class ActivityGraphTbl2 extends _ActivityTbl {

		@Override
		public IDataQuery<?> createDataObjectQuery(final ComponentParameter cp) {
			final ProcessBean process = getProcessBean(cp);
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
