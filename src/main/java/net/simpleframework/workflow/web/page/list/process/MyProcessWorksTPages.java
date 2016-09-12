package net.simpleframework.workflow.web.page.list.process;

import static net.simpleframework.common.I18n.$m;

import java.util.Map;

import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.LinkButton;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ui.pager.EPagerBarLayout;
import net.simpleframework.mvc.component.ui.pager.TablePagerBean;
import net.simpleframework.mvc.component.ui.pager.TablePagerColumn;
import net.simpleframework.mvc.component.ui.pager.db.AbstractDbTablePagerHandler;
import net.simpleframework.mvc.template.lets.OneTableTemplatePage;
import net.simpleframework.workflow.engine.EProcessModelStatus;
import net.simpleframework.workflow.engine.bean.ProcessBean;
import net.simpleframework.workflow.engine.bean.ProcessModelBean;
import net.simpleframework.workflow.engine.bean.WorkitemBean;
import net.simpleframework.workflow.web.page.IWorkflowPageAware;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public abstract class MyProcessWorksTPages implements IWorkflowPageAware {

	public static class MyProcessWorks_OrgTPage extends MyProcessWorksTPage {

		@Override
		protected WorkitemBean getOpenWorkitem(final PageParameter pp, final ProcessBean process) {
			return wfwService.getWorkitems(process, null).iterator().next();
		}
	}

	public static class MyProcessWorks_DeptTPage extends MyProcessWorks_OrgTPage {
	}

	public static class MyProcessWorks_RoleTPage extends MyProcessWorksTPage {
	}

	public static class ProcessModelSelectPage extends OneTableTemplatePage {

		@Override
		protected void onForward(final PageParameter pp) throws Exception {
			super.onForward(pp);
			final TablePagerBean tablePager = (TablePagerBean) addTablePagerBean(pp,
					"ProcessModelSelectPage_tbl", ProcessModelSelectTbl.class).setShowCheckbox(false)
					.setShowLineNo(false).setPagerBarLayout(EPagerBarLayout.none);
			tablePager
					.addColumn(new TablePagerColumn("modelText", $m("MyProcessWorksTPage.10")))
					.addColumn(
							new TablePagerColumn("modelVer", $m("MyProcessWorksTPage.11"), 80)
									.setFilterSort(false).center()).addColumn(TablePagerColumn.OPE(80));
		}

		public static class ProcessModelSelectTbl extends AbstractDbTablePagerHandler {
			@Override
			public IDataQuery<?> createDataObjectQuery(final ComponentParameter cp) {
				return wfpmService.getModelList(EProcessModelStatus.deploy);
			}

			@Override
			protected Map<String, Object> getRowData(final ComponentParameter cp,
					final Object dataObject) {
				final ProcessModelBean pm = (ProcessModelBean) dataObject;
				final KVMap data = new KVMap();
				data.add("modelText", pm.getModelText()).add("modelVer", pm.getModelVer())
						.add(TablePagerColumn.OPE, toOpeHTML(cp, pm));
				return data;
			}

			protected String toOpeHTML(final ComponentParameter cp, final ProcessModelBean pm) {
				final StringBuilder ope = new StringBuilder();
				ope.append(LinkButton.corner($m("MyProcessWorksTPage.12")).setOnclick(
						"$Actions.reloc('modelId=" + pm.getId() + "');"));
				return ope.toString();
			}
		}
	}
}
