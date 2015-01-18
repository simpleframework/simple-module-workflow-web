package net.simpleframework.workflow.web.component.abort;

import java.util.List;

import net.simpleframework.mvc.component.AbstractComponentHandler;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.workflow.engine.ActivityBean;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class DefaultActivityAbortHandler extends AbstractComponentHandler implements
		IActivityAbortHandler {

	@Override
	public List<ActivityBean> getActivities(final ComponentParameter cp) {
		return null;
	}
}