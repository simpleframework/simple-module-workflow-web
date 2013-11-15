package net.simpleframework.workflow.web.component.processlist;

import static net.simpleframework.common.I18n.$m;

import java.util.Map;

import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.mvc.common.element.LinkElement;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ui.pager.AbstractTablePagerSchema;
import net.simpleframework.mvc.component.ui.pager.db.AbstractDbTablePagerHandler;
import net.simpleframework.workflow.engine.EProcessStatus;
import net.simpleframework.workflow.engine.IWorkflowContextAware;
import net.simpleframework.workflow.engine.ProcessBean;
import net.simpleframework.workflow.engine.ProcessModelBean;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class DefaultProcessListHandler extends AbstractDbTablePagerHandler implements
		IProcessListHandler, IWorkflowContextAware {

	@Override
	public Object getBeanProperty(final ComponentParameter cp, final String beanProperty) {
		if ("title".equals(beanProperty)) {
			final ProcessModelBean processModel = context.getModelService().getBean(
					cp.getParameter(ProcessModelBean.modelId));
			if (processModel != null) {
				final StringBuilder sb = new StringBuilder();
				sb.append(processModel);
				wrapNavImage(cp, sb);
				return sb.toString();
			}
		}
		return super.getBeanProperty(cp, beanProperty);
	}

	@Override
	public Map<String, Object> getFormParameters(final ComponentParameter cp) {
		return ((KVMap) super.getFormParameters(cp)).add(ProcessModelBean.modelId,
				cp.getParameter(ProcessModelBean.modelId));
	}

	@Override
	public IDataQuery<?> createDataObjectQuery(final ComponentParameter cp) {
		return context.getProcessService().getProcessList(
				context.getModelService().getBean(cp.getParameter(ProcessModelBean.modelId)));
	}

	@Override
	public String jsActivityListAction(final ProcessBean processBean) {
		return "$Actions['activitylist_window']('" + ProcessBean.processId + "="
				+ processBean.getId() + "');";
	}

	@Override
	protected Map<String, Object> getRowData(final ComponentParameter cp, final Object dataObject) {
		final ProcessBean processBean = (ProcessBean) dataObject;
		final KVMap rowData = new KVMap();
		rowData
				.add("title",
						new LinkElement(StringUtils.text(processBean.getTitle(),
								$m("DefaultWorklistHandle.0")))
								.setOnclick(jsActivityListAction(processBean)));
		rowData.add("userText", context.getParticipantService().getUser(processBean.getUserId()));

		rowData.add("createDate", processBean.getCreateDate());
		rowData.add("completeDate", processBean.getCompleteDate());

		final EProcessStatus status = processBean.getStatus();
		rowData.add("status", ProcessListUtils.getStatusIcon(cp, status) + status);

		rowData.add("action", AbstractTablePagerSchema.IMG_DOWNMENU);
		return rowData;
	}
}
