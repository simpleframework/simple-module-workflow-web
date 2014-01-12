package net.simpleframework.workflow.web;

import net.simpleframework.mvc.AbstractMVCPage;
import net.simpleframework.mvc.common.UrlsCache;
import net.simpleframework.workflow.web.page.AbstractWorkTPage;
import net.simpleframework.workflow.web.page.MyInitiateItemsTPage;
import net.simpleframework.workflow.web.page.MyWorklistTPage;
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
		// urls.put(MyWorklistTPage.class.getName(), MyWorklistPage.class);
		urls.put(MyInitiateItemsTPage.class.getName(), MyInitiateItemsPage.class);
	}

	public String getMyWorkUrl(final Class<? extends AbstractWorkTPage> mClass) {
		return getMyWorkUrl(mClass, null);
	}

	public String getMyWorkUrl(final Class<? extends AbstractWorkTPage> mClass, final String params) {
		return AbstractMVCPage.url(getUrl(mClass.getName()), params);
	}
}
