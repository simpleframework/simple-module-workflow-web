package net.simpleframework.workflow.web;

import net.simpleframework.common.StringUtils;
import net.simpleframework.mvc.AbstractMVCPage;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.UrlsCache;
import net.simpleframework.workflow.engine.WorkitemBean;
import net.simpleframework.workflow.web.page.MyInitiateItemsTPage;
import net.simpleframework.workflow.web.page.MyWorklistTPage;
import net.simpleframework.workflow.web.page.t1.WorkflowCompleteInfoPage;
import net.simpleframework.workflow.web.page.t1.WorkflowFormPage;
import net.simpleframework.workflow.web.page.t1.WorkflowGraphMonitorPage;
import net.simpleframework.workflow.web.page.t1.WorkflowMonitorPage;
import net.simpleframework.workflow.web.page.t2.AbstractWorkPage.MyInitiateItemsPage;
import net.simpleframework.workflow.web.page.t2.AbstractWorkPage.MyWorklistPage;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class WorkflowUrlsFactory extends UrlsCache {

	public WorkflowUrlsFactory() {
		put(MyWorklistTPage.class, MyWorklistPage.class);
		put(MyInitiateItemsTPage.class, MyInitiateItemsPage.class);

		put(WorkflowFormPage.class);
		put(WorkflowCompleteInfoPage.class);
		put(WorkflowMonitorPage.class);
		put(WorkflowGraphMonitorPage.class);
	}

	public String getUrl(final PageParameter pp, final Class<? extends AbstractMVCPage> mClass,
			final WorkitemBean workitem) {
		return getUrl(pp, mClass, workitem, null);
	}

	public String getUrl(final PageParameter pp, final Class<? extends AbstractMVCPage> mClass,
			final WorkitemBean workitem, final String params) {
		return getUrl(pp, mClass,
				StringUtils.join(new String[] { "workitemId=" + workitem.getId(), params }, "&"));
	}
}
