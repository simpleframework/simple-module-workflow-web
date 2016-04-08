package net.simpleframework.workflow.web.page.mgr2;

import static net.simpleframework.common.I18n.$m;

import java.io.IOException;
import java.util.Map;

import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.common.ID;
import net.simpleframework.common.StringUtils;
import net.simpleframework.ctx.permission.PermissionDept;
import net.simpleframework.ctx.trans.Transaction;
import net.simpleframework.mvc.AbstractMVCPage;
import net.simpleframework.mvc.IForward;
import net.simpleframework.mvc.JavascriptForward;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.ButtonElement;
import net.simpleframework.mvc.common.element.ElementList;
import net.simpleframework.mvc.common.element.JS;
import net.simpleframework.mvc.common.element.LinkButton;
import net.simpleframework.mvc.common.element.LinkElement;
import net.simpleframework.mvc.common.element.SpanElement;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.base.ajaxrequest.AjaxRequestBean;
import net.simpleframework.mvc.component.ui.menu.MenuBean;
import net.simpleframework.mvc.component.ui.menu.MenuItem;
import net.simpleframework.mvc.component.ui.menu.MenuItems;
import net.simpleframework.mvc.component.ui.pager.EPagerBarLayout;
import net.simpleframework.mvc.component.ui.pager.TablePagerBean;
import net.simpleframework.mvc.component.ui.pager.TablePagerColumn;
import net.simpleframework.workflow.engine.EProcessStatus;
import net.simpleframework.workflow.engine.IWorkflowContext;
import net.simpleframework.workflow.engine.bean.ProcessBean;
import net.simpleframework.workflow.engine.bean.ProcessModelBean;
import net.simpleframework.workflow.web.WorkflowLogRef.ProcessUpdateLogPage;
import net.simpleframework.workflow.web.WorkflowUtils;
import net.simpleframework.workflow.web.page.t1.AbstractWorkflowMgrPage;
import net.simpleframework.workflow.web.page.t1.ProcessMgrPage.ProcessAbortPage;
import net.simpleframework.workflow.web.page.t1.ProcessMgrPage.ProcessStatusDescPage;
import net.simpleframework.workflow.web.page.t1.ProcessMgrPage.ProcessTbl;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class ProcessMgrTPage extends AbstractWorkflowMgrTPage {

	@Override
	protected void onForward(final PageParameter pp) throws Exception {
		super.onForward(pp);

		addTablePagerBean(pp);

		// 删除
		addAjaxRequest(pp, "ProcessMgrTPage_del").setHandlerMethod("doDelete").setConfirmMessage(
				$m("Confirm.Delete"));

		// 放弃
		final AjaxRequestBean ajaxRequest = addAjaxRequest(pp, "ProcessMgrTPage_abortPage",
				_ProcessAbortPage.class);
		addWindowBean(pp, "ProcessMgrTPage_abort", ajaxRequest).setResizable(false)
				.setTitle(EProcessStatus.abort.toString()).setWidth(420).setHeight(240);
	}

	protected TablePagerBean addTablePagerBean(final PageParameter pp) {
		final TablePagerBean tablePager = (TablePagerBean) addTablePagerBean(pp,
				"ProcessMgrTPage_tbl", _ProcessTbl.class).setPagerBarLayout(EPagerBarLayout.bottom)
				.setContainerId("idProcessMgrTPage_tbl");
		tablePager.addColumn(TablePagerColumn.ICON()).addColumn(AbstractWorkflowMgrPage.TC_TITLE())
				.addColumn(new TablePagerColumn("userText", $m("ProcessMgrPage.0"), 100))
				.addColumn(AbstractWorkflowMgrPage.TC_CREATEDATE())
				.addColumn(AbstractWorkflowMgrPage.TC_COMPLETEDATE())
				.addColumn(TablePagerColumn.OPE(70));
		return tablePager;
	}

	@Transaction(context = IWorkflowContext.class)
	public IForward doDelete(final ComponentParameter cp) {
		final Object[] ids = StringUtils.split(cp.getParameter("processId"));
		wfpService.delete(ids);
		return new JavascriptForward("$Actions['ProcessMgrTPage_tbl']();");
	}

	@Override
	protected Class<? extends AbstractMVCPage> getStatusDescPage() {
		return _ProcessStatusDescPage.class;
	}

	@Override
	protected Class<? extends AbstractMVCPage> getUpdateLogPage() {
		return ProcessUpdateLogPage.class;
	}

	@Override
	protected SpanElement createOrgElement(final PageParameter pp) {
		final SpanElement oele = super.createOrgElement(pp);
		final ProcessModelBean pm = WorkflowUtils.getProcessModel(pp);
		if (pm != null) {
			oele.setText(oele.getText() + " - " + pm.getModelText());
		}
		return oele;
	}

	@Override
	public ElementList getLeftElements(final PageParameter pp) {
		final ElementList el = ElementList.of(
				LinkButton.backBtn()
						.setOnclick(JS.loc(uFactory.getUrl(pp, ProcessModelMgrTPage.class))),
				SpanElement.SPACE15);
		return el.appendAll(super.getLeftElements(pp));
	}

	@Override
	protected String toHtml(final PageParameter pp, final Map<String, Object> variables,
			final String currentVariable) throws IOException {
		final StringBuilder sb = new StringBuilder();
		sb.append("<div id='idProcessMgrTPage_tbl'></div>");
		return sb.toString();
	}

	public static class _ProcessTbl extends ProcessTbl {
		@Override
		public IDataQuery<?> createDataObjectQuery(final ComponentParameter cp) {
			final PermissionDept org = getPermissionOrg(cp);
			ProcessModelBean pm;
			if (org != null && (pm = WorkflowUtils.getProcessModel(cp)) != null) {
				final ID orgId = org.getId();
				cp.addFormParameter("orgId", orgId).addFormParameter("modelId", pm.getId());
				return wfpService.getProcessList(orgId, pm);
			}
			return null;
		}

		@Override
		protected ButtonElement createLogButton(final ComponentParameter cp, final ProcessBean process) {
			return super.createLogButton(cp, process).setOnclick(
					"$Actions['AbstractWorkflowMgrTPage_update_log']('processId=" + process.getId()
							+ "');");
		}

		@Override
		protected LinkElement createTitleElement(final ComponentParameter cp,
				final ProcessBean process) {
			return super.createTitleElement(cp, process).setHref(
					uFactory.getUrl(cp, ActivityMgrTPage.class, "processId=" + process.getId()));
		}

		@Override
		public MenuItems getContextMenu(final ComponentParameter cp, final MenuBean menuBean,
				final MenuItem menuItem) {
			if (menuItem == null) {
				return MenuItems.of(
						MenuItem.of($m("AbstractWorkflowMgrPage.1")).setOnclick_act(
								"AbstractWorkflowMgrTPage_status", "processId", "op=running"),
						MenuItem.sep(),
						MenuItem.of($m("AbstractWorkflowMgrPage.0")).setOnclick_act(
								"AbstractWorkflowMgrTPage_status", "processId", "op=suspended"),
						MenuItem.of(EProcessStatus.abort.toString()).setOnclick_act(
								"ProcessMgrTPage_abort", "processId"), MenuItem.sep(), MenuItem
								.itemDelete().setOnclick_act("ProcessMgrTPage_del", "processId"));
			}
			return null;
		}
	}

	public static class _ProcessAbortPage extends ProcessAbortPage {
		@Override
		protected IForward doJavascriptForward(final ComponentParameter cp) {
			return new JavascriptForward(
					"$Actions['ProcessMgrTPage_abort'].close(); $Actions['ProcessMgrTPage_tbl']();");
		}
	}

	public static class _ProcessStatusDescPage extends ProcessStatusDescPage {

		@Override
		protected JavascriptForward toSavedForward(final ComponentParameter cp) {
			return new JavascriptForward("$Actions['ProcessMgrTPage_tbl']();");
		}
	}
}