package net.simpleframework.workflow.web.page.t1;

import static net.simpleframework.common.I18n.$m;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import net.simpleframework.ado.query.DataQueryUtils;
import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.ado.query.ListDataQuery;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.ctx.common.bean.AttachmentFile;
import net.simpleframework.ctx.trans.Transaction;
import net.simpleframework.mvc.AbstractMVCPage;
import net.simpleframework.mvc.IForward;
import net.simpleframework.mvc.JavascriptForward;
import net.simpleframework.mvc.PageMapping;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.ButtonElement;
import net.simpleframework.mvc.common.element.ElementList;
import net.simpleframework.mvc.common.element.LinkButton;
import net.simpleframework.mvc.common.element.LinkElement;
import net.simpleframework.mvc.common.element.SpanElement;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ext.attachments.AbstractAttachmentHandler;
import net.simpleframework.mvc.component.ext.attachments.AttachmentBean;
import net.simpleframework.mvc.component.ext.attachments.IAttachmentSaveCallback;
import net.simpleframework.mvc.component.ui.menu.MenuBean;
import net.simpleframework.mvc.component.ui.menu.MenuItem;
import net.simpleframework.mvc.component.ui.menu.MenuItems;
import net.simpleframework.mvc.component.ui.pager.AbstractTablePagerSchema;
import net.simpleframework.mvc.component.ui.pager.EPagerBarLayout;
import net.simpleframework.mvc.component.ui.pager.TablePagerBean;
import net.simpleframework.mvc.component.ui.pager.TablePagerColumn;
import net.simpleframework.mvc.component.ui.pager.db.GroupDbTablePagerHandler;
import net.simpleframework.mvc.component.ui.swfupload.SwfUploadBean;
import net.simpleframework.mvc.component.ui.window.WindowBean;
import net.simpleframework.mvc.template.struct.NavigationButtons;
import net.simpleframework.workflow.engine.EProcessModelStatus;
import net.simpleframework.workflow.engine.IWorkflowContext;
import net.simpleframework.workflow.engine.bean.ProcessModelBean;
import net.simpleframework.workflow.schema.ProcessDocument;
import net.simpleframework.workflow.web.WorkflowLogRef.ProcessModelUpdateLogPage;
import net.simpleframework.workflow.web.WorkflowUtils;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
@PageMapping(url = "/workflow/mgr/model")
public class ProcessModelMgrPage extends AbstractWorkflowMgrPage {

	@Override
	protected void onForward(final PageParameter pp) throws Exception {
		super.onForward(pp);

		addTablePagerBean(pp);
		// 删除
		addDeleteAjaxRequest(pp);

		// 上传模型文件
		addComponentBean(pp, "ProcessModelMgrPage_upload_page", AttachmentBean.class)
				.setShowMenu(false).setShowSubmit(true).setShowEdit(false)
				.setHandlerClass(ModelUploadAction.class);
		addComponentBean(pp, "ProcessModelMgrPage_upload", WindowBean.class)
				.setContentRef("ProcessModelMgrPage_upload_page").setTitle($m("ProcessModelMgrPage.3"))
				.setHeight(480).setWidth(400);
	}

	protected TablePagerBean addTablePagerBean(final PageParameter pp) {
		final TablePagerBean tablePager = (TablePagerBean) addComponentBean(pp,
				"ProcessModelMgrPage_tbl", TablePagerBean.class).setGroupColumn("gc").setSort(false)
				.setPagerBarLayout(EPagerBarLayout.none).setContainerId("idProcessModelMgrPage_tbl")
				.setHandlerClass(ProcessModelTbl.class);
		tablePager
				.addColumn(TablePagerColumn.ICON())
				.addColumn(new TablePagerColumn("modelText", $m("ProcessModelMgrPage.0")))
				.addColumn(
						new TablePagerColumn("processCount", $m("ProcessModelMgrPage.1"), 60)
								.setFilter(false))
				.addColumn(
						new TablePagerColumn("userText", $m("ProcessModelMgrPage.2"), 80)
								.setFilter(false))
				.addColumn(
						new TablePagerColumn("version", $m("MyInitiateItemsTPage.4"), 80)
								.setFilter(false).center()).addColumn(TC_CREATEDATE().setFilter(false))
				.addColumn(TablePagerColumn.OPE(90));
		return tablePager;
	}

	@Transaction(context = IWorkflowContext.class)
	public IForward doDelete(final ComponentParameter cp) {
		final Object[] ids = StringUtils.split(cp.getParameter("modelId"));
		wfpmService.delete(ids);
		return new JavascriptForward("$Actions['ProcessModelMgrPage_tbl']();");
	}

	@Override
	protected String toHtml(final PageParameter pp, final Map<String, Object> variables,
			final String variable) throws IOException {
		final StringBuilder sb = new StringBuilder();
		sb.append("<div align='center' class='ProcessModelMgrPage'>");
		sb.append(" <div id='idProcessModelMgrPage_tbl'></div>");
		sb.append("</div>");
		return sb.toString();
	}

	@Override
	public ElementList getLeftElements(final PageParameter pp) {
		return ElementList.of(new LinkButton($m("ProcessModelMgrPage.4"))
				.setOnclick("$Actions['ProcessModelMgrPage_upload']();"));
	}

	@Override
	public NavigationButtons getNavigationBar(final PageParameter pp) {
		return super.getNavigationBar(pp).append(new SpanElement("#(WorkflowWebContext.1)"));
	}

	@Override
	protected Class<? extends AbstractMVCPage> getUpdateLogPage() {
		return ProcessModelUpdateLogPage.class;
	}

	public static abstract class AbstractProcessModelTbl extends GroupDbTablePagerHandler {
		@Override
		public AbstractTablePagerSchema createTablePagerSchema() {
			return new DefaultTablePagerSchema() {
				@Override
				public Object getVal(final Object dataObject, final String key) {
					final ProcessModelBean pm = (ProcessModelBean) dataObject;
					if ("gc".equals(key)) {
						final String[] arr = StringUtils.split(pm.getModelText(), ".");
						if (arr.length > 1) {
							return arr[0];
						} else {
							return $m("MyInitiateItemsGroupTPage.0");
						}
					}
					return super.getVal(dataObject, key);
				}
			};
		};

		@Override
		protected Map<String, Object> getRowData(final ComponentParameter cp, final Object dataObject) {
			final ProcessModelBean processModel = (ProcessModelBean) dataObject;
			final EProcessModelStatus status = processModel.getStatus();
			final Object id = processModel.getId();
			final LinkElement le = new LinkElement(processModel).setHref(url(ProcessMgrPage.class,
					"modelId=" + id));
			if (status != EProcessModelStatus.deploy) {
				le.setColor("#777");
			}
			final KVMap row = new KVMap()
					.add(TablePagerColumn.ICON, WorkflowUtils.getStatusIcon(cp, status))
					.add("modelText", toModelTextHTML(cp, processModel))
					.add("processCount", toProcessCountHTML(cp, processModel))
					.add("userText", processModel.getUserText())
					.add("version", processModel.getModelVer())
					.add("createDate", processModel.getCreateDate())
					.add(TablePagerColumn.OPE, toOpeHTML(cp, processModel));
			return row;
		}

		protected LinkElement toModelTextHTML(final ComponentParameter cp,
				final ProcessModelBean processModel) {
			final LinkElement le = new LinkElement(WorkflowUtils.getShortMtext(processModel))
					.setHref(url(ProcessMgrPage.class, "modelId=" + processModel.getId()));
			if (processModel.getStatus() != EProcessModelStatus.deploy) {
				le.setColor("#777");
			}
			return le;
		}

		protected Object toProcessCountHTML(final ComponentParameter cp,
				final ProcessModelBean processModel) {
			return processModel.getProcessCount();
		}

		protected abstract String toOpeHTML(ComponentParameter cp, ProcessModelBean processModel);
	}

	public static class ProcessModelTbl extends AbstractProcessModelTbl {
		@Override
		public IDataQuery<?> createDataObjectQuery(final ComponentParameter cp) {
			final List<ProcessModelBean> list = DataQueryUtils.toList(wfpmService.getModelList());
			wfpmService.sort(list);
			return new ListDataQuery<ProcessModelBean>(list);
		}

		@Override
		public MenuItems getContextMenu(final ComponentParameter cp, final MenuBean menuBean,
				final MenuItem menuItem) {
			return MenuItems.of(
					MenuItem.of($m("ProcessModelMgrPage.6")).setOnclick_act(
							"AbstractWorkflowMgrPage_status", "modelId",
							"op=" + EProcessModelStatus.edit.name()), MenuItem.sep(), MenuItem
							.itemDelete().setOnclick_act("AbstractWorkflowMgrPage_del", "modelId"));
		}

		@Override
		protected String toOpeHTML(final ComponentParameter cp, final ProcessModelBean processModel) {
			final Object id = processModel.getId();
			final StringBuilder sb = new StringBuilder();
			if (processModel.getStatus() == EProcessModelStatus.edit) {
				final EProcessModelStatus deploy = EProcessModelStatus.deploy;
				sb.append(new ButtonElement(deploy)
						.setOnclick("$Actions['AbstractWorkflowMgrPage_status']('modelId=" + id + "&op="
								+ deploy.name() + "');"));
			} else {
				sb.append(WorkflowUtils.createLogButton().setOnclick(
						"$Actions['AbstractWorkflowMgrPage_update_log']('modelId=" + id + "');"));
			}
			sb.append(AbstractTablePagerSchema.IMG_DOWNMENU);
			return sb.toString();
		}

		@Override
		protected Map<String, Object> getRowAttributes(final ComponentParameter cp,
				final Object dataObject) {
			final ProcessModelBean processModel = (ProcessModelBean) dataObject;
			final Map<String, Object> kv = new KVMap();
			final StringBuilder sb = new StringBuilder();
			final EProcessModelStatus s = processModel.getStatus();
			if (s == EProcessModelStatus.edit) {
				// 菜单索引
				sb.append(";0");
			}
			if (sb.length() > 0) {
				kv.put(AbstractTablePagerSchema.MENU_DISABLED, sb.substring(1));
			}
			return kv;
		}
	}

	public static class ModelUploadAction extends AbstractAttachmentHandler {
		@Override
		public void setSwfUploadBean(final ComponentParameter cp, final SwfUploadBean swfUpload) {
			super.setSwfUploadBean(cp, swfUpload);
			swfUpload.setFileTypes("*.xml").setFileTypesDesc($m("ProcessModelMgrPage.5"));
		}

		@Transaction(context = IWorkflowContext.class)
		@Override
		public JavascriptForward doSave(final ComponentParameter cp,
				final IAttachmentSaveCallback callback) throws IOException {
			final Map<String, AttachmentFile> attachments = getUploadCache(cp);
			for (final AttachmentFile aFile : attachments.values()) {
				wfpmService.doAddModel(cp.getLoginId(),
						new ProcessDocument(new FileInputStream(aFile.getAttachment())).clone());
			}
			// 清除
			clearCache(cp);
			return new JavascriptForward("$Actions['ProcessModelMgrPage_tbl']();")
					.append("$Actions['ProcessModelMgrPage_upload'].close();");
		}
	}

	@Override
	protected Class<? extends AbstractMVCPage> getStatusDescPage() {
		return StatusDescPage.class;
	}

	public static class StatusDescPage extends AbstractStatusDescPage {

		@Transaction(context = IWorkflowContext.class)
		@Override
		public JavascriptForward onSave(final ComponentParameter cp) throws Exception {
			final EProcessModelStatus op = cp.getEnumParameter(EProcessModelStatus.class, "op");
			for (final String aId : StringUtils.split(cp.getParameter("modelId"), ";")) {
				final ProcessModelBean processModel = wfpmService.getBean(aId);
				setLogDescription(cp, processModel);
				if (op == EProcessModelStatus.deploy) {
					wfpmService.doDeploy(processModel);
				} else if (op == EProcessModelStatus.edit) {
					wfpmService.doResume(processModel);
				}
			}
			return super.onSave(cp).append("$Actions['ProcessModelMgrPage_tbl']();");
		}

		@Override
		public String getTitle(final PageParameter pp) {
			final EProcessModelStatus op = pp.getEnumParameter(EProcessModelStatus.class, "op");
			if (op == EProcessModelStatus.edit) {
				return $m("ProcessModelMgrPage.6");
			}
			return op != null ? op.toString() : null;
		}
	}
}
