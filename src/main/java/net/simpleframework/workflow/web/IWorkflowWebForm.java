package net.simpleframework.workflow.web;

import java.util.Map;

import net.simpleframework.mvc.JavascriptForward;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.workflow.engine.IWorkflowForm;
import net.simpleframework.workflow.engine.WorkitemComplete;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public interface IWorkflowWebForm extends IWorkflowForm {

	/**
	 * 获取表单的装载地址
	 * 
	 * @return
	 */
	String getForwardUrl(PageParameter pp);

	/**
	 * 完成事件
	 * 
	 * @param pp
	 * @param workitemComplete
	 * @return
	 */
	JavascriptForward onComplete(PageParameter pp, WorkitemComplete workitemComplete);

	void doUpdateProcessKV(PageParameter pp);

	/**
	 * 给流程变量赋值
	 * 
	 * @param pp
	 * @param variables
	 */
	void bindVariables(PageParameter pp, Map<String, Object> variables);
}
