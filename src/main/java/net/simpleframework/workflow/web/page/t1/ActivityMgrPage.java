package net.simpleframework.workflow.web.page.t1;

import static net.simpleframework.common.I18n.$m;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.ado.query.ListDataQuery;
import net.simpleframework.common.Convert;
import net.simpleframework.common.DateUtils;
import net.simpleframework.common.ID;
import net.simpleframework.common.NumberUtils;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.mvc.AbstractMVCPage;
import net.simpleframework.mvc.IForward;
import net.simpleframework.mvc.JavascriptForward;
import net.simpleframework.mvc.PageMapping;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.ElementList;
import net.simpleframework.mvc.common.element.InputElement;
import net.simpleframework.mvc.common.element.LinkButton;
import net.simpleframework.mvc.common.element.LinkElement;
import net.simpleframework.mvc.common.element.ProgressElement;
import net.simpleframework.mvc.common.element.SpanElement;
import net.simpleframework.mvc.common.element.TabButton;
import net.simpleframework.mvc.common.element.TabButtons;
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
import net.simpleframework.workflow.schema.UserNode;
import net.simpleframework.workflow.web.WorkflowLogRef.ActivityUpdateLogPage;
import net.simpleframework.workflow.web.WorkflowUtils;
import net.simpleframework.workflow.web.component.abort.ActivityAbortBean;

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
		addTablePagerBean(pp);

		// 放弃
		addAjaxRequest(pp, "ActivityMgrPage_abort_page", ActivityAbortPage.class);
		addWindowBean(pp, "ActivityMgrPage_abort").setResizable(false)
				.setContentRef("ActivityMgrPage_abort_page").setTitle(EProcessStatus.abort.toString())
				.setWidth(420).setHeight(240);

		addComponentBean(pp, "ActivityMgrPage_abort2", ActivityAbortBean.class);

		// workitems
		addAjaxRequest(pp, "ActivityMgrPage_workitems_page", WorkitemsMgrPage.class);
		addWindowBean(pp, "ActivityMgrPage_workitems")
				.setContentRef("ActivityMgrPage_workitems_page").setWidth(800).setHeight(480);
	}

	protected TablePagerBean addTablePagerBean(final PageParameter pp) {
		final TablePagerBean tablePager = (TablePagerBean) addTablePagerBean(pp,
				"ActivityMgrPage_tbl").setPagerBarLayout(EPagerBarLayout.none)
				.setContainerId("idActivityMgrPage_tbl").setHandlerClass(ActivityTbl.class);
		tablePager.addColumn(TC_TASKNODE())
				.addColumn(TC_STATUS().setPropertyClass(EActivityStatus.class))
				.addColumn(TC_PARTICIPANTS()).addColumn(TC_PARTICIPANTS2()).addColumn(TC_CREATEDATE())
				.addColumn(TC_TIMEOUT()).addColumn(TC_COMPLETEDATE()).addColumn(TC_PREVIOUS())
				.addColumn(TablePagerColumn.OPE().setWidth(70));
		return tablePager;
	}

	@Override
	public ElementList getLeftElements(final PageParameter pp) {
		final ProcessBean process = getProcessBean(pp);
		return ElementList.of(
				createBackButton()
						.setHref(url(ProcessMgrPage.class, "modelId=" + process.getModelId())),
				SpanElement.SPACE,
				LinkButton.of("放弃...").setOnclick(
						"$Actions['ActivityMgrPage_abort2']('processId=" + process.getId() + "');"),
				SpanElement.SPACE15, SpanElement.strongText(WorkflowUtils.getProcessTitle(process)));
	}

	@Override
	public ElementList getRightElements(final PageParameter pp) {
		final ProcessBean process = getProcessBean(pp);
		final Object id = process.getId();
		return ElementList.of(createTabsElement(pp, TabButtons.of(
				new TabButton("列表模式", url(ActivityMgrPage.class, "processId=" + id)), new TabButton(
						"图形模式", url(ActivityGraphMgrPage.class, "processId=" + id)))));
	}

	@Override
	public NavigationButtons getNavigationBar(final PageParameter pp) {
		return super.getNavigationBar(pp).append(new SpanElement($m("ActivityMgrPage.0")));
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
			final List<ActivityBean> list = toTreeList(aService.getActivities(process));
			setRelativeDate(cp, list);
			return new ListDataQuery<ActivityBean>(list);
		}

		protected List<ActivityBean> toTreeList(final List<ActivityBean> list) {
			ActivityBean root = null;
			final Map<ID, List<ActivityBean>> cache = new LinkedHashMap<ID, List<ActivityBean>>();
			for (final ActivityBean activity : list) {
				final ID preId = activity.getPreviousId();
				if (preId == null) {
					root = activity;
					root.setAttr("_margin", 0);
				} else {
					ActivityBean pre = null;
					for (final ActivityBean _pre : list) {
						if (preId.equals(_pre.getId())) {
							pre = _pre;
							break;
						}
					}
					List<ActivityBean> _l = cache.get(preId);
					if (_l == null) {
						cache.put(preId, _l = new ArrayList<ActivityBean>());
					}
					_l.add(activity);
					activity.setAttr("_margin",
							(pre != null ? Convert.toInt(pre.getAttr("_margin")) : 0) + 1);
				}
			}
			final List<ActivityBean> l = new ArrayList<ActivityBean>();
			if (root != null) {
				l.add(root);
			}
			for (final List<ActivityBean> _l : cache.values()) {
				l.addAll(_l);
			}
			return l;
		}

		protected void setRelativeDate(final ComponentParameter cp, final List<ActivityBean> list) {
			long max = 0;
			for (final ActivityBean activity : list) {
				Date completeDate = activity.getCompleteDate();
				if (completeDate == null) {
					completeDate = new Date();
				}
				max = Math.max(max, completeDate.getTime() - activity.getCreateDate().getTime());
			}
			cp.setRequestAttr("relative_date", max);
		}

		protected Object toTasknode(final ActivityBean activity) {
			final AbstractTaskNode tasknode = aService.getTaskNode(activity);
			if (tasknode instanceof UserNode) {
				if (((UserNode) tasknode).isEmpty()) {
					return new SpanElement(activity).setStyle("color: #999;");
				} else {
					return createUserNodeLE(activity);
				}
			}
			return new SpanElement(activity).setStyle("color: #808;");
		}

		protected LinkElement createUserNodeLE(final ActivityBean activity) {
			return new LinkElement(activity)
					.setOnclick("$Actions['ActivityMgrPage_workitems']('activityId=" + activity.getId()
							+ "');");
		}

		@Override
		protected Map<String, Object> getRowData(final ComponentParameter cp, final Object dataObject) {
			final ActivityBean activity = (ActivityBean) dataObject;
			final KVMap row = new KVMap();
			String space = "";
			for (int i = 0; i < Convert.toInt(activity.getAttr("_margin")); i++) {
				space += i == 0 ? "| -- " : " -- ";
			}
			row.add("tasknode", space + toTasknode(activity));
			final ActivityBean pre = aService.getPreActivity(activity);
			if (pre != null) {
				final EActivityStatus pstatus = pre.getStatus();
				row.add("previous", WorkflowUtils.toStatusHTML(cp, pstatus, toTasknode(pre)));
			}
			row.add("participants", WorkflowUtils.getParticipants(activity, false));
			row.add("participants2", WorkflowUtils.getParticipants(activity, true));

			final Date createDate = activity.getCreateDate();
			row.add("createDate", createDate);
			Date completeDate = activity.getCompleteDate();
			row.add("completeDate", completeDate);
			final Long l = (Long) cp.getRequestAttr("relative_date");
			if (l != null) {
				if (completeDate == null) {
					completeDate = new Date();
				}
				final long l0 = (completeDate.getTime() - createDate.getTime());
				row.add("relativeDate",
						new ProgressElement((double) l0 / l).setText(DateUtils.toDifferenceDate(l0)));
			}
			Date timeoutDate;
			if ((timeoutDate = activity.getTimeoutDate()) != null) {
				final int d = Double.valueOf(
						(timeoutDate.getTime() - (completeDate != null ? completeDate.getTime() : System
								.currentTimeMillis())) / (1000 * 60)).intValue();
				row.add("timeoutDate", new SpanElement(NumberUtils.format(d / 60.0, "0.#"))
						.setColor(d > 0 ? "green" : "red"));
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
			final EActivityStatus op = cp.getEnumParameter(EActivityStatus.class, "op");
			for (final String aId : StringUtils.split(cp.getParameter("activityId"), ";")) {
				final ActivityBean activity = aService.getBean(aId);
				setLogDescription(cp, activity);
				if (op == EActivityStatus.suspended) {
					aService.doSuspend(activity);
				} else if (op == EActivityStatus.running) {
					aService.doResume(activity);
				}
			}
			return super.onSave(cp).append("$Actions['ActivityMgrPage_tbl']();");
		}
	}

	public static class ActivityAbortPage extends AbstractAbortPage {

		public IForward doOk(final ComponentParameter cp) {
			final ActivityBean activity = aService.getBean(cp.getParameter("activityId"));
			aService.doAbort(activity,
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

	static ProcessBean getProcessBean(final PageParameter pp) {
		return getCacheBean(pp, pService, "processId");
	}

	static TablePagerColumn TC_TASKNODE() {
		return new TablePagerColumn("tasknode", $m("ActivityMgrPage.1")).setFilterSort(false);
	}

	static TablePagerColumn TC_PREVIOUS() {
		return new TablePagerColumn("previous", $m("ActivityMgrPage.2"), 115).setFilterSort(false);
	}

	static TablePagerColumn TC_PARTICIPANTS() {
		return new TablePagerColumn("participants", $m("ActivityMgrPage.3"), 115).setNowrap(false)
				.setFilterSort(false);
	}

	static TablePagerColumn TC_PARTICIPANTS2() {
		return new TablePagerColumn("participants2", $m("ActivityMgrPage.4"), 115).setNowrap(false)
				.setFilterSort(false);
	}

	static TablePagerColumn TC_TIMEOUT() {
		return new TablePagerColumn("timeoutDate", $m("ActivityMgrPage.5"), 105).setPropertyClass(
				Date.class).setFilter(false);
	}

	static TablePagerColumn TC_RELATIVEDATE() {
		return new TablePagerColumn("relativeDate", $m("ActivityMgrPage.6"), 70).setFilter(false);
	}
}
