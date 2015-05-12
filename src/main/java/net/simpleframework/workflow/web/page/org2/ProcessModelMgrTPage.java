package net.simpleframework.workflow.web.page.org2;

import static net.simpleframework.common.I18n.$m;

import java.io.IOException;
import java.util.Map;

import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.common.ID;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.ctx.permission.PermissionDept;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.ButtonElement;
import net.simpleframework.mvc.common.element.ETextAlign;
import net.simpleframework.mvc.common.element.LinkElement;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ui.pager.EPagerBarLayout;
import net.simpleframework.mvc.component.ui.pager.TablePagerBean;
import net.simpleframework.mvc.component.ui.pager.TablePagerColumn;
import net.simpleframework.mvc.component.ui.pager.db.AbstractDbTablePagerHandler;
import net.simpleframework.workflow.engine.EProcessModelStatus;
import net.simpleframework.workflow.engine.bean.ProcessModelBean;
import net.simpleframework.workflow.web.WorkflowUtils;
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
				"ProcessModelMgrTPage_tbl").setSort(false).setPagerBarLayout(EPagerBarLayout.bottom)
				.setPageItems(30).setContainerId("idProcessModelMgrTPage_tbl")
				.setHandlerClass(ProcessModelTbl.class);
		tablePager
				.addColumn(new TablePagerColumn("modelText", $m("ProcessModelMgrPage.0")))
				.addColumn(
						new TablePagerColumn("processCount", $m("ProcessModelMgrPage.1"), 60)
								.setFilter(false))
				.addColumn(
						new TablePagerColumn("userText", $m("ProcessModelMgrPage.2"), 80)
								.setFilter(false))
				.addColumn(
						new TablePagerColumn("version", $m("MyInitiateItemsTPage.4"), 80).setTextAlign(
								ETextAlign.center).setFilter(false))
				.addColumn(AbstractWorkflowMgrPage.TC_CREATEDATE().setFilter(false))
				.addColumn(AbstractWorkflowMgrPage.TC_STATUS(EProcessModelStatus.class))
				.addColumn(TablePagerColumn.OPE().setWidth(90));
		// if (pp.getLogin().isManager())
		// tablePager);
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
			final PermissionDept org = getPermissionOrg(cp);
			if (org != null) {
				final ID orgId = org.getId();
				cp.addFormParameter("orgId", orgId);
				return mService.getModelListByDomain(orgId);
			}
			return null;
		}

		@Override
		protected Map<String, Object> getRowData(final ComponentParameter cp, final Object dataObject) {
			final ProcessModelBean pm = (ProcessModelBean) dataObject;
			final EProcessModelStatus status = pm.getStatus();
			final KVMap row = new KVMap();

			final LinkElement le = new LinkElement(pm).setHref(uFactory.getUrl(cp,
					ProcessMgrTPage.class, "modelId=" + pm.getId()));
			if (status != EProcessModelStatus.deploy) {
				le.setColor("#777");
			}

			row.add("modelText", le)
					.add("createDate", pm.getCreateDate())
					.add("processCount",
							drService.getProcessModelDomainR(getPermissionOrg(cp).getId(), pm)
									.getProcessCount()).add("userText", pm.getUserText())
					.add("version", pm.getModelVer())
					.add("status", WorkflowUtils.toStatusHTML(cp, status))
					.add(TablePagerColumn.OPE, toOpeHTML(cp, pm));
			return row;
		}

		protected String toOpeHTML(final ComponentParameter cp, final ProcessModelBean pm) {
			final StringBuilder sb = new StringBuilder();
			sb.append(new ButtonElement($m("ProcessModelMgrTPage.0")).setOnclick("$Actions.loc('"
					+ uFactory.getUrl(cp, ProcessMgrTPage.class, "modelId=" + pm.getId()) + "');"));
			// sb.append(SpanElement.SPACE).append(AbstractTablePagerSchema.IMG_DOWNMENU);
			return sb.toString();
		}
	}
}
