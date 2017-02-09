package net.simpleframework.workflow.web.component.fallback;

import net.simpleframework.common.StringUtils;
import net.simpleframework.workflow.web.component.AbstractWfActionBean;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class ActivityFallbackBean extends AbstractWfActionBean {
	private static final long serialVersionUID = -7087039001663391442L;

	@Override
	public boolean isRunImmediately() {
		return false;
	}

	@Override
	public String getHandlerClass() {
		final String sClass = super.getHandlerClass();
		return StringUtils.hasText(sClass) ? sClass : DefaultActivityFallbackHandler.class.getName();
	}
}
