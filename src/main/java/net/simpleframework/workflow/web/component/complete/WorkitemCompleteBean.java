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

	/* 是否完成当前环节，当前环节可以不完成，而直接创建后续环节 */
	private boolean bcomplete = true;

	/* 选择参与者时用所在部门显示的任务名 */
	private String[] dispWithDept;

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

	public boolean isBcomplete() {
		return bcomplete;
	}

	public WorkitemCompleteBean setBcomplete(final boolean bcomplete) {
		this.bcomplete = bcomplete;
		return this;
	}

	public String[] getDispWithDept() {
		return dispWithDept;
	}

	public WorkitemCompleteBean setDispWithDept(final String[] dispWithDept) {
		this.dispWithDept = dispWithDept;
		return this;
	}

	@Override
	public String getHandlerClass() {
		final String sClass = super.getHandlerClass();
		return StringUtils.hasText(sClass) ? sClass : DefaultWorkitemCompleteHandler.class.getName();
	}
}
