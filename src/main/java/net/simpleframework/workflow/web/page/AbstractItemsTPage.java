package net.simpleframework.workflow.web.page;

import static net.simpleframework.common.I18n.$m;

import java.util.List;

import net.simpleframework.ctx.IModuleRef;
import net.simpleframework.mvc.AbstractMVCPage;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.SupElement;
import net.simpleframework.mvc.component.base.ajaxrequest.AjaxRequestBean;
import net.simpleframework.mvc.component.ui.pager.ITablePagerHandler;
import net.simpleframework.mvc.component.ui.pager.TablePagerBean;
import net.simpleframework.mvc.component.ui.pager.TablePagerColumn;
import net.simpleframework.mvc.template.lets.Category_ListPage;
import net.simpleframework.mvc.template.struct.CategoryItem;
import net.simpleframework.mvc.template.struct.CategoryItems;
import net.simpleframework.workflow.engine.IWorkflowServiceAware;
import net.simpleframework.workflow.web.IWorkflowWebContext;
import net.simpleframework.workflow.web.WorkflowUrlsFactory;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public abstract class AbstractItemsTPage extends Category_ListPage implements IWorkflowServiceAware {

	@Override
	protected void onForward(final PageParameter pp) {
		super.onForward(pp);
		pp.addImportCSS(AbstractItemsTPage.class, "/my_work.css");

		final IModuleRef ref = ((IWorkflowWebContext) workflowContext).getLogRef();
		Class<? extends AbstractMVCPage> lPage;
		if (ref != null && (lPage = getUpdateLogPage()) != null) {
			final AjaxRequestBean ajaxRequest = addAjaxRequest(pp,
					"AbstractItemsTPage_update_logPage", lPage);
			addWindowBean(pp, "AbstractItemsTPage_update_log", ajaxRequest).setHeight(540).setWidth(
					864);
		}
	}

	protected Class<? extends AbstractMVCPage> getUpdateLogPage() {
		return null;
	}

	@Override
	protected TablePagerBean addTablePagerBean(final PageParameter pp, final String name,
			final Class<? extends ITablePagerHandler> handlerClass) {
		return (TablePagerBean) addTablePagerBean(pp, name, handlerClass, false).setResize(false)
				.setShowCheckbox(false).setShowFilterBar(true).setShowLineNo(false).setShowHead(true)
				.setPageItems(30);
	}

	public CategoryItem createCategoryItem(final PageParameter pp, final String text,
			final Class<? extends AbstractItemsTPage> mClass) {
		return new CategoryItem(text).setHref(getUrlsFactory().getUrl(pp, mClass)).setSelected(
				mClass.isAssignableFrom(getClass()));
	}

	public CategoryItem createCategoryItem_mywork(final PageParameter pp) {
		final CategoryItem item = createCategoryItem(pp, $m("AbstractItemsTPage.0"),
				MyRunningWorklistTPage.class).setIconClass("my_work_icon");// .setIconClass("my_work_icon")
		final int count = wService.getUnreadWorklist(pp.getLoginId()).getCount();
		if (count > 0) {
			item.setNum(new SupElement(count).setHighlight(true));
		}
		return item;
	}

	public CategoryItem createCategoryItem_mywork_complete(final PageParameter pp) {
		return createCategoryItem(pp, $m("AbstractItemsTPage.1"), MyFinalWorklistTPage.class)
				.setIconClass("my_work_complete_icon");// .setIconClass("my_work_complete_icon")
	}

	public CategoryItem createCategoryItem_delegate(final PageParameter pp) {
		final CategoryItem delegate = createCategoryItem(pp, $m("AbstractItemsTPage.3"),
				MyDelegateListTPage.class).setIconClass("my_work_complete_icon");// .setIconClass("delegate_list_icon")
		delegate.setSelected(delegate.isSelected()
				|| UserDelegateListTPage.class == getOriginalClass());
		return delegate;
	}

	public CategoryItem createCategoryItem_myinitiate(final PageParameter pp) {
		return createCategoryItem(pp, $m("AbstractItemsTPage.2"), MyInitiateItemsTPage.class)
				.setIconClass("my_work_complete_icon");// .setIconClass("my_initiate_icon")
	}

	public CategoryItem createCategoryItem_queryworks(final PageParameter pp) {
		return createCategoryItem(pp, $m("AbstractItemsTPage.4"), MyQueryWorksTPage.class)
				.setIconClass("my_work_complete_icon");// .setIconClass("my_initiate_icon")
	}

	public CategoryItem createCategoryItem_myWorkviews(final PageParameter pp) {
		return createCategoryItem(pp, $m("AbstractItemsTPage.5"), MyWorkviewsTPage.class)
				.setIconClass("my_work_icon");
	}

	@Override
	protected CategoryItems getCategoryList(final PageParameter pp) {
		final CategoryItem item0 = createCategoryItem_mywork(pp);
		final List<CategoryItem> children = item0.getChildren();
		children.add(createCategoryItem_myinitiate(pp));
		children.add(createCategoryItem_mywork_complete(pp));
		children.add(createCategoryItem_delegate(pp));
		children.add(createCategoryItem_queryworks(pp));
		return CategoryItems.of(createCategoryItem_myWorkviews(pp), item0);
	}

	protected TablePagerColumn TC_TITLE() {
		return new TablePagerColumn("title", $m("AbstractWorkitemsTPage.0")).setSort(false);
	}

	protected static WorkflowUrlsFactory getUrlsFactory() {
		return ((IWorkflowWebContext) workflowContext).getUrlsFactory();
	}
}
