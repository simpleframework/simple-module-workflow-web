package net.simpleframework.workflow.web;

import net.simpleframework.ctx.IModuleRef;
import net.simpleframework.workflow.engine.IWorkflowContext;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public interface IWorkflowWebContext extends IWorkflowContext {

	/**
	 * 获取日志模块的引用
	 * 
	 * @return
	 */
	IModuleRef getLogRef();
}
