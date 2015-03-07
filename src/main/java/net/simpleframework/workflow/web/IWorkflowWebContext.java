package net.simpleframework.workflow.web;

import net.simpleframework.ctx.IModuleRef;
import net.simpleframework.mvc.IMVCContextVar;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.workflow.engine.IWorkflowContext;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public interface IWorkflowWebContext extends IWorkflowContext, IMVCContextVar {

	/**
	 * 获取部门管理角色名
	 * 
	 * @param pp
	 * @return
	 */
	String getDepartmentMgrRole(PageParameter pp);

	/**
	 * 获取日志模块的引用
	 * 
	 * @return
	 */
	IModuleRef getLogRef();

	WorkflowUrlsFactory getUrlsFactory();
}
