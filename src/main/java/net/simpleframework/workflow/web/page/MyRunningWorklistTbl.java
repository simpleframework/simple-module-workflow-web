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
import net.simpleframework.common.DateUtils.NumberConvert;
import net.simpleframework.common.ID;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.common.object.NamedObject;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.AbstractElement;
import net.simpleframework.mvc.common.element.ButtonElement;
import net.simpleframework.mvc.common.element.EVerticalAlign;
import net.simpleframework.mvc.common.element.ImageElement;
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
import net.simpleframework.workflow.engine.ActivityBean;
import net.simpleframework.workflow.engine.DelegationBean;
import net.simpleframework.workflow.engine.EActivityStatus;
import net.simpleframework.workflow.engine.EDelegationStatus;
import net.simpleframework.workflow.engine.EWorkitemStatus;
import net.simpleframework.workflow.engine.IWorkflowServiceAware;
import net.simpleframework.workflow.engine.ProcessBean;
import net.simpleframework.workflow.engine.ProcessModelBean;
import net.simpleframework.workflow.engine.WorkitemBean;
import net.simpleframework.workflow.schema.AbstractTaskNode;
import net.simpleframework.workflow.web.IWorkflowWebContext;
import net.simpleframework.workflow.web.WorkflowUrlsFactory;
import net.simpleframework.workflow.web.WorkflowUtils;
import net.simpleframework.workflow.web.page.t1.WorkflowFormPage;
import net.simpleframework.workflow.web.page.t1.WorkflowMonitorPage;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class MyRunningWorklistTbl extends GroupDbTablePagerHandler implements IWorkflowServiceAware {

	@Override
	public IDataQuery<?> createDataObjectQuery(final ComponentParameter cp) {
		final ID userId = cp.getLoginId();
		final String v = cp.getParameter("v");
		if (StringUtils.hasText(v)) {
			cp.addFormParameter("v", v);
		}
		if ("unread".equals(v)) {
			return wService.getUnreadWorklist(userId);
		}
		return wService.getRunningWorklist(userId);
	}

	@Override
	protected ExpressionValue createFilterExpressionValue(final DbDataQuery<?> qs,
			final TablePagerColumn oCol, final Iterator<FilterItem> it) {
		if ("title".equals(oCol.getColumnName())) {
			final TablePagerColumn oCol2 = (TablePagerColumn) oCol.clone();
			oCol2.setColumnAlias("p.title");
			final ExpressionValue ev = super.createFilterExpressionValue(qs, oCol2, it);
			final SQLValue sv = qs.getSqlValue();
			final StringBuilder sb = new StringBuilder();
			sb.append("select * from (").append(sv.getSql()).append(") t left join ")
					.append(pService.getTablename(ProcessBean.class))
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
			final ActivityBean activity = wService.getActivity(getWorkitem(bean));
			final ProcessModelBean processModel = pService.getProcessModel(aService
					.getProcessBean(activity));
			if (bModelname) {
				return processModel;
			} else {
				return new TaskWrapper(aService.getTaskNode(activity), processModel);
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

	protected ImageElement _createImageMark(final ComponentParameter cp, final String img) {
		return new ImageElement(cp.getCssResourceHomePath(MyRunningWorklistTPage.class) + "/images/"
				+ img).setVerticalAlign(EVerticalAlign.middle);
	}

	protected ImageElement MARK_RETAKE(final ComponentParameter cp) {
		return _createImageMark(cp, "status_retake.png").setTitle($m("MyRunningWorklistTbl.13"));
	}

	protected ImageElement MARK_DELEGATE(final ComponentParameter cp, final WorkitemBean workitem) {
		return PhotoImage.icon12(cp.getPhotoUrl(workitem.getUserId())).setTitle(
				$m("MyRunningWorklistTbl.0", permission.getUser(workitem.getUserId())));
	}

	protected ImageElement MARK_TOP(final ComponentParameter cp) {
		return _createImageMark(cp, "mark_top.png").setTitle($m("MyRunningWorklistTbl.1"));
	}

	protected ImageElement MARK_UNREAD(final ComponentParameter cp) {
		return _createImageMark(cp, "mark_unread.png").setTitle($m("MyRunningWorklistTbl.2"));
	}

	protected AbstractElement<?> createImageMark(final ComponentParameter cp,
			final WorkitemBean workitem) {
		AbstractElement<?> img = null;
		final EWorkitemStatus status = workitem.getStatus();
		if (workitem.getRetakeRef() != null) {
			img = MARK_RETAKE(cp);
		} else if (status == EWorkitemStatus.delegate) {
			img = MARK_DELEGATE(cp, workitem);
		} else if (workitem.isTopMark()) {
			img = MARK_TOP(cp);
		} else if (!workitem.isReadMark()) {
			img = MARK_UNREAD(cp);
		}
		return img;
	}

	protected WorkitemBean getWorkitem(final Object dataObject) {
		return (WorkitemBean) dataObject;
	}

	protected void appendTaskname(final StringBuilder sb, final ComponentParameter cp,
			final ActivityBean activity) {
		if (!"taskname".equals(cp.getParameter(G))) {
			sb.append("[").append(new SpanElement(activity).setClassName("tasknode_txt")).append("] ");
		}
	}

	private final WorkflowUrlsFactory uFactory = ((IWorkflowWebContext) workflowContext)
			.getUrlsFactory();

	@Override
	protected Map<String, Object> getRowData(final ComponentParameter cp, final Object dataObject) {
		final WorkitemBean workitem = (WorkitemBean) dataObject;
		final KVMap row = new KVMap();

		final ActivityBean activity = wService.getActivity(workitem);

		final AbstractElement<?> img = createImageMark(cp, workitem);
		if (img != null) {
			row.add(TablePagerColumn.ICON, img);
		}

		final StringBuilder title = new StringBuilder();
		appendTaskname(title, cp, activity);

		final Date timeoutDate = activity.getTimeoutDate();
		if (timeoutDate != null && !aService.isFinalStatus(activity)) {
			if (activity.getStatus() == EActivityStatus.timeout) {
				int d = 0;
				if (activity.getCompleteDate() == null) {
					d = Double.valueOf(
							(System.currentTimeMillis() - timeoutDate.getTime()) / (1000 * 60 * 60 * 24))
							.intValue();
				}
				title.append(new SpanElement(d > 0 ? $m("MyRunningWorklistTbl.20", d)
						: $m("MyRunningWorklistTbl.21")).setClassName("worklist_timeout"));
			} else {
				final int m = Double.valueOf(
						(timeoutDate.getTime() - System.currentTimeMillis()) / (1000 * 60)).intValue();
				if (m < wfSettings.getHoursToTimeoutWarning() * 60) {
					final int h = m / 60;
					title.append(new SpanElement(h > 0 ? $m("MyRunningWorklistTbl.19", m)
							: $m("MyRunningWorklistTbl.18")).setClassName("worklist_timeout2"));
				}
			}
		}

		final EWorkitemStatus status = workitem.getStatus();
		final DelegationBean delegation = (status == EWorkitemStatus.delegate) ? dService
				.queryRunningDelegation(workitem) : null;
		final boolean receiving = delegation != null
				&& delegation.getStatus() == EDelegationStatus.receiving;
		AbstractElement<?> tEle;
		final ProcessBean processBean = aService.getProcessBean(activity);
		if (receiving) {
			tEle = new SpanElement(WorkflowUtils.getProcessTitle(processBean));
		} else {
			tEle = new LinkElement(WorkflowUtils.getProcessTitle(processBean)).setStrong(
					!workitem.isReadMark()).setOnclick(
					"$Actions.loc('" + uFactory.getUrl(cp, WorkflowFormPage.class, workitem) + "');");
		}
		title.append(tEle.setColor_gray(!StringUtils.hasText(processBean.getTitle())));

		row.add("title", title.toString()).add("userFrom", WorkflowUtils.getUserFrom(activity))
				.add("userTo", WorkflowUtils.getUserTo(activity));
		final Date createDate = workitem.getCreateDate();
		row.add("createDate",
				new SpanElement(DateUtils.getRelativeDate(createDate, DATE_NUMBERCONVERT))
						.setTitle(Convert.toDateString(createDate)));
		row.add("completeDate", workitem.getCompleteDate()).add("status",
				WorkflowUtils.toStatusHTML(cp, status));

		final StringBuilder ope = new StringBuilder();
		if (receiving) {
			ope.append(new ButtonElement(EDelegationStatus.receiving).setHighlight(true).setOnclick(
					"$Actions['MyWorklistTPage_delegate_receiving']('workitemId=" + workitem.getId()
							+ "');"));
		} else {
			ope.append(new ButtonElement($m("MyRunningWorklistTbl.3")).setOnclick("$Actions.loc('"
					+ uFactory.getUrl(cp, WorkflowMonitorPage.class, workitem) + "');"));
		}
		ope.append(SpanElement.SPACE).append(AbstractTablePagerSchema.IMG_DOWNMENU);
		row.put(TablePagerColumn.OPE, ope.toString());
		return row;
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
				"$Actions.loc('"
						+ ((IWorkflowWebContext) workflowContext).getUrlsFactory().getUrl(pp,
								WorkflowMonitorPage.class)
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

	static NumberConvert DATE_NUMBERCONVERT = new NumberConvert() {
		@Override
		public Object convert(final Number n) {
			return SpanElement.num(n).addStyle("margin-right: 2px;");
		}
	};
}
