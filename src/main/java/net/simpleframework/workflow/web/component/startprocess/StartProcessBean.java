package net.simpleframework.workflow.web.component.startprocess;

import net.simpleframework.workflow.web.component.AbstractWfActionBean;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class StartProcessBean extends AbstractWfActionBean {
	/* 确认消息 */
	private String confirmMessage;

	@Override
	public boolean isRunImmediately() {
		return false;
	}

	public String getConfirmMessage() {
		return confirmMessage;
	}

	public StartProcessBean setConfirmMessage(final String confirmMessage) {
		this.confirmMessage = confirmMessage;
		return this;
	}
}
