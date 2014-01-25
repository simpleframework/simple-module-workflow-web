package net.simpleframework.workflow.web.page;

import static net.simpleframework.common.I18n.$m;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.common.ID;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.mvc.common.element.ButtonElement;
import net.simpleframework.mvc.common.element.LabelElement;
import net.simpleframework.mvc.common.element.LinkElement;
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
import net.simpleframework.workflow.engine.EWorkitemStatus;
import net.simpleframework.workflow.engine.IWorkflowContextAware;
import net.simpleframework.workflow.engine.ProcessModelBean;
import net.simpleframework.workflow.engine.WorkitemBean;
import net.simpleframework.workflow.schema.AbstractTaskNode;
import net.simpleframework.workflow.web.IWorkflowWebContext;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class MyWorklistTbl extends GroupDbTablePagerHandler implements IWorkflowContextAware {

	@Override
	public IDataQuery<?> createDataObjectQuery(final ComponentParameter cp) {
		final EWorkitemStatus status = MyWorklistTPage.getWorkitemStatus(cp);
		final ID userId = cp.getLoginId();
		if (status != null) {
			cp.addFormParameter("status", status.name());
			return wService.getWorkitemList(userId, status);
		} else {
			return wService.getWorkitemList(userId);
		}
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
							.setStyle("font-weight: normal; margin-left: 5px; color: #999; font-size: 9pt;"));
					sb.append(toCountHTML());
					return sb.toString();
				}
			};
		}
		return super.getGroupWrapper(cp, groupVal);
	}

	@Override
	protected Map<String, Object> getRowData(final ComponentParameter cp, final Object dataObject) {
		final WorkitemBean workitem = (WorkitemBean) dataObject;
		final KVMap row = new KVMap();

		final ActivityBean activity = wService.getActivity(workitem);
		final StringBuilder sb = new StringBuilder();

		if (!"taskname".equals(cp.getParameter(G))) {
			sb.append("[").append(aService.getTaskNode(activity)).append("] ");
		}
		sb.append(new LinkElement(StringUtils.text(aService.getProcessBean(activity).toString(),
				$m("MyWorklistTbl.0"))).setStrong(!workitem.isReadMark()).setOnclick(
				"$Actions.loc('"
						+ (((IWorkflowWebContext) context).getUrlsFactory()).getWorkflowFormUrl(workitem)
						+ "');"));
		row.add("title", sb.toString()).add("userFrom", getUserFrom(cp, activity))
				.add("userTo", getUserTo(cp, activity)).add("createDate", workitem.getCreateDate())
				.add("completeDate", workitem.getCompleteDate());

		sb.setLength(0);
		sb.append(new ButtonElement($m("MyWorklistTbl.1")).setOnclick("$Actions.loc('"
				+ ((IWorkflowWebContext) context).getUrlsFactory().getWorkflowMonitorUrl(workitem)
				+ "');"));
		sb.append(SpanElement.SPACE).append(AbstractTablePagerSchema.IMG_DOWNMENU);
		row.put(TablePagerColumn.OPE, sb.toString());
		return row;
	}

	private final Map<String, String> userCache = new ConcurrentHashMap<String, String>();

	private String getUserTo(final ComponentParameter cp, final ActivityBean activity) {
		final String key = "to_" + activity.getId();
		String userTo = userCache.get(key);
		if (userTo == null) {
			final StringBuilder sb = new StringBuilder();
			final IDataQuery<ActivityBean> qs = aService.getNextActivities(activity);
			ActivityBean nextActivity;
			final ArrayList<ID> ids = new ArrayList<ID>();
			while ((nextActivity = qs.next()) != null) {
				WorkitemBean workitem;
				final IDataQuery<WorkitemBean> qs2 = wService.getWorkitemList(nextActivity);
				while ((workitem = qs2.next()) != null) {
					final ID userId = workitem.getUserId();
					if (!ids.contains(userId)) {
						if (ids.size() > 0) {
							sb.append(", ");
						}
						sb.append(cp.getUser(userId));
						ids.add(userId);
					}
				}
			}
			if (sb.length() > 0) {
				userCache.put(key, userTo = sb.toString());
			}
		}
		return userTo;
	}

	private String getUserFrom(final ComponentParameter cp, final ActivityBean activity) {
		final ActivityBean preActivity = aService.getPreActivity(activity);
		if (preActivity == null) {
			return null;
		}
		final String key = "from_" + preActivity.getId();
		String userFrom = userCache.get(key);
		if (userFrom == null) {
			final StringBuilder sb = new StringBuilder();
			final IDataQuery<WorkitemBean> qs = wService.getWorkitemList(preActivity,
					EWorkitemStatus.complete);
			WorkitemBean workitem;
			final ArrayList<ID> ids = new ArrayList<ID>();
			while ((workitem = qs.next()) != null) {
				final ID userId = workitem.getUserId();
				if (!ids.contains(userId)) {
					if (ids.size() > 0) {
						sb.append(", ");
					}
					sb.append(cp.getUser(userId));
					ids.add(userId);
				}
			}
			if (sb.length() > 0) {
				userCache.put(key, userFrom = sb.toString());
			}
		}
		return userFrom;
	}

	@Override
	public MenuItems getContextMenu(final ComponentParameter cp, final MenuBean menuBean,
			final MenuItem menuItem) {
		final MenuItems items = MenuItems.of();
		final EWorkitemStatus status = MyWorklistTPage.getWorkitemStatus(cp);
		if (status == EWorkitemStatus.complete) {
			items.append(MenuItem.of($m("MyWorklistTbl.2")).setOnclick_act("MyWorklistTPage_retake",
					"workitemId"));
		} else {
			items.append(MenuItem.of($m("MyWorklistTbl.3")).setOnclick_act("MyWorklistTPage_readMark",
					"workitemId"));
			items.append(MenuItem.sep());
			items.append(MenuItem.of($m("MyWorklistTbl.4")).setOnclick_act("MyWorklistTPage_fallback",
					"workitemId"));
			items.append(MenuItem.sep());
			items.append(MenuItem.itemDelete().setOnclick_act("MyWorklistTPage_delete", "workitemId"));
			items.append(MenuItem.sep());
			items.append(MenuItem.of($m("MyWorklistTbl.5")).setOnclick_act("MyWorklistTPage_delegate",
					"workitemId"));
		}
		return items;
	}
}