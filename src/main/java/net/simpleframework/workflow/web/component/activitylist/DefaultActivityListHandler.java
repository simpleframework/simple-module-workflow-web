package net.simpleframework.workflow.web.component.activitylist;

import java.util.Map;

import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.common.Convert;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ui.pager.AbstractTablePagerSchema;
import net.simpleframework.mvc.component.ui.pager.db.AbstractDbTablePagerHandler;
import net.simpleframework.workflow.engine.ActivityBean;
import net.simpleframework.workflow.engine.EActivityStatus;
import net.simpleframework.workflow.engine.EWorkitemStatus;
import net.simpleframework.workflow.engine.IActivityService;
import net.simpleframework.workflow.engine.IWorkflowContextAware;
import net.simpleframework.workflow.engine.ProcessBean;
import net.simpleframework.workflow.engine.WorkitemBean;
import net.simpleframework.workflow.engine.participant.IParticipantModel;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public class DefaultActivityListHandler extends AbstractDbTablePagerHandler implements
		IActivityListHandler, IWorkflowContextAware {

	@Override
	public Map<String, Object> getFormParameters(final ComponentParameter cp) {
		return ((KVMap) super.getFormParameters(cp)).add(ProcessBean.processId,
				cp.getParameter(ProcessBean.processId));
	}

	@Override
	public Object getBeanProperty(final ComponentParameter cp, final String beanProperty) {
		if ("pageItems".equals(beanProperty)) {
			return Convert.toInt(cp.getRequestAttr("pageItems"), Integer.MAX_VALUE);
		}
		return super.getBeanProperty(cp, beanProperty);
	}

	@Override
	public IDataQuery<?> createDataObjectQuery(final ComponentParameter cp) {
		final ProcessBean processBean = context.getProcessService().getBean(
				cp.getParameter(ProcessBean.processId));
		final IDataQuery<?> qs = context.getActivityService().getActivities(processBean);
		cp.setRequestAttr("pageItems", qs.getCount());
		return qs;
	}

	private String getParticipants(final ActivityBean activity, final EWorkitemStatus status) {
		final StringBuilder sb = new StringBuilder();
		final IDataQuery<WorkitemBean> qs = context.getWorkitemService().getWorkitemList(activity);
		final IParticipantModel service = context.getParticipantService();
		WorkitemBean item;
		int i = 0;
		while ((item = qs.next()) != null) {
			if (status != null && status != item.getStatus()) {
				continue;
			}
			if (i > 0) {
				sb.append(", ");
			}
			sb.append("<span class='participants2' params='").append(WorkitemBean.workitemId)
					.append("=").append(item.getId()).append("'>")
					.append(service.getUser(item.getUserId())).append("</span>");
			i++;
		}
		return sb.toString();
	}

	@Override
	protected Map<String, Object> getRowData(final ComponentParameter cp, final Object dataObject) {
		final ActivityBean activity = (ActivityBean) dataObject;
		final KVMap rowData = new KVMap();
		final IActivityService service = context.getActivityService();
		rowData.add("tasknode", service.taskNode(activity));
		final ActivityBean pre = service.getPreActivity(activity);
		if (pre != null) {
			rowData.add("previous", service.taskNode(pre));
		}
		rowData.add("participants", getParticipants(activity, null));
		rowData.add("participants2", getParticipants(activity, EWorkitemStatus.complete));
		rowData.add("createDate", activity.getCreateDate());
		rowData.add("completeDate", activity.getCompleteDate());

		final EActivityStatus status = activity.getStatus();
		rowData.add("status", ActivityListUtils.getStatusIcon(cp, status) + status);

		rowData.add("action", AbstractTablePagerSchema.IMG_DOWNMENU);
		return rowData;
	}
}
