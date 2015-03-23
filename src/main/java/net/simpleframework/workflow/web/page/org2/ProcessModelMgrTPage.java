package net.simpleframework.workflow.web.page.org2;

import static net.simpleframework.common.I18n.$m;

import java.io.IOException;
import java.util.Map;

import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.common.ID;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.ctx.permission.PermissionDept;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.ETextAlign;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ui.pager.EPagerBarLayout;
import net.simpleframework.mvc.component.ui.pager.TablePagerBean;
import net.simpleframework.mvc.component.ui.pager.TablePagerColumn;
import net.simpleframework.mvc.component.ui.pager.db.AbstractDbTablePagerHandler;
import net.simpleframework.workflow.engine.EProcessModelStatus;
import net.simpleframework.workflow.engine.ProcessModelBean;
import net.simpleframework.workflow.web.page.t1.AbstractWorkflowMgrPage;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class ProcessModelMgrTPage extends AbstractWorkflowMgrTPage {

	@Override
	protected void onForward(final PageParameter pp) {
		super.onForward(pp);

		final TablePagerBean tablePager = (TablePagerBean) addTablePagerBean(pp,
				"ProcessModelMgrTPage_tbl").setPagerBarLayout(EPagerBarLayout.bottom).setPageItems(30)
				.setContainerId("idProcessModelMgrTPage_tbl").setHandlerClass(ProcessModelTbl.class);
		tablePager
				.addColumn(
						new TablePagerColumn("modelText", $m("ProcessModelMgrPage.0")).setTextAlign(
								ETextAlign.left).setSort(false))
				.addColumn(
						new TablePagerColumn("processCount", $m("ProcessModelMgrPage.1"), 80)
								.setSort(false))
				.addColumn(
						new TablePagerColumn("userText", $m("ProcessModelMgrPage.2"), 115).setSort(false))
				.addColumn(AbstractWorkflowMgrPage.TC_CREATEDATE())
				.addColumn(
						AbstractWorkflowMgrPage.TC_STATUS().setPropertyClass(EProcessModelStatus.class))
				.addColumn(TablePagerColumn.OPE().setWidth(90));
	}

	@Override
	protected String toHtml(final PageParameter pp, final Map<String, Object> variables,
			final String currentVariable) throws IOException {
		final StringBuilder sb = new StringBuilder();
		// sb.append("<div class='tbar'>");
		// sb.append(ElementList.of(LinkButton.addBtn()));
		// sb.append("</div>");
		sb.append("<div id='idProcessModelMgrTPage_tbl'>");
		sb.append("</div>");
		return sb.toString();
	}

	public static class ProcessModelTbl extends AbstractDbTablePagerHandler {
		@Override
		public IDataQuery<?> createDataObjectQuery(final ComponentParameter cp) {
			final PermissionDept org = getOrg(cp);
			if (org != null) {
				final ID orgId = org.getId();
				cp.addFormParameter("orgId", orgId);
				return workflowContext.getProcessModelService().getModelListByDomain(orgId);
			}
			return null;
		}

		@Override
		protected Map<String, Object> getRowData(final ComponentParameter cp, final Object dataObject) {
			final ProcessModelBean pm = (ProcessModelBean) dataObject;
			final KVMap row = new KVMap();
			row.put("modelText", pm.getModelText());
			row.put("createDate", pm.getCreateDate());
			row.put("status", pm.getStatus());
			return row;
		}
	}
}
