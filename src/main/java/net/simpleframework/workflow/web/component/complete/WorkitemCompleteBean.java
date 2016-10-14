package net.simpleframework.workflow.web.component.complete;

import net.simpleframework.common.StringUtils;
import net.simpleframework.workflow.web.component.AbstractWfActionBean;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class WorkitemCompleteBean extends AbstractWfActionBean {
	/* 是否完成当前环节，当前环节可以不完成，而直接创建后续环节 */
	private boolean bcomplete = true;

	/* 选择参与者时用所在部门显示的任务名 */
	private String[] deptdispTasks;
	/* 选择参与者时，取消对某一任务的空参与者判断 */
	private String[] novalidationTasks;

	/* 确认消息 */
	private String confirmMessage;

	@Override
	public boolean isRunImmediately() {
		return false;
	}

	public String getConfirmMessage() {
		return confirmMessage;
	}

	public WorkitemCompleteBean setConfirmMessage(final String confirmMessage) {
		this.confirmMessage = confirmMessage;
		return this;
	}

	public boolean isBcomplete() {
		return bcomplete;
	}

	public WorkitemCompleteBean setBcomplete(final boolean bcomplete) {
		this.bcomplete = bcomplete;
		return this;
	}

	public String[] getDeptdispTasks() {
		return deptdispTasks;
	}

	public WorkitemCompleteBean setDeptdispTasks(final String[] deptdispTasks) {
		this.deptdispTasks = deptdispTasks;
		return this;
	}

	public String[] getNovalidationTasks() {
		return novalidationTasks;
	}

	public WorkitemCompleteBean setNovalidationTasks(final String[] novalidationTasks) {
		this.novalidationTasks = novalidationTasks;
		return this;
	}

	@Override
	public String getHandlerClass() {
		final String sClass = super.getHandlerClass();
		return StringUtils.hasText(sClass) ? sClass : DefaultWorkitemCompleteHandler.class.getName();
	}

	private static final long serialVersionUID = -166932913518682826L;
}
