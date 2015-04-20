package net.simpleframework.workflow.web.component.workview;

import java.util.List;

import net.simpleframework.common.ID;
import net.simpleframework.mvc.JavascriptForward;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.IComponentHandler;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public interface IDoWorkviewHandler extends IComponentHandler {

	/**
	 * 发送传阅
	 * 
	 * @param cp
	 * @param ids
	 */
	JavascriptForward doSent(ComponentParameter cp, List<ID> ids);

	/**
	 * 
	 * @param cp
	 * @return
	 */
	String[] getSelectedRoles(ComponentParameter cp);
}
