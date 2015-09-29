package net.simpleframework.workflow.web.page.t1;

import static net.simpleframework.common.I18n.$m;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.ado.query.ListDataQuery;
import net.simpleframework.common.Convert;
import net.simpleframework.common.DateUtils;
import net.simpleframework.common.ID;
import net.simpleframework.common.NumberUtils;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.ButtonElement;
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
			List<ActivityBean> list = wfaService.getActivities(process);
			if (isTreeview_opt(cp)) {
				list = toTreeList(list);
			}
			setRelativeDate(cp, list);
			return new ListDataQuery<ActivityBean>(list);
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
			List<ActivityBean> list = wfaService.getActivities(process, taskid);
			if (isTreeview_opt(cp)) {
				list = toTreeList(list);
			}
			setRelativeDate(cp, list);
			return new ListDataQuery<ActivityBean>(list);
		}
		return null;
	}

	final static String COOKIE_TREEVIEW = "wf_monitor_treeview";
	final static String COOKIE_HIDE_NULLTASK = "wf_monitor_hide_nulltask";

	protected boolean isTreeview_opt(final PageParameter pp) {
		String treeview = pp.getParameter("treeview");
		if (!StringUtils.hasText(treeview)) {
			treeview = pp.getCookie(COOKIE_TREEVIEW);
		}
		return Convert.toBool(treeview);
	}

	protected boolean isNulltask_opt(final PageParameter pp) {
		String nulltask = pp.getParameter("nulltask");
		if (!StringUtils.hasText(nulltask)) {
			nulltask = pp.getCookie(COOKIE_HIDE_NULLTASK);
		}
		return Convert.toBool(nulltask, true);
	}

	protected List<ActivityBean> toTreeList(final List<ActivityBean> list) {
		ActivityBean root = null;
		final Map<ID, List<ActivityBean>> cache = new LinkedHashMap<ID, List<ActivityBean>>();
		for (final ActivityBean activity : list) {
			final ID preId = activity.getPreviousId();
			if (preId == null) {
				root = activity;
				root.setAttr("_margin", 0);
			} else {
				ActivityBean pre = null;
				for (final ActivityBean _pre : list) {
					if (preId.equals(_pre.getId())) {
						pre = _pre;
						break;
					}
				}
				List<ActivityBean> _l = cache.get(preId);
				if (_l == null) {
					cache.put(preId, _l = new ArrayList<ActivityBean>());
				}
				_l.add(activity);
				activity.setAttr("_margin",
						(pre != null ? Convert.toInt(pre.getAttr("_margin")) : 0) + 1);
			}
		}
		final List<ActivityBean> l = new ArrayList<ActivityBean>();
		if (root != null) {
			l.add(root);
		}
		for (final List<ActivityBean> _l : cache.values()) {
			l.addAll(_l);
		}
		return l;
	}

	protected void setRelativeDate(final ComponentParameter cp, final List<ActivityBean> list) {
		long max = 0;
		for (final ActivityBean activity : list) {
			Date completeDate = activity.getCompleteDate();
			if (completeDate == null) {
				completeDate = new Date();
			}
			max = Math.max(max, completeDate.getTime() - activity.getCreateDate().getTime());
		}
		cp.setRequestAttr("relative_date", max);
	}

	protected Object toTasknode(final ActivityBean activity) {
		final AbstractTaskNode tasknode = wfaService.getTaskNode(activity);
		if (tasknode instanceof UserNode) {
			if (((UserNode) tasknode).isEmpty()) {
				return new SpanElement(activity).setStyle("color: #999;");
			} else {
				return createUsernodeElement(activity);
			}
		}
		return new SpanElement(activity).setStyle("color: #808;");
	}

	protected LinkElement createUsernodeElement(final ActivityBean activity) {
		return new LinkElement(activity)
				.setOnclick("$Actions['ActivityMgrPage_workitems']('activityId=" + activity.getId()
						+ "');");
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

		final StringBuilder tn = new StringBuilder();
		if (isTreeview_opt(cp)) {
			String space = "";
			for (int i = 0; i < Convert.toInt(activity.getAttr("_margin")); i++) {
				space += i == 0 ? "| -- " : " -- ";
			}
			tn.append(space);
		}
		tn.append(toTasknode(activity));
		row.add("tasknode", tn.toString());

		final ActivityBean pre = wfaService.getPreActivity(activity);
		if (pre != null) {
			final EActivityStatus pstatus = pre.getStatus();
			row.add("previous", WorkflowUtils.toStatusHTML(cp, pstatus, toTasknode(pre)));
		}

		row.add("participants", WorkflowUtils.getParticipants(cp, activity, false)).add(
				"participants2", WorkflowUtils.getParticipants(cp, activity, true));

		final Date createDate = activity.getCreateDate();
		row.add("createDate", createDate);
		Date completeDate = activity.getCompleteDate();
		row.add("completeDate", completeDate);
		final Long l = (Long) cp.getRequestAttr("relative_date");
		if (l != null) {
			if (completeDate == null) {
				completeDate = new Date();
			}
			final long l0 = (completeDate.getTime() - createDate.getTime());
			row.add("relativeDate",
					new ProgressElement((double) l0 / l).setText(DateUtils.toDifferenceDate(l0)));
		}
		Date timeoutDate;
		if ((timeoutDate = activity.getTimeoutDate()) != null) {
			final int d = Double.valueOf(
					(timeoutDate.getTime() - (completeDate != null ? completeDate.getTime() : System
							.currentTimeMillis())) / (1000 * 60)).intValue();
			row.add("timeoutDate", new SpanElement(NumberUtils.format(d / 60.0, "0.#"))
					.setColor(d > 0 ? "green" : "red"));
		}

		row.add("status", WorkflowUtils.toStatusHTML(cp, activity.getStatus())).add(
				TablePagerColumn.OPE, toOpeHTML(cp, activity));
		return row;
	}

	protected ButtonElement createLogButton(final ComponentParameter cp, final ActivityBean activity) {
		return WorkflowUtils.createLogButton().setOnclick(
				"$Actions['AbstractWorkflowMgrPage_update_log']('activityId=" + activity.getId()
						+ "');");
	}

	protected String toOpeHTML(final ComponentParameter cp, final ActivityBean activity) {
		final StringBuilder sb = new StringBuilder();
		sb.append(createLogButton(cp, activity));
		sb.append(AbstractTablePagerSchema.IMG_DOWNMENU);
		return sb.toString();
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

	@Override
	public MenuItems getContextMenu(final ComponentParameter cp, final MenuBean menuBean,
			final MenuItem menuItem) {
		return MenuItems.of(MI_STATUS_RUNNING(), MenuItem.sep(), MI_STATUS_SUSPENDED(),
				MenuItem.sep(), MI_STATUS_DO_ABORT());
	}
}
