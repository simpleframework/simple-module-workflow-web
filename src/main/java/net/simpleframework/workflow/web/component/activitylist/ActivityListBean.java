package net.simpleframework.workflow.web.component.activitylist;

import net.simpleframework.common.StringUtils;
import net.simpleframework.ctx.common.xml.XmlElement;
import net.simpleframework.mvc.PageDocument;
import net.simpleframework.mvc.component.ComponentUtils;
import net.simpleframework.mvc.component.ui.pager.EPagerBarLayout;
import net.simpleframework.mvc.component.ui.pager.TablePagerBean;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class ActivityListBean extends TablePagerBean {

	public ActivityListBean(final PageDocument pageDocument, final XmlElement element) {
		super(pageDocument, element);
		setPagerBarLayout(EPagerBarLayout.none);
		setShowLineNo(true);
	}

	public ActivityListBean(final PageDocument pageDocument) {
		this(pageDocument, null);
	}

	@Override
	public String getDataPath() {
		return ComponentUtils.getResourceHomePath(ActivityListBean.class) + "/jsp/activity_list.jsp";
	}

	@Override
	public String getHandleClass() {
		return StringUtils.text(super.getHandleClass(), DefaultActivityListHandler.class.getName());
	}
}
