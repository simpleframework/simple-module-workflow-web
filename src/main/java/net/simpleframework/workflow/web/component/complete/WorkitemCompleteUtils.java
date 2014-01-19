package net.simpleframework.workflow.web.component.complete;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.simpleframework.common.JsonUtils;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.object.ObjectUtils;
import net.simpleframework.common.web.JavascriptUtils;
import net.simpleframework.mvc.JavascriptForward;
import net.simpleframework.mvc.MVCUtils;
import net.simpleframework.mvc.PageRequestResponse;
import net.simpleframework.mvc.common.element.Checkbox;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.workflow.engine.ActivityComplete;
import net.simpleframework.workflow.engine.IWorkflowContextAware;
import net.simpleframework.workflow.engine.IWorkflowForm;
import net.simpleframework.workflow.engine.TransitionUtils;
import net.simpleframework.workflow.engine.WorkitemBean;
import net.simpleframework.workflow.engine.WorkitemComplete;
import net.simpleframework.workflow.engine.participant.Participant;
import net.simpleframework.workflow.schema.TransitionNode;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class WorkitemCompleteUtils implements IWorkflowContextAware {

	public static final String BEAN_ID = "workitemcomplete_@bid";

	public static ComponentParameter get(final PageRequestResponse rRequest) {
		return ComponentParameter.get(rRequest, BEAN_ID);
	}

	public static ComponentParameter get(final HttpServletRequest request,
			final HttpServletResponse response) {
		return ComponentParameter.get(request, response, BEAN_ID);
	}

	public static String toParams(final ComponentParameter cp, final WorkitemBean workitem) {
		final StringBuilder sb = new StringBuilder();
		if (workitem != null) {
			final String workitemIdParameterName = (String) cp
					.getBeanProperty("workitemIdParameterName");
			sb.append(workitemIdParameterName).append("=").append(workitem.getId()).append("&");
		}
		sb.append(BEAN_ID).append("=").append(cp.hashId());
		return sb.toString();
	}

	public static WorkitemBean getWorkitemBean(final ComponentParameter cp) {
		return context.getWorkitemService().getBean(
				cp.getParameter((String) cp.getBeanProperty("workitemIdParameterName")));
	}

	public static void doWorkitemComplete(final ComponentParameter cp) throws IOException {
		final JavascriptForward js = new JavascriptForward();
		try {
			final String confirmMessage = (String) cp.getBeanProperty("confirmMessage");
			if (StringUtils.hasText(confirmMessage)) {
				js.append("if (!confirm('").append(JavascriptUtils.escape(confirmMessage))
						.append("')) return;");
			}

			final WorkitemBean workitem = getWorkitemBean(cp);
			final WorkitemComplete workitemComplete = WorkitemComplete.get(workitem);
			// 绑定变量
			final IWorkflowForm workflowForm = (IWorkflowForm) workitemComplete.getWorkflowForm();
			workflowForm.bindVariables(workitemComplete.getVariables());

			final IWorkitemCompleteHandler hdl = (IWorkitemCompleteHandler) cp.getComponentHandler();
			if (!workitemComplete.isAllCompleted()) {
				js.append(hdl.onComplete(cp, workitemComplete));
			} else {
				final String componentName = cp.getComponentName();
				// 是否有手动情况
				final ActivityComplete activityComplete = workitemComplete.getActivityComplete();
				if (activityComplete.isTransitionManual()) {
					js.append("$Actions['").append(componentName).append("_transitionSelect']('")
							.append(toParams(cp, workitem)).append("');");
				} else if (activityComplete.isParticipantManual()) {
					js.append("$Actions['").append(componentName).append("_participantSelect']('")
							.append(toParams(cp, workitem)).append("');");
				} else {
					js.append(hdl.onComplete(cp, workitemComplete));
				}
			}
		} catch (final Throwable ex) {
			js.append("$error(").append(JsonUtils.toJSON(MVCUtils.createException(cp, ex)))
					.append(");");
		}
		final Writer out = cp.getResponseWriter();
		out.write(js.toString());
		out.flush();
	}

	public static Collection<TransitionNode> getTransitions(final PageRequestResponse rRequest,
			final WorkitemBean workitem) {
		final ArrayList<TransitionNode> al = new ArrayList<TransitionNode>();
		final String[] transitions = StringUtils.split(rRequest.getParameter("transitions"));
		final ActivityComplete activityComplete = WorkitemComplete.get(workitem)
				.getActivityComplete();
		// 通过手动方式选取的路由
		if (transitions != null && transitions.length > 0) {
			for (final String id : transitions) {
				final TransitionNode transition = activityComplete.getTransitionById(id);
				if (TransitionUtils.isTransitionManual(transition)) {
					al.add(transition);
				}
			}
		} else {
			al.addAll(activityComplete.getTransitions());
		}
		return al;
	}

	public static String toParticipantsHTML(ComponentParameter cp) {
		StringBuilder sb = new StringBuilder();
		final WorkitemBean workitem = getWorkitemBean(cp);
		for (final TransitionNode transition : WorkitemCompleteUtils.getTransitions(cp, workitem)) {
			sb.append("<div class='transition' transition='").append(transition.getId()).append("'>")
					.append(transition.to()).append("</div>");
			sb.append("<div class='participants'>");
			final Collection<Participant> coll = WorkitemComplete.get(workitem).getActivityComplete()
					.getParticipants(transition);
			if (coll == null || coll.size() == 0) {
				sb.append("#(participant_manual.1)");
			} else {
				for (Participant participant : coll) {
					sb.append("<div class='ritem'>");
					sb.append(new Checkbox(ObjectUtils.hashStr(participant), cp
							.getUser(participant.userId)).setValue(participant.getId()));
					sb.append("</div>");
				}
			}
			sb.append("<div class='msg'></div>");
			sb.append("</div>");
		}
		return sb.toString();
	}
}
