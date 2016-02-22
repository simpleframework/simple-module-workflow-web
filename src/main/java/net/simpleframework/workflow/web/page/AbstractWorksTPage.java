package net.simpleframework.workflow.web.page;

import static net.simpleframework.common.I18n.$m;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.ETextAlign;
import net.simpleframework.mvc.component.ui.pager.ITablePagerHandler;
import net.simpleframework.mvc.component.ui.pager.TablePagerBean;
import net.simpleframework.mvc.component.ui.pager.TablePagerColumn;
import net.simpleframework.mvc.template.lets.Category_ListPage;
import net.simpleframework.workflow.web.page.t1.AbstractWorkflowMgrPage;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class AbstractWorksTPage extends Category_ListPage implements IWorkflowPageAware {

	@Override
	protected TablePagerBean addTablePagerBean(final PageParameter pp, final String name,
			final Class<? extends ITablePagerHandler> handlerClass) {
		return (TablePagerBean) addTablePagerBean(pp, name, handlerClass, false).setResize(false)
				.setShowCheckbox(false).setFilter(true).setShowLineNo(false).setShowHead(true)
				.setPageItems(30);
	}

	public static TablePagerColumn TC_CREATEDATE() {
		return AbstractWorkflowMgrPage.TC_CREATEDATE().setFilterSort(false);
	}

	public static TablePagerColumn TC_ICON() {
		return TablePagerColumn.ICON().setWidth(16);
	}

	public static TablePagerColumn TC_TITLE() {
		return new TablePagerColumn("title", $m("AbstractItemsTPage.6")).setNowrap(false).setSort(
				false);
	}

	public static TablePagerColumn TC_PNO() {
		return new TablePagerColumn("pno", $m("MyRunningWorklistTPage.14"), 110).setSort(false);
	}

	public static TablePagerColumn TC_USER(final String columnName, final String columnText) {
		return new TablePagerColumn(columnName, columnText, 55).setTextAlign(ETextAlign.center)
				.setFilterSort(false).setNowrap(false);
	}

	public static <T extends Enum<T>> TablePagerColumn TC_STATUS(final Class<T> e) {
		final TablePagerColumn col = new TablePagerColumn("status", $m("AbstractItemsTPage.9"), 42)
				.setSort(false);
		if (e != null) {
			col.setPropertyClass(e);
		}
		return col;
	}
}
