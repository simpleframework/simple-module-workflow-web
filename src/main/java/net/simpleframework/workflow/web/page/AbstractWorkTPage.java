package net.simpleframework.workflow.web.page;

import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.template.lets.Category_ListPage;
import net.simpleframework.mvc.template.struct.CategoryItem;
import net.simpleframework.mvc.template.struct.CategoryItems;
import net.simpleframework.workflow.engine.IWorkflowContextAware;
import net.simpleframework.workflow.web.IWorkflowWebContext;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public abstract class AbstractWorkTPage extends Category_ListPage implements IWorkflowContextAware {

	private CategoryItem createCategoryItem(final PageParameter pp, String text,
			final Class<? extends AbstractWorkTPage> mClass) {
		return new CategoryItem(text).setHref(
				((IWorkflowWebContext) context).getUrlsFactory().getMyWorkUrl(mClass)).setSelected(
				mClass.equals(getClass()));
	}

	@Override
	protected CategoryItems getCategoryList(PageParameter pp) {
		return CategoryItems.of(createCategoryItem(pp, "我的工作", MyWorklistTPage.class),
				createCategoryItem(pp, "启动新工作", MyInitiateItemsTPage.class));
	}
}
