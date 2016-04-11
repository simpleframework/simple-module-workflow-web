package net.simpleframework.workflow.web;

import static net.simpleframework.common.I18n.$m;

import java.util.LinkedHashSet;
import java.util.Set;

import net.simpleframework.common.ID;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.object.ObjectEx.CacheV;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.AbstractElement;
import net.simpleframework.mvc.common.element.ButtonElement;
import net.simpleframework.mvc.common.element.ImageElement;
import net.simpleframework.mvc.common.element.SpanElement;
import net.simpleframework.workflow.engine.EWorkitemStatus;
import net.simpleframework.workflow.engine.IWorkflowContextAware;
import net.simpleframework.workflow.engine.bean.AbstractWorkitemBean;
import net.simpleframework.workflow.engine.bean.ActivityBean;
import net.simpleframework.workflow.engine.bean.DelegationBean;
import net.simpleframework.workflow.engine.bean.ProcessBean;
import net.simpleframework.workflow.engine.bean.ProcessModelBean;
import net.simpleframework.workflow.engine.bean.UserStatBean;
import net.simpleframework.workflow.engine.bean.WorkitemBean;
import net.simpleframework.workflow.engine.bean.WorkviewBean;
import net.simpleframework.workflow.engine.participant.Participant;
import net.simpleframework.workflow.schema.AbstractTaskNode;
import net.simpleframework.workflow.schema.UserNode;
import net.simpleframework.workflow.web.page.list.AbstractItemsTPage;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public abstract class WorkflowUtils implements IWorkflowContextAware {

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

	public static AbstractElement<?> getStatusIcon(final PageParameter pp, final Enum<?> status) {
		return new ImageElement(pp.getCssResourceHomePath(AbstractItemsTPage.class)
				+ "/images/status_" + status.name() + ".png").setClassName("icon16").setTitle(
				status.toString());
	}

	public static String getParticipants(final PageParameter pp, final ActivityBean activity,
			final boolean r) {
		final StringBuilder sb = new StringBuilder();
		int i = 0;
		for (final Participant p : (r ? wfaService.getParticipants2(activity) : wfaService
				.getParticipants(activity, true))) {
			if (i++ > 0) {
				sb.append(", ");
			}
			sb.append(p.getUser().getText());
			if (!r) {
				final DelegationBean delegation = wfdService.queryRunningDelegation(p.getWorkitem());
				if (delegation != null) {
					sb.append(SpanElement.colora00($m("WorkflowUtils.1", delegation.getUserText())));
				}
			}
		}
		return sb.toString();
	}

	public static String getUserFrom(final ActivityBean activity, final String sep) {
		return activity.getAttrCache("_UserFrom", new CacheV<String>() {
			@Override
			public String get() {
				ActivityBean preActivity = activity;
				while ((preActivity = wfaService.getPreActivity(preActivity)) != null) {
					final AbstractTaskNode tasknode = wfaService.getTaskNode(preActivity);
					if (tasknode instanceof UserNode && !((UserNode) tasknode).isEmpty()) {
						break;
					}
				}
				if (preActivity == null) {
					return null;
				}

				final Set<String> list = new LinkedHashSet<String>();
				for (final WorkitemBean workitem : wfwService.getWorkitems(preActivity,
						EWorkitemStatus.complete)) {
					list.add(workitem.getUserText());
				}
				return list.size() > 0 ? StringUtils.join(list, StringUtils.text(sep, ", ")) : null;
			}
		});
	}

	public static WorkviewBean getWorkviewBean(final PageParameter pp) {
		final String workviewId = pp.getParameter("workviewId");
		if (workviewId == null) {
			return null;
		}
		return pp.getRequestCache(workviewId, new CacheV<WorkviewBean>() {
			@Override
			public WorkviewBean get() {
				return wfvService.getBean(workviewId);
			}
		});
	}

	public static WorkitemBean getWorkitemBean(final PageParameter pp) {
		final String workitemId = pp.getParameter("workitemId");
		if (workitemId == null) {
			return null;
		}
		return pp.getRequestCache(workitemId, new CacheV<WorkitemBean>() {
			@Override
			public WorkitemBean get() {
				return wfwService.getBean(workitemId);
			}
		});
	}

	public static ActivityBean getActivityBean(final PageParameter pp, final WorkitemBean workitem) {
		if (workitem == null) {
			final String activityId = pp.getParameter("activityId");
			return pp.getRequestCache("activity_" + StringUtils.blank(activityId),
					new CacheV<ActivityBean>() {
						@Override
						public ActivityBean get() {
							Object _activityId = activityId;
							if (_activityId == null) {
								WorkitemBean workitem2;
								if ((workitem2 = getWorkitemBean(pp)) != null) {
									_activityId = workitem2.getActivityId();
								}
							}
							return wfaService.getBean(_activityId);
						}
					});
		} else {
			final ID activityId = workitem.getActivityId();
			return pp.getRequestCache("activity_" + activityId, new CacheV<ActivityBean>() {
				@Override
				public ActivityBean get() {
					return wfaService.getBean(activityId);
				}
			});
		}
	}

	public static ActivityBean getActivityBean(final PageParameter pp) {
		return getActivityBean(pp, null);
	}

	public static AbstractTaskNode getTaskNode(final PageParameter pp) {
		return pp.getRequestCache("_TaskNode", new CacheV<AbstractTaskNode>() {
			@Override
			public AbstractTaskNode get() {
				return wfaService.getTaskNode(getActivityBean(pp));
			}
		});
	}

	public static ProcessBean getProcessBean(final PageParameter pp,
			final AbstractWorkitemBean workitem) {
		if (workitem == null) {
			final String processId = pp.getParameter("processId");
			return pp.getRequestCache("process_" + StringUtils.blank(processId),
					new CacheV<ProcessBean>() {
						@Override
						public ProcessBean get() {
							Object _processId = processId;
							if (_processId == null) {
								AbstractWorkitemBean workitem2 = getWorkitemBean(pp);
								if (workitem2 == null) {
									workitem2 = getWorkviewBean(pp);
								}
								if (workitem2 != null) {
									_processId = workitem2.getProcessId();
								}
							}
							return wfpService.getBean(_processId);
						}
					});
		} else {
			final ID processId = workitem.getProcessId();
			return pp.getRequestCache("process_" + processId, new CacheV<ProcessBean>() {
				@Override
				public ProcessBean get() {
					return wfpService.getBean(processId);
				}
			});
		}
	}

	public static ProcessBean getProcessBean(final PageParameter pp) {
		return getProcessBean(pp, null);
	}

	public static ProcessModelBean getProcessModel(final PageParameter pp) {
		final String model = pp.getParameter("model");
		if (StringUtils.hasText(model)) {
			final ProcessModelBean pm = pp.getRequestCache("model_" + model,
					new CacheV<ProcessModelBean>() {
						@Override
						public ProcessModelBean get() {
							return wfpmService.getProcessModelByName(model);
						}
					});
			if (pm != null) {
				return pm;
			}
		}
		final String modelId = pp.getParameter("modelId");
		return pp.getRequestCache("model_" + StringUtils.blank(modelId),
				new CacheV<ProcessModelBean>() {
					@Override
					public ProcessModelBean get() {
						Object _modelId = modelId;
						if (_modelId == null) {
							ProcessBean process;
							if ((process = getProcessBean(pp)) != null) {
								_modelId = process.getModelId();
							}
						}
						return wfpmService.getBean(_modelId);
					}
				});
	}

	public static UserStatBean getUserStat(final PageParameter pp) {
		return pp.getRequestCache("_getUserStat", new CacheV<UserStatBean>() {
			@Override
			public UserStatBean get() {
				return wfusService.getUserStat(pp.getLoginId());
			}
		});
	}
}
