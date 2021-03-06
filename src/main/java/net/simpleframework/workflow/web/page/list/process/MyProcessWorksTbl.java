package net.simpleframework.workflow.web.page.list.process;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.common.ID;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.coll.ArrayUtils;
import net.simpleframework.ctx.permission.PermissionDept;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ui.pager.AbstractTablePagerSchema;
import net.simpleframework.mvc.component.ui.pager.TablePagerColumns;
import net.simpleframework.mvc.component.ui.pager.db.AbstractDbTablePagerHandler;
import net.simpleframework.workflow.engine.bean.ProcessModelBean;
import net.simpleframework.workflow.web.WorkflowUtils;
import net.simpleframework.workflow.web.page.IWorkflowPageAware;
import net.simpleframework.workflow.web.page.list.process.IProcessWorksHandler.EProcessWorks;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class MyProcessWorksTbl extends AbstractDbTablePagerHandler implements IWorkflowPageAware {

	@Override
	public IDataQuery<?> createDataObjectQuery(final ComponentParameter cp) {
		final ProcessModelBean pm = WorkflowUtils.getProcessModel(cp);
		if (pm != null) {
			cp.addFormParameter("modelId", pm.getId());
		} else {
			final String _gstr = cp.getParameter("pgroup");
			if (StringUtils.hasText(_gstr)) {
				cp.addFormParameter("pgroup", _gstr);
			}
		}

		return AbstractProcessWorksHandler.getProcessWorksHandler(cp).createDataObjectQuery(cp,
				getProcessWorks());
	}

	@Override
	protected void doExcelExport(final ComponentParameter cp, final IDataQuery<?> dQuery,
			final AbstractTablePagerSchema tablePagerData, final TablePagerColumns columns)
			throws IOException {
		AbstractProcessWorksHandler.getProcessWorksHandler(cp).doExcelExport(cp, dQuery,
				tablePagerData, columns);
	}

	@Override
	protected Map<String, Object> getRowData(final ComponentParameter cp, final Object dataObject) {
		return AbstractProcessWorksHandler.getProcessWorksHandler(cp).getRowData(cp, dataObject,
				getProcessWorks());
	}

	protected EProcessWorks getProcessWorks() {
		return EProcessWorks.my;
	}

	public static class MyProcessWorks_DeptTbl extends MyProcessWorksTbl {

		@Override
		public IDataQuery<?> createDataObjectQuery(final ComponentParameter cp) {
			PermissionDept dept = cp.getDept(ID.of(cp.getParameter("deptId")));
			if (!dept.exists()) {
				dept = cp.getLdept();
			} else {
				cp.addFormParameter("deptId", dept.getId());
			}
			cp.setAttr("dept", dept);

			final List<Object> deptIds = ArrayUtils.toParams(dept.getId());
			final boolean child = cp.getBoolParameter("child");
			cp.addFormParameter("child", child);
			if (child) {
				for (final PermissionDept _dept : dept.getDeptChildren()) {
					deptIds.add(_dept.getId());
				}
			}
			cp.setAttr("deptIds", deptIds);

			return super.createDataObjectQuery(cp);
		}

		@Override
		protected EProcessWorks getProcessWorks() {
			return EProcessWorks.dept;
		}
	}

	public static class MyProcessWorks_OrgTbl extends MyProcessWorksTbl {

		@Override
		protected EProcessWorks getProcessWorks() {
			return EProcessWorks.org;
		}
	}

	public static class MyProcessWorks_RoleTbl extends MyProcessWorksTbl {

		@Override
		protected EProcessWorks getProcessWorks() {
			return EProcessWorks.role;
		}
	}
}