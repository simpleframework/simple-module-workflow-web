package net.simpleframework.workflow.web;

import net.simpleframework.ctx.IModuleRef;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.component.ext.userselect.DefaultUserSelectHandler;
import net.simpleframework.workflow.engine.IWorkflowContext;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public interface IWorkflowWebContext extends IWorkflowContext {

	IModuleRef getOrganizationRef();

	/**
	 * 获取日志模块的引用
	 * 
	 * @return
	 */
	IModuleRef getLogRef();

	WorkflowUrlsFactory getUrlsFactory();

	/**
	 * 获取部门级别的流程查询角色
	 * 
	 * @param pp
	 * @return
	 */
	String getProcessWorks_DeptRole(PageParameter pp);

	/**
	 * 获取机构级别的流程查询角色
	 * 
	 * @param pp
	 * @return
	 */
	String getProcessWorks_OrgRole(PageParameter pp);

	Class<? extends DefaultUserSelectHandler> getDelegate_UserSelectHandler(PageParameter pp);
}
