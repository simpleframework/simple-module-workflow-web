package net.simpleframework.workflow.web;

import net.simpleframework.mvc.AbstractMVCPage;
import net.simpleframework.mvc.common.UrlsCache;
import net.simpleframework.workflow.engine.WorkitemBean;
import net.simpleframework.workflow.web.page.AbstractWorkTPage;
import net.simpleframework.workflow.web.page.MyInitiateItemsTPage;
import net.simpleframework.workflow.web.page.MyWorklistTPage;
import net.simpleframework.workflow.web.page.t1.WorkflowCompleteInfoPage;
import net.simpleframework.workflow.web.page.t1.WorkflowFormPage;
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
		urls.put(MyWorklistTPage.class.getName(), MyWorklistPage.class);
		urls.put(MyInitiateItemsTPage.class.getName(), MyInitiateItemsPage.class);
	}

	public Class<? extends AbstractMVCPage> getWorkflowFormClass() {
		return WorkflowFormPage.class;
	}

	public String getWorkflowFormUrl(final WorkitemBean workitem) {
		return AbstractMVCPage.url(getWorkflowFormClass(), "workitemId=" + workitem.getId());
	}

	public String getWorkflowMonitorUrl(final WorkitemBean workitem) {
		return AbstractMVCPage.url(WorkflowMonitorPage.class, "workitemId=" + workitem.getId());
	}

	public String getWorkflowCompleteInfoUrl(final WorkitemBean workitem) {
		return AbstractMVCPage.url(WorkflowCompleteInfoPage.class, "workitemId=" + workitem.getId());
	}

	public String getWorklistUrl(final Class<? extends AbstractWorkTPage> mClass, final String params) {
		return AbstractMVCPage.url(getUrl(mClass.getName()), params);
	}

	public String getWorklistUrl(final Class<? extends AbstractWorkTPage> mClass) {
		return getWorklistUrl(mClass, null);
	}
}
