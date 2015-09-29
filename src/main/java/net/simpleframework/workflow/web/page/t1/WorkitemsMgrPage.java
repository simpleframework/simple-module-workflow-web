package net.simpleframework.workflow.web.page.t1;

import static net.simpleframework.common.I18n.$m;

import java.util.Map;

import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.ado.query.ListDataQuery;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.ctx.IModuleRef;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.ButtonElement;
import net.simpleframework.mvc.common.element.ElementList;
import net.simpleframework.mvc.common.element.LinkButton;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.base.ajaxrequest.AjaxRequestBean;
import net.simpleframework.mvc.component.ui.pager.TablePagerBean;
import net.simpleframework.mvc.component.ui.pager.TablePagerColumn;
import net.simpleframework.mvc.component.ui.pager.db.AbstractDbTablePagerHandler;
import net.simpleframework.mvc.template.lets.OneTableTemplatePage;
import net.simpleframework.workflow.engine.EWorkitemStatus;
import net.simpleframework.workflow.engine.IWorkflowContextAware;
import net.simpleframework.workflow.engine.bean.ActivityBean;
import net.simpleframework.workflow.engine.bean.WorkitemBean;
import net.simpleframework.workflow.web.IWorkflowWebContext;
import net.simpleframework.workflow.web.WorkflowLogRef.WorkitemUpdateLogPage;
import net.simpleframework.workflow.web.WorkflowUtils;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class WorkitemsMgrPage extends OneTableTemplatePage implements IWorkflowContextAware {

	@Override
	protected void onForward(final PageParameter pp) throws Exception {
		super.onForward(pp);

		// workitems
		final TablePagerBean tablePager = addTablePagerBean(pp, "WorkitemsPage_tbl",
				WorkitemsTbl.class).setShowLineNo(false);
		tablePager.addColumn(new TablePagerColumn("userText", $m("WorkitemsMgrPage.0")))
				.addColumn(new TablePagerColumn("userText2", $m("WorkitemsMgrPage.1")))
				.addColumn(TablePagerColumn.DATE("createDate", $m("WorkitemsMgrPage.2")))
				.addColumn(TablePagerColumn.DATE("completeDate", $m("WorkitemsMgrPage.3")))
				.addColumn(AbstractWorkflowMgrPage.TC_STATUS(EWorkitemStatus.class))
				.addColumn(TablePagerColumn.OPE(70));

		// log
		final IModuleRef ref = ((IWorkflowWebContext) workflowContext).getLogRef();
		if (ref != null) {
			final AjaxRequestBean ajaxRequest = addAjaxRequest(pp, "WorkitemsMgrPage_update_logPage",
					WorkitemUpdateLogPage.class);
			addWindowBean(pp, "WorkitemsMgrPage_update_log", ajaxRequest).setHeight(540).setWidth(864);
		}
	}

	@Override
	public String getTitle(final PageParameter pp) {
		return $m("WorkitemsMgrPage.5") + " - " + WorkflowUtils.getActivityBean(pp);
	}

	@Override
	public ElementList getLeftElements(final PageParameter pp) {
		return ElementList.of(LinkButton.closeBtn());
	}

	public static class WorkitemsTbl extends AbstractDbTablePagerHandler {

		@Override
		public IDataQuery<?> createDataObjectQuery(final ComponentParameter cp) {
			final ActivityBean activity = WorkflowUtils.getActivityBean(cp);
			cp.addFormParameter("activityId", activity.getId());
			return new ListDataQuery<WorkitemBean>(wfwService.getWorkitems(activity));
		}

		@Override
		protected Map<String, Object> getRowData(final ComponentParameter cp, final Object dataObject) {
			final WorkitemBean workitem = (WorkitemBean) dataObject;
			final KVMap row = new KVMap();
			row.add("userText", workitem.getUserText()).add("userText2", workitem.getUserText2())
					.add("createDate", workitem.getCreateDate())
					.add("completeDate", workitem.getCompleteDate());
			final EWorkitemStatus status = workitem.getStatus();
			row.add("status", WorkflowUtils.toStatusHTML(cp, status));

			final StringBuilder sb = new StringBuilder();
			sb.append(ButtonElement
					.logBtn()
					.setDisabled(((IWorkflowWebContext) workflowContext).getLogRef() == null)
					.setOnclick(
							"$Actions['WorkitemsMgrPage_update_log']('workitemId=" + workitem.getId()
									+ "');"));
			row.add(TablePagerColumn.OPE, sb.toString());
			return row;
		}
	}
}