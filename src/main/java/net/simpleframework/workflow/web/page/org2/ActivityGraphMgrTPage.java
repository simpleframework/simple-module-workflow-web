package net.simpleframework.workflow.web.page.org2;

import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ui.pager.TablePagerBean;
import net.simpleframework.workflow.engine.bean.ProcessBean;
import net.simpleframework.workflow.web.WorkflowUtils;
import net.simpleframework.workflow.web.page.t1.WorkflowGraphMonitorPage;
import net.simpleframework.workflow.web.page.t1.WorkflowGraphUtils;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class ActivityGraphMgrTPage extends ActivityMgrTPage {

	@Override
	protected void onForward(final PageParameter pp) {
		super.onForward(pp);
		pp.addImportCSS(WorkflowGraphMonitorPage.class, "/monitor.css");
	}

	@Override
	protected TablePagerBean addTablePagerBean(final PageParameter pp) {
		return (TablePagerBean) super.addTablePagerBean(pp)
				.setContainerId("ActivityGraphMgrTPage_tbl").setHandlerClass(_ActivityGraphTbl.class);
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
		final ProcessBean process = WorkflowUtils.getProcessBean(pp);
		final StringBuilder sb = new StringBuilder();
		sb.append(WorkflowGraphUtils.toGraphHTML(pp, process,
				new KVMap().add("tbl", "ActivityMgrTPage_tbl")));
		sb.append("<div id='ActivityGraphMgrTPage_tbl'></div>");
		return sb.toString();
	}

	public static class _ActivityGraphTbl extends _ActivityTbl {
		@Override
		public IDataQuery<?> createDataObjectQuery(final ComponentParameter cp) {
			return createDataObjectQuery_bytask(cp);
		}
	}
}