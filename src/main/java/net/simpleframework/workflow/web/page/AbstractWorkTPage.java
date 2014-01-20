package net.simpleframework.workflow.web.page;

import java.util.List;

import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.component.ui.pager.ITablePagerHandler;
import net.simpleframework.mvc.component.ui.pager.TablePagerBean;
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

	@Override
	protected void onForward(final PageParameter pp) {
		super.onForward(pp);
		pp.addImportCSS(AbstractWorkTPage.class, "/my_work.css");
	}

	@Override
	protected TablePagerBean addTablePagerBean(final PageParameter pp, final String name,
			final Class<? extends ITablePagerHandler> handleClass) {
		return addTablePagerBean(pp, name, handleClass, false).setShowFilterBar(true)
				.setShowHead(true).setShowCheckbox(true);
	}

	private CategoryItem createCategoryItem(final PageParameter pp, final String text,
			final Class<? extends AbstractWorkTPage> mClass, final String params) {
		return new CategoryItem(text).setHref(
				(((IWorkflowWebContext) context).getUrlsFactory()).getMyWorkUrl(mClass, params))
				.setSelected(mClass.equals(getClass()));
	}

	@Override
	protected CategoryItems getCategoryList(final PageParameter pp) {
		final CategoryItem item0 = createCategoryItem(pp, "我的工作", MyWorklistTPage.class, null);
		final String _status = pp.getParameter("status");
		final List<CategoryItem> children = item0.getChildren();
		// children.add(createCategoryItem(pp, "待办工作", MyWorklistTPage.class,
		// "status=running")
		// .setSelected("running".equals(_status)));
		children.add(createCategoryItem(pp, "办毕工作", MyWorklistTPage.class, "status=complete")
				.setSelected("complete".equals(_status)));
		return CategoryItems.of(item0,
				createCategoryItem(pp, "启动新工作", MyInitiateItemsTPage.class, null));
	}

	@Override
	protected int getCategoryWidth(final PageParameter pp) {
		return 180;
	}
}
