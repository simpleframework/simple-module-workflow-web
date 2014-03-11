package net.simpleframework.workflow.web.page;

import static net.simpleframework.common.I18n.$m;

import java.util.List;

import net.simpleframework.common.object.ObjectFactory;
import net.simpleframework.ctx.IModuleRef;
import net.simpleframework.mvc.AbstractMVCPage;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.component.base.ajaxrequest.AjaxRequestBean;
import net.simpleframework.mvc.component.ui.pager.ITablePagerHandler;
import net.simpleframework.mvc.component.ui.pager.TablePagerBean;
import net.simpleframework.mvc.component.ui.window.WindowBean;
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
public abstract class AbstractItemsTPage extends Category_ListPage implements IWorkflowContextAware {

	@Override
	protected void onForward(final PageParameter pp) {
		super.onForward(pp);
		pp.addImportCSS(AbstractItemsTPage.class, "/my_work.css");

		final IModuleRef ref = ((IWorkflowWebContext) context).getLogRef();
		Class<? extends AbstractMVCPage> lPage;
		if (ref != null && (lPage = getUpdateLogPage()) != null) {
			pp.addComponentBean("AbstractItemsTPage_update_logPage", AjaxRequestBean.class)
					.setUrlForward(AbstractMVCPage.url(lPage));
			pp.addComponentBean("AbstractItemsTPage_update_log", WindowBean.class)
					.setContentRef("AbstractItemsTPage_update_logPage").setHeight(540).setWidth(864);
		}
	}

	protected Class<? extends AbstractMVCPage> getUpdateLogPage() {
		return null;
	}

	@Override
	protected TablePagerBean addTablePagerBean(final PageParameter pp, final String name,
			final Class<? extends ITablePagerHandler> handlerClass) {
		return addTablePagerBean(pp, name, handlerClass, false).setShowFilterBar(true)
				.setShowHead(true).setShowCheckbox(true);
	}

	private CategoryItem createCategoryItem(final PageParameter pp, final String text,
			final Class<? extends AbstractItemsTPage> mClass, final String params) {
		return new CategoryItem(text).setHref(
				((IWorkflowWebContext) context).getUrlsFactory().getUrl(pp, mClass, params))
				.setSelected(mClass == ObjectFactory.original(getClass()));
	}

	@Override
	protected CategoryItems getCategoryList(final PageParameter pp) {
		final CategoryItem item0 = createCategoryItem(pp, $m("AbstractItemsTPage.0"),
				MyWorklistTPage.class, null).setIconClass("my_work_icon");
		final String _status = pp.getParameter("status");
		final List<CategoryItem> children = item0.getChildren();
		// children.add(createCategoryItem(pp, "待办工作", MyWorklistTPage.class,
		// "status=running")
		// .setSelected("running".equals(_status)));
		children.add(createCategoryItem(pp, $m("AbstractItemsTPage.1"), MyWorklistTPage.class,
				"status=complete").setIconClass("my_work_complete_icon").setSelected(
				"complete".equals(_status)));
		children
				.add(createCategoryItem(pp, $m("AbstractItemsTPage.3"), DelegateListTPage.class, null)
						.setIconClass("delegate_list_icon"));
		return CategoryItems.of(item0,
				createCategoryItem(pp, $m("AbstractItemsTPage.2"), MyInitiateItemsTPage.class, null)
						.setIconClass("my_initiate_icon"));
	}

	@Override
	protected int getCategoryWidth(final PageParameter pp) {
		return 180;
	}
}
