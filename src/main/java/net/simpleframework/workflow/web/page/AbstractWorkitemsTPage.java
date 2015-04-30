package net.simpleframework.workflow.web.page;

import static net.simpleframework.common.I18n.$m;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.BlockElement;
import net.simpleframework.mvc.common.element.ElementList;
import net.simpleframework.mvc.common.element.InputElement;
import net.simpleframework.mvc.common.element.LinkElement;
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
		return ElementList.of(new BlockElement().setClassName("worklist_search").addElements(
				new InputElement().setPlaceholder("请输入全文搜索内容"),
				new LinkElement("搜索").setClassName("simple_btn2")));
	}

	protected TablePagerColumn TC_STATUS() {
		return new TablePagerColumn("status", $m("AbstractWorkitemsTPage.3"), 55);
	}
}
