package net.simpleframework.workflow.web.page.list.delegate;

import static net.simpleframework.common.I18n.$m;
import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ui.pager.TablePagerBean;
import net.simpleframework.mvc.component.ui.pager.TablePagerColumn;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class MyDelegateRevListTPage extends MyDelegateListTPage {

	@Override
	protected TablePagerBean addTablePagerBean(final PageParameter pp) {
		return (TablePagerBean) super.addTablePagerBean(pp).setHandlerClass(
				MyWorkDelegateRevTbl.class);
	}

	@Override
	protected TablePagerColumn TC_USERTEXT() {
		return super.TC_USERTEXT().setColumnText($m("MyDelegateListTPage.9"));
	}

	public static class MyWorkDelegateRevTbl extends MyWorkDelegateTbl {

		@Override
		public IDataQuery<?> createDataObjectQuery(final ComponentParameter cp) {
			return null;
		}
	}
}
