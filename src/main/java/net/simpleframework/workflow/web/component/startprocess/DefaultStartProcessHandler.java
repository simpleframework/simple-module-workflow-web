package net.simpleframework.workflow.web.component.startprocess;

import net.simpleframework.mvc.JavascriptForward;
import net.simpleframework.mvc.component.AbstractComponentHandler;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.workflow.engine.InitiateItem;
import net.simpleframework.workflow.engine.bean.ProcessBean;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class DefaultStartProcessHandler extends AbstractComponentHandler
		implements IStartProcessHandler {
	@Override
	public void onInit(final ComponentParameter cp, final InitiateItem initiateItem) {
	}

	@Override
	public JavascriptForward onStartProcess(final ComponentParameter cp, final ProcessBean process) {
		return null;
	}
}
