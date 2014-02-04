package net.simpleframework.workflow.web.page;

import static net.simpleframework.common.I18n.$m;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.ETextAlign;
import net.simpleframework.mvc.common.element.ElementList;
import net.simpleframework.mvc.common.element.Option;
import net.simpleframework.mvc.component.ui.pager.TablePagerColumn;
import net.simpleframework.mvc.component.ui.pager.db.GroupDbTablePagerHandler;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class AbstractWorkitemsTPage extends AbstractItemsTPage {

	@Override
	public ElementList getRightElements(final PageParameter pp) {
		GroupDbTablePagerHandler.setDefaultGroupVal(pp, "MyWorklistTPage_tbl", "modelname");

		return ElementList
				.of(createGroupElement(pp, "MyWorklistTPage_tbl", new Option("modelname",
						$m("AbstractWorkitemsTPage.1")), new Option("taskname",
						$m("AbstractWorkitemsTPage.2"))));
	}

	protected TablePagerColumn TITLE() {
		return new TablePagerColumn("title", $m("AbstractWorkitemsTPage.0")).setTextAlign(
				ETextAlign.left).setSort(false);
	}
}
