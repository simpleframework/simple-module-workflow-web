package net.simpleframework.workflow.web.component.fallback;

import static net.simpleframework.common.I18n.$m;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.simpleframework.mvc.JavascriptForward;
import net.simpleframework.mvc.PageRequestResponse;
import net.simpleframework.mvc.common.element.ButtonElement;
import net.simpleframework.mvc.common.element.Checkbox;
import net.simpleframework.mvc.common.element.SpanElement;
import net.simpleframework.mvc.component.AbstractComponentRender;
import net.simpleframework.mvc.component.AbstractComponentRender.IJavascriptCallback;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.workflow.engine.IWorkflowContextAware;
import net.simpleframework.workflow.engine.bean.ActivityBean;
import net.simpleframework.workflow.schema.AbstractTaskNode;
import net.simpleframework.workflow.schema.UserNode;
import net.simpleframework.workflow.web.WorkflowUtils;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public abstract class ActivityFallbackUtils implements IWorkflowContextAware {
	public static final String BEAN_ID = "activityfallback_@bid";

	public static ComponentParameter get(final PageRequestResponse rRequest) {
		return ComponentParameter.get(rRequest, BEAN_ID);
	}

	public static ComponentParameter get(final HttpServletRequest request,
			final HttpServletResponse response) {
		return ComponentParameter.get(request, response, BEAN_ID);
	}

	public static String toParams(final ComponentParameter cp) {
		final StringBuilder sb = new StringBuilder();
		ActivityBean activity;
		if ((activity = WorkflowUtils.getActivityBean(cp)) != null) {
			sb.append("activityId=").append(activity.getId()).append("&");
		}
		sb.append(BEAN_ID).append("=").append(cp.hashId());
		return sb.toString();
	}

	public static void doForword(final ComponentParameter cp) throws Exception {
		AbstractComponentRender.doJavascriptForward(cp, new IJavascriptCallback() {
			@Override
			public void doJavascript(final JavascriptForward js) {
				js.append("$Actions['").append(cp.getComponentName()).append("_win']('")
						.append(toParams(cp)).append("');");
			}
		});
	}

	public static String toListHTML(final ComponentParameter cp) {
		final StringBuilder sb = new StringBuilder();
		sb.append("<div class='items'>");
		final ActivityBean activity = WorkflowUtils.getActivityBean(cp);
		final Map<String, UserNode> cache = new LinkedHashMap<String, UserNode>();
		ActivityBean pre = activity;
		while ((pre = wfaService.getPreActivity(pre)) != null) {
			final AbstractTaskNode tasknode = wfaService.getTaskNode(pre);
			if (tasknode instanceof UserNode) {
				cache.put(tasknode.getName(), (UserNode) tasknode);
			}
		}
		for (final UserNode usernode : cache.values()) {
			sb.append("<div class='nitem'>");
			sb.append(usernode);
			sb.append("</div>");
			sb.append("<div class='nitem'>");
			sb.append(usernode);
			sb.append("</div>");
			sb.append("<div class='nitem'>");
			sb.append(usernode);
			sb.append("</div>");
		}
		sb.append("</div>");
		return sb.toString();
	}

	public static String toBottomHTML(final ComponentParameter cp) {
		final StringBuilder sb = new StringBuilder();
		sb.append("<div class='left'>");
		sb.append(new Checkbox("idActivityFallback_opt1", $m("ActivityFallbackUtils.0"))
				.setChecked(true));
		sb.append("</div>");
		sb.append("<div class='right'>");
		sb.append(ButtonElement.okBtn()).append(SpanElement.SPACE);
		sb.append(ButtonElement.closeBtn());
		sb.append("</div>");
		return sb.toString();
	}
}