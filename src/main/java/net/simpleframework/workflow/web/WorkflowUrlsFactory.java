package net.simpleframework.workflow.web;

import net.simpleframework.mvc.AbstractMVCPage;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.UrlsCache;
import net.simpleframework.workflow.engine.bean.WorkitemBean;
import net.simpleframework.workflow.web.component.comments.mgr2.MyCommentsMgrTPage;
import net.simpleframework.workflow.web.page.list.AbstractItemsTPage;
import net.simpleframework.workflow.web.page.list.delegate.MyDelegateListTPage;
import net.simpleframework.workflow.web.page.list.delegate.MyDelegateRevListTPage;
import net.simpleframework.workflow.web.page.list.delegate.UserDelegateListTPage;
import net.simpleframework.workflow.web.page.list.initiate.MyInitiateItemsGroupTPage;
import net.simpleframework.workflow.web.page.list.initiate.MyInitiateItemsTPage;
import net.simpleframework.workflow.web.page.list.process.MyProcessWorksTPage;
import net.simpleframework.workflow.web.page.list.process.MyProcessWorksTPages.MyProcessWorks_DeptTPage;
import net.simpleframework.workflow.web.page.list.process.MyProcessWorksTPages.MyProcessWorks_OrgTPage;
import net.simpleframework.workflow.web.page.list.process.MyProcessWorksTPages.MyProcessWorks_RoleTPage;
import net.simpleframework.workflow.web.page.list.stat.MyWorklogsTPage;
import net.simpleframework.workflow.web.page.list.stat.MyWorkstatTPage;
import net.simpleframework.workflow.web.page.list.worklist.MyFinalWorklistTPage;
import net.simpleframework.workflow.web.page.list.worklist.MyRunningWorklistTPage;
import net.simpleframework.workflow.web.page.list.workviews.MyWorkviewsSentTPage;
import net.simpleframework.workflow.web.page.list.workviews.MyWorkviewsTPage;
import net.simpleframework.workflow.web.page.list.workviews.MyWorkviewsTPage.MyWorkviewsUnreadTPage;
import net.simpleframework.workflow.web.page.mgr2.ActivityGraphMgrTPage;
import net.simpleframework.workflow.web.page.mgr2.ActivityMgrTPage;
import net.simpleframework.workflow.web.page.mgr2.ProcessMgrTPage;
import net.simpleframework.workflow.web.page.mgr2.ProcessModelMgrTPage;
import net.simpleframework.workflow.web.page.t1.ProcessModelMgrPage;
import net.simpleframework.workflow.web.page.t1.form.WorkflowCompleteInfoPage;
import net.simpleframework.workflow.web.page.t1.form.WorkflowFormPage;
import net.simpleframework.workflow.web.page.t1.form.WorkflowGraphMonitorPage;
import net.simpleframework.workflow.web.page.t1.form.WorkflowMonitorPage;
import net.simpleframework.workflow.web.page.t1.form.WorkflowViewPage;
import net.simpleframework.workflow.web.page.t2.AbstractWorkPage.MyDelegateListPage;
import net.simpleframework.workflow.web.page.t2.AbstractWorkPage.MyDelegateRevListPage;
import net.simpleframework.workflow.web.page.t2.AbstractWorkPage.MyFinalWorklistPage;
import net.simpleframework.workflow.web.page.t2.AbstractWorkPage.MyInitiateItemsGroupPage;
import net.simpleframework.workflow.web.page.t2.AbstractWorkPage.MyInitiateItemsPage;
import net.simpleframework.workflow.web.page.t2.AbstractWorkPage.MyProcessWorksPage;
import net.simpleframework.workflow.web.page.t2.AbstractWorkPage.MyProcessWorks_DeptPage;
import net.simpleframework.workflow.web.page.t2.AbstractWorkPage.MyProcessWorks_OrgPage;
import net.simpleframework.workflow.web.page.t2.AbstractWorkPage.MyProcessWorks_RolePage;
import net.simpleframework.workflow.web.page.t2.AbstractWorkPage.MyRunningWorklistPage;
import net.simpleframework.workflow.web.page.t2.AbstractWorkPage.MyWorklogsPage;
import net.simpleframework.workflow.web.page.t2.AbstractWorkPage.MyWorkstatPage;
import net.simpleframework.workflow.web.page.t2.AbstractWorkPage.MyWorkviewsPage;
import net.simpleframework.workflow.web.page.t2.AbstractWorkPage.MyWorkviewsSentPage;
import net.simpleframework.workflow.web.page.t2.AbstractWorkPage.MyWorkviewsUnreadPage;
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
		put(MyInitiateItemsGroupTPage.class, MyInitiateItemsGroupPage.class);

		put(MyDelegateListTPage.class, MyDelegateListPage.class);
		put(MyDelegateRevListTPage.class, MyDelegateRevListPage.class);
		put(UserDelegateListTPage.class, UserDelegateListPage.class);

		put(MyProcessWorksTPage.class, MyProcessWorksPage.class);
		put(MyProcessWorks_DeptTPage.class, MyProcessWorks_DeptPage.class);
		put(MyProcessWorks_OrgTPage.class, MyProcessWorks_OrgPage.class);
		put(MyProcessWorks_RoleTPage.class, MyProcessWorks_RolePage.class);

		put(MyWorkviewsTPage.class, MyWorkviewsPage.class);
		put(MyWorkviewsSentTPage.class, MyWorkviewsSentPage.class);
		put(MyWorkviewsUnreadTPage.class, MyWorkviewsUnreadPage.class);

		put(MyWorkstatTPage.class, MyWorkstatPage.class);
		put(MyWorklogsTPage.class, MyWorklogsPage.class);

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

		put(MyCommentsMgrTPage.class);
	}

	public String getUrl(final PageParameter pp, final Class<? extends AbstractMVCPage> mClass,
			final WorkitemBean workitem) {
		return getUrl(pp, mClass, workitem != null ? ("workitemId=" + workitem.getId()) : null);
	}

	public String getIcon(final PageParameter pp, final String icon) {
		return pp.getCssResourceHomePath(AbstractItemsTPage.class) + "/images/" + icon;
	}
}
