package net.simpleframework.workflow.web.page.t1;

import static net.simpleframework.common.I18n.$m;

import java.util.Date;
import java.util.Map;

import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.ado.query.ListDataObjectQuery;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.ctx.IModuleRef;
import net.simpleframework.mvc.AbstractMVCPage;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.ButtonElement;
import net.simpleframework.mvc.common.element.ETextAlign;
import net.simpleframework.mvc.common.element.ElementList;
import net.simpleframework.mvc.common.element.LinkButton;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.base.ajaxrequest.AjaxRequestBean;
import net.simpleframework.mvc.component.ui.pager.TablePagerBean;
import net.simpleframework.mvc.component.ui.pager.TablePagerColumn;
import net.simpleframework.mvc.component.ui.pager.db.AbstractDbTablePagerHandler;
import net.simpleframework.mvc.component.ui.window.WindowBean;
import net.simpleframework.mvc.template.lets.OneTableTemplatePage;
import net.simpleframework.workflow.engine.ActivityBean;
import net.simpleframework.workflow.engine.EWorkitemStatus;
import net.simpleframework.workflow.engine.IWorkflowContextAware;
import net.simpleframework.workflow.engine.WorkitemBean;
import net.simpleframework.workflow.web.IWorkflowWebContext;
import net.simpleframework.workflow.web.WorkflowLogRef.WorkitemUpdateLogPage;
import net.simpleframework.workflow.web.page.WorkflowUtils;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class WorkitemsMgrPage extends OneTableTemplatePage implements IWorkflowContextAware {

	@Override
	protected void onForward(final PageParameter pp) {
		super.onForward(pp);

		// workitems
		final TablePagerBean tablePager = addTablePagerBean(pp, "WorkitemsPage_tbl",
				WorkitemsTbl.class).setShowLineNo(false);
		tablePager
				.addColumn(new TablePagerColumn("userId", $m("WorkitemsMgrPage.0")))
				.addColumn(new TablePagerColumn("userId2", $m("WorkitemsMgrPage.1")))
				.addColumn(
						new TablePagerColumn("createDate", $m("WorkitemsMgrPage.2"), 115)
								.setPropertyClass(Date.class))
				.addColumn(
						new TablePagerColumn("completeDate", $m("WorkitemsMgrPage.3"), 115)
								.setPropertyClass(Date.class))
				.addColumn(
						new TablePagerColumn("status", $m("WorkitemsMgrPage.4"), 70).setPropertyClass(
								EWorkitemStatus.class).setTextAlign(ETextAlign.left))
				.addColumn(TablePagerColumn.OPE().setWidth(70));

		// log
		final IModuleRef ref = ((IWorkflowWebContext) context).getLogRef();
		if (ref != null) {
			pp.addComponentBean("WorkitemsMgrPage_update_logPage", AjaxRequestBean.class)
					.setUrlForward(AbstractMVCPage.url(WorkitemUpdateLogPage.class));
			pp.addComponentBean("WorkitemsMgrPage_update_log", WindowBean.class)
					.setContentRef("WorkitemsMgrPage_update_logPage").setHeight(540).setWidth(864);
		}
	}

	@Override
	public String getTitle(final PageParameter pp) {
		return $m("WorkitemsMgrPage.5") + " - " + aService.getTaskNode(getActivityBean(pp));
	}

	@Override
	public ElementList getLeftElements(final PageParameter pp) {
		return ElementList.of(LinkButton.closeBtn());
	}

	public static class WorkitemsTbl extends AbstractDbTablePagerHandler {

		@Override
		public IDataQuery<?> createDataObjectQuery(final ComponentParameter cp) {
			final ActivityBean activity = getActivityBean(cp);
			cp.addFormParameter("activityId", activity.getId());
			return new ListDataObjectQuery<WorkitemBean>(wService.getWorkitemList(activity));
		}

		@Override
		protected Map<String, Object> getRowData(final ComponentParameter cp, final Object dataObject) {
			final WorkitemBean workitem = (WorkitemBean) dataObject;
			final KVMap row = new KVMap();
			row.add("userId", cp.getUser(workitem.getUserId()))
					.add("userId2", cp.getUser(workitem.getUserId2()))
					.add("createDate", workitem.getCreateDate())
					.add("completeDate", workitem.getCompleteDate());
			final EWorkitemStatus status = workitem.getStatus();
			row.add("status", WorkflowUtils.createStatusImage(cp, status) + status.toString());

			final StringBuilder sb = new StringBuilder();
			sb.append(ButtonElement
					.logBtn()
					.setDisabled(((IWorkflowWebContext) context).getLogRef() == null)
					.setOnclick(
							"$Actions['WorkitemsMgrPage_update_log']('workitemId=" + workitem.getId()
									+ "');"));
			row.add(TablePagerColumn.OPE, sb.toString());
			return row;
		}
	}

	private static ActivityBean getActivityBean(final PageParameter pp) {
		return getCacheBean(pp, aService, "activityId");
	}
}