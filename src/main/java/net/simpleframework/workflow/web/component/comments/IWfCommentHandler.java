package net.simpleframework.workflow.web.component.comments;

import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.IComponentHandler;
import net.simpleframework.workflow.engine.comment.WfComment;

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
	 * @return
	 */
	IDataQuery<WfComment> comments(ComponentParameter cp);

	/**
	 * 保存数据
	 * 
	 * @param cp
	 */
	void onSave(ComponentParameter cp);

	String toHTML(ComponentParameter cp);
}
