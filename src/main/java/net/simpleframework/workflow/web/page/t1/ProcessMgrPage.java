package net.simpleframework.workflow.web.page.t1;

import static net.simpleframework.common.I18n.$m;

import java.io.IOException;
import java.util.Map;

import net.simpleframework.ado.query.DataQueryUtils;
import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.common.Convert;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.ctx.trans.Transaction;
import net.simpleframework.mvc.AbstractMVCPage;
import net.simpleframework.mvc.IForward;
import net.simpleframework.mvc.JavascriptForward;
import net.simpleframework.mvc.PageMapping;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.ButtonElement;
import net.simpleframework.mvc.common.element.ElementList;
import net.simpleframework.mvc.common.element.InputElement;
import net.simpleframework.mvc.common.element.LinkElement;
import net.simpleframework.mvc.common.element.SpanElement;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.base.ajaxrequest.AjaxRequestBean;
import net.simpleframework.mvc.component.ui.menu.MenuBean;
import net.simpleframework.mvc.component.ui.menu.MenuItem;
import net.simpleframework.mvc.component.ui.menu.MenuItems;
import net.simpleframework.mvc.component.ui.pager.AbstractTablePagerSchema;
import net.simpleframework.mvc.component.ui.pager.EPagerBarLayout;
import net.simpleframework.mvc.component.ui.pager.TablePagerBean;
import net.simpleframework.mvc.component.ui.pager.TablePagerColumn;
import net.simpleframework.mvc.component.ui.pager.db.AbstractDbTablePagerHandler;
import net.simpleframework.mvc.template.struct.NavigationButtons;
import net.simpleframework.workflow.engine.EProcessAbortPolicy;
import net.simpleframework.workflow.engine.EProcessStatus;
import net.simpleframework.workflow.engine.IWorkflowContext;
import net.simpleframework.workflow.engine.bean.ProcessBean;
import net.simpleframework.workflow.engine.bean.ProcessModelBean;
import net.simpleframework.workflow.web.WorkflowLogRef.ProcessUpdateLogPage;
import net.simpleframework.workflow.web.WorkflowUtils;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
@PageMapping(url = "/workflow/mgr/process")
public class ProcessMgrPage extends AbstractWorkflowMgrPage {

	@Override
	protected void onForward(final PageParameter pp) throws Exception {
		super.onForward(pp);

		final TablePagerBean tablePager = (TablePagerBean) addComponentBean(pp, "ProcessMgrPage_tbl",
				TablePagerBean.class).setPagerBarLayout(EPagerBarLayout.bottom)
						.setContainerId("idProcessMgrPage_tbl").setHandlerClass(ProcessTbl.class);
		tablePager.addColumn(TablePagerColumn.ICON()).addColumn(TC_TITLE())
				.addColumn(new TablePagerColumn("userText", $m("ProcessMgrPage.0"), 100))
				.addColumn(TC_CREATEDATE()).addColumn(TC_COMPLETEDATE())
				.addColumn(TablePagerColumn.OPE(90));

		// 删除
		addDeleteAjaxRequest(pp);

		// 放弃
		final AjaxRequestBean ajaxRequest = addAjaxRequest(pp, "ProcessMgrPage_abortPage",
				ProcessAbortPage.class);
		addWindowBean(pp, "ProcessMgrPage_abort", ajaxRequest).setResizable(false)
				.setTitle(EProcessStatus.abort.toString()).setWidth(420).setHeight(240);
	}

	@Transaction(context = IWorkflowContext.class)
	public IForward doDelete(final ComponentParameter cp) {
		final Object[] ids = StringUtils.split(cp.getParameter("processId"));
		wfpService.delete(ids);
		return new JavascriptForward("$Actions['ProcessMgrPage_tbl']();");
	}

	@Override
	protected String toHtml(final PageParameter pp, final Map<String, Object> variables,
			final String variable) throws IOException {
		final StringBuilder sb = new StringBuilder();
		sb.append("<div align='center' class='ProcessMgrPage'>");
		sb.append(" <div id='idProcessMgrPage_tbl'></div>");
		sb.append("</div>");
		return sb.toString();
	}

	@Override
	public ElementList getLeftElements(final PageParameter pp) {
		return ElementList
				.of(createBackButton().setHref(uFactory.getUrl(pp, ProcessModelMgrPage.class)));
	}

	@Override
	public NavigationButtons getNavigationBar(final PageParameter pp) {
		return super.getNavigationBar(pp).append(new SpanElement($m("ProcessMgrPage.1")));
	}

	@Override
	protected Class<? extends AbstractMVCPage> getUpdateLogPage() {
		return ProcessUpdateLogPage.class;
	}

	public static class ProcessTbl extends AbstractDbTablePagerHandler {

		@Override
		public IDataQuery<?> createDataObjectQuery(final ComponentParameter cp) {
			final ProcessModelBean processModel = WorkflowUtils.getProcessModel(cp);
			if (processModel == null) {
				return DataQueryUtils.nullQuery();
			}
			cp.addFormParameter("modelId", processModel.getId());
			return wfpService.getProcessList(null, new ProcessModelBean[] { processModel }, "", null,
					null);
		}

		@Override
		protected Map<String, Object> getRowData(final ComponentParameter cp,
				final Object dataObject) {
			final ProcessBean process = (ProcessBean) dataObject;
			final EProcessStatus status = process.getStatus();
			final KVMap row = new KVMap()
					.add(TablePagerColumn.ICON, WorkflowUtils.getStatusIcon(cp, status))
					.add("title", createTitleElement(cp, process)).add("userText", process.getUserText())
					.add("createDate", process.getCreateDate())
					.add("completeDate", process.getCompleteDate());
			row.add(TablePagerColumn.OPE, toOpeHTML(cp, process));
			return row;
		}

		protected LinkElement createTitleElement(final ComponentParameter cp,
				final ProcessBean process) {
			return new LinkElement(WorkflowUtils.getProcessTitle(process))
					.setHref(url(ActivityMgrPage.class, "processId=" + process.getId()))
					.setColor_gray(!StringUtils.hasText(process.getTitle()));
		}

		protected ButtonElement createLogButton(final ComponentParameter cp,
				final ProcessBean process) {
			return WorkflowUtils.createLogButton()
					.setOnclick("$Actions['AbstractWorkflowMgrPage_update_log']('processId="
							+ process.getId() + "');");
		}

		protected String toOpeHTML(final ComponentParameter cp, final ProcessBean process) {
			final StringBuilder sb = new StringBuilder();
			sb.append(createLogButton(cp, process));
			sb.append(AbstractTablePagerSchema.IMG_DOWNMENU);
			return sb.toString();
		}

		@Override
		public MenuItems getContextMenu(final ComponentParameter cp, final MenuBean menuBean,
				final MenuItem menuItem) {
			if (menuItem == null) {
				return MenuItems.of(
						MenuItem.of($m("AbstractWorkflowMgrPage.1")).setOnclick_act(
								"AbstractWorkflowMgrPage_status", "processId", "op=running"),
						MenuItem.sep(),
						MenuItem.of($m("AbstractWorkflowMgrPage.0")).setOnclick_act(
								"AbstractWorkflowMgrPage_status", "processId", "op=suspended"),
						MenuItem.of(EProcessStatus.abort.toString())
								.setOnclick_act("ProcessMgrPage_abort", "processId"),
						MenuItem.sep(),
						MenuItem.itemDelete().setOnclick_act("AbstractWorkflowMgrPage_del", "processId"));
			}
			return null;
		}

		@Override
		protected Map<String, Object> getRowAttributes(final ComponentParameter cp,
				final Object dataObject) {
			final ProcessBean process = (ProcessBean) dataObject;
			final Map<String, Object> kv = new KVMap();
			final StringBuilder sb = new StringBuilder();
			final EProcessStatus s = process.getStatus();
			if (s != EProcessStatus.suspended) {
				sb.append(";0");
			}
			if (s != EProcessStatus.running) {
				sb.append(";1");
			}
			if (s != EProcessStatus.running && s != EProcessStatus.timeout) {
				sb.append(";2");
			}
			if (sb.length() > 0) {
				kv.put(AbstractTablePagerSchema.MENU_DISABLED, sb.substring(1));
			}
			return kv;
		}
	}

	@Override
	protected Class<? extends AbstractMVCPage> getStatusDescPage() {
		return ProcessStatusDescPage.class;
	}

	public static class ProcessStatusDescPage extends AbstractStatusDescPage {
		@Override
		public String getIDParameterKey() {
			return "processId";
		}

		@Transaction(context = IWorkflowContext.class)
		@Override
		public JavascriptForward onSave(final ComponentParameter cp) throws Exception {
			final EProcessStatus op = cp.getEnumParameter(EProcessStatus.class, "op");
			for (final String aId : StringUtils.split(cp.getParameter("processId"), ";")) {
				final ProcessBean process = wfpService.getBean(aId);
				setLogDescription(cp, process);
				if (op == EProcessStatus.suspended) {
					wfpService.doSuspend(process);
				} else if (op == EProcessStatus.running) {
					wfpService.doResume(process);
				}
			}
			final JavascriptForward js = toSavedForward(cp);
			js.append(super.onSave(cp));
			return js;
		}

		protected JavascriptForward toSavedForward(final ComponentParameter cp) {
			return new JavascriptForward("$Actions['ProcessMgrPage_tbl']();");
		}
	}

	public static class ProcessAbortPage extends AbstractAbortPage {

		@Transaction(context = IWorkflowContext.class)
		public IForward doOk(final ComponentParameter cp) {
			final ProcessBean process = WorkflowUtils.getProcessBean(cp);
			wfpService.doAbort(process,
					Convert.toEnum(EProcessAbortPolicy.class, cp.getParameter("abort_policy")));
			return doJavascriptForward(cp);
		}

		protected IForward doJavascriptForward(final ComponentParameter cp) {
			return new JavascriptForward(
					"$Actions['ProcessMgrPage_abort'].close(); $Actions['ProcessMgrPage_tbl']();");
		}

		@Override
		protected Enum<?>[] getEnumConstants() {
			return EProcessAbortPolicy.values();
		}

		@Override
		protected InputElement getIdInput(final PageParameter pp) {
			return InputElement.hidden("processId").setValue(pp);
		}
	}
}
