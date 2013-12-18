package net.simpleframework.workflow.web.component.action.complete;

import net.simpleframework.common.StringUtils;
import net.simpleframework.mvc.component.AbstractComponentBean;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class WorkitemCompleteBean extends AbstractComponentBean {

	@Override
	public boolean isRunImmediately() {
		return false;
	}

	@Override
	public String getHandleClass() {
		return StringUtils.text(super.getHandleClass(),
				DefaultWorkitemCompleteHandler.class.getName());
	}
}
