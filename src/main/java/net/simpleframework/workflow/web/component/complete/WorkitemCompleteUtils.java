package net.simpleframework.workflow.web.component.complete;

import static net.simpleframework.common.I18n.$m;

import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.simpleframework.common.Convert;
import net.simpleframework.common.JsonUtils;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.coll.ArrayUtils;
import net.simpleframework.common.logger.Log;
import net.simpleframework.common.logger.LogFactory;
import net.simpleframework.common.object.ObjectUtils;
import net.simpleframework.common.web.JavascriptUtils;
import net.simpleframework.ctx.permission.PermissionDept;
import net.simpleframework.mvc.JavascriptForward;
import net.simpleframework.mvc.MVCUtils;
import net.simpleframework.mvc.PageRequestResponse;
import net.simpleframework.mvc.common.element.Checkbox;
import net.simpleframework.mvc.common.element.Radio;
import net.simpleframework.mvc.common.element.SpanElement;
import net.simpleframework.mvc.component.AbstractComponentRender;
import net.simpleframework.mvc.component.AbstractComponentRender.IJavascriptCallback;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.workflow.engine.ActivityComplete;
import net.simpleframework.workflow.engine.IWorkflowContextAware;
import net.simpleframework.workflow.engine.TransitionUtils;
import net.simpleframework.workflow.engine.WorkitemComplete;
import net.simpleframework.workflow.engine.bean.DelegationBean;
import net.simpleframework.workflow.engine.bean.WorkitemBean;
import net.simpleframework.workflow.engine.participant.Participant;
import net.simpleframework.workflow.schema.AbstractTaskNode;
import net.simpleframework.workflow.schema.TransitionNode;
import net.simpleframework.workflow.schema.UserNode;
import net.simpleframework.workflow.web.IWorkflowWebForm;
import net.simpleframework.workflow.web.WorkflowUtils;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public abstract class WorkitemCompleteUtils implements IWorkflowContextAware {

	public static final String BEAN_ID = "workitemcomplete_@bid";

	public static ComponentParameter get(final PageRequestResponse rRequest) {
		return ComponentParameter.get(rRequest, BEAN_ID);
	}

	public static ComponentParameter get(final HttpServletRequest request,
			final HttpServletResponse response) {
		return ComponentParameter.get(request, response, BEAN_ID);
	}

	public static String toParams(final ComponentParameter cp) {
		final StringBuilder sb = new StringBuilder();
		final WorkitemBean workitem = WorkflowUtils.getWorkitemBean(cp);
		if (workitem != null) {
			sb.append("workitemId=").append(workitem.getId()).append("&");
		}
		sb.append(BEAN_ID).append("=").append(cp.hashId());
		// 要选择的参与者
		final String selected_userid = cp.getParameter("selected_userid");
		if (StringUtils.hasText(selected_userid)) {
			sb.append("&selected_userid=").append(selected_userid);
		}
		final String selected_deptid = cp.getParameter("selected_deptid");
		if (StringUtils.hasText(selected_deptid)) {
			sb.append("&selected_deptid=").append(selected_deptid);
		}
		return sb.toString();
	}

	public static void doForword(final ComponentParameter cp) throws Exception {
		AbstractComponentRender.doJavascriptForward(cp, new IJavascriptCallback() {
			@Override
			public void doJavascript(final JavascriptForward js) {
				try {
					final WorkitemBean workitem = WorkflowUtils.getWorkitemBean(cp);
					final WorkitemComplete workitemComplete = WorkitemComplete.get(workitem);

					// 绑定变量
					final IWorkflowWebForm workflowForm = (IWorkflowWebForm) workitemComplete
							.getWorkflowForm();
					workflowForm.bindVariables(cp, workitemComplete.getVariables());

					if (!workitemComplete.isAllCompleted()) {
						_appendWorkitemComplete(cp, js);
					} else {
						// 是否有手动情况
						final ActivityComplete activityComplete = getActivityComplete(cp);
						activityComplete.reset();
						if (activityComplete.isTransitionManual()) {
							js.append("$Actions['").append(cp.getComponentName())
									.append("_TransitionSelect']('").append(toParams(cp)).append("');");
						} else if (activityComplete.isParticipantManual()) {
							js.append("$Actions['").append(cp.getComponentName())
									.append("_ParticipantSelect']('").append(toParams(cp)).append("');");
						} else {
							_appendWorkitemComplete(cp, js);
						}
					}
				} catch (final Throwable ex) {
					js.append(createErrorForward(cp, ex));
				}
			}
		});
	}

	private static void _appendWorkitemComplete(final ComponentParameter cp,
			final JavascriptForward js) throws Exception {
		final String confirmMessage = (String) cp.getBeanProperty("confirmMessage");
		if (StringUtils.hasText(confirmMessage)) {
			js.append("if (!confirm('");
			js.append(JavascriptUtils.escape(confirmMessage)).append("')) return;");
			js.append("$Actions['").append(cp.getComponentName()).append("_Comfirm']('")
					.append(toParams(cp)).append("');");
		} else {
			final IWorkitemCompleteHandler hdl = (IWorkitemCompleteHandler) cp.getComponentHandler();
			js.append(hdl.onComplete(cp, WorkflowUtils.getWorkitemBean(cp)));
		}
	}

	static JavascriptForward createErrorForward(final PageRequestResponse rRequest,
			final Throwable ex) {
		log.error(ex);
		return (JavascriptForward) new JavascriptForward("$error(")
				.append(JsonUtils.toJSON(MVCUtils.createException(rRequest, ex))).append(");")
				.setAttr("_throwable", ex);

	}

	private static Collection<TransitionNode> getTransitions(final ComponentParameter cp) {
		final ArrayList<TransitionNode> al = new ArrayList<TransitionNode>();
		final String[] transitions = StringUtils.split(cp.getParameter("transitions"));
		final ActivityComplete activityComplete = getActivityComplete(cp);
		// 通过手动方式选取的路由
		if (transitions.length > 0) {
			for (final String id : transitions) {
				final TransitionNode transition = activityComplete.getTransitionById(id);
				if (transition != null) {
					al.add(transition);
				}
			}
		} else {
			al.addAll(activityComplete.getTransitions());
		}
		return al;
	}

	public static String toTransitionsHTML(final ComponentParameter cp) {
		final StringBuilder sb = new StringBuilder();
		final UserNode node = (UserNode) WorkflowUtils.getTaskNode(cp);
		int i = 0;
		for (final TransitionNode transition : getTransitions(cp)) {
			final String val = transition.getId();
			sb.append("<div class='ritem'>");
			String labelTxt = transition.getText();
			if (!StringUtils.hasText(labelTxt)) {
				labelTxt = transition.to().toString();
			}
			if (!TransitionUtils.isTransitionManual(transition)) {
				sb.append(new Checkbox(val, labelTxt).setDisabled(true).setChecked(true).setValue(val));
			} else {
				if (node.isMultiTransitionSelected()) {
					sb.append(new Checkbox(val, labelTxt).setValue(val));
				} else {
					sb.append(new Radio(val, labelTxt).setName("transitions_radio").setValue(val)
							.setChecked(i++ == 0));
				}
			}
			sb.append("</div>");
		}
		return sb.toString();
	}

	public static String toParticipantsHTML(final ComponentParameter cp) {
		final StringBuilder sb = new StringBuilder();
		final String[] deptdispTasks = (String[]) cp.getBeanProperty("deptdispTasks");
		final String[] novalidationTasks = (String[]) cp.getBeanProperty("novalidationTasks");
		final ActivityComplete activityComplete = getActivityComplete(cp);
		for (final TransitionNode transition : getTransitions(cp)) {
			final AbstractTaskNode to = transition.to();
			final boolean manual = activityComplete.isParticipantManual(to);
			final boolean multi = activityComplete.isParticipantMultiSelected(to);

			final Object transitionId = transition.getId();
			sb.append("<div class='transition' novalidation='")
					.append(ArrayUtils.contains(novalidationTasks, to.getName()))
					.append("' transition='").append(transitionId).append("'>");
			if (manual && multi) {
				sb.append(new Checkbox("cbox_" + transitionId, transition.to())
						.setOnclick("var p = this.up('.transition').next(); var b = this.checked;"
								+ "p.select('input[type=checkbox]').each(function (box) { box.checked = b; });"));
			} else {
				sb.append(transition.to());
			}

			sb.append("</div>");
			sb.append("<div class='participants'>");
			final Collection<Participant> coll = activityComplete.getParticipants(transition);
			if (coll == null || coll.size() == 0) {
				sb.append("<div class='msg'>");
				sb.append(" <span>#(participant_select.1)</span>");
				sb.append("</div>");
			} else {
				final String[] userArr = StringUtils.split(cp.getParameter("selected_userid"));
				final String[] deptArr = StringUtils.split(cp.getParameter("selected_deptid"));
				int i = 0;
				for (final Participant participant : coll) {
					sb.append("<div class='ritem'>");
					final String val = participant.toString();
					Object user = participant.getUser();
					final DelegationBean delegation = wfdService
							.queryRunningDelegation(participant.getUserId());
					if (delegation != null) {
						user = user + $m("WorkflowUtils.1", delegation.getUserText());
					}
					if (ArrayUtils.contains(deptdispTasks, to.getName())) {
						final PermissionDept dept = cp.getDept(participant.getDeptId());
						user = dept.getText() + "<br>"
								+ SpanElement.color999(user).addStyle("font-size: 10.5px");
					}
					final String id = ObjectUtils.hashStr(participant);
					Checkbox box;
					if (!manual) {
						box = new Checkbox(id, user).setDisabled(true).setChecked(true);
					} else {
						final boolean check = isCheck(userArr, transition, participant.getUserId())
								|| isCheck(deptArr, transition, participant.getDeptId());
						if (multi) {
							box = new Checkbox(id, user).setChecked(check);
						} else {
							box = new Radio(id, user).setChecked(i++ == 0 || check)
									.setName(transition.getId());
						}
					}
					sb.append(box.setValue(val));
					sb.append("</div>");
				}
			}
			sb.append(" <div class='msg'></div>");
			sb.append("</div>");
		}
		return sb.toString();
	}

	private static boolean isCheck(final String[] arr, final TransitionNode transition,
			final Object id) {
		if (arr == null || arr.length == 0 || id == null) {
			return false;
		}
		final String taskName = transition.to().getName();
		final String tid = Convert.toString(id);
		for (final String s : arr) {
			final String[] arr2 = StringUtils.split(s, ":");
			if (arr2.length == 1 && arr2[0].equals(tid)) {
				return true;
			}
			if (arr2.length == 2 && arr2[0].equals(taskName) && arr2[1].equals(tid)) {
				return true;
			}
		}
		return false;
	}

	static ActivityComplete getActivityComplete(final ComponentParameter cp) {
		return WorkitemComplete.get(WorkflowUtils.getWorkitemBean(cp)).getActivityComplete()
				.setBcomplete((Boolean) cp.getBeanProperty("bcomplete"));
	}

	private static Log log = LogFactory.getLogger(WorkitemCompleteUtils.class);
}
