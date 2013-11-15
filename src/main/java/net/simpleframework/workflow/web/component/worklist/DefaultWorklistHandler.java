package net.simpleframework.workflow.web.component.worklist;

import static net.simpleframework.common.I18n.$m;

import java.util.Map;

import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.common.ID;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.common.coll.ParameterMap;
import net.simpleframework.mvc.common.element.LinkElement;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ComponentUtils;
import net.simpleframework.mvc.component.ui.menu.MenuBean;
import net.simpleframework.mvc.component.ui.menu.MenuItem;
import net.simpleframework.mvc.component.ui.menu.MenuItems;
import net.simpleframework.mvc.component.ui.pager.AbstractTablePagerSchema;
import net.simpleframework.mvc.component.ui.pager.TablePagerColumns;
import net.simpleframework.mvc.component.ui.pager.db.AbstractDbTablePagerHandler;
import net.simpleframework.mvc.ctx.permission.IPagePermissionHandler;
import net.simpleframework.workflow.engine.ActivityBean;
import net.simpleframework.workflow.engine.EWorkitemStatus;
import net.simpleframework.workflow.engine.IWorkflowContextAware;
import net.simpleframework.workflow.engine.IWorkitemService;
import net.simpleframework.workflow.engine.WorkitemBean;
import net.simpleframework.workflow.engine.participant.IParticipantModel;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class DefaultWorklistHandler extends AbstractDbTablePagerHandler implements
		IWorklistHandler, IWorkflowContextAware {

	@Override
	public Object getBeanProperty(final ComponentParameter cp, final String beanProperty) {
		String title;
		if ("title".equals(beanProperty) && (title = getTitle(cp)) != null) {
			final StringBuilder sb = new StringBuilder();
			sb.append(title);
			wrapNavImage(cp, sb);
			return sb.toString();
		}
		return super.getBeanProperty(cp, beanProperty);
	}

	@Override
	public String getTitle(final ComponentParameter cp) {
		return null;
	}

	@Override
	public Map<String, Object> getFormParameters(final ComponentParameter cp) {
		return ((KVMap) super.getFormParameters(cp)).add(WorklistUtils.STATUS,
				cp.getParameter(WorklistUtils.STATUS));
	}

	@Override
	public IDataQuery<?> createDataObjectQuery(final ComponentParameter cp) {
		final EWorkitemStatus status = WorklistUtils.getWorkitemStatus(cp);
		final ID userId = ((IPagePermissionHandler) context.getParticipantService()).getLoginId(cp);
		final IWorkitemService service = context.getWorkitemService();
		if (status != null) {
			return service.getWorkitemList(userId, status);
		} else {
			return service.getWorkitemList(userId);
		}
	}

	@Override
	public String jsWorkflowFormAction(final WorkitemBean workitemBean) {
		final StringBuilder sb = new StringBuilder();
		sb.append("$Actions['workflowFormWindow']('").append(WorkitemBean.workitemId);
		sb.append("=").append(workitemBean.getId()).append("');");
		return sb.toString();
	}

	private final ParameterMap usersMap = new ParameterMap();

	private String getUserTo(final ActivityBean activity) {
		final String key = "to_" + activity.getId();
		String userTo = usersMap.get(key);
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
				usersMap.put(key, userTo = sb.toString());
			}
		}
		return userTo;
	}

	private String getUserFrom(ActivityBean activity) {
		activity = context.getActivityService().getPreActivity(activity);
		if (activity == null) {
			return null;
		}
		final String key = "from_" + activity.getId();
		String userFrom = usersMap.get(key);
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
				usersMap.put(key, userFrom = sb.toString());
			}
		}
		return userFrom;
	}

	@Override
	public AbstractTablePagerSchema createTablePagerSchema() {
		return new DefaultDbTablePagerSchema() {
			@Override
			public Map<String, Object> getRowData(final ComponentParameter cp, final Object dataObject) {
				final WorkitemBean workitemBean = (WorkitemBean) dataObject;
				final ActivityBean activity = context.getWorkitemService().getActivity(workitemBean);
				final KVMap rowData = new KVMap();
				try {
					String title = StringUtils.text(context.getActivityService()
							.getProcessBean(activity).getTitle(), $m("DefaultWorklistHandle.0"));
					if (!context.getWorkitemService().isFinalStatus(workitemBean)) {
						title = new LinkElement(title).setOnclick(jsWorkflowFormAction(workitemBean))
								.toString();
					}
					if (EWorkitemStatus.running == workitemBean.getStatus()
							&& !workitemBean.isReadMark()) {
						title = "<strong>" + title + "</strong>";
						rowData.add(
								"icon",
								"<img style='vertical-align: middle;' src='"
										+ ComponentUtils.getCssResourceHomePath(cp) + "/images/unread.png'>");
					}
					rowData.add("title", title);
					rowData.add("activity", context.getActivityService().taskNode(activity));
					final String userFrom = getUserFrom(activity);
					if (userFrom != null) {
						rowData.add("userFrom", userFrom);
					}
					final String userTo = getUserTo(activity);
					if (userTo != null) {
						rowData.add("userTo", userTo);
					}
					rowData.add("createDate", workitemBean.getCreateDate());
					rowData.add("completeDate", workitemBean.getCompleteDate());
					rowData.add("action", IMG_DOWNMENU);
				} catch (final Exception e) { // 此处装载工作列表，需要catch掉
					log.warn(e);
				}
				return rowData;
			}

			@Override
			public TablePagerColumns getTablePagerColumns(final ComponentParameter cp) {
				final TablePagerColumns columns = new TablePagerColumns(super.getTablePagerColumns(cp));
				final EWorkitemStatus status = WorklistUtils.getWorkitemStatus(cp);
				if (EWorkitemStatus.running == status) {
					columns.remove("userTo");
					columns.remove("completeDate");
				}
				return columns;
			}
		};
	}

	@Override
	public MenuItems getContextMenu(final ComponentParameter cp, final MenuBean menuBean,
			final MenuItem menuItem) {
		final EWorkitemStatus status = WorklistUtils.getWorkitemStatus(cp);
		final MenuItems items = MenuItems.of();
		if (status == EWorkitemStatus.complete) {
			items.add(MenuItem.of($m("DefaultWorklistHandle.1"), null).setOnclick(
					"$pager_action(item).retake();"));
		} else if (status == EWorkitemStatus.running) {
			items.add(MenuItem.of($m("DefaultWorklistHandle.3"), null).setOnclick(
					"$pager_action(item).readMark();"));
			items.add(MenuItem.sep());
			items.add(MenuItem.of($m("DefaultWorklistHandle.4"), null).setOnclick(
					"$pager_action(item).fallback();"));
			items.add(MenuItem.sep());
			items.add(MenuItem.of($m("DefaultWorklistHandle.2"), null).setOnclick(
					"$pager_action(item).delegate();"));
		}
		return items;
	}
}