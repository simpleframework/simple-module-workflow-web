package net.simpleframework.workflow.web.page;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.common.ID;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.ETextAlign;
import net.simpleframework.mvc.common.element.LinkElement;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ui.pager.TablePagerBean;
import net.simpleframework.mvc.component.ui.pager.TablePagerColumn;
import net.simpleframework.mvc.component.ui.pager.db.AbstractDbTablePagerHandler;
import net.simpleframework.workflow.engine.ActivityBean;
import net.simpleframework.workflow.engine.EWorkitemStatus;
import net.simpleframework.workflow.engine.IWorkitemService;
import net.simpleframework.workflow.engine.WorkitemBean;
import net.simpleframework.workflow.engine.participant.IParticipantModel;

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
		tablePager
				.addColumn(new TablePagerColumn("title", "流程主题").setTextAlign(ETextAlign.left))
				// .addColumn(new TablePagerColumn("activity", "环节", 120))
				.addColumn(new TablePagerColumn("userFrom", "发送人", 120))
				.addColumn(new TablePagerColumn("userTo", "接收人", 120))
				.addColumn(new TablePagerColumn("createDate", "创建日期", 115).setPropertyClass(Date.class))
				// .addColumn(
				// new TablePagerColumn("completeDate", "完成日期",
				// 115).setPropertyClass(Date.class))
				.addColumn(TablePagerColumn.OPE().setWidth(80));
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
			// final Object id = workitem.getId();
			final ActivityBean activity = context.getWorkitemService().getActivity(workitem);
			final String title = StringUtils.text(context.getActivityService()
					.getProcessBean(activity).getTitle(), "未设置主题");
			final KVMap row = new KVMap();
			row.add(
					"title",
					new LinkElement(title).setOnclick("$Actions.loc('"
							+ getUrlsFactory().getMyWorkFormUrl(workitem) + "');"));
			final String userFrom = getUserFrom(activity);
			if (userFrom != null) {
				row.add("userFrom", userFrom);
			}
			final String userTo = getUserTo(activity);
			if (userTo != null) {
				row.add("userTo", userTo);
			}
			final StringBuilder sb = new StringBuilder();
			row.put(TablePagerColumn.OPE, sb.toString());
			return row;
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