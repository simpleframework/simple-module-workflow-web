package net.simpleframework.workflow.web.component.complete;

import net.simpleframework.mvc.JavascriptForward;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.IComponentHandler;
import net.simpleframework.workflow.engine.WorkitemComplete;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public interface IWorkitemCompleteHandler extends IComponentHandler {

	/**
	 * 工作项完成的触发动作
	 * 
	 * @param cp
	 * @param workitemComplete
	 * @return
	 * @throws Exception
	 */
	JavascriptForward onComplete(ComponentParameter cp, WorkitemComplete workitemComplete)
			throws Exception;
}