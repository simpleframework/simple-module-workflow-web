package net.simpleframework.workflow.web.component.complete;

import net.simpleframework.common.StringUtils;
import net.simpleframework.ctx.common.bean.BeanDefaults;
import net.simpleframework.mvc.component.AbstractComponentBean;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class WorkitemCompleteBean extends AbstractComponentBean {

	/* workitemId的参数名 */
	private String workitemIdParameterName = BeanDefaults.getString(getClass(),
			"workitemIdParameterName", "workitemId");

	/* 确认消息 */
	private String confirmMessage;

	@Override
	public boolean isRunImmediately() {
		return false;
	}

	public String getWorkitemIdParameterName() {
		return workitemIdParameterName;
	}

	public WorkitemCompleteBean setWorkitemIdParameterName(final String workitemIdParameterName) {
		this.workitemIdParameterName = workitemIdParameterName;
		return this;
	}

	public String getConfirmMessage() {
		return confirmMessage;
	}

	public WorkitemCompleteBean setConfirmMessage(final String confirmMessage) {
		this.confirmMessage = confirmMessage;
		return this;
	}

	@Override
	public String getHandlerClass() {
		final String sClass = super.getHandlerClass();
		return StringUtils.hasText(sClass) ? sClass : DefaultWorkitemCompleteHandler.class.getName();
	}
}
