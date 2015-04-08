package net.simpleframework.workflow.web.page;

import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.component.ui.pager.TablePagerBean;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class MyWorkviewsTPage extends AbstractItemsTPage {

	@Override
	protected void onForward(final PageParameter pp) {
		super.onForward(pp);

		addTablePagerBean(pp);
	}

	protected TablePagerBean addTablePagerBean(final PageParameter pp) {
		final TablePagerBean tablePager = super.addTablePagerBean(pp, "MyWorkviewsTPage_tbl",
				MyRunningWorklistTbl.class);
		return tablePager;
	}
}
