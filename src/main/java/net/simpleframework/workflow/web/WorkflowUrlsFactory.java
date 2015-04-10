package net.simpleframework.workflow.web;

import net.simpleframework.mvc.AbstractMVCPage;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.UrlsCache;
import net.simpleframework.workflow.engine.WorkitemBean;
import net.simpleframework.workflow.web.page.MyDelegateListTPage;
import net.simpleframework.workflow.web.page.MyFinalWorklistTPage;
import net.simpleframework.workflow.web.page.MyInitiateItemsTPage;
import net.simpleframework.workflow.web.page.MyQueryWorksTPage;
import net.simpleframework.workflow.web.page.MyQueryWorks_DeptTPage;
import net.simpleframework.workflow.web.page.MyQueryWorks_RoleTPage;
import net.simpleframework.workflow.web.page.MyRunningWorklistTPage;
import net.simpleframework.workflow.web.page.MyWorkviewsTPage;
import net.simpleframework.workflow.web.page.UserDelegateListTPage;
import net.simpleframework.workflow.web.page.org2.ActivityGraphMgrTPage;
import net.simpleframework.workflow.web.page.org2.ActivityMgrTPage;
import net.simpleframework.workflow.web.page.org2.ProcessMgrTPage;
import net.simpleframework.workflow.web.page.org2.ProcessModelMgrTPage;
import net.simpleframework.workflow.web.page.t1.ProcessModelMgrPage;
import net.simpleframework.workflow.web.page.t1.WorkflowCompleteInfoPage;
import net.simpleframework.workflow.web.page.t1.WorkflowFormPage;
import net.simpleframework.workflow.web.page.t1.WorkflowGraphMonitorPage;
import net.simpleframework.workflow.web.page.t1.WorkflowMonitorPage;
import net.simpleframework.workflow.web.page.t1.WorkflowViewPage;
import net.simpleframework.workflow.web.page.t2.AbstractWorkPage.MyDelegateListPage;
import net.simpleframework.workflow.web.page.t2.AbstractWorkPage.MyFinalWorklistPage;
import net.simpleframework.workflow.web.page.t2.AbstractWorkPage.MyInitiateItemsPage;
import net.simpleframework.workflow.web.page.t2.AbstractWorkPage.MyQueryWorksPage;
import net.simpleframework.workflow.web.page.t2.AbstractWorkPage.MyQueryWorks_DeptPage;
import net.simpleframework.workflow.web.page.t2.AbstractWorkPage.MyQueryWorks_RolePage;
import net.simpleframework.workflow.web.page.t2.AbstractWorkPage.MyRunningWorklistPage;
import net.simpleframework.workflow.web.page.t2.AbstractWorkPage.MyWorkviewsPage;
import net.simpleframework.workflow.web.page.t2.AbstractWorkPage.UserDelegateListPage;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class WorkflowUrlsFactory extends UrlsCache {

	public WorkflowUrlsFactory() {
		put(MyRunningWorklistTPage.class, MyRunningWorklistPage.class);
		put(MyFinalWorklistTPage.class, MyFinalWorklistPage.class);
		put(MyInitiateItemsTPage.class, MyInitiateItemsPage.class);
		put(MyDelegateListTPage.class, MyDelegateListPage.class);
		put(UserDelegateListTPage.class, UserDelegateListPage.class);
		put(MyQueryWorksTPage.class, MyQueryWorksPage.class);
		put(MyQueryWorks_DeptTPage.class, MyQueryWorks_DeptPage.class);
		put(MyQueryWorks_RoleTPage.class, MyQueryWorks_RolePage.class);
		put(MyWorkviewsTPage.class, MyWorkviewsPage.class);

		put(ProcessModelMgrPage.class);

		put(WorkflowFormPage.class);
		put(WorkflowCompleteInfoPage.class);
		put(WorkflowMonitorPage.class);
		put(WorkflowGraphMonitorPage.class);
		put(WorkflowViewPage.class);

		put(ProcessModelMgrTPage.class);
		put(ProcessMgrTPage.class);
		put(ActivityMgrTPage.class);
		put(ActivityGraphMgrTPage.class);
	}

	public String getUrl(final PageParameter pp, final Class<? extends AbstractMVCPage> mClass,
			final WorkitemBean workitem) {
		return getUrl(pp, mClass, "workitemId=" + workitem.getId());
	}
}
