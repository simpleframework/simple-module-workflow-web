package net.simpleframework.workflow.web.page;

import static net.simpleframework.common.I18n.$m;

import java.util.Map;

import net.simpleframework.ado.query.DataQueryUtils;
import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.ado.query.ListDataObjectQuery;
import net.simpleframework.common.ID;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.mvc.JavascriptForward;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.ETextAlign;
import net.simpleframework.mvc.common.element.LinkElement;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ui.pager.TablePagerBean;
import net.simpleframework.mvc.component.ui.pager.TablePagerColumn;
import net.simpleframework.mvc.component.ui.pager.db.AbstractDbTablePagerHandler;
import net.simpleframework.workflow.engine.InitiateItem;
import net.simpleframework.workflow.engine.InitiateItems;
import net.simpleframework.workflow.engine.ProcessBean;
import net.simpleframework.workflow.engine.WorkitemBean;
import net.simpleframework.workflow.web.component.startprocess.DefaultStartProcessHandler;
import net.simpleframework.workflow.web.component.startprocess.StartProcessBean;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class MyInitiateItemsTPage extends AbstractWorkTPage {

	@Override
	protected void onForward(final PageParameter pp) {
		super.onForward(pp);

		final TablePagerBean tablePager = addTablePagerBean(pp, "MyInitiateItemsTPage_tbl",
				MyInitiateItemsTbl.class);
		tablePager
				.addColumn(new TablePagerColumn("modelText", "可启动流程").setTextAlign(ETextAlign.left))
				.addColumn(TablePagerColumn.OPE().setWidth(80));

		// 发起流程
		pp.addComponentBean("MyInitiateItemsTPage_startProcess", StartProcessBean.class)
				.setConfirmMessage($m("MyInitiateItemsTPage.0"))
				.setHandleClass(_StartProcessHandler.class);
	}

	public static class MyInitiateItemsTbl extends AbstractDbTablePagerHandler {

		@Override
		public IDataQuery<?> createDataObjectQuery(final ComponentParameter cp) {
			final ID loginId = cp.getLoginId();
			InitiateItems items;
			if (loginId == null
					|| (items = context.getProcessModelService().getInitiateItems(loginId)) == null) {
				return DataQueryUtils.nullQuery();
			}
			return new ListDataObjectQuery<InitiateItem>(items);
		}

		@Override
		protected Map<String, Object> getRowData(final ComponentParameter cp, final Object dataObject) {
			final InitiateItem initiateItem = (InitiateItem) dataObject;
			final KVMap row = new KVMap().add("modelText", new LinkElement(initiateItem)
					.setOnclick("$Actions['MyInitiateItemsTPage_startProcess']('modelId="
							+ initiateItem.getModelId() + "');"));
			final StringBuilder sb = new StringBuilder();
			row.put(TablePagerColumn.OPE, sb.toString());
			return row;
		}
	}

	public static class _StartProcessHandler extends DefaultStartProcessHandler {

		@Override
		public JavascriptForward onStartProcess(final ComponentParameter cp, final ProcessBean process) {
			final WorkitemBean workitem = context.getProcessService().getFirstWorkitem(process);
			if (workitem != null) {
				final JavascriptForward js = new JavascriptForward();
				js.append("$Actions.loc('").append(getUrlsFactory().getMyWorkFormUrl(workitem))
						.append("');");
				return js;
			} else {
				return null;
			}
		}
	}
}
