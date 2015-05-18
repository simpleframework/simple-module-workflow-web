package net.simpleframework.workflow.web.page;

import net.simpleframework.common.BeanUtils;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.ctx.script.MVEL2Template;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.ElementList;
import net.simpleframework.mvc.common.element.TabButton;
import net.simpleframework.mvc.common.element.TabButtons;
import net.simpleframework.workflow.engine.EWorkitemStatus;
import net.simpleframework.workflow.engine.bean.UserStatBean;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class MyWorkstatTPage extends AbstractItemsTPage {

	@Override
	protected void onForward(final PageParameter pp) {
		super.onForward(pp);
	}

	@Override
	public ElementList getRightElements(final PageParameter pp) {
		final TabButtons tabs = TabButtons.of(new TabButton("状态统计"));
		return ElementList.of(createTabsElement(pp, tabs));
	}

	protected String toStat1HTML(final PageParameter pp) {
		final UserStatBean userStat = usService.getUserStat(pp.getLoginId());
		final StringBuilder sb = new StringBuilder();
		sb.append("<div class='stat1 clearfix'>");
		sb.append(" <div class='topic'>当前工作状态</div>");
		sb.append(" <div class='col'>");
		sb.append("  <div class='num'>").append(usService.getAllWorkitems(userStat)).append("</div>");
		sb.append("  <div class='lbl'>全部</div>");
		sb.append(" </div>");
		for (final EWorkitemStatus s : EWorkitemStatus.values()) {
			sb.append(" <div class='col'>");
			sb.append("  <div class='num'>")
					.append(BeanUtils.getProperty(userStat, "workitem_" + s.name())).append("</div>");
			sb.append("  <div class='lbl'>").append(s).append("</div>");
			sb.append(" </div>");
		}
		sb.append("</div>");
		return sb.toString();
	}

	protected String toStat2HTML(final PageParameter pp) {
		final StringBuilder sb = new StringBuilder();
		sb.append("<div class='stat2 clearfix'>");
		sb.append(" <div class='topic'>最近工作完成情况</div>");
		sb.append("</div>");
		return sb.toString();
	}

	@Override
	protected String toListHTML(final PageParameter pp) {
		final StringBuilder sb = new StringBuilder();
		sb.append("<div class='MyWorkstatTPage'>");
		sb.append(toStat1HTML(pp));
		sb.append(toStat2HTML(pp));
		sb.append("</div>");

		sb.append(MVEL2Template.replace(new KVMap(), MyWorkstatTPage.class,
				"MyWorkstatTPage_stat.html"));
		return sb.toString();
	}
}
