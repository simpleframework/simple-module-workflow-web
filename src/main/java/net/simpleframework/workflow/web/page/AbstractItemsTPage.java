package net.simpleframework.workflow.web.page;

import static net.simpleframework.common.I18n.$m;

import java.util.List;

import net.simpleframework.common.StringUtils;
import net.simpleframework.common.web.HttpUtils;
import net.simpleframework.ctx.IModuleRef;
import net.simpleframework.mvc.AbstractMVCPage;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.BlockElement;
import net.simpleframework.mvc.common.element.ETextAlign;
import net.simpleframework.mvc.common.element.EVerticalAlign;
import net.simpleframework.mvc.common.element.ElementList;
import net.simpleframework.mvc.common.element.ImageElement;
import net.simpleframework.mvc.common.element.InputElement;
import net.simpleframework.mvc.common.element.LinkElement;
import net.simpleframework.mvc.common.element.SupElement;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.base.ajaxrequest.AjaxRequestBean;
import net.simpleframework.mvc.component.ui.pager.ITablePagerHandler;
import net.simpleframework.mvc.component.ui.pager.TablePagerBean;
import net.simpleframework.mvc.component.ui.pager.TablePagerColumn;
import net.simpleframework.mvc.template.lets.Category_ListPage;
import net.simpleframework.mvc.template.struct.CategoryItem;
import net.simpleframework.mvc.template.struct.CategoryItems;
import net.simpleframework.workflow.web.IWorkflowWebContext;
import net.simpleframework.workflow.web.WorkflowUtils;
import net.simpleframework.workflow.web.page.t1.AbstractWorkflowMgrPage;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public abstract class AbstractItemsTPage extends Category_ListPage implements IWorkflowPageAware {

	@Override
	protected void onForward(final PageParameter pp) throws Exception {
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
		return new CategoryItem(text).setHref(uFactory.getUrl(pp, mClass)).setSelected(
				mClass.isAssignableFrom(getClass()));
	}

	public CategoryItem createCategoryItem_mywork(final PageParameter pp) {
		final CategoryItem item = createCategoryItem(pp, $m("AbstractItemsTPage.0"),
				MyRunningWorklistTPage.class).setIconClass("my_work_icon");// .setIconClass("my_work_icon")
		final int count = WorkflowUtils.getUserStat(pp).getWorkitem_unread();
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
		final CategoryItem item = createCategoryItem(pp, $m("AbstractItemsTPage.5"),
				MyWorkviewsTPage.class).setIconClass("my_work_icon");
		final int count = WorkflowUtils.getUserStat(pp).getWorkview_unread();
		if (count > 0) {
			item.setNum(new SupElement(count).setHighlight(true));
		}
		return item;
	}

	public CategoryItem createCategoryItem_myWorkstat(final PageParameter pp) {
		final CategoryItem item = createCategoryItem(pp, $m("AbstractItemsTPage.12"),
				MyWorkstatTPage.class).setIconClass("my_work_complete_icon");
		return item;
	}

	@Override
	protected CategoryItems getCategoryList(final PageParameter pp) {
		final CategoryItem item0 = createCategoryItem_mywork(pp);
		final List<CategoryItem> children = item0.getChildren();
		children.add(createCategoryItem_mywork_complete(pp));
		children.add(createCategoryItem_queryworks(pp));
		children.add(createCategoryItem_myinitiate(pp));
		children.add(createCategoryItem_delegate(pp));
		children.add(createCategoryItem_myWorkstat(pp));
		return CategoryItems.of(createCategoryItem_myWorkviews(pp), item0);
	}

	protected ElementList getIndexSearchElements(final PageParameter pp) {
		final InputElement txt = new InputElement().setId("idAbstractItemsTPage_search");
		final String t = pp.getLocaleParameter("t");
		if (StringUtils.hasText(t)) {
			txt.setValue(t);
		}
		return ElementList.of(new BlockElement().setClassName("worklist_search").addElements(txt,
				LinkElement.style2($m("AbstractItemsTPage.11"))));
	}

	protected String getIndexSearchJavascript(final String url) {
		final StringBuilder js = new StringBuilder();
		js.append("var s = $('idAbstractItemsTPage_search');");
		js.append("$UI.addBackgroundTitle(s, '").append($m("AbstractItemsTPage.10")).append("');");
		js.append("var Func = function() {");
		js.append(" var v = $F(s).trim();");
		js.append(" if (v == '')");
		js.append("   $Actions.loc('").append(url).append("');");
		js.append(" else");
		js.append("	  $Actions.loc('").append(HttpUtils.addParameters(url, "t="))
				.append("' + encodeURIComponent(v));");
		js.append("};");
		js.append("$Actions.observeSubmit(s, Func);");
		js.append("s.next().observe('click', Func);");
		return js.toString();
	}

	protected TablePagerColumn TC_ICON() {
		return TablePagerColumn.ICON().setWidth(16);
	}

	protected TablePagerColumn TC_TITLE() {
		return new TablePagerColumn("title", $m("AbstractItemsTPage.6")).setSort(false);
	}

	protected TablePagerColumn TC_PNO() {
		return new TablePagerColumn("pno", $m("MyRunningWorklistTPage.14"), 110).setSort(false);
	}

	protected TablePagerColumn TC_USER(final String columnName, final String columnText) {
		return new TablePagerColumn(columnName, columnText, 55).setTextAlign(ETextAlign.center)
				.setFilterSort(false).setNowrap(false);
	}

	protected <T extends Enum<T>> TablePagerColumn TC_STATUS(final Class<T> e) {
		final TablePagerColumn col = new TablePagerColumn("status", $m("AbstractItemsTPage.9"), 42)
				.setSort(false);
		if (e != null) {
			col.setPropertyClass(e);
		}
		return col;
	}

	protected TablePagerColumn TC_CREATEDATE() {
		return AbstractWorkflowMgrPage.TC_CREATEDATE().setFilterSort(false);
	}

	protected static ImageElement _createImageMark(final ComponentParameter cp, final String img) {
		return new ImageElement(cp.getCssResourceHomePath(AbstractItemsTPage.class) + "/images/"
				+ img).setVerticalAlign(EVerticalAlign.middle);
	}

	protected static ImageElement MARK_TOP(final ComponentParameter cp) {
		return _createImageMark(cp, "mark_top.png").setTitle($m("MyRunningWorklistTbl.1"));
	}

	protected static ImageElement MARK_UNREAD(final ComponentParameter cp) {
		return _createImageMark(cp, "mark_unread.png").setTitle($m("MyRunningWorklistTbl.2"));
	}
}
