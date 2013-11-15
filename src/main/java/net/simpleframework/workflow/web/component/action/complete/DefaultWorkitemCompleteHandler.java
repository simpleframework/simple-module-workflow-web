package net.simpleframework.workflow.web.component.action.complete;

import net.simpleframework.common.web.HttpUtils;
import net.simpleframework.mvc.component.AbstractComponentHandler;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.workflow.engine.WorkitemComplete;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class DefaultWorkitemCompleteHandler extends AbstractComponentHandler implements
		IWorkitemCompleteHandler {

	@Override
	public void complete(final ComponentParameter cp, final WorkitemComplete workitemComplete) {
		workitemComplete.complete(HttpUtils.map(cp.request));
	}

	@Override
	public String jsCompleteCallback(final ComponentParameter cp) {
		final StringBuilder sb = new StringBuilder();
		sb.append("$Actions['workflowFormWindow'].close();");
		sb.append("var wl = $Actions['myWorklist']; if (wl) wl.refresh();");
		return sb.toString();
	}
}
