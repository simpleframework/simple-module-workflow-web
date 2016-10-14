package net.simpleframework.workflow.web.component.workview;

import net.simpleframework.common.StringUtils;
import net.simpleframework.workflow.web.component.AbstractWfActionBean;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class DoWorkviewBean extends AbstractWfActionBean {
	private static final long serialVersionUID = -5603189919099923486L;

	private String[] sentMenu;

	public String[] getSentMenu() {
		if (sentMenu == null) {
			return new String[] { "user-select", "role-select", "-", "last-select" };
		}
		return sentMenu;
	}

	public DoWorkviewBean setSentMenu(final String[] sentMenu) {
		this.sentMenu = sentMenu;
		return this;
	}

	@Override
	public boolean isRunImmediately() {
		return false;
	}

	@Override
	public String getHandlerClass() {
		final String sClass = super.getHandlerClass();
		return StringUtils.hasText(sClass) ? sClass : DefaultDoWorkviewHandler.class.getName();
	}
}