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

	IModuleRef getOrganizationRef();

	/**
	 * 获取日志模块的引用
	 * 
	 * @return
	 */
	IModuleRef getLogRef();

	/**
	 * 获取收藏的引用
	 * 
	 * @return
	 */
	IModuleRef getFavoriteRef();

	WorkflowUrlsFactory getUrlsFactory();

	/**
	 * 获取部门级别的流程查询角色
	 * 
	 * @param pp
	 * @return
	 */
	String getQueryWorks_DeptRole(PageParameter pp);

	/**
	 * 获取机构级别的流程查询角色
	 * 
	 * @param pp
	 * @return
	 */
	String getQueryWorks_OrgRole(PageParameter pp);
}
