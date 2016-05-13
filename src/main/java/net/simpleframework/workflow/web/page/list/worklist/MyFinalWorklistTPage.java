package net.simpleframework.workflow.web.page.list.worklist;

import static net.simpleframework.common.I18n.$m;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.simpleframework.ado.EFilterRelation;
import net.simpleframework.ado.FilterItem;
import net.simpleframework.ado.FilterItems;
import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.ado.query.ListDataQuery;
import net.simpleframework.common.Convert;
import net.simpleframework.common.ID;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.TimePeriod;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.ctx.trans.Transaction;
import net.simpleframework.mvc.IForward;
import net.simpleframework.mvc.JavascriptForward;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.ButtonElement;
import net.simpleframework.mvc.common.element.ETextAlign;
import net.simpleframework.mvc.common.element.ElementList;
import net.simpleframework.mvc.common.element.ImageElement;
import net.simpleframework.mvc.common.element.LinkButton;
import net.simpleframework.mvc.common.element.LinkElement;
import net.simpleframework.mvc.common.element.SpanElement;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.base.ajaxrequest.AjaxRequestBean;
import net.simpleframework.mvc.component.ui.menu.MenuBean;
import net.simpleframework.mvc.component.ui.menu.MenuItem;
import net.simpleframework.mvc.component.ui.menu.MenuItems;
import net.simpleframework.mvc.component.ui.pager.TablePagerBean;
import net.simpleframework.mvc.component.ui.pager.TablePagerColumn;
import net.simpleframework.mvc.template.AbstractTemplatePage;
import net.simpleframework.workflow.engine.EActivityStatus;
import net.simpleframework.workflow.engine.EWorkitemStatus;
import net.simpleframework.workflow.engine.IWorkflowContext;
import net.simpleframework.workflow.engine.bean.ActivityBean;
import net.simpleframework.workflow.engine.bean.WorkitemBean;
import net.simpleframework.workflow.schema.AbstractTaskNode;
import net.simpleframework.workflow.web.WorkflowUtils;
import net.simpleframework.workflow.web.page.list.AbstractItemsTPage;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class MyFinalWorklistTPage extends MyRunningWorklistTPage {

	@Override
	protected void addComponents(final PageParameter pp) {
		// 取回
		addAjaxRequest(pp, "MyWorklistTPage_retake").setHandlerMethod("doRetake").setConfirmMessage(
				$m("MyFinalWorklistTPage.2"));

		// 发送用户列表
		final AjaxRequestBean ajaxRequest = addAjaxRequest(pp, "MyFinalWorklistTPage_userTo_page",
				UserToPage.class);
		addWindowBean(pp, "MyFinalWorklistTPage_userTo", ajaxRequest).setWidth(360).setHeight(450)
				.setTitle($m("MyFinalWorklistTPage.5"));

		final MenuBean mb = createGroupMenuComponent(pp);
		addGroupMenuItems(pp, mb, getWorklistPageUrl(pp));
	}

	@Override
	protected String getWorklistPageUrl(final PageParameter pp) {
		return uFactory.getUrl(pp, MyFinalWorklistTPage.class);
	}

	@Override
	protected void setGroupParam(final PageParameter pp) {
		String g = pp.getParameter("g");
		if (g == null) {
			g = pp.getCookie("group_worklist_final");
		}
		if ("modelname".equals(g) || "taskname".equals(g) || "none".equals(g)) {
			pp.putParameter("g", g);
			pp.addCookie("group_worklist_final", g, 365 * 60 * 60 * 24);
		} else {
			pp.putParameter("g", "taskname");
		}
	}

	@Override
	protected TablePagerBean addTablePagerBean(final PageParameter pp) {
		final TablePagerBean tablePager = addTablePagerBean(pp, "MyWorklistTPage_tbl",
				MyCompleteWorklistTbl.class);
		tablePager
				.addColumn(TC_ICON())
				.addColumn(TC_TITLE())
				.addColumn(TC_PNO())
				.addColumn(
						TC_USER("userTo", $m("MyFinalWorklistTPage.0")).setTextAlign(ETextAlign.left)
								.setWidth(130).setNowrap(true))
				.addColumn(
						TablePagerColumn.DATE("completeDate", $m("MyFinalWorklistTPage.1")).setWidth(60)
								.setFilterSort(false)).addColumn(TC_PSTAT())
				.addColumn(TablePagerColumn.OPE(70));
		return tablePager;
	}

	@Transaction(context = IWorkflowContext.class)
	public IForward doRetake(final ComponentParameter cp) {
		wfwService.doRetake(WorkflowUtils.getWorkitemBean(cp));
		return new JavascriptForward("$Actions.loc('").append(
				uFactory.getUrl(cp, MyRunningWorklistTPage.class)).append("');");
	}

	@Override
	public ElementList getLeftElements(final PageParameter pp) {
		final ElementList el = ElementList.of(LinkButton.menu($m("MyRunningWorklistTbl.14")).setId(
				"idMyWorklistTPage_groupMenu"));
		el.add(SpanElement.SPACE);
		el.add(LinkButton.of($m("MyRunningWorklistTbl.15")).setOnclick(
				"$Actions.reloc('retake=true');"));
		el.add(SpanElement.SPACE);
		el.add(LinkButton.of($m("MyRunningWorklistTbl.22")).setOnclick(
				"$Actions.reloc('delegation=true');"));
		return el;
	}

	public static class MyCompleteWorklistTbl extends MyRunningWorklistTbl {
		@Override
		public IDataQuery<?> createDataObjectQuery(final ComponentParameter cp) {
			if (cp.getBoolParameter("delegation")) {
				cp.addFormParameter("delegation", true);

				return wfwService.getWorklist(cp.getLoginId(), getModels(cp),
						FilterItems.of(new FilterItem("userId", EFilterRelation.not_equal, "@userId2")),
						EWorkitemStatus.complete, EWorkitemStatus.abort);
			}

			if (cp.getBoolParameter("retake")) {
				cp.addFormParameter("retake", true);

				final IDataQuery<?> dq = wfwService.getWorklist(cp.getLoginId(), getModels(cp),
						FilterItems.of().addEqual("createdate", TimePeriod.week),
						EWorkitemStatus.complete);
				final List<WorkitemBean> list = new ArrayList<WorkitemBean>();
				WorkitemBean workitem;
				l: while ((workitem = (WorkitemBean) dq.next()) != null) {
					final ActivityBean activity = wfaService.getBean(workitem.getActivityId());
					for (final WorkitemBean workitem2 : wfwService.getNextWorkitems(activity)) {
						if (workitem2.isReadMark() || wfwService.isFinalStatus(workitem2)) {
							continue l;
						}
					}
					list.add(workitem);
				}
				return new ListDataQuery<WorkitemBean>(list);
			}
			return wfwService.getWorklist(cp.getLoginId(), getModels(cp), EWorkitemStatus.complete,
					EWorkitemStatus.abort);
		}

		@Override
		protected ImageElement createImageMark(final ComponentParameter cp,
				final WorkitemBean workitem) {
			ImageElement img = null;
			if (workitem.getStatus() == EWorkitemStatus.abort) {
				img = AbstractItemsTPage._createImageMark(cp, "status_abort.png").setTitle(
						$m("MyRunningWorklistTbl.24"));
			} else if (workitem.getRetakeId() != null) {
				img = MARK_RETAKE(cp);
			} else if (!workitem.getUserId().equals(workitem.getUserId2())) {
				img = MARK_DELEGATE(cp, workitem);
			} else if (workitem.isTopMark()) {
				img = MARK_TOP(cp);
			}
			return img;
		}

		@Override
		protected void doRowData(final ComponentParameter cp, final KVMap row,
				final WorkitemBean workitem) {
			final ActivityBean activity = WorkflowUtils.getActivityBean(cp, workitem);
			row.add("userTo", getUserTo(activity));

			final Date completeDate = workitem.getCompleteDate();
			if (completeDate != null) {
				row.add("completeDate", new SpanElement(Convert.toDateString(completeDate, "yy-MM-dd"))
						.setTitle(Convert.toDateTimeString(completeDate)));
			}
		}

		String getUserTo(final ActivityBean activity) {
			final ID activityId = activity.getId();
			return activity.getAttrCache("to_" + activityId, new CacheV<String>() {
				@Override
				public String get() {
					boolean more = false;
					final Set<String> list = new LinkedHashSet<String>();
					lbl: {
						for (final ActivityBean nextActivity : wfaService.getNextActivities(activity)) {
							final AbstractTaskNode tasknode = wfaService.getTaskNode(nextActivity);
							final EActivityStatus status = nextActivity.getStatus();
							for (final WorkitemBean workitem : wfwService.getWorkitems(nextActivity)) {
								if (list.size() > 1) {
									more = true;
									break lbl;
								}
								final StringBuilder sb = new StringBuilder();
								final SpanElement ele = (wfaService.isFinalStatus(nextActivity) ? SpanElement
										.color777(tasknode) : SpanElement.color333(tasknode)).setTitle(status
										.toString());
								final String utxt = workitem.getUserText();
								sb.append("[").append(ele).append("] ")
										.append(SpanElement.color060(utxt).setTitle(utxt));
								list.add(sb.toString());
							}
						}
					}
					if (more) {
						list.add(LinkElement
								.style2("...")
								.setOnclick(
										"$Actions['MyFinalWorklistTPage_userTo']('activityId=" + activityId
												+ "');").toString());
					}
					return list.size() > 0 ? StringUtils.join(list, "<br>") : null;
				}
			});
		}

		@Override
		public MenuItems getContextMenu(final ComponentParameter cp, final MenuBean menuBean,
				final MenuItem menuItem) {
			final MenuItems items = MenuItems.of();
			items.append(MENU_MONITOR(cp));
			items.append(MenuItem.sep());
			items.append(MenuItem.of($m("MyFinalWorklistTPage.3")).setIconClass("menu_retake")
					.setOnclick_act("MyWorklistTPage_retake", "workitemId"));
			items.append(MenuItem.sep());
			final MenuItem mItems = MenuItem.of($m("MyRunningWorklistTbl.6"));
			mItems.addChild(
					MENU_MARK_TOP().setOnclick_act("MyWorklistTPage_topMark", "workitemId", "op=top"))
					.addChild(
							MENU_MARK_UNTOP().setOnclick_act("MyWorklistTPage_topMark", "workitemId",
									"op=untop"));
			items.append(mItems);
			return items;
		}
	}

	public static class UserToPage extends AbstractTemplatePage {

		@Override
		protected String toHtml(final PageParameter pp, final Map<String, Object> variables,
				final String currentVariable) throws IOException {
			final StringBuilder sb = new StringBuilder();
			sb.append("<div class='UserToPage'>");
			final ActivityBean activity = wfaService.getBean(pp.getParameter("activityId"));
			if (activity != null) {
				for (final ActivityBean nextActivity : wfaService.getNextActivities(activity)) {
					final AbstractTaskNode tasknode = wfaService.getTaskNode(nextActivity);
					final EActivityStatus status = nextActivity.getStatus();
					for (final WorkitemBean workitem : wfwService.getWorkitems(nextActivity)) {
						sb.append("<div class='iitem clearfix'>");
						sb.append(" <div class='left'>").append(workitem.getUserText()).append("</div>");
						sb.append(" <div class='right'>")
								.append(SpanElement.color777(tasknode + " [" + status + "]"))
								.append("</div>");
						sb.append("</div>");
					}
				}
			}
			sb.append(" <div class='bb'>").append(ButtonElement.closeBtn()).append(" </div>");
			sb.append("</div>");
			return sb.toString();
		}
	}
}
