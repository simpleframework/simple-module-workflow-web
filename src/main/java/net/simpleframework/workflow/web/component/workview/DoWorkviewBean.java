package net.simpleframework.workflow.web.component.workview;

import net.simpleframework.common.StringUtils;
import net.simpleframework.mvc.component.AbstractComponentBean;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class DoWorkviewBean extends AbstractComponentBean {
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