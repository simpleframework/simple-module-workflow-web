package net.simpleframework.workflow.web.page;

import static net.simpleframework.common.I18n.$m;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import net.simpleframework.ado.EFilterRelation;
import net.simpleframework.ado.FilterItem;
import net.simpleframework.ado.FilterItems;
import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.common.Convert;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.ctx.trans.Transaction;
import net.simpleframework.mvc.IForward;
import net.simpleframework.mvc.JavascriptForward;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.ETextAlign;
import net.simpleframework.mvc.common.element.ElementList;
import net.simpleframework.mvc.common.element.ImageElement;
import net.simpleframework.mvc.common.element.JS;
import net.simpleframework.mvc.common.element.LinkButton;
import net.simpleframework.mvc.common.element.SpanElement;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ui.menu.MenuBean;
import net.simpleframework.mvc.component.ui.menu.MenuItem;
import net.simpleframework.mvc.component.ui.menu.MenuItems;
import net.simpleframework.mvc.component.ui.pager.TablePagerBean;
import net.simpleframework.mvc.component.ui.pager.TablePagerColumn;
import net.simpleframework.workflow.engine.EWorkitemStatus;
import net.simpleframework.workflow.engine.IWorkflowContext;
import net.simpleframework.workflow.engine.bean.ActivityBean;
import net.simpleframework.workflow.engine.bean.WorkitemBean;
import net.simpleframework.workflow.web.WorkflowUtils;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class MyFinalWorklistTPage extends MyRunningWorklistTPage {

	@Override
	protected void addComponents(final PageParameter pp) {
		addTablePagerBean(pp);
		// 取回
		addAjaxRequest(pp, "MyWorklistTPage_retake").setHandlerMethod("doRetake").setConfirmMessage(
				$m("MyFinalWorklistTPage.2"));

		// 标记置顶
		addAjaxRequest(pp, "MyWorklistTPage_topMark").setHandlerMethod("doTopMark");

		final String url = getWorklistPageUrl(pp);
		final MenuBean mb = createViewMenuComponent(pp);
		mb.addItem(MyRunningWorklistTbl.MENU_VIEW_ALL().setOnclick(JS.loc(url))).addItem(
				MenuItem.sep());
		final MenuItem item = MyRunningWorklistTbl.MENU_VIEW_DELEGATION().setOnclick(
				"$Actions.reloc('delegation=true');");
		if (pp.getBoolParameter("delegation")) {
			item.setIconClass(MenuItem.ICON_SELECTED);
		}
		mb.addItem(item).addItem(MenuItem.sep());
		addGroupMenuItems(pp, mb, url);
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
								.setWidth(130))
				.addColumn(
						TablePagerColumn.DATE("completeDate", $m("MyFinalWorklistTPage.1")).setWidth(60)
								.setFilterSort(false)).addColumn(TC_PSTAT())
				.addColumn(TablePagerColumn.OPE(70));
		return tablePager;
	}

	@Transaction(context = IWorkflowContext.class)
	public IForward doRetake(final ComponentParameter cp) {
		wfwService.doRetake(WorkflowUtils.getWorkitemBean(cp));
		return new JavascriptForward("$Actions['MyWorklistTPage_tbl']();");
	}

	@Override
	public ElementList getLeftElements(final PageParameter pp) {
		return ElementList.of(LinkButton.menu($m("MyRunningWorklistTbl.14")).setId(
				"idMyWorklistTPage_viewMenu"));
	}

	public static class MyCompleteWorklistTbl extends MyRunningWorklistTbl {
		@Override
		public IDataQuery<?> createDataObjectQuery(final ComponentParameter cp) {
			FilterItems items = null;
			if (cp.getBoolParameter("delegation")) {
				items = FilterItems.of(new FilterItem("userId", EFilterRelation.not_equal, "@userId2"));
			}
			return wfwService.getWorklist(cp.getLoginId(), items, EWorkitemStatus.complete,
					EWorkitemStatus.abort);
		}

		@Override
		protected ImageElement createImageMark(final ComponentParameter cp,
				final WorkitemBean workitem) {
			ImageElement img = null;
			if (workitem.getRetakeId() != null) {
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
			row.add("userTo", SpanElement.color060(getUserTo(activity)));

			final Date completeDate = workitem.getCompleteDate();
			if (completeDate != null) {
				row.add("completeDate", new SpanElement(Convert.toDateString(completeDate, "yy-MM-dd"))
						.setTitle(Convert.toDateString(completeDate)));
			}
		}

		String getUserTo(final ActivityBean activity) {
			return activity.getAttrCache("to_" + activity.getId(), new CacheV<String>() {
				@Override
				public String get() {
					final Set<String> list = new LinkedHashSet<String>();
					for (final ActivityBean nextActivity : wfaService.getNextActivities(activity)) {
						final String tasknode = wfaService.getTaskNode(nextActivity).toString();
						for (final WorkitemBean workitem : wfwService.getWorkitems(nextActivity)) {
							list.add(new SpanElement("[" + tasknode + "] " + workitem.getUserText())
									.setTitle(tasknode).toString());
						}
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
			items.append(MenuItem.sep()).append(MENU_LOG());
			return items;
		}
	}
}
