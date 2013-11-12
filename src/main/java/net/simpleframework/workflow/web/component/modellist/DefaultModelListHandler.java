package net.simpleframework.workflow.web.component.modellist;

import static net.simpleframework.common.I18n.$m;

import java.util.Date;
import java.util.Map;

import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.mvc.common.element.ETextAlign;
import net.simpleframework.mvc.common.element.LinkElement;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ui.menu.MenuBean;
import net.simpleframework.mvc.component.ui.menu.MenuItem;
import net.simpleframework.mvc.component.ui.menu.MenuItems;
import net.simpleframework.mvc.component.ui.pager.AbstractTablePagerSchema;
import net.simpleframework.mvc.component.ui.pager.TablePagerColumn;
import net.simpleframework.mvc.component.ui.pager.TablePagerColumns;
import net.simpleframework.mvc.component.ui.pager.db.AbstractDbTablePagerHandler;
import net.simpleframework.workflow.engine.EProcessModelStatus;
import net.simpleframework.workflow.engine.IWorkflowContextAware;
import net.simpleframework.workflow.engine.ProcessModelBean;
import net.simpleframework.workflow.engine.participant.IParticipantModel;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public class DefaultModelListHandler extends AbstractDbTablePagerHandler implements
		IModelListHandler, IWorkflowContextAware {

	@Override
	public IDataQuery<?> createDataObjectQuery(final ComponentParameter cp) {
		return context.getModelService().getModelList();
	}

	@Override
	public String jsProcessListAction(final ProcessModelBean processModel) {
		return null;
	}

	protected MenuItems HEADER_MENU = MenuItems.of(MenuItem.of(
			$m("model_list_menu.0", MenuItem.ICON_ADD)).setOnclick(
			"$pager_action(item).upload_model();"));

	@Override
	public MenuItems getHeaderMenu(final ComponentParameter cp, final MenuBean menuBean) {
		return HEADER_MENU;
	}

	protected MenuItems CONTEXT_MENU = MenuItems.of(
			MenuItem.itemDelete().setOnclick("$pager_action(item).del();"),
			MenuItem.sep(),
			MenuItem.of($m("model_list_menu.1"), "menu_icon_option").setOnclick(
					"$pager_action(item).opt();"));

	@Override
	public MenuItems getContextMenu(final ComponentParameter cp, final MenuBean menuBean,
			final MenuItem menuItem) {
		return CONTEXT_MENU;
	}

	protected TablePagerColumns DEFAULT_COLUMNS = TablePagerColumns
			.of(TablePagerColumn.ICON(), new TablePagerColumn("modelText", $m("model_list.column.0"))
					.setTextAlign(ETextAlign.left), new TablePagerColumn("processCount",
					$m("model_list.column.1"), 100), new TablePagerColumn("userText",
					$m("model_list.column.2"), 120), new TablePagerColumn("createDate",
					$m("model_list.column.3"), 115).setPropertyClass(Date.class), new TablePagerColumn(
					"status", $m("model_list.column.4"), 80), TablePagerColumn.ACTION());

	@Override
	public AbstractTablePagerSchema createTablePagerSchema() {
		return new DefaultDbTablePagerSchema() {
			@Override
			public TablePagerColumns getTablePagerColumns(final ComponentParameter cp) {
				return DEFAULT_COLUMNS;
			}
		};
	}

	@Override
	protected Map<String, Object> getRowData(final ComponentParameter cp, final Object dataObject) {
		final ProcessModelBean processModel = (ProcessModelBean) dataObject;
		final KVMap rowData = new KVMap();
		rowData.add("modelText",
				new LinkElement(processModel).setOnclick(jsProcessListAction(processModel)));
		final IParticipantModel service = context.getParticipantService();
		rowData.add("userText", service.getUser(processModel.getUserId()));
		rowData.add("createDate", processModel.getCreateDate());
		final EProcessModelStatus status = processModel.getStatus();
		rowData.add("status", ModelListUtils.getStatusIcon(cp, status) + status);
		rowData.add(TablePagerColumn.ACTION, AbstractTablePagerSchema.IMG_DOWNMENU);
		return rowData;
	}
}
