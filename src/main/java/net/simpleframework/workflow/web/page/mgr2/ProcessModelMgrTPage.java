package net.simpleframework.workflow.web.page.mgr2;

import static net.simpleframework.common.I18n.$m;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import net.simpleframework.ado.query.DataQueryUtils;
import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.ado.query.ListDataQuery;
import net.simpleframework.common.ID;
import net.simpleframework.ctx.permission.PermissionDept;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.ButtonElement;
import net.simpleframework.mvc.common.element.JS;
import net.simpleframework.mvc.common.element.LinkElement;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ui.pager.EPagerBarLayout;
import net.simpleframework.mvc.component.ui.pager.TablePagerBean;
import net.simpleframework.mvc.component.ui.pager.TablePagerColumn;
import net.simpleframework.workflow.engine.bean.ProcessModelBean;
import net.simpleframework.workflow.web.page.t1.AbstractWorkflowMgrPage;
import net.simpleframework.workflow.web.page.t1.ProcessModelMgrPage.AbstractProcessModelTbl;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class ProcessModelMgrTPage extends AbstractWorkflowMgrTPage {

	@Override
	protected void onForward(final PageParameter pp) throws Exception {
		super.onForward(pp);

		final TablePagerBean tablePager = (TablePagerBean) addTablePagerBean(pp,
				"ProcessModelMgrTPage_tbl", ProcessModelTbl.class).setGroupColumn("gc").setSort(false)
						.setShowCheckbox(false).setPagerBarLayout(EPagerBarLayout.none)
						.setContainerId("idProcessModelMgrTPage_tbl");
		tablePager.addColumn(TablePagerColumn.ICON())
				.addColumn(new TablePagerColumn("modelText", $m("ProcessModelMgrPage.0")))
				.addColumn(new TablePagerColumn("processCount", $m("ProcessModelMgrPage.1"), 60)
						.setFilter(false))
				.addColumn(new TablePagerColumn("userText", $m("ProcessModelMgrPage.2"), 80).center()
						.setFilter(false))
				.addColumn(new TablePagerColumn("version", $m("MyInitiateItemsTPage.4"), 80).center()
						.setFilter(false))
				.addColumn(AbstractWorkflowMgrPage.TC_CREATEDATE().setFilter(false))
				.addColumn(TablePagerColumn.OPE(80));
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

	public static class ProcessModelTbl extends AbstractProcessModelTbl {
		@Override
		public IDataQuery<?> createDataObjectQuery(final ComponentParameter cp) {
			final PermissionDept org = getPermissionOrg(cp);
			if (org != null) {
				final ID orgId = org.getId();
				cp.addFormParameter("orgId", orgId);
				final List<ProcessModelBean> list = DataQueryUtils
						.toList(wfpmService.getModelListByDomain(orgId));
				wfpmService.sort(list);
				return new ListDataQuery<ProcessModelBean>(list);
			}
			return null;
		}

		@Override
		protected LinkElement toModelTextHTML(final ComponentParameter cp,
				final ProcessModelBean processModel) {
			return super.toModelTextHTML(cp, processModel).setHref(
					uFactory.getUrl(cp, ProcessMgrTPage.class, "modelId=" + processModel.getId()));
		}

		@Override
		protected Object toProcessCountHTML(final ComponentParameter cp,
				final ProcessModelBean processModel) {
			return wfpmdService.getProcessModelDomainR(getPermissionOrg(cp).getId(), processModel)
					.getProcessCount();
		}

		@Override
		protected String toOpeHTML(final ComponentParameter cp, final ProcessModelBean pm) {
			final StringBuilder sb = new StringBuilder();
			sb.append(new ButtonElement($m("ProcessModelMgrTPage.0")).setOnclick(
					JS.loc(uFactory.getUrl(cp, ProcessMgrTPage.class, "modelId=" + pm.getId()))));
			return sb.toString();
		}
	}
}
