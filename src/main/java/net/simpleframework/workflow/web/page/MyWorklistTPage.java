package net.simpleframework.workflow.web.page;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.common.ID;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.mvc.IForward;
import net.simpleframework.mvc.JavascriptForward;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.ETextAlign;
import net.simpleframework.mvc.common.element.LinkElement;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ui.menu.MenuBean;
import net.simpleframework.mvc.component.ui.menu.MenuItem;
import net.simpleframework.mvc.component.ui.menu.MenuItems;
import net.simpleframework.mvc.component.ui.pager.AbstractTablePagerSchema;
import net.simpleframework.mvc.component.ui.pager.TablePagerBean;
import net.simpleframework.mvc.component.ui.pager.TablePagerColumn;
import net.simpleframework.mvc.component.ui.pager.db.AbstractDbTablePagerHandler;
import net.simpleframework.workflow.engine.ActivityBean;
import net.simpleframework.workflow.engine.EWorkitemStatus;
import net.simpleframework.workflow.engine.IWorkitemService;
import net.simpleframework.workflow.engine.WorkitemBean;
import net.simpleframework.workflow.engine.participant.IParticipantModel;
import net.simpleframework.workflow.web.AbstractWorkflowFormPage;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class MyWorklistTPage extends AbstractWorkTPage {

	@Override
	protected void onForward(final PageParameter pp) {
		super.onForward(pp);

		final TablePagerBean tablePager = addTablePagerBean(pp, "MyWorklistTPage_tbl",
				MyWorklistTbl.class);

		final EWorkitemStatus status = getWorkitemStatus(pp);
		tablePager.addColumn(new TablePagerColumn("title", "流程主题").setTextAlign(ETextAlign.left)
				.setSort(false).setFilter(false));
		if (status == EWorkitemStatus.complete) {
			tablePager.addColumn(new TablePagerColumn("userTo", "接收人", 120).setSort(false).setFilter(
					false));
			tablePager.addColumn(new TablePagerColumn("completeDate", "完成日期", 115)
					.setPropertyClass(Date.class));
		} else {
			tablePager.addColumn(new TablePagerColumn("userFrom", "发送人", 120).setSort(false)
					.setFilter(false));
			tablePager.addColumn(new TablePagerColumn("createDate", "创建日期", 115)
					.setPropertyClass(Date.class));
		}
		tablePager.addColumn(TablePagerColumn.OPE().setWidth(35));

		// readMark
		addAjaxRequest(pp, "MyWorklistTPage_action").setHandleMethod("doAction");

		// 委托
		addAjaxRequest(pp, "MyWorklistTPage_delegate_page", WorkitemDelegatePage.class);
		addWindowBean(pp, "MyWorklistTPage_delegate").setContentRef("MyWorklistTPage_delegate_page")
				.setTitle("委托").setHeight(400).setWidth(320);
	}

	public IForward doAction(final ComponentParameter cp) {
		final WorkitemBean workitem = AbstractWorkflowFormPage.getWorkitemBean(cp);
		final String action = cp.getParameter("action");
		if ("readMark".equals(action)) {
			context.getWorkitemService().readMark(workitem, workitem.isReadMark() ? true : false);
		} else if ("retake".equals(action)) {
			context.getWorkitemService().retake(workitem);
		} else if ("fallback".equals(action)) {
			context.getActivityService().fallback(context.getWorkitemService().getActivity(workitem));
		}
		return new JavascriptForward("$Actions['MyWorklistTPage_tbl']();");
	}

	public static class MyWorklistTbl extends AbstractDbTablePagerHandler {

		@Override
		public IDataQuery<?> createDataObjectQuery(final ComponentParameter cp) {
			final EWorkitemStatus status = getWorkitemStatus(cp);
			final ID userId = cp.getLoginId();
			final IWorkitemService service = context.getWorkitemService();
			if (status != null) {
				cp.addFormParameter("status", status.name());
				return service.getWorkitemList(userId, status);
			} else {
				return service.getWorkitemList(userId);
			}
		}

		@Override
		protected Map<String, Object> getRowData(final ComponentParameter cp, final Object dataObject) {
			final WorkitemBean workitem = (WorkitemBean) dataObject;
			final StringBuilder sb = new StringBuilder();
			final ActivityBean activity = context.getWorkitemService().getActivity(workitem);
			sb.append("[").append(context.getActivityService().taskNode(activity)).append("] ");
			sb.append(new LinkElement(StringUtils.text(
					context.getActivityService().getProcessBean(activity).getTitle(), "未设置主题"))
					.setStrong(!workitem.isReadMark()).setOnclick(
							"$Actions.loc('" + getUrlsFactory().getMyWorkFormUrl(workitem) + "');"));
			final KVMap row = new KVMap();
			row.add("title", sb.toString());
			final String userFrom = getUserFrom(activity);
			if (userFrom != null) {
				row.add("userFrom", userFrom);
			}
			final String userTo = getUserTo(activity);
			if (userTo != null) {
				row.add("userTo", userTo);
			}
			row.add("createDate", workitem.getCreateDate());
			row.add("completeDate", workitem.getCompleteDate());
			sb.setLength(0);
			sb.append(AbstractTablePagerSchema.IMG_DOWNMENU);
			row.put(TablePagerColumn.OPE, sb.toString());
			return row;
		}

		@Override
		public MenuItems getContextMenu(final ComponentParameter cp, final MenuBean menuBean,
				final MenuItem menuItem) {
			final MenuItems items = MenuItems.of();
			final EWorkitemStatus status = getWorkitemStatus(cp);
			if (status == EWorkitemStatus.complete) {
				items.append(MenuItem.of("取回").setOnclick_act("MyWorklistTPage_action", "workitemId",
						"action=retake"));
			} else {
				items.append(MenuItem.of("回退").setOnclick_act("MyWorklistTPage_action", "workitemId",
						"action=fallback"));
				items.append(MenuItem.sep());
				items.append(MenuItem.of("委托").setOnclick_act("MyWorklistTPage_delegate", "workitemId"));
				items.append(MenuItem.sep());
				items.append(MenuItem.of("标记已读/未读").setOnclick_act("MyWorklistTPage_action",
						"workitemId", "action=readMark"));
			}
			return items;
		}
	}

	private static final Map<String, String> usersCache = new HashMap<String, String>();

	private static String getUserTo(final ActivityBean activity) {
		final String key = "to_" + activity.getId();
		String userTo = usersCache.get(key);
		if (userTo == null) {
			final StringBuilder sb = new StringBuilder();
			final IWorkitemService service = context.getWorkitemService();
			final IParticipantModel pService = context.getParticipantService();
			final IDataQuery<ActivityBean> qs = context.getActivityService().getNextActivities(
					activity);
			ActivityBean activity2;
			int i = 0;
			while ((activity2 = qs.next()) != null) {
				WorkitemBean workitem;
				final IDataQuery<WorkitemBean> qs2 = service.getWorkitemList(activity2);
				while ((workitem = qs2.next()) != null) {
					if (i > 0) {
						sb.append(", ");
					}
					sb.append(pService.getUser(workitem.getUserId()));
					i++;
				}
			}
			if (sb.length() > 0) {
				usersCache.put(key, userTo = sb.toString());
			}
		}
		return userTo;
	}

	private static String getUserFrom(ActivityBean activity) {
		activity = context.getActivityService().getPreActivity(activity);
		if (activity == null) {
			return null;
		}
		final String key = "from_" + activity.getId();
		String userFrom = usersCache.get(key);
		if (userFrom == null) {
			final StringBuilder sb = new StringBuilder();
			final IDataQuery<WorkitemBean> qs = context.getWorkitemService().getWorkitemList(activity,
					EWorkitemStatus.complete);
			final IParticipantModel service = context.getParticipantService();
			WorkitemBean workitem;
			int i = 0;
			while ((workitem = qs.next()) != null) {
				if (i > 0) {
					sb.append(", ");
				}
				sb.append(service.getUser(workitem.getUserId()));
				i++;
			}
			if (sb.length() > 0) {
				usersCache.put(key, userFrom = sb.toString());
			}
		}
		return userFrom;
	}

	private static EWorkitemStatus getWorkitemStatus(final PageParameter pp) {
		final String status = pp.getParameter("status");
		if (!"false".equals(status)) {
			return StringUtils.hasText(status) ? EWorkitemStatus.valueOf(status)
					: EWorkitemStatus.running;
		}
		return null;
	}
}