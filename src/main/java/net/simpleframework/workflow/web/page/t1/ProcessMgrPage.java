package net.simpleframework.workflow.web.page.t1;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import net.simpleframework.ado.query.DataQueryUtils;
import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.ctx.trans.Transaction;
import net.simpleframework.module.common.web.page.AbstractDescPage;
import net.simpleframework.mvc.AbstractMVCPage;
import net.simpleframework.mvc.IForward;
import net.simpleframework.mvc.JavascriptForward;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.ETextAlign;
import net.simpleframework.mvc.common.element.ElementList;
import net.simpleframework.mvc.common.element.Icon;
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
		public IDataQuery<?> createDataObjectQuery(ComponentParameter cp) {
			ProcessModelBean processModel = context.getProcessModelService().getBean(
					cp.getParameter("modelId"));
			if (processModel == null) {
				return DataQueryUtils.nullQuery();
			}
			cp.addFormParameter("modelId", processModel.getId());
			return context.getProcessService().getProcessList(processModel);
		}

		@Override
		protected Map<String, Object> getRowData(ComponentParameter cp, Object dataObject) {
			ProcessBean process = (ProcessBean) dataObject;
			final KVMap row = new KVMap()
					.add("title", new LinkElement(StringUtils.text(process.getTitle(), "未设置主题")))
					.add("userId", cp.getUser(process.getUserId()))
					.add("createDate", process.getCreateDate())
					.add("completeDate", process.getCompleteDate()).add("status", process.getStatus());
			final StringBuilder sb = new StringBuilder();
			sb.append(createLogButton("processId=" + process.getId()));
			sb.append(SpanElement.SPACE).append(AbstractTablePagerSchema.IMG_DOWNMENU);
			row.add(TablePagerColumn.OPE, sb.toString());
			return row;
		}

		@Override
		public MenuItems getContextMenu(final ComponentParameter cp, final MenuBean menuBean,
				final MenuItem menuItem) {
			return MenuItems.of(
					MenuItem.of("挂起").setOnclick_act("AbstractWorkflowMgrPage_status", "processId",
							"op=suspended"),
					MenuItem.sep(),
					MenuItem.of("恢复运行").setOnclick_act("AbstractWorkflowMgrPage_status", "processId",
							"op=running"), MenuItem.sep(),
					MenuItem.itemDelete().setOnclick_act("AbstractWorkflowMgrPage_del", "processId"));
		}

		@Override
		protected Map<String, Object> getRowAttributes(ComponentParameter cp, Object dataObject) {
			ProcessBean process = (ProcessBean) dataObject;
			final Map<String, Object> kv = new KVMap();
			final StringBuilder sb = new StringBuilder();
			final EProcessStatus s = process.getStatus();
			if (s != EProcessStatus.running) {
				sb.append(";0");
			}
			if (s != EProcessStatus.suspended) {
				sb.append(";1");
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

	public static class StatusDescPage extends AbstractDescPage {

		@Override
		public JavascriptForward onSave(ComponentParameter cp) throws Exception {
			final EProcessStatus op = cp.getEnumParameter(EProcessStatus.class, "op");
			final IProcessService service = context.getProcessService();
			final String[] arr = StringUtils.split(cp.getParameter("processId"), ";");
			if (arr != null) {
				for (final String id : arr) {
					final ProcessBean process = service.getBean(id);
					if (process != null && op != process.getStatus()) {
						setLogDescription(cp, process);
						process.setStatus(op);
						service.update(new String[] { "status" }, process);
					}
				}
			}
			return super.onSave(cp).append("$Actions['ProcessMgrPage_tbl']();");
		}

		@Override
		public String getTitle(final PageParameter pp) {
			final EProcessStatus op = pp.getEnumParameter(EProcessStatus.class, "op");
			if (op == EProcessStatus.suspended) {
				return "挂起";
			} else if (op == EProcessStatus.running) {
				return "恢复运行";
			}
			return null;
		}
	}
}
