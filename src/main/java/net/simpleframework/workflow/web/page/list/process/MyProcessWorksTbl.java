package net.simpleframework.workflow.web.page.list.process;

import java.util.List;
import java.util.Map;

import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.common.ID;
import net.simpleframework.common.coll.ArrayUtils;
import net.simpleframework.ctx.permission.PermissionDept;
import net.simpleframework.mvc.component.ComponentParameter;
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
		final IDataQuery<?> dq = AbstractProcessWorksHandler.getProcessWorksHandler(cp)
				.createDataObjectQuery(cp, EProcessWorks.my);
		if (dq != null) {
			return dq;
		}

		final ProcessModelBean pm = WorkflowUtils.getProcessModel(cp);
		if (pm != null) {
			cp.addFormParameter("modelId", pm.getId());
		}
		return wfpService.getProcessWlist(cp.getLoginId(), pm);
	}

	@Override
	protected Map<String, Object> getRowData(final ComponentParameter cp, final Object dataObject) {
		return AbstractProcessWorksHandler.getProcessWorksHandler(cp).getRowData(cp, dataObject,
				EProcessWorks.my);
	}

	public static class MyProcessWorks_DeptTbl extends MyProcessWorksTbl {
		@Override
		public IDataQuery<?> createDataObjectQuery(final ComponentParameter cp) {
			final IDataQuery<?> dq = AbstractProcessWorksHandler.getProcessWorksHandler(cp)
					.createDataObjectQuery(cp, EProcessWorks.dept);
			if (dq != null) {
				return dq;
			}

			final boolean child = cp.getBoolParameter("child");
			cp.addFormParameter("child", child);
			final PermissionDept dept = cp.getLogin().getDept();
			final List<Object> deptIds = ArrayUtils.toParams(dept.getId());
			if (child) {
				for (final PermissionDept _dept : dept.getDeptChildren()) {
					deptIds.add(_dept.getId());
				}
			}
			final ProcessModelBean pm = WorkflowUtils.getProcessModel(cp);
			if (pm != null) {
				cp.addFormParameter("modelId", pm.getId());
			}
			return wfpService.getProcessWlistInDept(deptIds.toArray(new ID[deptIds.size()]), pm);
		}

		@Override
		protected Map<String, Object> getRowData(final ComponentParameter cp, final Object dataObject) {
			return AbstractProcessWorksHandler.getProcessWorksHandler(cp).getRowData(cp, dataObject,
					EProcessWorks.dept);
		}
	}

	public static class MyProcessWorks_OrgTbl extends MyProcessWorksTbl {
		@Override
		public IDataQuery<?> createDataObjectQuery(final ComponentParameter cp) {
			final IDataQuery<?> dq = AbstractProcessWorksHandler.getProcessWorksHandler(cp)
					.createDataObjectQuery(cp, EProcessWorks.org);
			if (dq != null) {
				return dq;
			}

			final ProcessModelBean pm = WorkflowUtils.getProcessModel(cp);
			if (pm != null) {
				cp.addFormParameter("modelId", pm.getId());
			}
			return wfpService.getProcessWlistInDomain(cp.getLDomainId(), pm);
		}

		@Override
		protected Map<String, Object> getRowData(final ComponentParameter cp, final Object dataObject) {
			return AbstractProcessWorksHandler.getProcessWorksHandler(cp).getRowData(cp, dataObject,
					EProcessWorks.org);
		}
	}

	public static class MyProcessWorks_RoleTbl extends MyProcessWorksTbl {
		@Override
		public IDataQuery<?> createDataObjectQuery(final ComponentParameter cp) {
			final IDataQuery<?> dq = AbstractProcessWorksHandler.getProcessWorksHandler(cp)
					.createDataObjectQuery(cp, EProcessWorks.role);
			if (dq != null) {
				return dq;
			}

			final ProcessModelBean pm = WorkflowUtils.getProcessModel(cp);
			if (pm != null) {
				cp.addFormParameter("modelId", pm.getId());
			}
			return null;
		}

		@Override
		protected Map<String, Object> getRowData(final ComponentParameter cp, final Object dataObject) {
			return AbstractProcessWorksHandler.getProcessWorksHandler(cp).getRowData(cp, dataObject,
					EProcessWorks.role);
		}
	}
}