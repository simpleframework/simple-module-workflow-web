package net.simpleframework.workflow.web.page.list.worklist;

import static net.simpleframework.common.I18n.$m;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.common.NumberUtils;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.coll.ArrayUtils;
import net.simpleframework.common.web.JavascriptUtils;
import net.simpleframework.ctx.trans.Transaction;
import net.simpleframework.mvc.IForward;
import net.simpleframework.mvc.JavascriptForward;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.BlockElement;
import net.simpleframework.mvc.common.element.ButtonElement;
import net.simpleframework.mvc.common.element.ETextAlign;
import net.simpleframework.mvc.common.element.ElementList;
import net.simpleframework.mvc.common.element.InputElement;
import net.simpleframework.mvc.common.element.LinkButton;
import net.simpleframework.mvc.common.element.LinkElement;
import net.simpleframework.mvc.common.element.SpanElement;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.base.ajaxrequest.AjaxRequestBean;
import net.simpleframework.mvc.component.base.validation.EValidatorMethod;
import net.simpleframework.mvc.component.base.validation.EWarnType;
import net.simpleframework.mvc.component.base.validation.Validator;
import net.simpleframework.mvc.component.ui.menu.EMenuEvent;
import net.simpleframework.mvc.component.ui.menu.MenuBean;
import net.simpleframework.mvc.component.ui.menu.MenuItem;
import net.simpleframework.mvc.component.ui.pager.TablePagerBean;
import net.simpleframework.mvc.component.ui.pager.TablePagerColumn;
import net.simpleframework.mvc.component.ui.progressbar.ProgressBarRegistry;
import net.simpleframework.mvc.component.ui.tree.AbstractTreeHandler;
import net.simpleframework.mvc.component.ui.tree.TreeBean;
import net.simpleframework.mvc.component.ui.tree.TreeNode;
import net.simpleframework.mvc.component.ui.tree.TreeNodes;
import net.simpleframework.mvc.template.AbstractTemplatePage;
import net.simpleframework.mvc.template.struct.FilterButton;
import net.simpleframework.mvc.template.struct.FilterButtons;
import net.simpleframework.workflow.engine.IWorkflowContext;
import net.simpleframework.workflow.engine.bean.DelegationBean;
import net.simpleframework.workflow.engine.bean.ProcessModelBean;
import net.simpleframework.workflow.engine.bean.UserStatBean;
import net.simpleframework.workflow.engine.bean.WorkitemBean;
import net.simpleframework.workflow.web.WorkflowUtils;
import net.simpleframework.workflow.web.page.list.AbstractItemsTPage;
import net.simpleframework.workflow.web.page.list.delegate.AbstractDelegateFormPage.WorkitemDelegateReceivingPage;
import net.simpleframework.workflow.web.page.list.delegate.AbstractDelegateFormPage.WorkitemDelegateSetPage;
import net.simpleframework.workflow.web.page.list.delegate.UserDelegateListTPage;
import net.simpleframework.workflow.web.page.list.initiate.MyInitiateItemsGroupTPage;
import net.simpleframework.workflow.web.page.list.process.MyProcessWorksTPage;
import net.simpleframework.workflow.web.page.list.stat.MyWorkstatTPage;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class MyRunningWorklistTPage extends AbstractItemsTPage {

	@Override
	protected void onForward(final PageParameter pp) throws Exception {
		super.onForward(pp);
		setGroupParam(pp);

		// 添加表格
		addTablePagerBean(pp);
		// 标记置顶
		addAjaxRequest(pp, "MyWorklistTPage_topMark").setHandlerMethod("doTopMark");

		// 选择流程
		final AjaxRequestBean ajaxRequest = addAjaxRequest(pp, "MyWorklistTPage_pmSelected_page",
				PMSelectedPage.class);
		addWindowBean(pp, "MyWorklistTPage_pmSelected", ajaxRequest).setPopup(true)
				.setDestroyOnClose(true).setHeight(450).setWidth(350)
				.setTitle($m("MyRunningWorklistTPage.19"));

		// 添加其他组件
		addComponents(pp);
	}

	protected void setGroupParam(final PageParameter pp) {
		String g = pp.getParameter("g");
		if (g == null) {
			g = pp.getCookie("group_worklist_running");
		}
		if ("modelname".equals(g) || "taskname".equals(g) || "none".equals(g)) {
			pp.putParameter("g", g);
			pp.addCookie("group_worklist_running", g, 365 * 60 * 60 * 24);
		} else {
			pp.putParameter("g", "modelname");
		}
	}

	protected void addComponents(final PageParameter pp) {
		// 回退
		addAjaxRequest(pp, "MyWorklistTPage_fallback").setHandlerMethod("doFallback")
				.setConfirmMessage($m("MyRunningWorklistTPage.3"));

		// 删除
		addAjaxRequest(pp, "MyWorklistTPage_delete").setHandlerMethod("doDelete").setConfirmMessage(
				$m("Confirm.Delete"));

		// 委托设置
		AjaxRequestBean ajaxRequest = addAjaxRequest(pp, "MyWorklistTPage_delegate_page",
				WorkitemDelegateSetPage.class);
		addWindowBean(pp, "MyWorklistTPage_delegate", ajaxRequest)
				.setTitle($m("MyRunningWorklistTPage.4")).setHeight(300).setWidth(500);

		// 委托确认
		ajaxRequest = addAjaxRequest(pp, "MyWorklistTPage_delegate_receiving_page",
				WorkitemDelegateReceivingPage.class);
		addWindowBean(pp, "MyWorklistTPage_delegate_receiving", ajaxRequest)
				.setTitle($m("MyRunningWorklistTPage.5")).setHeight(360).setWidth(500);

		// 标记菜单
		MenuBean mb = createMarkMenuComponent(pp);
		mb.addItem(MyRunningWorklistTbl.MENU_MARK_READ())
				.addItem(MyRunningWorklistTbl.MENU_MARK_UNREAD())
				.addItem(MyRunningWorklistTbl.MENU_MARK_ALLREAD());
		mb.addItem(MenuItem.sep()).addItem(MyRunningWorklistTbl.MENU_MARK_TOP())
				.addItem(MyRunningWorklistTbl.MENU_MARK_UNTOP());

		// 标记已读
		addAjaxRequest(pp, "MyWorklistTPage_readMark").setHandlerMethod("doReadMark");

		// 分组菜单
		mb = createGroupMenuComponent(pp);
		addGroupMenuItems(pp, mb, getWorklistPageUrl(pp));

		// 委托菜单
		mb = createDelegateMenuComponent(pp);
		mb.addItem(
				MenuItem
						.of($m("MyRunningWorklistTPage.6"))
						.setOnclick(
								"$Actions['MyWorklistTPage_tbl'].doAct('MyWorklistTPage_delegate', 'workitemId');"))
				.addItem(MenuItem.sep())
				.addItem(
						MenuItem.of($m("MyRunningWorklistTPage.7")).setOnclick(
								"$Actions['MyWorklistTPage_delegate']('delegationSource=user');"));

		// 更多操作
		mb = createOpeMenuComponent(pp);
		mb.addItem(MenuItem.of($m("MyRunningWorklistTbl.16")).setOnclick(
				"$Actions['MyWorklistTPage_tbl'].doAct('MyWorklistTPage_delete', 'workitemId');"));
		mb.addItem(MenuItem.sep());
		mb.addItem(MenuItem.of($m("MyRunningWorklistTPage.18")).setOnclick(
				"$Actions['MyWorklistTPage_mysettings']();"));

		// 我的设置
		ajaxRequest = addAjaxRequest(pp, "MyWorklistTPage_mysettings_page", MySettingsPage.class);
		addWindowBean(pp, "MyWorklistTPage_mysettings", ajaxRequest)
				.setTitle($m("MyRunningWorklistTPage.18")).setHeight(400).setWidth(350);
	}

	protected void addGroupMenuItems(final PageParameter pp, final MenuBean mb, final String url) {
		final MenuItem g0 = MyRunningWorklistTbl.MENU_VIEW_GROUP0().setOnclick(
				"$Actions.reloc('g=none');");
		final MenuItem g1 = MyRunningWorklistTbl.MENU_VIEW_GROUP1().setOnclick(
				"$Actions.reloc('g=modelname');");
		final MenuItem g2 = MyRunningWorklistTbl.MENU_VIEW_GROUP2().setOnclick(
				"$Actions.reloc('g=taskname');");
		final String g = pp.getParameter(G);
		if ("none".equals(g)) {
			g0.setIconClass(MenuItem.ICON_SELECTED);
		} else if ("modelname".equals(g)) {
			g1.setIconClass(MenuItem.ICON_SELECTED);
		} else if ("taskname".equals(g)) {
			g2.setIconClass(MenuItem.ICON_SELECTED);
		}
		mb.addItem(g0).addItem(g1).addItem(g2);
	}

	protected TablePagerBean addTablePagerBean(final PageParameter pp) {
		final TablePagerBean tablePager = addTablePagerBean(pp, "MyWorklistTPage_tbl",
				MyRunningWorklistTbl.class).setShowCheckbox(true);
		tablePager.addColumn(TC_ICON());
		final String g = pp.getParameter(G);
		if (!"taskname".equals(g)) {
			tablePager.addColumn(TC_TASK());
		}
		tablePager.addColumn(TC_TITLE()).addColumn(TC_PNO())
				.addColumn(TC_USER("userFrom", $m("MyRunningWorklistTPage.0")))
				.addColumn(TC_CREATEDATE().setColumnText($m("MyRunningWorklistTPage.1")).setWidth(75));
		// tablePager.addColumn(TC_PSTAT());
		tablePager.addColumn(TablePagerColumn.OPE(68));
		return tablePager;
	}

	protected TablePagerColumn TC_TASK() {
		return new TablePagerColumn("taskname", $m("MyRunningWorklistTPage.20"), 120)
				.setFilterSort(false);
	}

	protected TablePagerColumn TC_PSTAT() {
		return new TablePagerColumn("pstat", $m("MyRunningWorklistTPage.13"), 56).setTextAlign(
				ETextAlign.right).setFilterSort(false);
	}

	protected MenuBean createMarkMenuComponent(final PageParameter pp) {
		// 标记菜单
		return (MenuBean) addComponentBean(pp, "MyWorklistTPage_markMenu", MenuBean.class)
				.setMenuEvent(EMenuEvent.click).setSelector("#idMyWorklistTPage_markMenu");
	}

	protected MenuBean createGroupMenuComponent(final PageParameter pp) {
		return (MenuBean) addComponentBean(pp, "MyWorklistTPage_groupMenu", MenuBean.class)
				.setMenuEvent(EMenuEvent.click).setSelector("#idMyWorklistTPage_groupMenu");
	}

	protected MenuBean createDelegateMenuComponent(final PageParameter pp) {
		return (MenuBean) addComponentBean(pp, "MyWorklistTPage_delegateMenu", MenuBean.class)
				.setMenuEvent(EMenuEvent.click).setSelector("#idMyWorklistTPage_delegateMenu");
	}

	protected MenuBean createOpeMenuComponent(final PageParameter pp) {
		return (MenuBean) addComponentBean(pp, "MyWorklistTPage_opeMenu", MenuBean.class)
				.setMenuEvent(EMenuEvent.click).setSelector("#idMyWorklistTPage_opeMenu");
	}

	@Override
	public String toToolbarHTML(final PageParameter pp) {
		final StringBuilder sb = new StringBuilder();
		final DelegationBean delegation = wfdService.queryRunningDelegation(pp.getLoginId());
		if (delegation != null) {
			addAjaxRequest(pp, "MyRunningWorklistTPage_user_undelegate").setConfirmMessage(
					$m("MyRunningWorklistTPage.11")).setHandlerMethod("doUserUndelegate");

			final StringBuilder txt = new StringBuilder();
			txt.append($m("MyRunningWorklistTPage.8",
					new SpanElement(pp.getUser(delegation.getUserId())).setStrong(true)));
			txt.append(SpanElement.SPACE15);
			txt.append(new LinkElement($m("MyRunningWorklistTPage.10")).setHref(uFactory.getUrl(pp,
					UserDelegateListTPage.class)));
			txt.append(SpanElement.SPACE);
			txt.append(new LinkElement($m("MyRunningWorklistTPage.9"))
					.setOnclick("$Actions['MyRunningWorklistTPage_user_undelegate']();"));
			sb.append(new BlockElement().setClassName("worklist_tip").setText(txt.toString()));
		}
		final Set<String> modelIds = ArrayUtils.asSet(StringUtils.split(pp.getParameter("modelId"),
				";"));
		FilterButtons btns = getFilterButtons(pp);
		if (btns == null) {
			btns = FilterButtons.of();
		}
		for (final String modelId : modelIds) {
			final ProcessModelBean pm = wfpmService.getBean(modelId);
			if (pm != null) {
				final List<String> _modelIds = new ArrayList<String>(modelIds);
				_modelIds.remove(modelId);
				btns.add(new FilterButton(StringUtils.replace(pm.getModelText(), ".", " / ")).setLabel(
						$m("MyRunningWorklistTPage.17")).setOndelete(
						"$Actions.reloc('modelId="
								+ (_modelIds.size() > 0 ? StringUtils.join(_modelIds, ";") : "__del")
								+ "');"));
			}
		}
		final String v = pp.getParameter("v");
		if ("unread".equals(v)) {
			btns.add(new FilterButton($m("MyRunningWorklistTbl.9"))
					.setOndelete("$Actions.reloc('v=__del');"));
		}
		if (pp.getBoolParameter("delegation")) {
			btns.add(new FilterButton($m("MyRunningWorklistTbl.22"))
					.setOndelete("$Actions.reloc('delegation=__del');"));
		}
		if (pp.getBoolParameter("retake")) {
			btns.add(new FilterButton($m("MyRunningWorklistTbl.15"))
					.setOndelete("$Actions.reloc('retake=__del');"));
		}

		sb.append(super.toToolbarHTML(pp));
		if (btns.size() > 0) {
			sb.append("<div class='wl_filter_btns'>");
			sb.append(btns);
			sb.append("</div>");
		}
		sb.append(JavascriptUtils.wrapScriptTag(toJavascript(pp), true));
		return sb.toString();
	}

	protected String toJavascript(final PageParameter pp) {
		final UserStatBean userStat = wfusService.getUserStat(pp.getLoginId());
		final StringBuilder js = new StringBuilder();
		js.append("var container = $('idWorklistProgressBar');");
		js.append("if (container) {");
		js.append("  var bar = new $UI.ProgressBar(container, {");
		int maxValue = wfusService.getAllWorkitems(userStat);
		if (maxValue <= 0) {
			maxValue = 100;
		}
		js.append("    maxProgressValue : ").append(maxValue).append(",");
		js.append("    startAfterCreate : false,");
		js.append("    showText : false,");
		js.append("    showAbortAction : false,");
		js.append("    showDetailAction : false");
		js.append("  });");
		final int complete = userStat.getWorkitem_complete();
		js.append("  bar.setProgress(").append(complete).append(");");
		js.append("  container.insert(\"<div class='pinfo'>").append($m("MyRunningWorklistTPage.15"))
				.append("<span>").append(NumberUtils.formatPercent((double) complete / maxValue))
				.append(",</span><a href='")
				.append(pp.wrapContextPath(uFactory.getUrl(pp, MyWorkstatTPage.class))).append("'>")
				.append($m("MyRunningWorklistTPage.16")).append("</a></div>\");");
		js.append("}");

		if (!MyFinalWorklistTPage.class.isAssignableFrom(getOriginalClass())) {
			final int interval = userStat.getWorklist_refresh_interval();
			if (interval > 0) {
				js.append("new PeriodicalExecuter(function() { $Actions['MyWorklistTPage_tbl'](); }, ")
						.append(interval * 60).append(");");
			}
		}
		return js.toString();
	}

	@Override
	public String[] getDependentComponents(final PageParameter pp) {
		return new String[] { ProgressBarRegistry.PROGRESSBAR };
	}

	@Override
	public ElementList getRightElements(final PageParameter pp) {
		return ElementList.of(new BlockElement().setId("idWorklistProgressBar"));
	}

	protected String getWorklistPageUrl(final PageParameter pp) {
		return uFactory.getUrl(pp, MyRunningWorklistTPage.class);
	}

	@Override
	public ElementList getLeftElements(final PageParameter pp) {
		return ElementList.of(
				LinkButton.of($m("MyRunningWorklistTPage.12")).setHref(
						uFactory.getUrl(pp, MyInitiateItemsGroupTPage.class)),
				SpanElement.SPACE,
				LinkButton.menu($m("MyRunningWorklistTbl.14")).setId("idMyWorklistTPage_groupMenu"),
				SpanElement.SPACE,
				LinkButton.of($m("MyRunningWorklistTPage.17")).setOnclick(
						"$Actions['MyWorklistTPage_pmSelected']();"), SpanElement.SPACE,
				LinkButton.of($m("MyRunningWorklistTbl.9")).setOnclick("$Actions.reloc('v=unread');"),
				SpanElement.SPACE,
				LinkButton.menu($m("MyRunningWorklistTbl.6")).setId("idMyWorklistTPage_markMenu"),
				SpanElement.SPACE,
				LinkButton.menu($m("MyRunningWorklistTbl.5")).setId("idMyWorklistTPage_delegateMenu"),
				LinkButton.menu($m("MyRunningWorklistTbl.17")).setId("idMyWorklistTPage_opeMenu"));
	}

	protected BlockElement createStatElement(final PageParameter pp) {
		return new BlockElement();
	}

	@Transaction(context = IWorkflowContext.class)
	public IForward doUserUndelegate(final ComponentParameter cp) {
		final DelegationBean delegation = wfdService.queryRunningDelegation(cp.getLoginId());
		if (delegation != null) {
			wfdService.doAbort(delegation);
		}
		return JavascriptForward.RELOC;
	}

	@Transaction(context = IWorkflowContext.class)
	public IForward doReadMark(final ComponentParameter cp) {
		final String op = cp.getParameter("op");
		if ("allread".equals(op)) {
			final IDataQuery<WorkitemBean> dq = wfwService.getRunningWorklist(cp.getLoginId(), null);
			WorkitemBean workitem;
			while ((workitem = dq.next()) != null) {
				if (!workitem.isReadMark()) {
					wfwService.doReadMark(workitem);
				}
			}
		} else {
			for (final Object id : StringUtils.split(cp.getParameter("workitemId"))) {
				final WorkitemBean workitem = wfwService.getBean(id);
				if ("unread".equals(op)) {
					wfwService.doUnReadMark(workitem);
				} else {
					wfwService.doReadMark(workitem);
				}
			}
		}
		return new JavascriptForward("$Actions['MyWorklistTPage_tbl']();");
	}

	@Transaction(context = IWorkflowContext.class)
	public IForward doTopMark(final ComponentParameter cp) {
		for (final Object id : StringUtils.split(cp.getParameter("workitemId"))) {
			final WorkitemBean workitem = wfwService.getBean(id);
			if ("untop".equals(cp.getParameter("op"))) {
				wfwService.doUnTopMark(workitem);
			} else {
				wfwService.doTopMark(workitem);
			}
		}
		return new JavascriptForward("$Actions['MyWorklistTPage_tbl']();");
	}

	@Transaction(context = IWorkflowContext.class)
	public IForward doFallback(final ComponentParameter cp) {
		wfaService.doFallback(wfwService.getActivity(WorkflowUtils.getWorkitemBean(cp)));
		return new JavascriptForward("$Actions['MyWorklistTPage_tbl']();");
	}

	@Transaction(context = IWorkflowContext.class)
	public IForward doDelete(final ComponentParameter cp) {
		for (final String workitemId : StringUtils.split(cp.getParameter("workitemId"))) {
			wfwService.doDeleteProcess(wfwService.getBean(workitemId));
		}
		return new JavascriptForward("$Actions['MyWorklistTPage_tbl']();");
	}

	public static class MySettingsPage extends AbstractTemplatePage {
		@Override
		protected void onForward(final PageParameter pp) throws Exception {
			super.onForward(pp);

			// 验证
			addValidationBean(pp, "MySettingsPage_validation").setWarnType(EWarnType.insertAfter)
					.setTriggerSelector(".MySettingsPage .button2")
					.addValidators(new Validator(EValidatorMethod.digits, "#ms_interval"));
			// 提交
			addAjaxRequest(pp, "MySettingsPage_ok").setHandlerMethod("doOk").setSelector(
					".MySettingsPage");
		}

		@Transaction(context = IWorkflowContext.class)
		public IForward doOk(final ComponentParameter cp) {
			final UserStatBean stat = wfusService.getUserStat(cp.getLoginId());
			stat.setWorklist_refresh_interval(cp.getIntParameter("ms_interval"));
			wfusService.update(new String[] { "worklist_refresh_interval" }, stat);
			return new JavascriptForward("$Actions.reloc();");
		}

		@Override
		protected String toHtml(final PageParameter pp, final Map<String, Object> variables,
				final String currentVariable) throws IOException {
			final StringBuilder sb = new StringBuilder();
			final UserStatBean stat = wfusService.getUserStat(pp.getLoginId());
			final InputElement interval = new InputElement("ms_interval").setWidth("100px").setVal(
					stat.getWorklist_refresh_interval());
			sb.append("<div class='simple_window_tcb MySettingsPage'>");
			sb.append(" <div class='c'>");
			sb.append("  <div class='lbl'>#(MySettingsPage.0)</div>");
			sb.append("  <div>").append(interval).append(new SpanElement($m("MySettingsPage.1")))
					.append("</div>");
			sb.append(" </div>");
			sb.append(" <div class='b'>")
					.append(ButtonElement.okBtn().setOnclick("$Actions['MySettingsPage_ok']();"))
					.append(SpanElement.SPACE).append(ButtonElement.closeBtn()).append("</div>");
			sb.append("</div>");
			return sb.toString();
		}
	}

	public static class PMSelectedPage extends AbstractTemplatePage {

		@Override
		protected void onForward(final PageParameter pp) throws Exception {
			super.onForward(pp);

			addTreeBean(pp, "PMSelectedPage_tree").setContainerId("idPMSelectedPage_tree")
					.setHandlerClass(PMSelectedHandler.class);
		}

		@Override
		protected String toHtml(final PageParameter pp, final Map<String, Object> variables,
				final String currentVariable) throws IOException {
			final StringBuilder sb = new StringBuilder();
			sb.append("<div id='idPMSelectedPage_tree'></div>");
			return sb.toString();
		}
	}

	public static class PMSelectedHandler extends AbstractTreeHandler {
		private Map<String, List<ProcessModelBean>> data;

		@Override
		public TreeNodes getTreenodes(final ComponentParameter cp, final TreeNode parent) {
			final TreeNodes items = TreeNodes.of();
			if (parent == null) {
				data = MyProcessWorksTPage.getProcessModelMap(cp);
				for (final String s : data.keySet()) {
					final TreeNode tn = new TreeNode((TreeBean) cp.componentBean, null, s);
					items.add(tn);
				}
			} else if (data != null) {
				final Object dataObject = parent.getDataObject();
				if (dataObject instanceof String) {
					final List<ProcessModelBean> list = data.get(dataObject);
					for (final ProcessModelBean pm : list) {
						final TreeNode tn = new TreeNode((TreeBean) cp.componentBean, null, pm);
						tn.setText(WorkflowUtils.getShortMtext(pm));
						tn.setJsClickCallback("$Actions.reloc('modelId=" + pm.getId() + "');");
						items.add(tn);
					}
				}
			}
			return items;
		}
	}
}