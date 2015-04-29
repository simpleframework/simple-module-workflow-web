package net.simpleframework.workflow.web.page;

import static net.simpleframework.common.I18n.$m;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.ElementList;
import net.simpleframework.mvc.component.ui.pager.TablePagerColumn;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class AbstractWorkitemsTPage extends AbstractItemsTPage {

	@Override
	public ElementList getRightElements(final PageParameter pp) {
		// setDefaultGroupVal(pp, getDefaultGroupVal());
		// return ElementList
		// .of(createGroupElement(pp, "MyWorklistTPage_tbl", new
		// Option("modelname",
		// $m("AbstractWorkitemsTPage.1")), new Option("taskname",
		// $m("AbstractWorkitemsTPage.2"))));
		return null;
	}

	protected TablePagerColumn TC_STATUS() {
		return new TablePagerColumn("status", $m("AbstractWorkitemsTPage.3"), 55);
	}
}
