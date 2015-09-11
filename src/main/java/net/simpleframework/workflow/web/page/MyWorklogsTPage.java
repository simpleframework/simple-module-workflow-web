package net.simpleframework.workflow.web.page;

import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.ElementList;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class MyWorklogsTPage extends AbstractItemsTPage {

	@Override
	public ElementList getRightElements(final PageParameter pp) {
		return ElementList.of(MyWorkstatTPage.getStatTabs(pp));
	}

	@Override
	protected String toListHTML(final PageParameter pp) {
		final StringBuilder sb = new StringBuilder();
		sb.append("<div class='MyWorklogsTPage'>");
		sb.append(" <div class='topbar clearfix'>");
		sb.append(" </div>");
		sb.append(" <div class='logs'>");
		sb.append(" </div>");
		sb.append("</div>");
		return sb.toString();
	}
}
