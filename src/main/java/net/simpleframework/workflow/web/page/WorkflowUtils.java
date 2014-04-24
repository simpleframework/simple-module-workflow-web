package net.simpleframework.workflow.web.page;

import static net.simpleframework.common.I18n.$m;
import net.simpleframework.common.Convert;
import net.simpleframework.common.StringUtils;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.ButtonElement;
import net.simpleframework.mvc.common.element.ImageElement;
import net.simpleframework.mvc.common.element.SpanElement;
import net.simpleframework.mvc.template.AbstractTemplatePage;
import net.simpleframework.workflow.engine.IWorkflowContextAware;
import net.simpleframework.workflow.engine.ProcessBean;
import net.simpleframework.workflow.engine.WorkitemBean;
import net.simpleframework.workflow.web.IWorkflowWebContext;

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

	public static String getTitle(final ProcessBean process) {
		return StringUtils.text(Convert.toString(process), $m("WorkflowUtils.0"));
	}

	public static ButtonElement createLogButton() {
		return ButtonElement.logBtn().setDisabled(
				((IWorkflowWebContext) workflowContext).getLogRef() == null);
	}

	public static String toStatusHTML(final PageParameter pp, final Enum<?> status, final Object txt) {
		final StringBuilder sb = new StringBuilder();
		sb.append(new ImageElement(pp.getCssResourceHomePath(WorkflowUtils.class) + "/images/status_"
				+ status.name() + ".png").setClassName("icon16").addStyle("margin: 0 4px;"));
		sb.append(new SpanElement(txt != null ? txt : status.toString()).setClassName("icon_txt"));
		return sb.toString();
	}

	public static String toStatusHTML(final PageParameter pp, final Enum<?> status) {
		return toStatusHTML(pp, status, null);
	}
}
