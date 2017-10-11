package net.simpleframework.workflow.web.component.comments;

import static net.simpleframework.common.I18n.$m;

import net.simpleframework.mvc.component.AbstractContainerBean;
import net.simpleframework.workflow.engine.IWorkflowContextAware;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class WfCommentBean extends AbstractContainerBean implements IWorkflowContextAware {
	private static final long serialVersionUID = 7858783481161363348L;

	/* 是否允许编辑 */
	private boolean editable = true;
	/* 允许填写意见的最大字符数 */
	private int maxlength = 200;
	/* 分组 */
	private EGroupBy groupBy;
	
	/* 是否显示分组切换 */
	private boolean disGroup = true;
	/* 是否显示签名 */
	private boolean disSign = true;
	
	/* 只显示指定节点的意见,多个节点豆号分隔 */
	private String disTaskName;
	
	/* 不显示指定节点的意见,多个节点豆号分隔 */
	private String notDisTaskName;
	
	/* 列表是否显示当前意见 */
	private boolean disCurComment=true;

	/* 管理员角色 */
	private String managerRole = workflowContext.getModule().getManagerRole(null);

	public WfCommentBean() {
		setHandlerClass(DefaultWfCommentHandler.class);
	}

	public boolean isDisCurComment() {
		return disCurComment;
	}

	public WfCommentBean setDisCurComment(boolean disCurComment) {
		this.disCurComment = disCurComment;
		return this;
	}

	public String getDisTaskName() {
		return disTaskName;
	}

	public WfCommentBean setDisTaskName(String disTaskName) {
		this.disTaskName = disTaskName;
		return this;
	}

	public String getNotDisTaskName() {
		return notDisTaskName;
	}

	public WfCommentBean setNotDisTaskName(String notDisTaskName) {
		this.notDisTaskName = notDisTaskName;
		return this;
	}

	public boolean isDisSign() {
		return disSign;
	}

	public WfCommentBean setDisSign(boolean disSign) {
		this.disSign = disSign;
		return this;
	}

	public boolean isDisGroup() {
		return disGroup;
	}

	public WfCommentBean setDisGroup(boolean disGroup) {
		this.disGroup = disGroup;
		return this;
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