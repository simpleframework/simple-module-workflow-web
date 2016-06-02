package net.simpleframework.workflow.web.component.comments;

import static net.simpleframework.common.I18n.$m;
import net.simpleframework.mvc.component.AbstractContainerBean;
import net.simpleframework.workflow.engine.IWorkflowContextAware;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class WfCommentBean extends AbstractContainerBean implements IWorkflowContextAware {
	/* 是否允许编辑 */
	private boolean editable = true;
	/* 允许填写意见的最大字符数 */
	private int maxlength = 200;
	/* 分组 */
	private EGroupBy groupBy;

	/* 管理员角色 */
	private String managerRole = workflowContext.getModule().getManagerRole(null);

	public WfCommentBean() {
		setHandlerClass(DefaultWfCommentHandler.class);
	}

	public boolean isEditable() {
		return editable;
	}

	public WfCommentBean setEditable(final boolean editable) {
		this.editable = editable;
		return this;
	}

	public int getMaxlength() {
		return maxlength;
	}

	public void setMaxlength(final int maxlength) {
		this.maxlength = maxlength;
	}

	public EGroupBy getGroupBy() {
		return groupBy == null ? EGroupBy.none : groupBy;
	}

	public WfCommentBean setGroupBy(final EGroupBy groupBy) {
		this.groupBy = groupBy;
		return this;
	}

	public String getManagerRole() {
		return managerRole;
	}

	public WfCommentBean setManagerRole(final String managerRole) {
		this.managerRole = managerRole;
		return this;
	}

	public static enum EGroupBy {
		/* 不分组 */
		none {
			@Override
			public String toString() {
				return $m("EGroupBy.none");
			}
		},
		/* 按任务分组 */
		taskname {
			@Override
			public String toString() {
				return $m("EGroupBy.taskname");
			}
		},
		/* 按部门分组 */
		dept {
			@Override
			public String toString() {
				return $m("EGroupBy.dept");
			}
		}
	}
}