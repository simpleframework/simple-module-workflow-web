package net.simpleframework.workflow.web.page;

import static net.simpleframework.common.I18n.$m;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.ado.query.ListDataQuery;
import net.simpleframework.common.ID;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.ctx.trans.Transaction;
import net.simpleframework.mvc.AbstractMVCPage;
import net.simpleframework.mvc.IForward;
import net.simpleframework.mvc.JavascriptForward;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.ButtonElement;
import net.simpleframework.mvc.common.element.EVerticalAlign;
import net.simpleframework.mvc.common.element.ElementList;
import net.simpleframework.mvc.common.element.ImageElement;
import net.simpleframework.mvc.common.element.LabelElement;
import net.simpleframework.mvc.common.element.LinkButton;
import net.simpleframework.mvc.common.element.LinkElement;
import net.simpleframework.mvc.common.element.PhotoImage;
import net.simpleframework.mvc.common.element.SpanElement;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ui.menu.EMenuEvent;
import net.simpleframework.mvc.component.ui.menu.MenuBean;
import net.simpleframework.mvc.component.ui.menu.MenuItem;
import net.simpleframework.mvc.component.ui.menu.MenuItems;
import net.simpleframework.mvc.component.ui.pager.AbstractTablePagerSchema;
import net.simpleframework.mvc.component.ui.pager.GroupWrapper;
import net.simpleframework.mvc.component.ui.pager.TablePagerBean;
import net.simpleframework.mvc.component.ui.pager.TablePagerColumn;
import net.simpleframework.mvc.component.ui.pager.db.GroupDbTablePagerHandler;
import net.simpleframework.workflow.engine.ActivityBean;
import net.simpleframework.workflow.engine.DelegationBean;
import net.simpleframework.workflow.engine.EDelegationStatus;
import net.simpleframework.workflow.engine.EWorkitemStatus;
import net.simpleframework.workflow.engine.IWorkflowContext;
import net.simpleframework.workflow.engine.ProcessModelBean;
import net.simpleframework.workflow.engine.WorkitemBean;
import net.simpleframework.workflow.web.IWorkflowWebContext;
import net.simpleframework.workflow.web.WorkflowLogRef.WorkitemUpdateLogPage;
import net.simpleframework.workflow.web.WorkflowUrlsFactory;
import net.simpleframework.workflow.web.page.AbstractDelegateFormPage.WorkitemDelegateReceivingPage;
import net.simpleframework.workflow.web.page.AbstractDelegateFormPage.WorkitemDelegateSetPage;
import net.simpleframework.workflow.web.page.t1.WorkflowFormPage;
import net.simpleframework.workflow.web.page.t1.WorkflowMonitorPage;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class MyWorklistTPage extends AbstractWorkitemsTPage {

	@Override
	protected void onForward(final PageParameter pp) {
		super.onForward(pp);

		final TablePagerBean tablePager = (TablePagerBean) addTablePagerBean(pp,
				"MyWorklistTPage_tbl", MyWorklistTbl.class).setShowFilterBar(false)
				.setShowLineNo(false).setPageItems(50);
		tablePager.addColumn(TablePagerColumn.ICON().setWidth(18));

		final EWorkitemStatus status = pp.getEnumParameter(EWorkitemStatus.class, "status");
		tablePager.addColumn(TITLE());
		if (status == EWorkitemStatus.complete) {
			tablePager.addColumn(new TablePagerColumn("userTo", $m("MyWorklistTPage.0"), 115)
					.setSort(false));
			tablePager.addColumn(new TablePagerColumn("completeDate", $m("MyWorklistTPage.1"), 115)
					.setPropertyClass(Date.class));
		} else {
			tablePager.addColumn(new TablePagerColumn("userFrom", $m("MyWorklistTPage.2"), 115)
					.setSort(false));
			tablePager.addColumn(new TablePagerColumn("createDate", $m("MyWorklistTPage.3"), 115)
					.setPropertyClass(Date.class));
		}
		tablePager.addColumn(TablePagerColumn.OPE().setWidth(70));

		// 取回
		addAjaxRequest(pp, "MyWorklistTPage_retake").setHandlerMethod("doRetake").setConfirmMessage(
				$m("MyWorklistTPage.4"));
		// 回退
		addAjaxRequest(pp, "MyWorklistTPage_fallback").setHandlerMethod("doFallback")
				.setConfirmMessage($m("MyWorklistTPage.5"));

		// 删除
		addAjaxRequest(pp, "MyWorklistTPage_delete").setHandlerMethod("doDelete").setConfirmMessage(
				$m("Confirm.Delete"));

		// 委托设置
		addAjaxRequest(pp, "MyWorklistTPage_delegate_page", WorkitemDelegateSetPage.class);
		addWindowBean(pp, "MyWorklistTPage_delegate").setContentRef("MyWorklistTPage_delegate_page")
				.setTitle($m("MyWorklistTPage.6")).setHeight(300).setWidth(500);

		// 委托确认
		addAjaxRequest(pp, "MyWorklistTPage_delegate_receiving_page",
				WorkitemDelegateReceivingPage.class);
		addWindowBean(pp, "MyWorklistTPage_delegate_receiving")
				.setContentRef("MyWorklistTPage_delegate_receiving_page")
				.setTitle($m("MyWorklistTPage.7")).setHeight(400).setWidth(500);

		// 标记菜单
		createMarkMenuComponent(pp);
		// 标记已读
		addAjaxRequest(pp, "MyWorklistTPage_readMark").setHandlerMethod("doReadMark");
	}

	@Override
	protected Class<? extends AbstractMVCPage> getUpdateLogPage() {
		return WorkitemUpdateLogPage.class;
	}

	protected void createMarkMenuComponent(final PageParameter pp) {
		// 标记菜单
		final MenuBean mb = (MenuBean) addComponentBean(pp, "MyWorklistTPage_markMenu",
				MenuBean.class).setMenuEvent(EMenuEvent.click).setSelector(
				"#idMyWorklistTPage_markMenu");
		mb.addItem(MENU_MARK_READ()).addItem(MENU_MARK_UNREAD()).addItem(MENU_MARK_ALLREAD());
		mb.addItem(MenuItem.sep()).addItem(MENU_MARK_TOP()).addItem(MENU_MARK_UNTOP());
	}

	@Override
	public ElementList getLeftElements(final PageParameter pp) {
		return ElementList.of(LinkButton.menu($m("MyWorklistTPage.8")).setId(
				"idMyWorklistTPage_markMenu"));
	}

	@Transaction(context = IWorkflowContext.class)
	public IForward doReadMark(final ComponentParameter cp) {
		final String op = cp.getParameter("op");
		if ("allread".equals(op)) {
		} else {
			final Object[] ids = StringUtils.split(cp.getParameter("workitemId"));
			if (ids != null) {
				for (final Object id : ids) {
					final WorkitemBean workitem = wService.getBean(id);
					wService.doReadMark(workitem, "unread".equals(op));
				}
			}
		}
		return new JavascriptForward("$Actions['MyWorklistTPage_tbl']();");
	}

	@Transaction(context = IWorkflowContext.class)
	public IForward doRetake(final ComponentParameter cp) {
		wService.retake(WorkflowUtils.getWorkitemBean(cp));
		return new JavascriptForward("$Actions['MyWorklistTPage_tbl']();");
	}

	@Transaction(context = IWorkflowContext.class)
	public IForward doFallback(final ComponentParameter cp) {
		aService.fallback(wService.getActivity(WorkflowUtils.getWorkitemBean(cp)));
		return new JavascriptForward("$Actions['MyWorklistTPage_tbl']();");
	}

	@Transaction(context = IWorkflowContext.class)
	public IForward doDelete(final ComponentParameter cp) {
		wService.deleteProcess(WorkflowUtils.getWorkitemBean(cp));
		return new JavascriptForward("$Actions['MyWorklistTPage_tbl']();");
	}

	public static class MyWorklistTbl extends GroupDbTablePagerHandler {
		@Override
		public IDataQuery<?> createDataObjectQuery(final ComponentParameter cp) {
			final EWorkitemStatus status = cp.getEnumParameter(EWorkitemStatus.class, "status");
			final ID userId = cp.getLoginId();
			List<WorkitemBean> list;
			if (status != null) {
				cp.addFormParameter("status", status.name());
				list = wService.getWorklist(userId, status);
			} else {
				list = wService.getRunningWorklist(userId);
			}
			return new ListDataQuery<WorkitemBean>(list);
		}

		@Override
		public Object getGroupValue(final ComponentParameter cp, final Object bean,
				final String groupColumn) {
			final boolean bModelname = "modelname".equals(groupColumn);
			if (bModelname || "taskname".equals(groupColumn)) {
				final ActivityBean activity = wService.getActivity((WorkitemBean) bean);
				final ProcessModelBean processModel = pService.getProcessModel(aService
						.getProcessBean(activity));
				if (bModelname) {
					return processModel;
				} else {
					return activity.setAttr("_processModel", processModel);
				}
			}
			return groupColumn;
		}

		@Override
		public GroupWrapper getGroupWrapper(final ComponentParameter cp, final Object groupVal) {
			if (groupVal instanceof ActivityBean) {
				return new GroupWrapper() {
					@Override
					public String toString() {
						final ActivityBean activity = (ActivityBean) groupVal;
						final StringBuilder sb = new StringBuilder();
						sb.append(new LabelElement(activity));
						sb.append(new SpanElement("(" + activity.getAttr("_processModel") + ")")
								.addStyle("font-weight: normal; margin-left: 5px; color: #999; font-size: 9pt;"));
						sb.append(toCountHTML());
						return sb.toString();
					}
				};
			}
			return super.getGroupWrapper(cp, groupVal);
		}

		@Override
		protected Map<String, Object> getRowData(final ComponentParameter cp, final Object dataObject) {
			final WorkitemBean workitem = (WorkitemBean) dataObject;
			final KVMap row = new KVMap();

			final ActivityBean activity = wService.getActivity(workitem);
			final EWorkitemStatus status = workitem.getStatus();

			ImageElement img = null;
			if (status == EWorkitemStatus.delegate) {
				img = PhotoImage.icon12(cp.getPhotoUrl(workitem.getUserId2())).setTitle(
						$m("MyWorklistTbl.5", permission.getUser(workitem.getUserId())));
			} else if (!workitem.isReadMark()) {
				img = createImageMark(cp, "mark_unread.png").setTitle($m("MyWorklistTbl.4"));
			}
			if (img != null) {
				row.add(TablePagerColumn.ICON, img);
			}

			final WorkflowUrlsFactory uFactory = ((IWorkflowWebContext) context).getUrlsFactory();

			final StringBuilder title = new StringBuilder();
			if (!"taskname".equals(cp.getParameter(G))) {
				title.append("[").append(new SpanElement(activity).setClassName("tasknode_txt"))
						.append("] ");
			}
			DelegationBean delegation = null;
			if (status == EWorkitemStatus.delegate) {
				delegation = dService.queryRunningDelegation(workitem);
			}
			final boolean receiving = delegation != null
					&& delegation.getStatus() == EDelegationStatus.receiving;
			if (receiving) {
				title.append(new SpanElement(WorkflowUtils.getTitle(aService.getProcessBean(activity))));
			} else {
				title.append(new LinkElement(WorkflowUtils.getTitle(aService.getProcessBean(activity)))
						.setStrong(!workitem.isReadMark()).setOnclick(
								"$Actions.loc('" + uFactory.getUrl(cp, WorkflowFormPage.class, workitem)
										+ "');"));
			}
			row.add("title", title.toString()).add("userFrom", getUserFrom(activity))
					.add("userTo", getUserTo(activity)).add("createDate", workitem.getCreateDate())
					.add("completeDate", workitem.getCompleteDate());

			final StringBuilder ope = new StringBuilder();
			if (receiving) {
				ope.append(new ButtonElement(EDelegationStatus.receiving).setHighlight(true)
						.setOnclick(
								"$Actions['MyWorklistTPage_delegate_receiving']('workitemId="
										+ workitem.getId() + "');"));
			} else {
				ope.append(new ButtonElement($m("MyWorklistTbl.0")).setOnclick("$Actions.loc('"
						+ uFactory.getUrl(cp, WorkflowMonitorPage.class, workitem) + "');"));
			}
			ope.append(SpanElement.SPACE).append(AbstractTablePagerSchema.IMG_DOWNMENU);
			row.put(TablePagerColumn.OPE, ope.toString());
			return row;
		}

		@Override
		public MenuItems getContextMenu(final ComponentParameter cp, final MenuBean menuBean,
				final MenuItem menuItem) {
			final MenuItems items = MenuItems.of();
			items.append(MenuItem.of($m("MyWorklistTbl.6")).setOnclick(
					"$Actions.loc('"
							+ ((IWorkflowWebContext) context).getUrlsFactory().getUrl(cp,
									WorkflowMonitorPage.class)
							+ "?workitemId=' + $pager_action(item).rowId());"));
			items.append(MenuItem.sep());
			final EWorkitemStatus status = cp.getEnumParameter(EWorkitemStatus.class, "status");
			if (status == EWorkitemStatus.complete) {
				items.append(MenuItem.of($m("MyWorklistTbl.1")).setOnclick_act(
						"MyWorklistTPage_retake", "workitemId"));
			} else {
				items.append(MenuItem.of($m("MyWorklistTbl.2")).setOnclick_act(
						"MyWorklistTPage_fallback", "workitemId"));
				items.append(MenuItem.of($m("MyWorklistTbl.3")).setOnclick_act(
						"MyWorklistTPage_delegate", "workitemId"));
				items.append(MenuItem.sep());
				final MenuItem mItems = MenuItem.of($m("MyWorklistTPage.8"));
				mItems.addChild(
						MyWorklistTPage.MENU_MARK_READ().setOnclick_act("MyWorklistTPage_readMark",
								"workitemId", "op=read"))
						.addChild(
								MyWorklistTPage.MENU_MARK_UNREAD().setOnclick_act(
										"MyWorklistTPage_readMark", "workitemId", "op=unread"))
						.addChild(
								MyWorklistTPage.MENU_MARK_ALLREAD().setOnclick_act(
										"MyWorklistTPage_readMark", "workitemId", "op=allread"));
				mItems.addChild(MenuItem.sep());
				mItems.addChild(MyWorklistTPage.MENU_MARK_TOP()).addChild(
						MyWorklistTPage.MENU_MARK_UNTOP());
				items.append(mItems);
				items.append(MenuItem.sep());
				items.append(MenuItem.itemDelete().setOnclick_act("MyWorklistTPage_delete",
						"workitemId"));
			}
			items.append(MenuItem.sep());
			items.append(MenuItem.of($m("MyWorklistTbl.7")).setOnclick_act(
					"AbstractItemsTPage_update_log", "workitemId"));
			return items;
		}
	}

	private static final Map<String, String> userCache = new ConcurrentHashMap<String, String>();

	static String getUserTo(final ActivityBean activity) {
		if (activity == null) {
			return null;
		}
		final String key = "to_" + activity.getId();
		String userTo = userCache.get(key);
		if (userTo == null) {
			final Set<String> list = new LinkedHashSet<String>();
			for (final ActivityBean nextActivity : aService.getNextActivities(activity)) {
				for (final WorkitemBean workitem : wService.getWorkitems(nextActivity)) {
					list.add(workitem.getUserText());
				}
			}
			if (list.size() > 0) {
				userCache.put(key, userTo = StringUtils.join(list, ", "));
			}
		}
		return userTo;
	}

	static String getUserFrom(final ActivityBean activity) {
		final ActivityBean preActivity = aService.getPreActivity(activity);
		if (preActivity == null) {
			return null;
		}
		final String key = "from_" + preActivity.getId();
		String userFrom = userCache.get(key);
		if (userFrom == null) {
			final Set<String> list = new LinkedHashSet<String>();
			for (final WorkitemBean workitem : wService.getWorkitems(preActivity,
					EWorkitemStatus.complete)) {
				list.add(workitem.getUserText());
			}
			if (list.size() > 0) {
				userCache.put(key, userFrom = StringUtils.join(list, ", "));
			}
		}
		return userFrom;
	}

	static ImageElement createImageMark(final ComponentParameter cp, final String img) {
		return new ImageElement(cp.getCssResourceHomePath(MyWorklistTPage.class) + "/images/" + img)
				.setVerticalAlign(EVerticalAlign.middle);
	}

	static MenuItem MENU_MARK_READ() {
		return MenuItem
				.of($m("MyWorklistTPage.9"))
				.setOnclick(
						"$Actions['MyWorklistTPage_tbl'].doAct('MyWorklistTPage_readMark', 'workitemId' , 'op=read');");
	}

	static MenuItem MENU_MARK_UNREAD() {
		return MenuItem
				.of($m("MyWorklistTPage.10"))
				.setOnclick(
						"$Actions['MyWorklistTPage_tbl'].doAct('MyWorklistTPage_readMark', 'workitemId' , 'op=unread');");
	}

	static MenuItem MENU_MARK_ALLREAD() {
		return MenuItem.of($m("MyWorklistTPage.11")).setOnclick(
				"$Actions['MyWorklistTPage_readMark']('op=allread');");
	}

	static MenuItem MENU_MARK_TOP() {
		return MenuItem.of($m("MyWorklistTPage.12"));
	}

	static MenuItem MENU_MARK_UNTOP() {
		return MenuItem.of($m("MyWorklistTPage.13"));
	}
}