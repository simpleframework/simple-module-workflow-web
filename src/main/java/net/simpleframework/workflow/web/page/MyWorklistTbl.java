package net.simpleframework.workflow.web.page;

import static net.simpleframework.common.I18n.$m;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.ado.query.ListDataQuery;
import net.simpleframework.common.Convert;
import net.simpleframework.common.ID;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.mvc.common.element.ButtonElement;
import net.simpleframework.mvc.common.element.ImageElement;
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
import net.simpleframework.workflow.engine.ProcessBean;
import net.simpleframework.workflow.engine.ProcessModelBean;
import net.simpleframework.workflow.engine.WorkitemBean;
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
public class MyWorklistTbl extends GroupDbTablePagerHandler implements IWorkflowContextAware {

	@Override
	public IDataQuery<?> createDataObjectQuery(final ComponentParameter cp) {
		final EWorkitemStatus status = cp.getEnumParameter(EWorkitemStatus.class, "status");
		final ID userId = cp.getLoginId();
		List<WorkitemBean> list;
		if (status != null) {
			cp.addFormParameter("status", status.name());
			list = wService.getWorkitemList(userId, status);
		} else {
			list = wService.getWorkitemList(userId, EWorkitemStatus.running,
					EWorkitemStatus.suspended, EWorkitemStatus.delegate);
		}
		return new ListDataQuery<WorkitemBean>(list);
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
				return activity.setAttr("_processModel", processModel);
			}
		}
		return groupColumn;
	}

	@Override
	public GroupWrapper getGroupWrapper(final ComponentParameter cp, final Object groupVal) {
		if (groupVal instanceof ActivityBean) {
			return new GroupWrapper() {
				@Override
				public String toString() {
					final ActivityBean activity = (ActivityBean) groupVal;
					final StringBuilder sb = new StringBuilder();
					sb.append(new LabelElement(activity));
					sb.append(new SpanElement("(" + activity.getAttr("_processModel") + ")")
							.addStyle("font-weight: normal; margin-left: 5px; color: #999; font-size: 9pt;"));
					sb.append(toCountHTML());
					return sb.toString();
				}
			};
		}
		return super.getGroupWrapper(cp, groupVal);
	}

	protected ImageElement createImageMark(final ComponentParameter cp, final String img) {
		return new ImageElement(cp.getCssResourceHomePath(MyWorklistTbl.class) + "/images/" + img);
	}

	@Override
	protected Map<String, Object> getRowData(final ComponentParameter cp, final Object dataObject) {
		final WorkitemBean workitem = (WorkitemBean) dataObject;
		final KVMap row = new KVMap();

		final ActivityBean activity = wService.getActivity(workitem);
		final StringBuilder sb = new StringBuilder();
		ImageElement img = null;
		if (workitem.getStatus() == EWorkitemStatus.delegate) {
			img = createImageMark(cp, "mark_delegate.png").setTitle($m("MyWorklistTbl.7"));
		} else if (!workitem.isReadMark()) {
			img = createImageMark(cp, "mark_unread.png").setTitle($m("MyWorklistTbl.6"));
		}
		if (img != null) {
			row.add(TablePagerColumn.ICON, img);
		}

		if (!"taskname".equals(cp.getParameter(G))) {
			sb.append("[").append(new SpanElement(activity).setClassName("tasknode_txt")).append("] ");
		}

		final WorkflowUrlsFactory uFactory = ((IWorkflowWebContext) context).getUrlsFactory();

		sb.append(new LinkElement(getTitle(aService.getProcessBean(activity))).setStrong(
				!workitem.isReadMark()).setOnclick(
				"$Actions.loc('" + uFactory.getUrl(cp, WorkflowFormPage.class, workitem) + "');"));
		row.add("title", sb.toString()).add("userFrom", getUserFrom(activity))
				.add("userTo", getUserTo(activity)).add("createDate", workitem.getCreateDate())
				.add("completeDate", workitem.getCompleteDate());

		sb.setLength(0);
		sb.append(new ButtonElement($m("MyWorklistTbl.1")).setOnclick("$Actions.loc('"
				+ uFactory.getUrl(cp, WorkflowMonitorPage.class, workitem) + "');"));
		sb.append(SpanElement.SPACE).append(AbstractTablePagerSchema.IMG_DOWNMENU);
		row.put(TablePagerColumn.OPE, sb.toString());
		return row;
	}

	private final Map<String, String> userCache = new ConcurrentHashMap<String, String>();

	protected String getUserTo(final ActivityBean activity) {
		if (activity == null) {
			return null;
		}
		final String key = "to_" + activity.getId();
		String userTo = userCache.get(key);
		if (userTo == null) {
			final Set<String> list = new LinkedHashSet<String>();
			for (final ActivityBean nextActivity : aService.getNextActivities(activity)) {
				for (final WorkitemBean workitem : wService.getWorkitemList(nextActivity)) {
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
			for (final WorkitemBean workitem : wService.getWorkitemList(preActivity,
					EWorkitemStatus.complete)) {
				list.add(workitem.getUserText());
			}
			if (list.size() > 0) {
				userCache.put(key, userFrom = StringUtils.join(list, ", "));
			}
		}
		return userFrom;
	}

	@Override
	public MenuItems getContextMenu(final ComponentParameter cp, final MenuBean menuBean,
			final MenuItem menuItem) {
		final MenuItems items = MenuItems.of();
		final EWorkitemStatus status = cp.getEnumParameter(EWorkitemStatus.class, "status");
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

	public static String getTitle(final ProcessBean process) {
		return StringUtils.text(Convert.toString(process), $m("MyWorklistTbl.0"));
	}
}