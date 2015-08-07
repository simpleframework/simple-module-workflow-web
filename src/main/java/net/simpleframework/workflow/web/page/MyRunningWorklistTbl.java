package net.simpleframework.workflow.web.page;

import static net.simpleframework.common.I18n.$m;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import net.simpleframework.ado.FilterItem;
import net.simpleframework.ado.db.DbDataQuery;
import net.simpleframework.ado.db.common.ExpressionValue;
import net.simpleframework.ado.db.common.SQLValue;
import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.common.Convert;
import net.simpleframework.common.DateUtils;
import net.simpleframework.common.ID;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.common.object.NamedObject;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.AbstractElement;
import net.simpleframework.mvc.common.element.ButtonElement;
import net.simpleframework.mvc.common.element.ImageElement;
import net.simpleframework.mvc.common.element.JS;
import net.simpleframework.mvc.common.element.LabelElement;
import net.simpleframework.mvc.common.element.LinkElement;
import net.simpleframework.mvc.common.element.PhotoImage;
import net.simpleframework.mvc.common.element.SpanElement;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ui.menu.MenuBean;
import net.simpleframework.mvc.component.ui.menu.MenuItem;
import net.simpleframework.mvc.component.ui.menu.MenuItems;
import net.simpleframework.mvc.component.ui.pager.AbstractTablePagerSchema;
import net.simpleframework.mvc.component.ui.pager.GroupWrapper;
import net.simpleframework.mvc.component.ui.pager.TablePagerColumn;
import net.simpleframework.mvc.component.ui.pager.db.GroupDbTablePagerHandler;
import net.simpleframework.workflow.engine.EActivityStatus;
import net.simpleframework.workflow.engine.EDelegationStatus;
import net.simpleframework.workflow.engine.EWorkitemStatus;
import net.simpleframework.workflow.engine.bean.ActivityBean;
import net.simpleframework.workflow.engine.bean.DelegationBean;
import net.simpleframework.workflow.engine.bean.ProcessBean;
import net.simpleframework.workflow.engine.bean.ProcessModelBean;
import net.simpleframework.workflow.engine.bean.WorkitemBean;
import net.simpleframework.workflow.engine.comment.WfCommentUser;
import net.simpleframework.workflow.schema.AbstractTaskNode;
import net.simpleframework.workflow.web.WorkflowUtils;
import net.simpleframework.workflow.web.page.t1.WorkflowFormPage;
import net.simpleframework.workflow.web.page.t1.WorkflowMonitorPage;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class MyRunningWorklistTbl extends GroupDbTablePagerHandler implements IWorkflowPageAware {

	@Override
	public IDataQuery<?> createDataObjectQuery(final ComponentParameter cp) {
		final ID userId = cp.getLoginId();
		final String v = cp.getParameter("v");
		cp.addFormParameter("v", v);
		if ("unread".equals(v)) {
			return wfwService.getUnreadWorklist(userId);
		}
		return wfwService.getRunningWorklist(userId);
	}

	@Override
	protected ExpressionValue createFilterExpressionValue(final DbDataQuery<?> qs,
			final TablePagerColumn oCol, final Iterator<FilterItem> it) {
		final String col = oCol.getColumnName();
		if ("title".equals(col) || "pno".equals(col)) {
			final TablePagerColumn oCol2 = (TablePagerColumn) oCol.clone();
			oCol2.setColumnAlias("p." + col);
			final ExpressionValue ev = super.createFilterExpressionValue(qs, oCol2, it);
			final SQLValue sv = qs.getSqlValue();
			final StringBuilder sb = new StringBuilder();
			sb.append("select t.* from (").append(sv.getSql()).append(") t left join ")
					.append(wfpService.getTablename(ProcessBean.class))
					.append(" p on t.processid=p.id where " + ev.getExpression());
			sv.setSql(sb.toString());
			sv.addValues(ev.getValues());
			return null;
		}
		return super.createFilterExpressionValue(qs, oCol, it);
	}

	@Override
	public Object getGroupValue(final ComponentParameter cp, final Object bean,
			final String groupColumn) {
		final boolean bModelname = "modelname".equals(groupColumn);
		if (bModelname || "taskname".equals(groupColumn)) {
			final ActivityBean activity = wfwService.getActivity(getWorkitem(bean));
			final ProcessModelBean processModel = wfpService.getProcessModel(wfaService
					.getProcessBean(activity));
			if (bModelname) {
				return processModel;
			} else {
				return new TaskWrapper(wfaService.getTaskNode(activity), processModel);
			}
		}
		return groupColumn;
	}

	class TaskWrapper extends NamedObject<TaskWrapper> {
		ProcessModelBean processModel;

		TaskWrapper(final AbstractTaskNode task, final ProcessModelBean processModel) {
			setName(task.toString());
			this.processModel = processModel;
		}

		@Override
		public String toString() {
			return getName();
		}
	}

	@Override
	public GroupWrapper getGroupWrapper(final ComponentParameter cp, final Object groupVal) {
		if (groupVal instanceof TaskWrapper) {
			return new GroupWrapper() {
				@Override
				public String toString() {
					final TaskWrapper wrapper = (TaskWrapper) groupVal;
					final StringBuilder sb = new StringBuilder();
					sb.append(new LabelElement(wrapper));
					sb.append(new SpanElement("(" + wrapper.processModel + ")")
							.setClassName("worklist_group_val"));
					sb.append(toCountHTML());
					return sb.toString();
				}
			};
		}
		return super.getGroupWrapper(cp, groupVal);
	}

	protected ImageElement MARK_RETAKE(final ComponentParameter cp) {
		return AbstractItemsTPage._createImageMark(cp, "status_retake.png").setTitle(
				$m("MyRunningWorklistTbl.13"));
	}

	protected ImageElement MARK_DELEGATE(final ComponentParameter cp, final WorkitemBean workitem) {
		return PhotoImage.icon12(cp.getPhotoUrl(workitem.getUserId())).setTitle(
				$m("MyRunningWorklistTbl.0", cp.getUser(workitem.getUserId())));
	}

	protected AbstractElement<?> createImageMark(final ComponentParameter cp,
			final WorkitemBean workitem) {
		AbstractElement<?> img = null;
		final EWorkitemStatus status = workitem.getStatus();
		ActivityBean fallback;
		if ((fallback = wfaService.getBean(workitem.getFallbackId())) != null) {
			img = AbstractItemsTPage._createImageMark(cp, "status_fallback.png").setTitle(
					$m("MyRunningWorklistTbl.23", wfaService.getTaskNode(fallback)));
		} else if (workitem.getRetakeRef() != null) {
			img = MARK_RETAKE(cp);
		} else if (status == EWorkitemStatus.delegate) {
			img = MARK_DELEGATE(cp, workitem);
		} else if (workitem.isTopMark()) {
			img = AbstractItemsTPage.MARK_TOP(cp);
		} else if (!workitem.isReadMark()) {
			img = AbstractItemsTPage.MARK_UNREAD(cp);
		}
		return img;
	}

	protected WorkitemBean getWorkitem(final Object dataObject) {
		return (WorkitemBean) dataObject;
	}

	@Override
	protected Map<String, Object> getRowData(final ComponentParameter cp, final Object dataObject) {
		final WorkitemBean workitem = (WorkitemBean) dataObject;
		final KVMap row = new KVMap();

		final AbstractElement<?> img = createImageMark(cp, workitem);
		if (img != null) {
			row.add(TablePagerColumn.ICON, img);
		}

		final EWorkitemStatus status = workitem.getStatus();
		final DelegationBean delegation = (status == EWorkitemStatus.delegate) ? wfdService
				.queryRunningDelegation(workitem) : null;
		final boolean receiving = delegation != null
				&& delegation.getStatus() == EDelegationStatus.receiving;

		row.add("title", toTitleHTML(cp, workitem, receiving));

		final ProcessBean process = WorkflowUtils.getProcessBean(cp, workitem);
		final String pno = process.getPno();
		row.add("pno", new SpanElement(pno).setTitle(pno)).add("pstat", toPstatHTML(cp, workitem))
				.put(TablePagerColumn.OPE, toOpeHTML(cp, workitem, receiving));

		doRowData(cp, row, workitem);
		return row;
	}

	protected void appendTaskname(final StringBuilder sb, final ComponentParameter cp,
			final ActivityBean activity) {
		if (!"taskname".equals(cp.getParameter(G))) {
			sb.append("[").append(SpanElement.color333(activity)).append("] ");
		}
	}

	protected String toTitleHTML(final ComponentParameter cp, final WorkitemBean workitem,
			final boolean receiving) {
		final StringBuilder sb = new StringBuilder();
		final ActivityBean activity = WorkflowUtils.getActivityBean(cp, workitem);

		appendTaskname(sb, cp, activity);

		final Date timeoutDate = activity.getTimeoutDate();
		if (timeoutDate != null && !wfaService.isFinalStatus(activity)) {
			if (activity.getStatus() == EActivityStatus.timeout) {
				int d = 0;
				if (activity.getCompleteDate() == null) {
					d = Double.valueOf(
							(System.currentTimeMillis() - timeoutDate.getTime()) / (1000 * 60 * 60 * 24))
							.intValue();
				}
				sb.append(new SpanElement(d > 0 ? $m("MyRunningWorklistTbl.20", d)
						: $m("MyRunningWorklistTbl.21")).setClassName("worklist_timeout"));
			} else {
				final int m = Double.valueOf(
						(timeoutDate.getTime() - System.currentTimeMillis()) / (1000 * 60)).intValue();
				if (m < wfSettings.getHoursToTimeoutWarning() * 60) {
					final int h = m / 60;
					sb.append(new SpanElement(h > 0 ? $m("MyRunningWorklistTbl.19", m)
							: $m("MyRunningWorklistTbl.18")).setClassName("worklist_timeout2"));
				}
			}
		}

		AbstractElement<?> tEle;
		final ProcessBean process = WorkflowUtils.getProcessBean(cp, workitem);
		if (receiving) {
			tEle = new SpanElement(WorkflowUtils.getProcessTitle(process));
		} else {
			tEle = new LinkElement(WorkflowUtils.getProcessTitle(process)).setStrong(
					!workitem.isReadMark()).setOnclick(
					JS.loc(uFactory.getUrl(cp, WorkflowFormPage.class, workitem)));
		}
		sb.append(tEle.setColor_gray(!StringUtils.hasText(process.getTitle())));
		return sb.toString();
	}

	protected String toPstatHTML(final ComponentParameter cp, final WorkitemBean workitem) {
		final StringBuilder sb = new StringBuilder();
		final ProcessBean process = WorkflowUtils.getProcessBean(cp, workitem);
		SpanElement commentsEle;
		final WfCommentUser commentUser = wfcuService.getCommentUser(workitem.getUserId(),
				workitem.getProcessId());
		final int ncomments;
		if (commentUser != null && (ncomments = commentUser.getNcomments()) > 0) {
			commentsEle = SpanElement.colora00(ncomments).setStrong(true);
		} else {
			commentsEle = new SpanElement(process.getComments());
		}
		sb.append(commentsEle.setItalic(true)).append("/")
				.append(new SpanElement(process.getViews()).setItalic(true));
		return sb.toString();
	}

	protected String toOpeHTML(final ComponentParameter cp, final WorkitemBean workitem,
			final boolean receiving) {
		final StringBuilder sb = new StringBuilder();
		if (receiving) {
			sb.append(new ButtonElement(EDelegationStatus.receiving).setHighlight(true).setOnclick(
					"$Actions['MyWorklistTPage_delegate_receiving']('workitemId=" + workitem.getId()
							+ "');"));
		} else {
			sb.append(new ButtonElement($m("MyRunningWorklistTbl.3")).setOnclick(JS.loc(uFactory
					.getUrl(cp, WorkflowMonitorPage.class, workitem))));
		}
		sb.append(AbstractTablePagerSchema.IMG_DOWNMENU);
		return sb.toString();
	}

	protected void doRowData(final ComponentParameter cp, final KVMap row,
			final WorkitemBean workitem) {
		final ActivityBean activity = WorkflowUtils.getActivityBean(cp, workitem);
		row.add("userFrom", SpanElement.color060(WorkflowUtils.getUserFrom(activity, "<br>")));
		final Date createDate = workitem.getCreateDate();
		final Date d = DateUtils.getZeroPoint().getTime();
		final String dtxt = createDate.after(d) ? Convert.toDateString(createDate, "HH:mm") : Convert
				.toDateString(createDate, "yy-MM-dd");
		row.add("createDate", new SpanElement(dtxt).setTitle(Convert.toDateString(createDate)));
	}

	@Override
	public MenuItems getContextMenu(final ComponentParameter cp, final MenuBean menuBean,
			final MenuItem menuItem) {
		final MenuItems items = MenuItems.of();
		items.append(MENU_MONITOR(cp));
		items.append(MenuItem.sep());
		items.append(MenuItem.of($m("MyRunningWorklistTbl.4")).setIconClass("menu_fallback")
				.setOnclick_act("MyWorklistTPage_fallback", "workitemId"));
		items.append(MenuItem.of($m("MyRunningWorklistTbl.5")).setIconClass("menu_delegate")
				.setOnclick_act("MyWorklistTPage_delegate", "workitemId"));
		items.append(MenuItem.sep());
		final MenuItem mItems = MenuItem.of($m("MyRunningWorklistTbl.6"));
		mItems.addChild(
				MENU_MARK_READ().setOnclick_act("MyWorklistTPage_readMark", "workitemId", "op=read"))
				.addChild(
						MENU_MARK_UNREAD().setOnclick_act("MyWorklistTPage_readMark", "workitemId",
								"op=unread"));
		mItems.addChild(MenuItem.sep());
		mItems.addChild(
				MENU_MARK_TOP().setOnclick_act("MyWorklistTPage_topMark", "workitemId", "op=top"))
				.addChild(
						MENU_MARK_UNTOP().setOnclick_act("MyWorklistTPage_topMark", "workitemId",
								"op=untop"));
		items.append(mItems);
		items.append(MenuItem.sep());
		items.append(MenuItem.of($m("MyRunningWorklistTbl.16")).setOnclick_act(
				"MyWorklistTPage_delete", "workitemId"));
		items.append(MenuItem.sep()).append(MENU_LOG());
		return items;
	}

	static MenuItem MENU_MONITOR(final PageParameter pp) {
		return MenuItem.of($m("MyRunningWorklistTbl.7")).setOnclick(
				"$Actions.loc('" + uFactory.getUrl(pp, WorkflowMonitorPage.class)
						+ "?workitemId=' + $pager_action(item).rowId());");
	}

	static MenuItem MENU_LOG() {
		return MenuItem.of($m("Button.Log")).setOnclick_act("AbstractItemsTPage_update_log",
				"workitemId");
	}

	static MenuItem MENU_MARK_READ() {
		return MenuItem
				.of($m("MyRunningWorklistTbl.8"))
				.setOnclick(
						"$Actions['MyWorklistTPage_tbl'].doAct('MyWorklistTPage_readMark', 'workitemId', 'op=read');");
	}

	static MenuItem MENU_MARK_UNREAD() {
		return MenuItem
				.of($m("MyRunningWorklistTbl.9"))
				.setIconClass("menu_unread")
				.setOnclick(
						"$Actions['MyWorklistTPage_tbl'].doAct('MyWorklistTPage_readMark', 'workitemId', 'op=unread');");
	}

	static MenuItem MENU_MARK_ALLREAD() {
		return MenuItem.of($m("MyRunningWorklistTbl.10")).setOnclick(
				"$Actions['MyWorklistTPage_readMark']('op=allread');");
	}

	static MenuItem MENU_MARK_TOP() {
		return MenuItem
				.of($m("MyRunningWorklistTbl.11"))
				.setIconClass("menu_top")
				.setOnclick(
						"$Actions['MyWorklistTPage_tbl'].doAct('MyWorklistTPage_topMark', 'workitemId', 'op=top');");
	}

	static MenuItem MENU_MARK_UNTOP() {
		return MenuItem
				.of($m("MyRunningWorklistTbl.12"))
				.setOnclick(
						"$Actions['MyWorklistTPage_tbl'].doAct('MyWorklistTPage_topMark', 'workitemId', 'op=untop');");
	}

	static MenuItem MENU_VIEW_ALL() {
		return MenuItem.of($m("MyRunningWorklistTbl.15"));
	}

	static MenuItem MENU_VIEW_DELEGATION() {
		return MenuItem.of($m("MyRunningWorklistTbl.22"));
	}

	static MenuItem MENU_VIEW_GROUP0() {
		return MenuItem.of($m("AbstractTemplatePage.0"));
	}

	static MenuItem MENU_VIEW_GROUP1() {
		return MenuItem.of($m("AbstractItemsTPage.7"));
	}

	static MenuItem MENU_VIEW_GROUP2() {
		return MenuItem.of($m("AbstractItemsTPage.8"));
	}
}
