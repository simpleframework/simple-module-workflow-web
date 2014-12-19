package net.simpleframework.workflow.web.component.comments;

import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.IComponentHandler;
import net.simpleframework.workflow.engine.WorkitemBean;
import net.simpleframework.workflow.engine.ext.WfComment;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public interface IWfCommentHandler extends IComponentHandler {

	/**
	 * 查询意见列表
	 * 
	 * @param cp
	 * @param workitem
	 * @return
	 */
	IDataQuery<WfComment> comments(ComponentParameter cp, WorkitemBean workitem);

	/**
	 * 保存数据
	 * 
	 * @param cp
	 * @param workitem
	 */
	void onSave(ComponentParameter cp, WorkitemBean workitem);

	String toHTML(ComponentParameter cp, WorkitemBean workitem);
}
