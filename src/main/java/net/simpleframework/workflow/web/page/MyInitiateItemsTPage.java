package net.simpleframework.workflow.web.page;

import static net.simpleframework.common.I18n.$m;

import java.util.Map;

import net.simpleframework.ado.query.DataQueryUtils;
import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.ado.query.ListDataQuery;
import net.simpleframework.common.ID;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.mvc.JavascriptForward;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.JS;
import net.simpleframework.mvc.common.element.LinkButton;
import net.simpleframework.mvc.common.element.LinkElement;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ui.pager.TablePagerBean;
import net.simpleframework.mvc.component.ui.pager.TablePagerColumn;
import net.simpleframework.mvc.component.ui.pager.db.AbstractDbTablePagerHandler;
import net.simpleframework.workflow.engine.InitiateItem;
import net.simpleframework.workflow.engine.InitiateItems;
import net.simpleframework.workflow.engine.bean.ProcessBean;
import net.simpleframework.workflow.engine.bean.ProcessModelBean;
import net.simpleframework.workflow.engine.bean.WorkitemBean;
import net.simpleframework.workflow.schema.ProcessDocument;
import net.simpleframework.workflow.web.component.startprocess.DefaultStartProcessHandler;
import net.simpleframework.workflow.web.component.startprocess.StartProcessBean;
import net.simpleframework.workflow.web.page.t1.WorkflowFormPage;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class MyInitiateItemsTPage extends AbstractItemsTPage {

	@Override
	protected void onForward(final PageParameter pp) throws Exception {
		super.onForward(pp);

		addTablePagerBean(pp);
		// 发起流程
		pp.addComponentBean("MyInitiateItemsTPage_startProcess", StartProcessBean.class)
		// .setConfirmMessage($m("MyInitiateItemsTPage.0"))
				.setHandlerClass(_StartProcessHandler.class);
	}

	protected TablePagerBean addTablePagerBean(final PageParameter pp) {
		final TablePagerBean tablePager = addTablePagerBean(pp, "MyInitiateItemsTPage_tbl",
				MyInitiateItemsTbl.class);
		tablePager
				.addColumn(TC_ICON())
				.addColumn(new TablePagerColumn("modelText", $m("MyInitiateItemsTPage.1")))
				.addColumn(
						new TablePagerColumn("processCount", $m("MyInitiateItemsTPage.3"), 80).setFilter(
								false).setPropertyClass(Integer.class))
				.addColumn(
						new TablePagerColumn("version", $m("MyInitiateItemsTPage.4"), 80)
								.setFilter(false)).addColumn(TablePagerColumn.OPE().setWidth(70));
		return tablePager;
	}

	public static class MyInitiateItemsTbl extends AbstractDbTablePagerHandler {

		@Override
		public IDataQuery<?> createDataObjectQuery(final ComponentParameter cp) {
			final ID loginId = cp.getLoginId();
			InitiateItems items;
			if (loginId == null || (items = wfpmService.getInitiateItems(loginId)) == null) {
				return DataQueryUtils.nullQuery();
			}
			return new ListDataQuery<InitiateItem>(items);
		}

		@Override
		protected Object getVal(final Object dataObject, final String key) {
			final InitiateItem initiateItem = (InitiateItem) dataObject;
			final ProcessModelBean processModel = wfpmService.getBean(initiateItem.getModelId());
			if ("modelText".equals(key)) {
				return processModel.toString();
			} else if ("processCount".equals(key)) {
				return processModel.getProcessCount();
			} else if ("version".equals(key)) {
				final ProcessDocument doc = wfpmService.getProcessDocument(processModel);
				return doc.getProcessNode().getVersion();
			}
			return super.getVal(dataObject, key);
		}

		@Override
		protected Map<String, Object> getRowData(final ComponentParameter cp, final Object dataObject) {
			final InitiateItem initiateItem = (InitiateItem) dataObject;
			final Object modelId = initiateItem.getModelId();
			final ProcessModelBean processModel = wfpmService.getBean(modelId);
			final KVMap row = new KVMap();
			row.add("modelText", new LinkElement(initiateItem)
					.setOnclick("$Actions['MyInitiateItemsTPage_startProcess']('modelId=" + modelId
							+ "');"));
			row.add("version", processModel.getModelVer());
			row.add("processCount", processModel.getProcessCount());
			final StringBuilder sb = new StringBuilder();
			sb.append(LinkButton.corner($m("MyInitiateItemsTPage.2")).setOnclick(
					"$Actions['MyInitiateItemsTPage_startProcess'].initiator_select('modelId=" + modelId
							+ "');"));
			row.put(TablePagerColumn.OPE, sb.toString());
			return row;
		}
	}

	public static class _StartProcessHandler extends DefaultStartProcessHandler {

		@Override
		public JavascriptForward onStartProcess(final ComponentParameter cp, final ProcessBean process) {
			final WorkitemBean workitem = wfpService.getFirstWorkitem(process);
			if (workitem != null) {
				final JavascriptForward js = new JavascriptForward();
				js.append(JS.loc(uFactory.getUrl(cp, WorkflowFormPage.class, workitem)));
				return js;
			} else {
				return null;
			}
		}
	}
}
