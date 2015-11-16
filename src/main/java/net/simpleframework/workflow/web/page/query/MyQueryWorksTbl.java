package net.simpleframework.workflow.web.page.query;

import static net.simpleframework.common.I18n.$m;

import java.util.List;
import java.util.Map;

import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.common.ID;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.coll.ArrayUtils;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.ctx.permission.PermissionDept;
import net.simpleframework.mvc.common.element.ButtonElement;
import net.simpleframework.mvc.common.element.LinkElement;
import net.simpleframework.mvc.common.element.SpanElement;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ui.pager.TablePagerColumn;
import net.simpleframework.mvc.component.ui.pager.db.AbstractDbTablePagerHandler;
import net.simpleframework.workflow.engine.bean.ProcessBean;
import net.simpleframework.workflow.engine.bean.ProcessModelBean;
import net.simpleframework.workflow.web.WorkflowUtils;
import net.simpleframework.workflow.web.page.IWorkflowPageAware;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class MyQueryWorksTbl extends AbstractDbTablePagerHandler implements IWorkflowPageAware {
	@Override
	public IDataQuery<?> createDataObjectQuery(final ComponentParameter cp) {
		final ProcessModelBean pm = WorkflowUtils.getProcessModel(cp);
		if (pm != null) {
			cp.addFormParameter("modelId", pm.getId());
		}
		return wfpService.getProcessWlist(cp.getLoginId(), pm);
	}

	@Override
	protected Map<String, Object> getRowData(final ComponentParameter cp, final Object dataObject) {
		final ProcessBean process = (ProcessBean) dataObject;
		final KVMap row = new KVMap();

		row.add("title", toTitleHTML(cp, process))
				.add("userText", SpanElement.color060(process.getUserText()))
				.add("createDate", process.getCreateDate())
				.add("status", WorkflowUtils.toStatusHTML(cp, process.getStatus()));
		row.add(TablePagerColumn.OPE, toOpeHTML(cp, process));
		return row;
	}

	protected String toTitleHTML(final ComponentParameter cp, final ProcessBean process) {
		final StringBuilder t = new StringBuilder();
		// final int c = Convert.toInt(process.getAttr("c"));
		// if (c > 0) {
		// t.append("[").append(c).append("] ");
		// }

		final String deptTxt = cp.getPermission().getDept(process.getDeptId()).toString();
		t.append("[").append(SpanElement.color777(deptTxt).setTitle(deptTxt)).append("] ");
		t.append(new LinkElement(WorkflowUtils.getProcessTitle(process)).setOnclick(
				"$Actions['MyQueryWorksTPage_workitem']('processId=" + process.getId() + "');")
				.setColor_gray(!StringUtils.hasText(process.getTitle())));
		return t.toString();
	}

	protected String toOpeHTML(final ComponentParameter cp, final ProcessBean process) {
		final StringBuilder ope = new StringBuilder();
		ope.append(new ButtonElement($m("MyQueryWorksTPage.1"))
				.setOnclick("$Actions['MyQueryWorksTPage_detail']('processId=" + process.getId()
						+ "');"));
		ope.append(SpanElement.SPACE).append(
				new ButtonElement($m("MyRunningWorklistTbl.3"))
						.setOnclick("$Actions['MyQueryWorksTPage_workitem']('processId="
								+ process.getId() + "&monitor=true');"));
		return ope.toString();
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
			final ProcessModelBean pm = WorkflowUtils.getProcessModel(cp);
			if (pm != null) {
				cp.addFormParameter("modelId", pm.getId());
			}
			return wfpService.getProcessWlistInDept(deptIds.toArray(new ID[deptIds.size()]), pm);
		}
	}

	public static class MyQueryWorks_OrgTbl extends MyQueryWorksTbl {
		@Override
		public IDataQuery<?> createDataObjectQuery(final ComponentParameter cp) {
			final ProcessModelBean pm = WorkflowUtils.getProcessModel(cp);
			if (pm != null) {
				cp.addFormParameter("modelId", pm.getId());
			}
			return wfpService.getProcessWlistInDomain(cp.getLDomainId(), pm);
		}
	}

	public static class MyQueryWorks_RoleTbl extends MyQueryWorksTbl {
		@Override
		public IDataQuery<?> createDataObjectQuery(final ComponentParameter cp) {
			final ProcessModelBean pm = WorkflowUtils.getProcessModel(cp);
			if (pm != null) {
				cp.addFormParameter("modelId", pm.getId());
			}
			return null;
		}
	}
}