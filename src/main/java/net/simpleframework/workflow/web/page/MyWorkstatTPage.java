package net.simpleframework.workflow.web.page;

import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.ElementList;
import net.simpleframework.mvc.common.element.TabButton;
import net.simpleframework.mvc.common.element.TabButtons;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class MyWorkstatTPage extends AbstractItemsTPage {
	@Override
	public ElementList getRightElements(final PageParameter pp) {
		final TabButtons tabs = TabButtons.of(new TabButton("工作状态"));
		return ElementList.of(createTabsElement(pp, tabs));
	}

	@Override
	protected String toListHTML(final PageParameter pp) {
		final StringBuilder sb = new StringBuilder();
		sb.append("<div class='MyWorkstatTPage'>");
		sb.append("</div>");
		return sb.toString();
	}
}
