package net.simpleframework.workflow.web.page.org2;

import static net.simpleframework.common.I18n.$m;

import java.io.IOException;
import java.util.Map;

import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.common.ID;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.ctx.permission.PermissionDept;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.PageRequestResponse.IVal;
import net.simpleframework.mvc.common.element.ElementList;
import net.simpleframework.mvc.common.element.LinkButton;
import net.simpleframework.mvc.common.element.LinkElement;
import net.simpleframework.mvc.common.element.SpanElement;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ui.pager.EPagerBarLayout;
import net.simpleframework.mvc.component.ui.pager.TablePagerBean;
import net.simpleframework.mvc.component.ui.pager.TablePagerColumn;
import net.simpleframework.mvc.component.ui.pager.db.AbstractDbTablePagerHandler;
import net.simpleframework.workflow.engine.EProcessStatus;
import net.simpleframework.workflow.engine.ProcessBean;
import net.simpleframework.workflow.engine.ProcessModelBean;
import net.simpleframework.workflow.web.WorkflowUtils;
import net.simpleframework.workflow.web.page.t1.AbstractWorkflowMgrPage;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class ProcessMgrTPage extends AbstractWorkflowMgrTPage {

	@Override
	protected void onForward(final PageParameter pp) {
		super.onForward(pp);

		addTablePagerBean(pp);
	}

	protected TablePagerBean addTablePagerBean(final PageParameter pp) {
		final TablePagerBean tablePager = (TablePagerBean) addTablePagerBean(pp,
				"ProcessMgrTPage_tbl").setPagerBarLayout(EPagerBarLayout.bottom).setPageItems(30)
				.setContainerId("idProcessMgrTPage_tbl").setHandlerClass(ProcessTbl.class);
		tablePager.addColumn(AbstractWorkflowMgrPage.TC_TITLE())
				.addColumn(new TablePagerColumn("userText", $m("ProcessMgrPage.0"), 120))
				.addColumn(AbstractWorkflowMgrPage.TC_CREATEDATE())
				.addColumn(AbstractWorkflowMgrPage.TC_COMPLETEDATE())
				.addColumn(AbstractWorkflowMgrPage.TC_STATUS().setPropertyClass(EProcessStatus.class))
				.addColumn(TablePagerColumn.OPE().setWidth(90));
		return tablePager;
	}

	@Override
	protected SpanElement createOrgElement(final PageParameter pp) {
		final SpanElement oele = super.createOrgElement(pp);
		final ProcessModelBean pm = getProcessModel(pp);
		if (pm != null) {
			oele.setText(oele.getText() + " - " + pm.getModelText());
		}
		return oele;
	}

	@Override
	protected String toHtml(final PageParameter pp, final Map<String, Object> variables,
			final String currentVariable) throws IOException {
		final StringBuilder sb = new StringBuilder();
		sb.append("<div class='tbar'>");
		sb.append(ElementList.of(LinkButton.backBtn().setOnclick(
				"$Actions.loc('" + getUrlsFactory().getUrl(pp, ProcessModelMgrTPage.class) + "')")));
		sb.append("</div>");
		sb.append("<div id='idProcessMgrTPage_tbl'>");
		sb.append("</div>");
		return sb.toString();
	}

	public static class ProcessTbl extends AbstractDbTablePagerHandler {
		@Override
		public IDataQuery<?> createDataObjectQuery(final ComponentParameter cp) {
			final PermissionDept org = getPermissionOrg(cp);
			ProcessModelBean pm;
			if (org != null && (pm = getProcessModel(cp)) != null) {
				final ID orgId = org.getId();
				cp.addFormParameter("orgId", orgId).addFormParameter("modelId", pm.getId());
				return workflowContext.getProcessService().getProcessList(orgId, pm);
			}
			return null;
		}

		@Override
		protected Map<String, Object> getRowData(final ComponentParameter cp, final Object dataObject) {
			final ProcessBean process = (ProcessBean) dataObject;
			final Object id = process.getId();
			final KVMap row = new KVMap().add("title",
					new LinkElement(WorkflowUtils.getProcessTitle(process)).setHref(getUrlsFactory()
							.getUrl(cp, ActivityMgrTPage.class, "processId=" + id)));
			return row;
		}
	}

	private static ProcessModelBean getProcessModel(final PageParameter pp) {
		return pp.getCache("@ProcessModelBean", new IVal<ProcessModelBean>() {
			@Override
			public ProcessModelBean get() {
				return workflowContext.getProcessModelService().getBean(pp.getParameter("modelId"));
			}
		});
	}
}