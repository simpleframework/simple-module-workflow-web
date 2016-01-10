package net.simpleframework.workflow.web.page.list.workviews;

import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.component.ui.pager.TablePagerBean;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class MyWorkviewsSentTPage extends MyWorkviewsTPage {

	@Override
	protected TablePagerBean addTablePagerBean(final PageParameter pp) {
		return super.addTablePagerBean(pp);
	}

	public static class _MyWorkviewsTbl extends MyWorkviewsTbl {
	}
}