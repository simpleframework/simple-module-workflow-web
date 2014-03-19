package net.simpleframework.workflow.web.page;

import static net.simpleframework.common.I18n.$m;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.ado.query.ListDataQuery;
import net.simpleframework.common.Convert;
import net.simpleframework.common.DateUtils;
import net.simpleframework.common.DateUtils.NumberConvert;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.mvc.PageParameter;
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
import net.simpleframework.workflow.engine.EDelegationStatus;
import net.simpleframework.workflow.engine.EWorkitemStatus;
import net.simpleframework.workflow.engine.IWorkflowContextAware;
import net.simpleframework.workflow.engine.ProcessModelBean;
import net.simpleframework.workflow.engine.WorkitemBean;
import net.simpleframework.workflow.schema.AbstractTaskNode;
import net.simpleframework.workflow.web.IWorkflowWebContext;
import net.simpleframework.workflow.web.WorkflowUrlsFactory;
import net.simpleframework.workflow.web.page.t1.WorkflowFormPage;
import net.simpleframework.workflow.web.page.t1.WorkflowMonitorPage;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class MyRunningWorklistTbl extends GroupDbTablePagerHandler implements IWorkflowContextAware {

	@Override
	public IDataQuery<?> createDataObjectQuery(final ComponentParameter cp) {
		return new ListDataQuery<WorkitemBean>(wService.getRunningWorklist(cp.getLoginId()));
	}

	@Override
	public Object getGroupValue(final ComponentParameter cp, final Object bean,
			final String groupColumn) {
		final boolean bModelname = "modelname".equals(groupColumn);
		if (bModelname || "taskname".equals(groupColumn)) {
			final ActivityBean activity = wService.getActivity((WorkitemBean) bean);
			final ProcessModelBean processModel = pService.getProcessModel(aService
					.getProcessBean(activity));
			if (bModelname) {
				return processModel;
			} else {
				return aService.getTaskNode(activity).setAttr("_processModel", processModel);
			}
		}
		return groupColumn;
	}

	@Override
	public GroupWrapper getGroupWrapper(final ComponentParameter cp, final Object groupVal) {
		if (groupVal instanceof AbstractTaskNode) {
			return new GroupWrapper() {
				@Override
				public String toString() {
					final AbstractTaskNode taskNode = (AbstractTaskNode) groupVal;
					final StringBuilder sb = new StringBuilder();
					sb.append(new LabelElement(taskNode));
					sb.append(new SpanElement("(" + taskNode.getAttr("_processModel") + ")")
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

	protected ImageElement createImageMark(final ComponentParameter cp, final WorkitemBean workitem) {
		final EWorkitemStatus status = workitem.getStatus();
		ImageElement img = null;
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

	@Override
	protected Map<String, Object> getRowData(final ComponentParameter cp, final Object dataObject) {
		final WorkitemBean workitem = (WorkitemBean) dataObject;
		final KVMap row = new KVMap();

		final ActivityBean activity = wService.getActivity(workitem);

		final ImageElement img = createImageMark(cp, workitem);
		if (img != null) {
			row.add(TablePagerColumn.ICON, img);
		}

		final WorkflowUrlsFactory uFactory = ((IWorkflowWebContext) context).getUrlsFactory();

		final StringBuilder title = new StringBuilder();
		if (!"taskname".equals(cp.getParameter(G))) {
			title.append("[").append(new SpanElement(activity).setClassName("tasknode_txt"))
					.append("] ");
		}

		final EWorkitemStatus status = workitem.getStatus();
		DelegationBean delegation = null;
		if (status == EWorkitemStatus.delegate) {
			delegation = dService.queryRunningDelegation(workitem);
		}
		final boolean receiving = delegation != null
				&& delegation.getStatus() == EDelegationStatus.receiving;
		if (receiving) {
			title.append(new SpanElement(WorkflowUtils.getTitle(aService.getProcessBean(activity))));
		} else {
			title.append(new LinkElement(WorkflowUtils.getTitle(aService.getProcessBean(activity)))
					.setStrong(!workitem.isReadMark()).setOnclick(
							"$Actions.loc('" + uFactory.getUrl(cp, WorkflowFormPage.class, workitem)
									+ "');"));
		}
		row.add("title", title.toString()).add("userFrom", getUserFrom(activity))
				.add("userTo", getUserTo(activity));
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
		items.append(MenuItem.itemDelete().setOnclick_act("MyWorklistTPage_delete", "workitemId"));
		items.append(MenuItem.sep()).append(MENU_LOG());
		return items;
	}

	final Map<String, String> userCache = new ConcurrentHashMap<String, String>();

	protected String getUserTo(final ActivityBean activity) {
		if (activity == null) {
			return null;
		}
		final String key = "to_" + activity.getId();
		String userTo = userCache.get(key);
		if (userTo == null) {
			final Set<String> list = new LinkedHashSet<String>();
			for (final ActivityBean nextActivity : aService.getNextActivities(activity)) {
				for (final WorkitemBean workitem : wService.getWorkitems(nextActivity)) {
					list.add(workitem.getUserText());
				}
			}
			if (list.size() > 0) {
				userCache.put(key, userTo = StringUtils.join(list, ", "));
			}
		}
		return userTo;
	}

	protected String getUserFrom(final ActivityBean activity) {
		final ActivityBean preActivity = aService.getPreActivity(activity);
		if (preActivity == null) {
			return null;
		}
		final String key = "from_" + preActivity.getId();
		String userFrom = userCache.get(key);
		if (userFrom == null) {
			final Set<String> list = new LinkedHashSet<String>();
			for (final WorkitemBean workitem : wService.getWorkitems(preActivity,
					EWorkitemStatus.complete)) {
				list.add(workitem.getUserText());
			}
			if (list.size() > 0) {
				userCache.put(key, userFrom = StringUtils.join(list, ", "));
			}
		}
		return userFrom;
	}

	static MenuItem MENU_MONITOR(final PageParameter pp) {
		return MenuItem.of($m("MyRunningWorklistTbl.7")).setOnclick(
				"$Actions.loc('"
						+ ((IWorkflowWebContext) context).getUrlsFactory().getUrl(pp,
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

	static NumberConvert DATE_NUMBERCONVERT = new NumberConvert() {
		@Override
		public Object convert(final Number n) {
			return SpanElement.num(n).addStyle("margin-right: 2px;");
		}
	};
}
