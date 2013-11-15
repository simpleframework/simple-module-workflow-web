package net.simpleframework.workflow.web.component.processlist;

import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.component.ComponentUtils;
import net.simpleframework.workflow.engine.EProcessStatus;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public abstract class ProcessListUtils {

	static String getStatusIcon(final PageParameter pp, final EProcessStatus status) {
		final StringBuilder sb = new StringBuilder();
		sb.append("<img style='height: 9px; width: 9px; margin-right: 5px;' src='");
		sb.append(ComponentUtils.getCssResourceHomePath(pp, ProcessListBean.class));
		sb.append("/images/dot_").append(status.name()).append(".png' />");
		return sb.toString();
	}
}
