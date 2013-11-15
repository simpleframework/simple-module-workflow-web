package net.simpleframework.workflow.web.component.worklist;

import static net.simpleframework.common.I18n.$m;

import java.awt.Dimension;

import javax.servlet.http.HttpSession;

import net.simpleframework.common.StringUtils;
import net.simpleframework.common.web.JavascriptUtils;
import net.simpleframework.common.web.html.HtmlConst;
import net.simpleframework.mvc.IForward;
import net.simpleframework.mvc.PageRequestResponse;
import net.simpleframework.mvc.UrlForward;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ui.pager.PagerUtils;
import net.simpleframework.workflow.engine.EWorkitemStatus;
import net.simpleframework.workflow.engine.IWorkflowContextAware;
import net.simpleframework.workflow.engine.IWorkflowForm;
import net.simpleframework.workflow.engine.WorkitemBean;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public abstract class WorklistUtils implements IWorkflowContextAware {
	public static final String STATUS = "status";

	public static EWorkitemStatus getWorkitemStatus(final PageRequestResponse rRequest) {
		final String status = rRequest.getParameter(STATUS);
		if (!"false".equals(status)) {
			return StringUtils.hasText(status) ? EWorkitemStatus.valueOf(status)
					: EWorkitemStatus.running;
		} else {
			return null;
		}
	}

	public static WorkitemBean getWorkitem(final PageRequestResponse rRequest) {
		return context.getWorkitemService().getBean(rRequest.getParameter(WorkitemBean.workitemId));
	}

	public static String getFormResponseText(final PageRequestResponse rRequest) {
		final WorkitemBean workitem = getWorkitem(rRequest);
		final IWorkflowForm workflowForm = (IWorkflowForm) context.getActivityService()
				.getWorkflowForm(context.getWorkitemService().getActivity(workitem));
		final StringBuilder sb = new StringBuilder();
		if (workflowForm != null) {
			sb.append(HtmlConst.TAG_SCRIPT_START);
			sb.append("(function() {");
			if (!workitem.isReadMark()) {
				sb.append("$Actions['").append(PagerUtils.get(rRequest).getComponentName())
						.append("'].refresh();");
			}
			sb.append("var win = $Actions['workflowFormWindow'].window;");
			final String title = workflowForm.getTitle();
			if (StringUtils.hasText(title)) {
				sb.append("win.setHeader(\"").append(JavascriptUtils.escape(title)).append("\");");
			}
			final Dimension d = workflowForm.getSize();
			if (d != null) {
				sb.append("win.setSize(").append(d.width).append(", ").append(d.height)
						.append(").center();");
			}
			sb.append("})();");
			sb.append(HtmlConst.TAG_SCRIPT_END);
			final IForward forward = new UrlForward(workflowForm.getFormForward());
			if (forward != null) {
				sb.append(forward.getResponseText(rRequest));
			}
		} else {
			sb.append("<p style='text-align: center;' class='important-tip f2'>")
					.append($m("WorklistUtils.0")).append("</p>");
		}
		return sb.toString();
	}

	public static String jsWorkflowForm(final ComponentParameter cp) {
		final StringBuilder sb = new StringBuilder();
		final HttpSession httpSession = cp.getSession();
		final Object id = httpSession.getAttribute(WorkitemBean.workitemId);
		if (id != null) {
			httpSession.removeAttribute(WorkitemBean.workitemId);
			final WorkitemBean workitem = context.getWorkitemService().getBean(id);
			if (workitem != null) {
				final IWorklistHandler lHandle = (IWorklistHandler) cp.getComponentHandler();
				sb.append("$ready(function() {");
				sb.append(lHandle.jsWorkflowFormAction(workitem));
				sb.append("});");
			}
		}
		return sb.toString();
	}
}
