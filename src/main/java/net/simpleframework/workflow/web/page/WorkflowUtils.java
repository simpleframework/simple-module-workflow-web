package net.simpleframework.workflow.web.page;

import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.EVerticalAlign;
import net.simpleframework.mvc.common.element.ImageElement;
import net.simpleframework.mvc.template.AbstractTemplatePage;
import net.simpleframework.workflow.engine.IWorkflowContextAware;
import net.simpleframework.workflow.engine.WorkitemBean;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public abstract class WorkflowUtils implements IWorkflowContextAware {

	public static WorkitemBean getWorkitemBean(final PageParameter pp) {
		return AbstractTemplatePage.getCacheBean(pp, wService, "workitemId");
	}

	public static ImageElement createStatusImage(final PageParameter pp, final Enum<?> status) {
		return new ImageElement(pp.getCssResourceHomePath(WorkflowUtils.class) + "/images/status_"
				+ status.name() + ".png").setVerticalAlign(EVerticalAlign.bottom)
				.setClassName("icon16").addStyle("margin: 0 4px;");
	}
}
