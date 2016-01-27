package net.simpleframework.workflow.web.page.t2;

import java.io.IOException;
import java.util.Map;

import net.simpleframework.ctx.permission.PermissionConst;
import net.simpleframework.mvc.PageMapping;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.template.t2.T2TemplatePage;
import net.simpleframework.workflow.web.page.AbstractWorksTPage;
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

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public abstract class AbstractWorkPage extends T2TemplatePage {

	@Override
	public String getPageRole(final PageParameter pp) {
		return PermissionConst.ROLE_ALL_ACCOUNT;
	}

	protected abstract Class<? extends AbstractWorksTPage> getWorkTPageClass();

	@Override
	protected String toHtml(final PageParameter pp, final Map<String, Object> variables,
			final String currentVariable) throws IOException {
		return pp.includeUrl(getWorkTPageClass());
	}

	@PageMapping(url = "/workflow/my/running")
	public static class MyRunningWorklistPage extends AbstractWorkPage {

		@Override
		protected Class<? extends AbstractItemsTPage> getWorkTPageClass() {
			return MyRunningWorklistTPage.class;
		}
	}

	@PageMapping(url = "/workflow/my/final")
	public static class MyFinalWorklistPage extends AbstractWorkPage {

		@Override
		protected Class<? extends AbstractItemsTPage> getWorkTPageClass() {
			return MyFinalWorklistTPage.class;
		}
	}

	@PageMapping(url = "/workflow/my/initiate-group")
	public static class MyInitiateItemsGroupPage extends AbstractWorkPage {
		@Override
		protected Class<? extends AbstractItemsTPage> getWorkTPageClass() {
			return MyInitiateItemsGroupTPage.class;
		}
	}

	@PageMapping(url = "/workflow/my/initiate")
	public static class MyInitiateItemsPage extends AbstractWorkPage {
		@Override
		protected Class<? extends AbstractItemsTPage> getWorkTPageClass() {
			return MyInitiateItemsTPage.class;
		}
	}

	@PageMapping(url = "/workflow/my/delegate")
	public static class MyDelegateListPage extends AbstractWorkPage {
		@Override
		protected Class<? extends AbstractItemsTPage> getWorkTPageClass() {
			return MyDelegateListTPage.class;
		}
	}

	@PageMapping(url = "/workflow/my/delegate-rev")
	public static class MyDelegateRevListPage extends AbstractWorkPage {
		@Override
		protected Class<? extends AbstractItemsTPage> getWorkTPageClass() {
			return MyDelegateRevListTPage.class;
		}
	}

	@PageMapping(url = "/workflow/my/user-delegate")
	public static class UserDelegateListPage extends AbstractWorkPage {
		@Override
		protected Class<? extends AbstractItemsTPage> getWorkTPageClass() {
			return UserDelegateListTPage.class;
		}
	}

	@PageMapping(url = "/workflow/my/views")
	public static class MyWorkviewsPage extends AbstractWorkPage {
		@Override
		protected Class<? extends AbstractItemsTPage> getWorkTPageClass() {
			return MyWorkviewsTPage.class;
		}
	}

	@PageMapping(url = "/workflow/my/views-sent")
	public static class MyWorkviewsSentPage extends AbstractWorkPage {
		@Override
		protected Class<? extends AbstractItemsTPage> getWorkTPageClass() {
			return MyWorkviewsSentTPage.class;
		}
	}

	@PageMapping(url = "/workflow/my/stat")
	public static class MyWorkstatPage extends AbstractWorkPage {
		@Override
		protected Class<? extends AbstractItemsTPage> getWorkTPageClass() {
			return MyWorkstatTPage.class;
		}
	}

	@PageMapping(url = "/workflow/my/log")
	public static class MyWorklogsPage extends AbstractWorkPage {
		@Override
		protected Class<? extends AbstractItemsTPage> getWorkTPageClass() {
			return MyWorklogsTPage.class;
		}
	}

	/*-------------------------------query--------------------------------*/

	@PageMapping(url = "/workflow/process-works/my")
	public static class MyProcessWorksPage extends AbstractWorkPage {
		@Override
		protected Class<? extends AbstractWorksTPage> getWorkTPageClass() {
			return MyProcessWorksTPage.class;
		}
	}

	@PageMapping(url = "/workflow/process-works/dept")
	public static class MyProcessWorks_DeptPage extends AbstractWorkPage {
		@Override
		protected Class<? extends AbstractWorksTPage> getWorkTPageClass() {
			return MyProcessWorks_DeptTPage.class;
		}
	}

	@PageMapping(url = "/workflow/process-works/org")
	public static class MyProcessWorks_OrgPage extends AbstractWorkPage {
		@Override
		protected Class<? extends AbstractWorksTPage> getWorkTPageClass() {
			return MyProcessWorks_OrgTPage.class;
		}
	}

	@PageMapping(url = "/workflow/process-works/role")
	public static class MyProcessWorks_RolePage extends AbstractWorkPage {
		@Override
		protected Class<? extends AbstractWorksTPage> getWorkTPageClass() {
			return MyProcessWorks_RoleTPage.class;
		}
	}
}
