package net.simpleframework.workflow.web.component.complete;

import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.simpleframework.common.JsonUtils;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.coll.ArrayUtils;
import net.simpleframework.common.logger.Log;
import net.simpleframework.common.logger.LogFactory;
import net.simpleframework.common.object.ObjectFactory.ObjectCreatorListener;
import net.simpleframework.common.web.JavascriptUtils;
import net.simpleframework.ctx.permission.PermissionUser;
import net.simpleframework.mvc.JavascriptForward;
import net.simpleframework.mvc.MVCUtils;
import net.simpleframework.mvc.PageRequestResponse;
import net.simpleframework.mvc.common.element.Checkbox;
import net.simpleframework.mvc.common.element.Radio;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.workflow.engine.ActivityComplete;
import net.simpleframework.workflow.engine.IWorkflowServiceAware;
import net.simpleframework.workflow.engine.TransitionUtils;
import net.simpleframework.workflow.engine.WorkitemBean;
import net.simpleframework.workflow.engine.WorkitemComplete;
import net.simpleframework.workflow.engine.participant.Participant;
import net.simpleframework.workflow.schema.AbstractTaskNode;
import net.simpleframework.workflow.schema.TransitionNode;
import net.simpleframework.workflow.schema.UserNode;
import net.simpleframework.workflow.web.IWorkflowWebForm;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class WorkitemCompleteUtils implements IWorkflowServiceAware {

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
		final String parameters = (String) cp.getBeanProperty("parameters");
		if (StringUtils.hasText(parameters)) {
			sb.append("&").append(parameters);
		}
		return sb.toString();
	}

	public static WorkitemBean getWorkitemBean(final ComponentParameter cp) {
		return wService.getBean(cp.getParameter((String) cp
				.getBeanProperty("workitemIdParameterName")));
	}

	public static void doWorkitemComplete(final ComponentParameter cp) throws Exception {
		final JavascriptForward js = new JavascriptForward();
		try {
			final String confirmMessage = (String) cp.getBeanProperty("confirmMessage");
			if (StringUtils.hasText(confirmMessage)) {
				js.append("if (!confirm('");
				js.append(JavascriptUtils.escape(confirmMessage)).append("')) return;");
			}

			final WorkitemBean workitem = getWorkitemBean(cp);
			final WorkitemComplete workitemComplete = WorkitemComplete.get(workitem);
			// 绑定变量
			final IWorkflowWebForm workflowForm = (IWorkflowWebForm) workitemComplete
					.getWorkflowForm();
			workflowForm.bindVariables(cp, workitemComplete.getVariables());

			final IWorkitemCompleteHandler hdl = (IWorkitemCompleteHandler) cp.getComponentHandler();
			if (!workitemComplete.isAllCompleted()) {
				js.append(hdl.onComplete(cp, workitem));
			} else {
				final String componentName = cp.getComponentName();
				// 是否有手动情况
				final ActivityComplete activityComplete = getActivityComplete(cp, workitem);
				if (activityComplete.isTransitionManual()) {
					js.append("$Actions['").append(componentName).append("_TransitionSelect']('")
							.append(toParams(cp, workitem)).append("');");
				} else if (activityComplete.isParticipantManual()) {
					js.append("$Actions['").append(componentName).append("_ParticipantSelect']('")
							.append(toParams(cp, workitem)).append("');");
				} else {
					js.append(hdl.onComplete(cp, workitem));
				}
			}
		} catch (final Throwable ex) {
			log.error(ex);
			js.append("$error(");
			js.append(JsonUtils.toJSON(MVCUtils.createException(cp, ex))).append(");");
		}
		final Writer out = cp.getResponseWriter();
		out.write(js.toString());
		out.flush();
	}

	private static Collection<TransitionNode> getTransitions(final ComponentParameter cp,
			final WorkitemBean workitem) {
		final ArrayList<TransitionNode> al = new ArrayList<TransitionNode>();
		final String[] transitions = StringUtils.split(cp.getParameter("transitions"));
		final ActivityComplete activityComplete = getActivityComplete(cp, workitem);
		// 通过手动方式选取的路由
		if (transitions.length > 0) {
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

	public static String toTransitionsHTML(final ComponentParameter cp, final WorkitemBean workitem) {
		final StringBuilder sb = new StringBuilder();
		final UserNode node = (UserNode) aService.getTaskNode(wService.getActivity(workitem));
		int i = 0;
		for (final TransitionNode transition : WorkitemCompleteUtils.getTransitions(cp, workitem)) {
			final String id = transition.getId();
			sb.append("<div class='ritem'>");
			if (node.isMultiTransitionSelected()) {
				sb.append(new Checkbox(id, transition.to()).setValue(id));
			} else {
				sb.append(new Radio(id, transition.to()).setName("transitions_radio").setValue(id)
						.setChecked(i++ == 0));
			}
			sb.append("</div>");
		}
		return sb.toString();
	}

	public static String toParticipantsHTML(final ComponentParameter cp) {
		final StringBuilder sb = new StringBuilder();
		final String[] dispWithDept = (String[]) cp.getBeanProperty("dispWithDept");
		final WorkitemBean workitem = getWorkitemBean(cp);
		final ActivityComplete activityComplete = getActivityComplete(cp, workitem);
		for (final TransitionNode transition : WorkitemCompleteUtils.getTransitions(cp, workitem)) {
			final AbstractTaskNode to = transition.to();
			final boolean manual = activityComplete.isParticipantManual(to);
			sb.append("<div class='transition' transition='").append(transition.getId()).append("'>");
			sb.append(transition.to()).append("</div>");
			sb.append("<div class='participants'>");
			final Collection<Participant> coll = activityComplete.getParticipants(transition);
			if (coll == null || coll.size() == 0) {
				sb.append("#(participant_select.0)");
			} else {
				final boolean multi = activityComplete.isParticipantMultiSelected(to);
				int i = 0;
				for (final Participant participant : coll) {
					sb.append("<div class='ritem'>");
					final String id = participant.toString();
					Object user = permission.getUser(participant.userId);
					if (ArrayUtils.contains(dispWithDept, to.getName())) {
						user = ((PermissionUser) user).getDept().getText();
					}
					Checkbox box;
					if (!manual) {
						box = new Checkbox(id, user).setChecked(true);
					} else {
						if (multi) {
							box = new Checkbox(id, user);
						} else {
							box = new Radio(id, user).setChecked(i++ == 0).setName(transition.getId());
						}
					}
					sb.append(box);
					sb.append("</div>");
				}
			}
			sb.append("<div class='msg'></div>");
			sb.append("</div>");
			if (!manual) {
				sb.insert(0, "<div style='display:none;'>");
				sb.append("</div>");
			}
		}
		return sb.toString();
	}

	static ActivityComplete getActivityComplete(final ComponentParameter cp,
			final WorkitemBean workitem) {
		return WorkitemComplete.get(workitem).getActivityComplete(new ObjectCreatorListener() {
			@Override
			public void onCreated(final Object o) {
				((ActivityComplete) o).setBcomplete((Boolean) cp.getBeanProperty("bcomplete"));
			}
		});
	}

	private static Log log = LogFactory.getLogger(WorkitemCompleteUtils.class);
}
