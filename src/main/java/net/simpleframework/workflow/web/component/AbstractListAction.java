package net.simpleframework.workflow.web.component;

import net.simpleframework.mvc.JavascriptForward;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.base.ajaxrequest.DefaultAjaxRequestHandler;
import net.simpleframework.mvc.component.ui.pager.PagerUtils;
import net.simpleframework.workflow.engine.IWorkflowContextAware;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public abstract class AbstractListAction extends DefaultAjaxRequestHandler implements
		IWorkflowContextAware {

	protected JavascriptForward jsRefreshAction(final ComponentParameter cp,
			final JavascriptForward js) {
		js.append("$Actions['").append(PagerUtils.get(cp).getComponentName()).append("'].refresh();");
		return js;
	}
}
