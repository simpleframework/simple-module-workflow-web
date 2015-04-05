package net.simpleframework.workflow.web.component.abort;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.simpleframework.mvc.JavascriptForward;
import net.simpleframework.mvc.PageRequestResponse;
import net.simpleframework.mvc.common.element.Checkbox;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.workflow.engine.ActivityBean;
import net.simpleframework.workflow.engine.IActivityService;
import net.simpleframework.workflow.engine.IWorkflowContextAware;
import net.simpleframework.workflow.schema.AbstractTaskNode;
import net.simpleframework.workflow.schema.UserNode;
import net.simpleframework.workflow.web.WorkflowUtils;
import net.simpleframework.workflow.web.component.WfComponentUtils;
import net.simpleframework.workflow.web.component.WfComponentUtils.IJavascriptCallback;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public abstract class ActivityAbortUtils implements IWorkflowContextAware {

	public static final String BEAN_ID = "activityabort_@bid";

	public static ComponentParameter get(final PageRequestResponse rRequest) {
		return ComponentParameter.get(rRequest, BEAN_ID);
	}

	public static ComponentParameter get(final HttpServletRequest request,
			final HttpServletResponse response) {
		return ComponentParameter.get(request, response, BEAN_ID);
	}

	public static void doForword(final ComponentParameter cp) throws Exception {
		WfComponentUtils.doForword(cp, new IJavascriptCallback() {
			@Override
			public void doJavascript(final JavascriptForward js) {
				js.append("$Actions['").append(cp.getComponentName()).append("_win']('")
						.append(cp.getParamsString()).append("');");
			}
		});
	}

	public static String toListHTML(final ComponentParameter cp) {
		final StringBuilder sb = new StringBuilder();
		final List<ActivityBean> list = ((IActivityAbortHandler) cp.getComponentHandler())
				.getActivities(cp);
		if (list != null) {
			final IActivityService aService = workflowContext.getActivityService();
			final Map<UserNode, List<ActivityBean>> cache = new LinkedHashMap<UserNode, List<ActivityBean>>();
			AbstractTaskNode tasknode;
			for (final ActivityBean activity : list) {
				if (!aService.isFinalStatus(activity)
						&& (tasknode = aService.getTaskNode(activity)) instanceof UserNode) {
					List<ActivityBean> l = cache.get(tasknode);
					if (l == null) {
						cache.put((UserNode) tasknode, l = new ArrayList<ActivityBean>());
					}
					l.add(activity);
				}
			}

			int i = 0;
			for (final Map.Entry<UserNode, List<ActivityBean>> e : cache.entrySet()) {
				sb.append("<div class='aitem'>");
				sb.append(" <div class='aitem1'>");
				sb.append(e.getKey().getText());
				sb.append(" </div>");
				final List<ActivityBean> v = e.getValue();
				for (final ActivityBean activity : v) {
					sb.append("<div class='aitem2'>");
					sb.append(new Checkbox("aitem" + i++, WorkflowUtils.getParticipants(activity, false))
							.setValue(activity.getId().toString()));
					sb.append("</div>");
				}
				sb.append("</div>");
			}
		}
		return sb.toString();
	}
}