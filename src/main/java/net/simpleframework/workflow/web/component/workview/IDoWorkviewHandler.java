package net.simpleframework.workflow.web.component.workview;

import java.util.List;

import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.common.ID;
import net.simpleframework.ctx.permission.PermissionUser;
import net.simpleframework.mvc.JavascriptForward;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.IComponentHandler;
import net.simpleframework.workflow.engine.IWorkflowContextAware;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public interface IDoWorkviewHandler extends IComponentHandler, IWorkflowContextAware {

	/**
	 * 发送传阅
	 * 
	 * @param cp
	 * @param ids
	 */
	JavascriptForward doSent(ComponentParameter cp, boolean allowSent, List<ID> ids);

	IDataQuery<PermissionUser> getUsers(ComponentParameter cp);

	/**
	 * 
	 * @param cp
	 * @return
	 */
	String[] getSelectedRoles(ComponentParameter cp);
}
