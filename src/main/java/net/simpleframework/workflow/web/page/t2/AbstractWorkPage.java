package net.simpleframework.workflow.web.page.t2;

import java.io.IOException;
import java.util.Map;

import net.simpleframework.ctx.permission.IPermissionConst;
import net.simpleframework.mvc.PageMapping;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.template.t2.T2TemplatePage;
import net.simpleframework.workflow.web.page.AbstractItemsTPage;
import net.simpleframework.workflow.web.page.DelegateListTPage;
import net.simpleframework.workflow.web.page.MyInitiateItemsTPage;
import net.simpleframework.workflow.web.page.MyWorklistTPage;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public abstract class AbstractWorkPage extends T2TemplatePage {

	@Override
	public String getRole(final PageParameter pp) {
		return IPermissionConst.ROLE_ALL_ACCOUNT;
	}

	protected abstract Class<? extends AbstractItemsTPage> getWorkTPageClass();

	@Override
	protected String toHtml(final PageParameter pp, final Map<String, Object> variables,
			final String currentVariable) throws IOException {
		return pp.includeUrl(getWorkTPageClass());
	}

	@PageMapping(url = "/workflow/my/work")
	public static class MyWorklistPage extends AbstractWorkPage {

		@Override
		protected Class<? extends AbstractItemsTPage> getWorkTPageClass() {
			return MyWorklistTPage.class;
		}
	}

	@PageMapping(url = "/workflow/my/initiate")
	public static class MyInitiateItemsPage extends AbstractWorkPage {
		@Override
		protected Class<? extends AbstractItemsTPage> getWorkTPageClass() {
			return MyInitiateItemsTPage.class;
		}
	}

	@PageMapping(url = "/workflow/delegate")
	public static class DelegateListPage extends AbstractWorkPage {
		@Override
		protected Class<? extends AbstractItemsTPage> getWorkTPageClass() {
			return DelegateListTPage.class;
		}
	}
}
