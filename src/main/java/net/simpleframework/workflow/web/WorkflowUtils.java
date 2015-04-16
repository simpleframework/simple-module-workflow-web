package net.simpleframework.workflow.web;

import static net.simpleframework.common.I18n.$m;

import java.util.LinkedHashSet;
import java.util.Set;

import net.simpleframework.common.ID;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.object.ObjectEx.IVal;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.ButtonElement;
import net.simpleframework.mvc.common.element.ImageElement;
import net.simpleframework.mvc.common.element.SpanElement;
import net.simpleframework.workflow.engine.AbstractWorkitemBean;
import net.simpleframework.workflow.engine.ActivityBean;
import net.simpleframework.workflow.engine.EWorkitemStatus;
import net.simpleframework.workflow.engine.IWorkflowServiceAware;
import net.simpleframework.workflow.engine.ProcessBean;
import net.simpleframework.workflow.engine.ProcessModelBean;
import net.simpleframework.workflow.engine.WorkitemBean;
import net.simpleframework.workflow.engine.WorkviewBean;
import net.simpleframework.workflow.engine.participant.Participant;
import net.simpleframework.workflow.web.page.AbstractItemsTPage;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public abstract class WorkflowUtils implements IWorkflowServiceAware {

	public static String getProcessTitle(final ProcessBean process) {
		if (process == null) {
			return "";
		}
		final String title = process.getTitle();
		return StringUtils.hasText(title) ? title : $m("WorkflowUtils.0");
	}

	public static ButtonElement createLogButton() {
		return ButtonElement.logBtn().setDisabled(
				((IWorkflowWebContext) workflowContext).getLogRef() == null);
	}

	public static String toStatusHTML(final PageParameter pp, final Enum<?> status, final Object txt) {
		final StringBuilder sb = new StringBuilder();
		sb.append(new ImageElement(pp.getCssResourceHomePath(AbstractItemsTPage.class)
				+ "/images/status_" + status.name() + ".png").setClassName("icon16").addStyle(
				"margin: 0 4px;"));
		sb.append(new SpanElement(txt != null ? txt : status.toString()).setClassName("icon_txt"));
		return sb.toString();
	}

	public static String toStatusHTML(final PageParameter pp, final Enum<?> status) {
		return toStatusHTML(pp, status, null);
	}

	public static String getParticipants(final ActivityBean activity, final boolean r) {
		final StringBuilder sb = new StringBuilder();
		int i = 0;
		for (final Participant p : (r ? aService.getParticipants2(activity) : aService
				.getParticipants(activity, true))) {
			if (i++ > 0) {
				sb.append(", ");
			}
			sb.append(permission.getUser(p.userId).getText());
		}
		return sb.toString();
	}

	public static String getUserTo(final ActivityBean activity) {
		return activity.getAttrCache("to_" + activity.getId(), new IVal<String>() {
			@Override
			public String get() {
				final Set<String> list = new LinkedHashSet<String>();
				for (final ActivityBean nextActivity : aService.getNextActivities(activity)) {
					for (final WorkitemBean workitem : wService.getWorkitems(nextActivity)) {
						list.add(workitem.getUserText());
					}
				}
				return list.size() > 0 ? StringUtils.join(list, ", ") : null;
			}
		});
	}

	public static String getUserFrom(final ActivityBean activity) {
		final ActivityBean preActivity = aService.getPreActivity(activity);
		if (preActivity == null) {
			return null;
		}
		return activity.getAttrCache("from_" + preActivity.getId(), new IVal<String>() {
			@Override
			public String get() {
				final Set<String> list = new LinkedHashSet<String>();
				for (final WorkitemBean workitem : wService.getWorkitems(preActivity,
						EWorkitemStatus.complete)) {
					list.add(workitem.getUserText());
				}
				return list.size() > 0 ? StringUtils.join(list, ", ") : null;
			}
		});
	}

	public static WorkviewBean getWorkviewBean(final PageParameter pp) {
		final String workviewId = pp.getParameter("workviewId");
		if (workviewId == null) {
			return null;
		}
		return pp.getRequestCache(workviewId, new IVal<WorkviewBean>() {
			@Override
			public WorkviewBean get() {
				return vService.getBean(workviewId);
			}
		});
	}

	public static WorkitemBean getWorkitemBean(final PageParameter pp) {
		final String workitemId = pp.getParameter("workitemId");
		if (workitemId == null) {
			return null;
		}
		return pp.getRequestCache(workitemId, new IVal<WorkitemBean>() {
			@Override
			public WorkitemBean get() {
				return wService.getBean(workitemId);
			}
		});
	}

	public static ActivityBean getActivityBean(final PageParameter pp, final WorkitemBean workitem) {
		if (workitem == null) {
			final String activityId = pp.getParameter("activityId");
			return pp.getRequestCache("activity_" + StringUtils.blank(activityId),
					new IVal<ActivityBean>() {
						@Override
						public ActivityBean get() {
							Object _activityId = activityId;
							if (_activityId == null) {
								WorkitemBean workitem2;
								if ((workitem2 = getWorkitemBean(pp)) != null) {
									_activityId = workitem2.getActivityId();
								}
							}
							return aService.getBean(_activityId);
						}
					});
		} else {
			final ID activityId = workitem.getActivityId();
			return pp.getRequestCache("activity_" + activityId, new IVal<ActivityBean>() {
				@Override
				public ActivityBean get() {
					return aService.getBean(activityId);
				}
			});
		}
	}

	public static ActivityBean getActivityBean(final PageParameter pp) {
		return getActivityBean(pp, null);
	}

	public static ProcessBean getProcessBean(final PageParameter pp,
			final AbstractWorkitemBean workitem) {
		if (workitem == null) {
			final String processId = pp.getParameter("processId");
			return pp.getRequestCache("process_" + StringUtils.blank(processId),
					new IVal<ProcessBean>() {
						@Override
						public ProcessBean get() {
							Object _processId = processId;
							if (_processId == null) {
								AbstractWorkitemBean workitem2;
								if ((workitem2 = getWorkitemBean(pp)) != null) {
									_processId = workitem2.getProcessId();
								}
							}
							return pService.getBean(_processId);
						}
					});
		} else {
			final ID processId = workitem.getProcessId();
			return pp.getRequestCache("process_" + processId, new IVal<ProcessBean>() {
				@Override
				public ProcessBean get() {
					return pService.getBean(processId);
				}
			});
		}
	}

	public static ProcessBean getProcessBean(final PageParameter pp) {
		return getProcessBean(pp, null);
	}

	public static ProcessModelBean getProcessModel(final PageParameter pp) {
		final String modelId = pp.getParameter("modelId");
		return pp.getRequestCache("model_" + StringUtils.blank(modelId),
				new IVal<ProcessModelBean>() {
					@Override
					public ProcessModelBean get() {
						Object _modelId = modelId;
						if (_modelId == null) {
							ProcessBean process;
							if ((process = getProcessBean(pp)) != null) {
								_modelId = process.getModelId();
							}
						}
						return mService.getBean(_modelId);
					}
				});
	}
}
