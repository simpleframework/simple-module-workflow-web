package net.simpleframework.workflow.web.component.modellist;

import java.util.Map;

import net.simpleframework.ado.ColumnData;
import net.simpleframework.ado.EOrder;
import net.simpleframework.ado.query.DataQueryUtils;
import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.ado.query.ListDataObjectQuery;
import net.simpleframework.common.ID;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.mvc.common.element.LinkElement;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ui.pager.AbstractTablePagerHandler;
import net.simpleframework.mvc.component.ui.pager.AbstractTablePagerSchema;
import net.simpleframework.mvc.component.ui.pager.TablePagerColumns;
import net.simpleframework.mvc.ctx.permission.IPagePermissionHandler;
import net.simpleframework.workflow.engine.IWorkflowContextAware;
import net.simpleframework.workflow.engine.InitiateItem;
import net.simpleframework.workflow.engine.InitiateItems;
import net.simpleframework.workflow.engine.ProcessModelBean;
import net.simpleframework.workflow.schema.ProcessNode;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class MyModelListHandler extends AbstractTablePagerHandler implements IMyModelListHandler,
		IWorkflowContextAware {

	@Override
	public IDataQuery<?> createDataObjectQuery(final ComponentParameter cp) {
		final ID loginId = ((IPagePermissionHandler) context.getParticipantService()).getLoginId(cp);
		InitiateItems items;
		if (loginId == null || (items = context.getModelService().getInitiateItems(loginId)) == null) {
			return DataQueryUtils.nullQuery();
		}
		return new ListDataObjectQuery<InitiateItem>(items);
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected int doSort(final ColumnData dbColumn, final Object o1, final Object o2) {
		final InitiateItem item1 = (InitiateItem) o1;
		final InitiateItem item2 = (InitiateItem) o2;
		final String col = dbColumn.getName();
		Comparable c1 = null, c2 = null;
		if ("createDate".equals(col)) {
			c1 = item1.model().getCreateDate();
			c2 = item2.model().getCreateDate();
		}
		if (c1 != null && c2 != null) {
			return dbColumn.getOrder() == EOrder.desc ? c1.compareTo(c2) : c2.compareTo(c1);
		} else {
			return 0;
		}
	}

	@Override
	public AbstractTablePagerSchema createTablePagerSchema() {
		return new DefaultTablePagerSchema() {
			@Override
			public Map<String, Object> getRowData(final ComponentParameter cp, final Object dataObject) {
				final InitiateItem item = (InitiateItem) dataObject;
				final ProcessModelBean processModel = item.model();
				final ProcessNode processNode = context.getModelService()
						.getProcessDocument(processModel).getProcessNode();
				final KVMap rowData = new KVMap();
				rowData.add("title",
						new LinkElement(processNode).setOnclick(jsStartProcessAction(processModel)));
				rowData.add("userText",
						context.getParticipantService().getUser(processModel.getUserId()));
				rowData.add("createDate", processModel.getCreateDate());
				return rowData;
			}

			@Override
			public TablePagerColumns getTablePagerColumns(final ComponentParameter cp) {
				final TablePagerColumns columns = super.getTablePagerColumns(cp);
				final TablePagerColumns columns2 = TablePagerColumns.of();
				columns2.append(columns.get("title")).append(columns.get("userText"))
						.append(columns.get("createDate"));
				return columns2;
			}
		};
	}

	@Override
	public String jsStartProcessAction(final ProcessModelBean processModel) {
		return "$Actions['ml_start_process']('" + ProcessModelBean.modelId + "="
				+ processModel.getId() + "');";
	}
}
