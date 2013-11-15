package net.simpleframework.workflow.web.component.worklist;

import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ui.pager.ITablePagerHandler;
import net.simpleframework.workflow.engine.WorkitemBean;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public interface IWorklistHandler extends ITablePagerHandler {

	/**
	 * 定义我的工作列表的标题
	 * 
	 * @param compParameter
	 * @return
	 */
	String getTitle(ComponentParameter cp);

	/**
	 * 定义打开表单的js
	 * 
	 * @param workitemBean
	 * @return
	 */
	String jsWorkflowFormAction(WorkitemBean workitemBean);
}
