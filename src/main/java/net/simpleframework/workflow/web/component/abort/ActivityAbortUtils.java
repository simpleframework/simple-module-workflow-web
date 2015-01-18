package net.simpleframework.workflow.web.component.abort;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.simpleframework.common.web.JavascriptUtils;
import net.simpleframework.mvc.IMVCConst;
import net.simpleframework.mvc.JavascriptForward;
import net.simpleframework.mvc.PageRequestResponse;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.workflow.engine.ActivityBean;
import net.simpleframework.workflow.engine.IActivityService;
import net.simpleframework.workflow.engine.IWorkflowContextAware;
import net.simpleframework.workflow.schema.AbstractTaskNode;
import net.simpleframework.workflow.schema.UserNode;

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

	public static void doActivityAbort(final ComponentParameter cp) throws IOException {
		final JavascriptForward js = new JavascriptForward();
		final Enumeration<String> e = cp.getParameterNames();
		final StringBuilder sb = new StringBuilder();
		int i = 0;
		while (e.hasMoreElements()) {
			final String key = e.nextElement();
			if (!IMVCConst.REQUEST_ID.equals(key)) {
				if (i++ > 0) {
					sb.append("&");
				}
				sb.append(key).append("=").append(cp.getParameter(key));
			}
		}
		js.append("$Actions['").append(cp.getComponentName()).append("_win']('")
				.append(sb.toString()).append("');");
		final Writer out = cp.getResponseWriter();
		out.write(JavascriptUtils.wrapFunction(js.toString()));
		out.flush();
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

			for (final Map.Entry<UserNode, List<ActivityBean>> e : cache.entrySet()) {
				sb.append("<div class='aitem'>");
				final UserNode node = e.getKey();
				sb.append(node.getText());
				sb.append("</div>");
			}
		}
		return sb.toString();
	}
}