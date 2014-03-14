package net.simpleframework.workflow.web.page.t1;

import static net.simpleframework.common.I18n.$m;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.ado.query.ListDataQuery;
import net.simpleframework.common.Convert;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.mvc.AbstractMVCPage;
import net.simpleframework.mvc.IForward;
import net.simpleframework.mvc.JavascriptForward;
import net.simpleframework.mvc.PageMapping;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.ETextAlign;
import net.simpleframework.mvc.common.element.ElementList;
import net.simpleframework.mvc.common.element.Icon;
import net.simpleframework.mvc.common.element.InputElement;
import net.simpleframework.mvc.common.element.LinkButton;
import net.simpleframework.mvc.common.element.LinkElement;
import net.simpleframework.mvc.common.element.SpanElement;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ui.menu.MenuBean;
import net.simpleframework.mvc.component.ui.menu.MenuItem;
import net.simpleframework.mvc.component.ui.menu.MenuItems;
import net.simpleframework.mvc.component.ui.pager.AbstractTablePagerSchema;
import net.simpleframework.mvc.component.ui.pager.EPagerBarLayout;
import net.simpleframework.mvc.component.ui.pager.TablePagerBean;
import net.simpleframework.mvc.component.ui.pager.TablePagerColumn;
import net.simpleframework.mvc.component.ui.pager.db.GroupDbTablePagerHandler;
import net.simpleframework.mvc.template.struct.NavigationButtons;
import net.simpleframework.workflow.engine.ActivityBean;
import net.simpleframework.workflow.engine.EActivityAbortPolicy;
import net.simpleframework.workflow.engine.EActivityStatus;
import net.simpleframework.workflow.engine.EProcessStatus;
import net.simpleframework.workflow.engine.ProcessBean;
import net.simpleframework.workflow.schema.AbstractTaskNode;
import net.simpleframework.workflow.web.WorkflowLogRef.ActivityUpdateLogPage;
import net.simpleframework.workflow.web.page.WorkflowUtils;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
@PageMapping(url = "/workflow/mgr/activity")
public class ActivityMgrPage extends AbstractWorkflowMgrPage {

	@Override
	protected void onForward(final PageParameter pp) {
		super.onForward(pp);

		// 表格
		final TablePagerBean tablePager = (TablePagerBean) addTablePagerBean(pp,
				"ActivityMgrPage_tbl").setPagerBarLayout(EPagerBarLayout.none)
				.setContainerId("idActivityMgrPage_tbl").setHandlerClass(ActivityTbl.class);
		tablePager.addColumn(TC_TASKNODE()).addColumn(TC_STATUS()).addColumn(TC_PARTICIPANTS())
				.addColumn(TC_PARTICIPANTS2()).addColumn(TC_CREATEDATE()).addColumn(TC_TIMEOUT())
				.addColumn(TC_COMPLETEDATE()).addColumn(TC_PREVIOUS())
				.addColumn(TablePagerColumn.OPE().setWidth(70));

		// 放弃
		addAjaxRequest(pp, "ActivityMgrPage_abort_page", ActivityAbortPage.class);
		addWindowBean(pp, "ActivityMgrPage_abort").setResizable(false)
				.setContentRef("ActivityMgrPage_abort_page").setTitle(EProcessStatus.abort.toString())
				.setWidth(420).setHeight(240);

		// workitems
		addAjaxRequest(pp, "ActivityMgrPage_workitems_page", WorkitemsMgrPage.class);
		addWindowBean(pp, "ActivityMgrPage_workitems")
				.setContentRef("ActivityMgrPage_workitems_page").setWidth(800).setHeight(480);
	}

	@Override
	public ElementList getLeftElements(final PageParameter pp) {
		final ProcessBean process = getProcessBean(pp);
		return ElementList.of(new LinkButton($m("ActivityMgrPage.7")).setIconClass(Icon.share_alt)
				.setHref(url(ProcessMgrPage.class, "modelId=" + process.getModelId())),
				SpanElement.SPACE15, SpanElement.strongText(WorkflowUtils.getTitle(process)));
	}

	@Override
	public NavigationButtons getNavigationBar(final PageParameter pp) {
		return super.getNavigationBar(pp).append(new SpanElement($m("ActivityMgrPage.8")));
	}

	@Override
	protected String toHtml(final PageParameter pp, final Map<String, Object> variables,
			final String variable) throws IOException {
		final StringBuilder sb = new StringBuilder();
		sb.append("<div align='center' class='ActivityMgrPage'>");
		sb.append(" <div id='idActivityMgrPage_tbl'></div>");
		sb.append("</div>");
		return sb.toString();
	}

	@Override
	protected Class<? extends AbstractMVCPage> getStatusDescPage() {
		return StatusDescPage.class;
	}

	@Override
	protected Class<? extends AbstractMVCPage> getUpdateLogPage() {
		return ActivityUpdateLogPage.class;
	}

	public static class ActivityTbl extends GroupDbTablePagerHandler {

		@Override
		public IDataQuery<?> createDataObjectQuery(final ComponentParameter cp) {
			final ProcessBean process = getProcessBean(cp);
			cp.addFormParameter("processId", process.getId());
			return new ListDataQuery<ActivityBean>(aService.getActivities(process));
		}

		protected Object toTasknode(final ActivityBean activity) {
			if (activity.getTasknodeType() == AbstractTaskNode.TT_USER) {
				return new LinkElement(activity)
						.setOnclick("$Actions['ActivityMgrPage_workitems']('activityId="
								+ activity.getId() + "');");
			}
			return activity;
		}

		@Override
		protected Map<String, Object> getRowData(final ComponentParameter cp, final Object dataObject) {
			final ActivityBean activity = (ActivityBean) dataObject;
			final KVMap row = new KVMap();
			row.add("tasknode", toTasknode(activity));
			final ActivityBean pre = aService.getPreActivity(activity);
			if (pre != null) {
				final EActivityStatus pstatus = pre.getStatus();
				row.add("previous", WorkflowUtils.toStatusHTML(cp, pstatus, toTasknode(pre)));
			}
			row.add("participants", getParticipants(activity, false));
			row.add("participants2", getParticipants(activity, true));
			row.add("createDate", activity.getCreateDate());
			row.add("completeDate", activity.getCompleteDate());
			Date timeoutDate;
			if ((timeoutDate = activity.getTimeoutDate()) != null) {
				final Calendar tCal = Calendar.getInstance();
				tCal.setTime(timeoutDate);
				final Calendar nCal = Calendar.getInstance();
				nCal.setTimeInMillis(System.currentTimeMillis());
				final int d = tCal.get(Calendar.HOUR_OF_DAY) - nCal.get(Calendar.HOUR_OF_DAY);
				row.add("timeoutDate", new SpanElement(d).setColor(d > 0 ? "green" : "red"));
			}

			final EActivityStatus status = activity.getStatus();
			row.add("status", WorkflowUtils.toStatusHTML(cp, status));
			row.add(TablePagerColumn.OPE, getOpe(activity));
			return row;
		}

		protected String getOpe(final ActivityBean activity) {
			final StringBuilder sb = new StringBuilder();
			sb.append(WorkflowUtils.createLogButton().setOnclick(
					"$Actions['AbstractWorkflowMgrPage_update_log']('activityId=" + activity.getId()
							+ "');"));
			sb.append(SpanElement.SPACE).append(AbstractTablePagerSchema.IMG_DOWNMENU);
			return sb.toString();
		}

		private String getParticipants(final ActivityBean activity, final boolean b) {
			return StringUtils.join(
					(b ? aService.getParticipants2(activity) : aService.getParticipants(activity, true))
							.values(), ", ");
		}

		@Override
		public MenuItems getContextMenu(final ComponentParameter cp, final MenuBean menuBean,
				final MenuItem menuItem) {
			return MenuItems.of(
					MenuItem.of($m("AbstractWorkflowMgrPage.1")).setOnclick_act(
							"AbstractWorkflowMgrPage_status", "activityId", "op=running"),
					MenuItem.sep(),
					MenuItem.of($m("AbstractWorkflowMgrPage.0")).setOnclick_act(
							"AbstractWorkflowMgrPage_status", "activityId", "op=suspended"),
					MenuItem.of(EActivityStatus.abort.toString()).setOnclick_act(
							"ActivityMgrPage_abort", "activityId"));
		}
	}

	public static class StatusDescPage extends AbstractStatusDescPage {

		@Override
		public JavascriptForward onSave(final ComponentParameter cp) throws Exception {
			final String[] idArr = StringUtils.split(cp.getParameter("activityId"), ";");
			final EActivityStatus op = cp.getEnumParameter(EActivityStatus.class, "op");
			for (final String aId : idArr) {
				final ActivityBean activity = aService.getBean(aId);
				setLogDescription(cp, activity);
				if (op == EActivityStatus.suspended) {
					aService.suspend(activity);
				} else if (op == EActivityStatus.running) {
					aService.resume(activity);
				}
			}
			return super.onSave(cp).append("$Actions['ActivityMgrPage_tbl']();");
		}
	}

	public static class ActivityAbortPage extends AbstractAbortPage {

		public IForward doOk(final ComponentParameter cp) {
			final ActivityBean activity = aService.getBean(cp.getParameter("activityId"));
			aService.abort(activity,
					Convert.toEnum(EActivityAbortPolicy.class, cp.getParameter("abort_policy")));
			return new JavascriptForward(
					"$Actions['ActivityMgrPage_abort'].close(); $Actions['ActivityMgrPage_tbl']();");
		}

		@Override
		protected Enum<?>[] getEnumConstants() {
			return EActivityAbortPolicy.values();
		}

		@Override
		protected InputElement getIdInput(final PageParameter pp) {
			return InputElement.hidden("activityId").setValue(pp);
		}
	}

	private static ProcessBean getProcessBean(final PageParameter pp) {
		return getCacheBean(pp, pService, "processId");
	}

	static TablePagerColumn TC_TASKNODE() {
		return new TablePagerColumn("tasknode", $m("ActivityMgrPage.0")).setSort(false).setFilter(
				false);
	}

	static TablePagerColumn TC_PREVIOUS() {
		return new TablePagerColumn("previous", $m("ActivityMgrPage.1")).setSort(false)
				.setFilter(false).setTextAlign(ETextAlign.left);
	}

	static TablePagerColumn TC_PARTICIPANTS() {
		return new TablePagerColumn("participants", $m("ActivityMgrPage.2")).setNowrap(false)
				.setSort(false).setFilter(false);
	}

	static TablePagerColumn TC_PARTICIPANTS2() {
		return new TablePagerColumn("participants2", $m("ActivityMgrPage.3")).setNowrap(false)
				.setSort(false).setFilter(false);
	}

	static TablePagerColumn TC_CREATEDATE() {
		return new TablePagerColumn("createDate", $m("ActivityMgrPage.4"), 115)
				.setPropertyClass(Date.class);
	}

	static TablePagerColumn TC_COMPLETEDATE() {
		return new TablePagerColumn("completeDate", $m("ActivityMgrPage.5"), 115)
				.setPropertyClass(Date.class);
	}

	static TablePagerColumn TC_TIMEOUT() {
		return new TablePagerColumn("timeoutDate", $m("ActivityMgrPage.9"), 105).setPropertyClass(
				Date.class).setFilter(false);
	}

	static TablePagerColumn TC_STATUS() {
		return new TablePagerColumn("status", $m("ActivityMgrPage.6"), 60).setTextAlign(
				ETextAlign.left).setPropertyClass(EActivityStatus.class);
	}
}
