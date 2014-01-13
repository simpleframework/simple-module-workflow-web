package net.simpleframework.workflow.web.component.startprocess;

import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.IComponentHandler;
import net.simpleframework.workflow.engine.InitiateItem;
import net.simpleframework.workflow.engine.ProcessBean;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public interface IStartProcessHandler extends IComponentHandler {

	/**
	 * 创建流程之前的初始化逻辑。抛出的异常将被作为创建流程时的错误
	 * 
	 * @param compParameter
	 * @param initiateItem
	 */
	void doInit(ComponentParameter cp, InitiateItem initiateItem);

	/**
	 * 创建流程实例
	 * 
	 * @param compParameter
	 * @param initiateItem
	 * @return 返回
	 */
	String jsStartProcessCallback(ComponentParameter cp, ProcessBean process);
}
