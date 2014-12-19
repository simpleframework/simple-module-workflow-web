package net.simpleframework.workflow.web.component.comments;

import net.simpleframework.mvc.component.AbstractContainerBean;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class WfCommentBean extends AbstractContainerBean {
	/* 是否允许编辑 */
	private boolean editable;

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
}