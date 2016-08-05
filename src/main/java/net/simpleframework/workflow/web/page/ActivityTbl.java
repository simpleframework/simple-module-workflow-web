package net.simpleframework.workflow.web.page;

import static net.simpleframework.common.I18n.$m;

import java.util.Date;
import java.util.List;
import java.util.Map;

import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.ado.query.ListDataQuery;
import net.simpleframework.common.Convert;
import net.simpleframework.common.DateUtils;
import net.simpleframework.common.NumberUtils;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.AbstractElement;
import net.simpleframework.mvc.common.element.ButtonElement;
import net.simpleframework.mvc.common.element.ETextAlign;
import net.simpleframework.mvc.common.element.LinkElement;
import net.simpleframework.mvc.common.element.ProgressElement;
import net.simpleframework.mvc.common.element.SpanElement;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ui.menu.MenuBean;
import net.simpleframework.mvc.component.ui.menu.MenuItem;
import net.simpleframework.mvc.component.ui.menu.MenuItems;
import net.simpleframework.mvc.component.ui.pager.AbstractTablePagerSchema;
import net.simpleframework.mvc.component.ui.pager.TablePagerColumn;
import net.simpleframework.mvc.component.ui.pager.db.GroupDbTablePagerHandler;
import net.simpleframework.workflow.engine.EActivityStatus;
import net.simpleframework.workflow.engine.IWorkflowContextAware;
import net.simpleframework.workflow.engine.bean.ActivityBean;
import net.simpleframework.workflow.engine.bean.ProcessBean;
import net.simpleframework.workflow.schema.AbstractTaskNode;
import net.simpleframework.workflow.schema.UserNode;
import net.simpleframework.workflow.web.WorkflowUtils;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class ActivityTbl extends GroupDbTablePagerHandler implements IWorkflowContextAware {
	@Override
	public IDataQuery<?> createDataObjectQuery(final ComponentParameter cp) {
		final ProcessBean process = WorkflowUtils.getProcessBean(cp);
		if (process != null) {
			cp.addFormParameter("processId", process.getId());
			final List<ActivityBean> list = wfaService.getActivities(process);
			return new ListDataQuery<ActivityBean>(setRelativeDate(cp, list));
		}
		return null;
	}

	protected IDataQuery<?> createDataObjectQuery_bytask(final ComponentParameter cp) {
		final ProcessBean process = WorkflowUtils.getProcessBean(cp);
		if (process != null) {
			cp.addFormParameter("processId", process.getId());
		}
		final String taskid = cp.getParameter("taskid");
		if (StringUtils.hasText(taskid)) {
			cp.addFormParameter("taskid", taskid);
			final List<ActivityBean> list = wfaService.getActivities(process, taskid);
			return new ListDataQuery<ActivityBean>(setRelativeDate(cp, list));
		}
		return null;
	}

	protected List<ActivityBean> setRelativeDate(final ComponentParameter cp,
			final List<ActivityBean> list) {
		long max = 0;
		for (final ActivityBean activity : list) {
			Date completeDate = activity.getCompleteDate();
			if (completeDate == null) {
				completeDate = new Date();
			}
			final long l = wfaService.getWorkCalendarListener(activity).getRelativeMilliseconds(
					activity, activity.getCreateDate(), completeDate);
			max = Math.max(max, l);
			cp.setRequestAttr("l_" + activity.getId(), l);
		}
		cp.setRequestAttr("relative_date", max);
		return list;
	}

	@Override
	protected Map<String, Object> getRowData(final ComponentParameter cp, final Object dataObject) {
		final ActivityBean activity = (ActivityBean) dataObject;
		if (isNulltask_opt(cp)) {
			final AbstractTaskNode tasknode = wfaService.getTaskNode(activity);
			if (!(tasknode instanceof UserNode) || ((UserNode) tasknode).isEmpty()) {
				return null;
			}
		}

		final KVMap row = new KVMap();
		row.add(TablePagerColumn.ICON, WorkflowUtils.getStatusIcon(cp, activity.getStatus())).add(
				"tasknode", toTasknodeElement(cp, activity));

		final ActivityBean pre = wfaService.getPreActivity(activity);
		if (pre != null) {
			// final EActivityStatus pstatus = pre.getStatus();
			// row.add("previous", WorkflowUtils.toStatusHTML(cp, pstatus,
			// toTasknodeElement(cp, pre)));
			String pre_participants = WorkflowUtils.getParticipants(cp, pre, true);
			if (!StringUtils.hasText(pre_participants)) {
				// 由于直退，前一任务并没有参与者，取定义参与者
				pre_participants = WorkflowUtils.getParticipants(cp, pre, false);
			}
			row.add("pre_participants", pre_participants);
		}

		row.add("participants", WorkflowUtils.getParticipants(cp, activity, false)).add(
				"participants2", WorkflowUtils.getParticipants(cp, activity, true));

		row.add("createDate", activity.getCreateDate())
				.add("completeDate", activity.getCompleteDate())
				.add("relativeDate", toRelativeDateHTML(cp, activity))
				.add("timeoutDate", toTimeoutDateHTML(cp, activity));

		row.add(TablePagerColumn.OPE, toOpeHTML(cp, activity));
		return row;
	}

	protected AbstractElement<?> toTasknodeElement(final ComponentParameter cp,
			final ActivityBean activity) {
		final AbstractTaskNode tasknode = wfaService.getTaskNode(activity);
		if (tasknode instanceof UserNode) {
			if (((UserNode) tasknode).isEmpty()) {
				return new SpanElement(activity.getTasknodeText()).setStyle("color: #999;");
			} else {
				return createUsernodeElement(activity);
			}
		}
		return new SpanElement(activity.getTasknodeText()).setStyle("color: #808;");
	}

	protected String toRelativeDateHTML(final ComponentParameter cp, final ActivityBean activity) {
		final Long l = (Long) cp.getRequestAttr("relative_date");
		if (l != null) {
			Date completeDate = activity.getCompleteDate();
			if (completeDate == null) {
				completeDate = new Date();
			}

			final Long l0 = (Long) cp.getRequestAttr("l_" + activity.getId());
			if (l0 != null) {
				return new ProgressElement((double) l0 / l).setColor("#3bf").setLinearStartColor(null)
						.setText(DateUtils.toDifferenceDate(l0)).toString();
			}
		}
		return null;
	}

	protected String toTimeoutDateHTML(final ComponentParameter cp, final ActivityBean activity) {
		final Date timeoutDate = activity.getTimeoutDate();
		if (timeoutDate != null) {
			final Date completeDate = activity.getCompleteDate();
			final int d = Double.valueOf(
					(timeoutDate.getTime() - (completeDate != null ? completeDate.getTime() : System
							.currentTimeMillis())) / (1000 * 60)).intValue();
			return new SpanElement(NumberUtils.format(d / 60.0, "0.#")).setColor(
					d > 0 ? "green" : "red").toString();
		}
		return null;
	}

	protected String toOpeHTML(final ComponentParameter cp, final ActivityBean activity) {
		final StringBuilder sb = new StringBuilder();
		sb.append(createLogButton(cp, activity));
		sb.append(AbstractTablePagerSchema.IMG_DOWNMENU);
		return sb.toString();
	}

	protected LinkElement createUsernodeElement(final ActivityBean activity) {
		return new LinkElement(activity.getTasknodeText())
				.setOnclick("$Actions['ActivityMgrPage_workitems']('activityId=" + activity.getId()
						+ "');");
	}

	protected ButtonElement createLogButton(final ComponentParameter cp, final ActivityBean activity) {
		return WorkflowUtils.createLogButton().setOnclick(
				"$Actions['AbstractWorkflowMgrPage_update_log']('activityId=" + activity.getId()
						+ "');");
	}

	@Override
	public MenuItems getContextMenu(final ComponentParameter cp, final MenuBean menuBean,
			final MenuItem menuItem) {
		return MenuItems.of(MI_STATUS_RUNNING(), MenuItem.sep(), MI_STATUS_SUSPENDED(),
				MenuItem.sep(), MI_STATUS_DO_ABORT());
	}

	protected MenuItem MI_STATUS_RUNNING() {
		return MenuItem.of($m("AbstractWorkflowMgrPage.1")).setOnclick_act(
				"AbstractWorkflowMgrPage_status", "activityId", "op=running");
	}

	protected MenuItem MI_STATUS_SUSPENDED() {
		return MenuItem.of($m("AbstractWorkflowMgrPage.0")).setOnclick_act(
				"AbstractWorkflowMgrPage_status", "activityId", "op=suspended");
	}

	protected MenuItem MI_STATUS_DO_ABORT() {
		return MenuItem.of(EActivityStatus.abort.toString()).setOnclick_act("ActivityMgrPage_abort",
				"activityId");
	}

	public final static String COOKIE_HIDE_NULLTASK = "wf_monitor_hide_nulltask";

	public static boolean isNulltask_opt(final PageParameter pp) {
		String nulltask = pp.getParameter("nulltask");
		if (!StringUtils.hasText(nulltask)) {
			nulltask = pp.getCookie(COOKIE_HIDE_NULLTASK);
		}
		return Convert.toBool(nulltask, true);
	}

	public static TablePagerColumn TC_TASKNODE() {
		return new TablePagerColumn("tasknode", $m("ActivityTbl.0")).setFilterSort(false);
	}

	public static TablePagerColumn TC_PREVIOUS() {
		return new TablePagerColumn("previous", $m("ActivityTbl.1"), 115).setFilterSort(false);
	}

	public static TablePagerColumn TC_PRE_PARTICIPANTS() {
		return new TablePagerColumn("pre_participants", $m("ActivityTbl.2"), 100)
				.setTextAlign(ETextAlign.center).setNowrap(false).setFilterSort(false);
	}

	public static TablePagerColumn TC_PARTICIPANTS() {
		return new TablePagerColumn("participants", $m("ActivityTbl.3"), 150)
				.setTextAlign(ETextAlign.center).setNowrap(false).setFilterSort(false);
	}

	public static TablePagerColumn TC_PARTICIPANTS2() {
		return new TablePagerColumn("participants2", $m("ActivityTbl.4"), 150)
				.setTextAlign(ETextAlign.center).setNowrap(false).setFilterSort(false);
	}

	public static TablePagerColumn TC_TIMEOUT() {
		return new TablePagerColumn("timeoutDate", $m("ActivityTbl.5"), 100).setFilterSort(false);
	}

	public static TablePagerColumn TC_RELATIVEDATE() {
		return new TablePagerColumn("relativeDate", $m("ActivityTbl.6"), 70).setFilterSort(false);
	}
}
