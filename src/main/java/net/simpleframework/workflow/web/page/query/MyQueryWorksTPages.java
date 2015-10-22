package net.simpleframework.workflow.web.page.query;

import static net.simpleframework.common.I18n.$m;

import java.util.List;
import java.util.Map;

import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.common.ID;
import net.simpleframework.common.coll.ArrayUtils;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.ctx.permission.PermissionDept;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.Checkbox;
import net.simpleframework.mvc.common.element.ETextAlign;
import net.simpleframework.mvc.common.element.ElementList;
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
import net.simpleframework.workflow.web.WorkflowUtils;
import net.simpleframework.workflow.web.page.IWorkflowPageAware;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public abstract class MyQueryWorksTPages implements IWorkflowPageAware {

	public static class MyQueryWorks_OrgTPage extends MyQueryWorksTPage {
		@Override
		protected TablePagerBean addTablePagerBean(final PageParameter pp) {
			return (TablePagerBean) super.addTablePagerBean(pp).setHandlerClass(
					MyQueryWorks_OrgTbl.class);
		}

		@Override
		protected WorkitemBean getOpenWorkitem(final PageParameter pp, final ProcessBean process) {
			return wfwService.getWorkitems(process, null).iterator().next();
		}

		public static class MyQueryWorks_OrgTbl extends MyQueryWorksTbl {
			@Override
			public IDataQuery<?> createDataObjectQuery(final ComponentParameter cp) {
				return wfpService.getProcessWlistInDomain(cp.getLogin().getDomainId(),
						WorkflowUtils.getProcessModel(cp));
			}
		}
	}

	public static class MyQueryWorks_DeptTPage extends MyQueryWorks_OrgTPage {
		@Override
		protected TablePagerBean addTablePagerBean(final PageParameter pp) {
			return (TablePagerBean) super.addTablePagerBean(pp).setHandlerClass(
					MyQueryWorks_DeptTbl.class);
		}

		@Override
		public ElementList getRightElements(final PageParameter pp) {
			final ElementList el = super.getRightElements(pp);
			if (pp.getLdept().hasChild()) {
				el.add(new Checkbox("idMyQueryWorks_DeptTPage_children", $m("MyQueryWorksTPage.2"))
						.setOnchange("$Actions['MyQueryWorksTPage_tbl']('child=' + this.checked);"));
			}
			return el;
		}

		public static class MyQueryWorks_DeptTbl extends MyQueryWorksTbl {
			@Override
			public IDataQuery<?> createDataObjectQuery(final ComponentParameter cp) {
				final boolean child = cp.getBoolParameter("child");
				cp.addFormParameter("child", child);
				final PermissionDept dept = cp.getLogin().getDept();
				final List<Object> deptIds = ArrayUtils.toParams(dept.getId());
				if (child) {
					for (final PermissionDept _dept : dept.getChildren()) {
						deptIds.add(_dept.getId());
					}
				}
				return wfpService.getProcessWlistInDept(deptIds.toArray(new ID[deptIds.size()]),
						WorkflowUtils.getProcessModel(cp));
			}
		}
	}

	public static class MyQueryWorks_RoleTPage extends MyQueryWorksTPage {
		@Override
		protected TablePagerBean addTablePagerBean(final PageParameter pp) {
			return (TablePagerBean) super.addTablePagerBean(pp).setHandlerClass(
					MyQueryWorks_RoleTbl.class);
		}

		public static class MyQueryWorks_RoleTbl extends MyQueryWorksTbl {
			@Override
			public IDataQuery<?> createDataObjectQuery(final ComponentParameter cp) {
				return null;
			}
		}
	}

	public static class ProcessModelSelectPage extends OneTableTemplatePage {

		@Override
		protected void onForward(final PageParameter pp) throws Exception {
			super.onForward(pp);
			final TablePagerBean tablePager = (TablePagerBean) addTablePagerBean(pp,
					"ProcessModelSelectPage_tbl", ProcessModelSelectTbl.class).setShowCheckbox(false)
					.setShowLineNo(false).setPagerBarLayout(EPagerBarLayout.none);
			tablePager
					.addColumn(new TablePagerColumn("modelText", $m("MyQueryWorksTPage.10")))
					.addColumn(
							new TablePagerColumn("modelVer", $m("MyQueryWorksTPage.11"), 80)
									.setFilterSort(false).setTextAlign(ETextAlign.center))
					.addColumn(TablePagerColumn.OPE(80));
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
				ope.append(LinkButton.corner($m("MyQueryWorksTPage.12")).setOnclick(
						"$Actions.reloc('modelId=" + pm.getId() + "');"));
				return ope.toString();
			}
		}
	}
}
