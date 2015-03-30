package net.simpleframework.workflow.web.page.org2;

import net.simpleframework.mvc.PageParameter;
import net.simpleframework.workflow.engine.ProcessBean;
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
	protected void addComponents(final PageParameter pp) {
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
		return WorkflowGraphUtils.toGraphHTML(pp, process);
	}
}