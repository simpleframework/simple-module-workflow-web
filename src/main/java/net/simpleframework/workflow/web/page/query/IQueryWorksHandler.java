package net.simpleframework.workflow.web.page.query;

import net.simpleframework.workflow.engine.IWorkflowContextAware;
import net.simpleframework.workflow.engine.bean.ProcessModelBean;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public interface IQueryWorksHandler extends IWorkflowContextAware {

	/**
	 * 获取要操作的流程模型
	 * 
	 * @return
	 */
	ProcessModelBean getProcessModel();
}
