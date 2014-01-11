package net.simpleframework.workflow.web.page.t1;

import java.io.IOException;
import java.util.Date;
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
import net.simpleframework.mvc.component.ui.pager.db.AbstractDbTablePagerHandler;
import net.simpleframework.mvc.template.struct.NavigationButtons;
import net.simpleframework.workflow.engine.EProcessAbortPolicy;
import net.simpleframework.workflow.engine.EProcessStatus;
import net.simpleframework.workflow.engine.IProcessService;
import net.simpleframework.workflow.engine.IWorkflowContext;
import net.simpleframework.workflow.engine.ProcessBean;
import net.simpleframework.workflow.engine.ProcessModelBean;
import net.simpleframework.workflow.web.WorkflowLogRef.ProcessUpdateLogPage;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class ProcessMgrPage extends AbstractWorkflowMgrPage {

	@Override
	protected void onForward(final PageParameter pp) {
		super.onForward(pp);

		final TablePagerBean tablePager = (TablePagerBean) addComponentBean(pp, "ProcessMgrPage_tbl",
				TablePagerBean.class).setPagerBarLayout(EPagerBarLayout.bottom)
				.setContainerId("idProcessMgrPage_tbl").setHandleClass(ProcessTbl.class);

		tablePager
				.addColumn(new TablePagerColumn("title", "标题").setTextAlign(ETextAlign.left))
				.addColumn(new TablePagerColumn("userId", "启动人", 120).setTextAlign(ETextAlign.left))
				.addColumn(new TablePagerColumn("createDate", "创建日期", 115).setPropertyClass(Date.class))
				.addColumn(
						new TablePagerColumn("completeDate", "完成日期", 115).setPropertyClass(Date.class))
				.addColumn(
						new TablePagerColumn("status", "状态", 70).setPropertyClass(EProcessStatus.class))
				.addColumn(TablePagerColumn.OPE().setWidth(90));

		// 删除
		addDeleteAjaxRequest(pp);

		// 放弃
		addAjaxRequest(pp, "ProcessMgrPage_abort_page", ProcessAbortPage.class);
		addWindowBean(pp, "ProcessMgrPage_abort").setResizable(false)
				.setContentRef("ProcessMgrPage_abort_page").setTitle(EProcessStatus.abort.toString())
				.setWidth(420).setHeight(240);
	}

	@Transaction(context = IWorkflowContext.class)
	public IForward doDelete(final ComponentParameter cp) {
		final Object[] ids = StringUtils.split(cp.getParameter("processId"));
		if (ids != null) {
			context.getProcessService().delete(ids);
		}
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
		return ElementList.of(new LinkButton("返回").setIconClass(Icon.share_alt).setHref(
				url(ProcessModelMgrPage.class)));
	}

	@Override
	public NavigationButtons getNavigationBar(final PageParameter pp) {
		return super.getNavigationBar(pp).append(new SpanElement("流程实例管理"));
	}

	@Override
	protected Class<? extends AbstractMVCPage> getUpdateLogPage() {
		return ProcessUpdateLogPage.class;
	}

	public static class ProcessTbl extends AbstractDbTablePagerHandler {

		@Override
		public IDataQuery<?> createDataObjectQuery(final ComponentParameter cp) {
			final ProcessModelBean processModel = getProcessModelBean(cp);
			if (processModel == null) {
				return DataQueryUtils.nullQuery();
			}
			cp.addFormParameter("modelId", processModel.getId());
			return context.getProcessService().getProcessList(processModel);
		}

		@Override
		protected Map<String, Object> getRowData(final ComponentParameter cp, final Object dataObject) {
			final ProcessBean process = (ProcessBean) dataObject;
			final Object id = process.getId();
			final KVMap row = new KVMap()
					.add("title",
							new LinkElement(StringUtils.text(process.getTitle(), "未设置主题")).setHref(url(
									ActivityMgrPage.class, "processId=" + id)))
					.add("userId", cp.getUser(process.getUserId()))
					.add("createDate", process.getCreateDate())
					.add("completeDate", process.getCompleteDate()).add("status", process.getStatus());
			final StringBuilder sb = new StringBuilder();
			sb.append(createLogButton("processId=" + id));
			sb.append(SpanElement.SPACE).append(AbstractTablePagerSchema.IMG_DOWNMENU);
			row.add(TablePagerColumn.OPE, sb.toString());
			return row;
		}

		@Override
		public MenuItems getContextMenu(final ComponentParameter cp, final MenuBean menuBean,
				final MenuItem menuItem) {
			return MenuItems.of(
					MenuItem.of("恢复运行").setOnclick_act("AbstractWorkflowMgrPage_status", "processId",
							"op=running"),
					MenuItem.sep(),
					MenuItem.of("挂起").setOnclick_act("AbstractWorkflowMgrPage_status", "processId",
							"op=suspended"),
					MenuItem.of(EProcessStatus.abort.toString()).setOnclick_act("ProcessMgrPage_abort",
							"processId"), MenuItem.sep(),
					MenuItem.itemDelete().setOnclick_act("AbstractWorkflowMgrPage_del", "processId"));
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
				sb.append(";1;2");
			}
			if (sb.length() > 0) {
				kv.put(AbstractTablePagerSchema.MENU_DISABLED, sb.substring(1));
			}
			return kv;
		}
	}

	@Override
	protected Class<? extends AbstractMVCPage> getStatusDescPage() {
		return StatusDescPage.class;
	}

	public static class StatusDescPage extends AbstractStatusDescPage {

		@Override
		public JavascriptForward onSave(final ComponentParameter cp) throws Exception {
			updateStatus(cp, context.getProcessService(),
					StringUtils.split(cp.getParameter("processId"), ";"),
					cp.getEnumParameter(EProcessStatus.class, "op"));
			return super.onSave(cp).append("$Actions['ProcessMgrPage_tbl']();");
		}
	}

	public static class ProcessAbortPage extends AbstractAbortPage {

		public IForward doOk(final ComponentParameter cp) {
			final IProcessService service = context.getProcessService();
			final ProcessBean process = service.getBean(cp.getParameter("processId"));
			service.abort(process,
					Convert.toEnum(EProcessAbortPolicy.class, cp.getParameter("abort_policy")));
			return new JavascriptForward(
					"$Actions['ProcessMgrPage_abort'].close(); $Actions['ProcessMgrPage_tbl']();");
		}

		@Override
		protected Enum<?>[] getEnumConstants() {
			return EProcessAbortPolicy.values();
		}

		@Override
		protected InputElement getIdInput(PageParameter pp) {
			return InputElement.hidden("processId").setValue(pp);
		}
	}

	private static ProcessModelBean getProcessModelBean(final PageParameter pp) {
		return getCacheBean(pp, context.getProcessModelService(), "modelId");
	}
}
