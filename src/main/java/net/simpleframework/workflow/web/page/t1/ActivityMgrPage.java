package net.simpleframework.workflow.web.page.t1;

import static net.simpleframework.common.I18n.$m;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.common.Convert;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.mvc.AbstractMVCPage;
import net.simpleframework.mvc.IForward;
import net.simpleframework.mvc.JavascriptForward;
import net.simpleframework.mvc.PageMapping;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.BlockElement;
import net.simpleframework.mvc.common.element.EElementEvent;
import net.simpleframework.mvc.common.element.ETextAlign;
import net.simpleframework.mvc.common.element.ElementList;
import net.simpleframework.mvc.common.element.Icon;
import net.simpleframework.mvc.common.element.InputElement;
import net.simpleframework.mvc.common.element.LinkButton;
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
import net.simpleframework.mvc.component.ui.tooltip.ETipElement;
import net.simpleframework.mvc.component.ui.tooltip.ETipPosition;
import net.simpleframework.mvc.component.ui.tooltip.TipBean;
import net.simpleframework.mvc.component.ui.tooltip.TipBean.HideOn;
import net.simpleframework.mvc.component.ui.tooltip.TipBean.Hook;
import net.simpleframework.mvc.component.ui.tooltip.TooltipBean;
import net.simpleframework.mvc.template.struct.NavigationButtons;
import net.simpleframework.workflow.engine.ActivityBean;
import net.simpleframework.workflow.engine.EActivityAbortPolicy;
import net.simpleframework.workflow.engine.EActivityStatus;
import net.simpleframework.workflow.engine.EProcessStatus;
import net.simpleframework.workflow.engine.EWorkitemStatus;
import net.simpleframework.workflow.engine.ProcessBean;
import net.simpleframework.workflow.engine.WorkitemBean;
import net.simpleframework.workflow.web.page.MyWorklistTbl;
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
				.setContainerId("idActivityMgrPage_tbl").setHandleClass(ActivityTbl.class);
		tablePager.addColumn(TC_TASKNODE()).addColumn(TC_PREVIOUS()).addColumn(TC_PARTICIPANTS())
				.addColumn(TC_PARTICIPANTS2()).addColumn(TC_CREATEDATE()).addColumn(TC_COMPLETEDATE())
				.addColumn(TC_STATUS()).addColumn(TablePagerColumn.OPE().setWidth(90));

		// 放弃
		addAjaxRequest(pp, "ActivityMgrPage_abort_page", ActivityAbortPage.class);
		addWindowBean(pp, "ActivityMgrPage_abort").setResizable(false)
				.setContentRef("ActivityMgrPage_abort_page").setTitle(EProcessStatus.abort.toString())
				.setWidth(420).setHeight(240);

		// tooltip
		final TooltipBean tooltip = addComponentBean(pp, "ActivityMgrPage_tip", TooltipBean.class);
		tooltip.addTip(new TipBean(tooltip).setSelector(".participants2")
				.setStem(ETipPosition.bottomMiddle)
				.setHook(new Hook(ETipPosition.topMiddle, ETipPosition.bottomMiddle))
				.setHideOn(new HideOn(ETipElement.tip, EElementEvent.mouseleave)).setWidth(300));
	}

	@Override
	public ElementList getLeftElements(final PageParameter pp) {
		final ProcessBean process = getProcessBean(pp);
		return ElementList.of(new LinkButton($m("ActivityMgrPage.7")).setIconClass(Icon.share_alt)
				.setHref(url(ProcessMgrPage.class, "modelId=" + process.getModelId())),
				SpanElement.SPACE15, SpanElement.strongText(MyWorklistTbl.getTopic(process)));
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
		return null;
	}

	public static class ActivityTbl extends GroupDbTablePagerHandler {

		@Override
		public IDataQuery<?> createDataObjectQuery(final ComponentParameter cp) {
			final ProcessBean process = getProcessBean(cp);
			cp.addFormParameter("processId", process.getId());
			return aService.getActivities(process);
		}

		@Override
		protected Map<String, Object> getRowData(final ComponentParameter cp, final Object dataObject) {
			final ActivityBean activity = (ActivityBean) dataObject;
			final KVMap row = new KVMap();
			row.add("tasknode", aService.getTaskNode(activity));
			final ActivityBean pre = aService.getPreActivity(activity);
			if (pre != null) {
				row.add("previous", aService.getTaskNode(pre));
			}
			row.add("participants", getParticipants(cp, activity, null));
			row.add("participants2", getParticipants(cp, activity, EWorkitemStatus.complete));
			row.add("createDate", activity.getCreateDate());
			row.add("completeDate", activity.getCompleteDate());

			final EActivityStatus status = activity.getStatus();
			row.add("status", WorkflowUtils.createStatusImage(cp, status) + status.toString());
			row.add(TablePagerColumn.OPE, getOpe(activity));
			return row;
		}

		protected String getOpe(final ActivityBean activity) {
			final StringBuilder sb = new StringBuilder();
			sb.append(createLogButton("activityId=" + activity.getId()));
			sb.append(SpanElement.SPACE).append(AbstractTablePagerSchema.IMG_DOWNMENU);
			return sb.toString();
		}

		private String getParticipants(final PageParameter pp, final ActivityBean activity,
				final EWorkitemStatus status) {
			final StringBuilder sb = new StringBuilder();
			final IDataQuery<WorkitemBean> qs = wService.getWorkitemList(activity);
			WorkitemBean item;
			int i = 0;
			while ((item = qs.next()) != null) {
				if (status != null && status != item.getStatus()) {
					continue;
				}
				if (i > 0) {
					sb.append(", ");
				}
				sb.append(new SpanElement(pp.getUser(item.getUserId())).setClassName("participants2"));
				sb.append(BlockElement.tip(item.toString()));
				i++;
			}
			return sb.toString();
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
			updateStatus(cp, aService, StringUtils.split(cp.getParameter("activityId"), ";"),
					cp.getEnumParameter(EActivityStatus.class, "op"));
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
		return new TablePagerColumn("previous", $m("ActivityMgrPage.1")).setSort(false).setFilter(
				false);
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

	static TablePagerColumn TC_STATUS() {
		return new TablePagerColumn("status", $m("ActivityMgrPage.6"), 70).setTextAlign(
				ETextAlign.left).setPropertyClass(EActivityStatus.class);
	}
}
