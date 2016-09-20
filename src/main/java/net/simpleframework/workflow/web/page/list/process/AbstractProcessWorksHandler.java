package net.simpleframework.workflow.web.page.list.process;

import static net.simpleframework.common.I18n.$m;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.common.ID;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.common.th.NotImplementedException;
import net.simpleframework.ctx.IApplicationContext;
import net.simpleframework.ctx.hdl.AbstractScanHandler;
import net.simpleframework.ctx.permission.PermissionDept;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.ButtonElement;
import net.simpleframework.mvc.common.element.Checkbox;
import net.simpleframework.mvc.common.element.ETextAlign;
import net.simpleframework.mvc.common.element.ElementList;
import net.simpleframework.mvc.common.element.LinkElement;
import net.simpleframework.mvc.common.element.SpanElement;
import net.simpleframework.mvc.common.element.TagElement;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ui.pager.AbstractTablePagerSchema;
import net.simpleframework.mvc.component.ui.pager.TablePagerBean;
import net.simpleframework.mvc.component.ui.pager.TablePagerColumn;
import net.simpleframework.mvc.component.ui.pager.TablePagerColumns;
import net.simpleframework.mvc.template.struct.FilterButtons;
import net.simpleframework.workflow.engine.bean.ProcessBean;
import net.simpleframework.workflow.engine.bean.ProcessModelBean;
import net.simpleframework.workflow.web.WorkflowUtils;
import net.simpleframework.workflow.web.page.AbstractWorksTPage;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public abstract class AbstractProcessWorksHandler extends AbstractScanHandler
		implements IProcessWorksHandler {

	static Map<String, IProcessWorksHandler> regists = new HashMap<String, IProcessWorksHandler>();

	@Override
	public void onScan(final IApplicationContext application) throws Exception {
		final String[] modelnames = getModelNames();
		if (modelnames == null) {
			return;
		}

		for (final String modelname : modelnames) {
			final ProcessModelBean pm = wfpmService.getProcessModelByName(modelname);
			if (pm == null) {
				oprintln(new StringBuilder("[IProcessWorksHandler] ")
						.append($m("AbstractProcessWorksHandler.1")).append(" - ")
						.append(getClass().getName()));
				return;
			}

			if (regists.containsKey(modelname)) {
				oprintln(new StringBuilder("[IProcessWorksHandler, name: ").append(modelname)
						.append("] ").append($m("AbstractProcessWorksHandler.2")).append(" - ")
						.append(getClass().getName()));
				return;
			}
			regists.put(modelname, this);
		}
	}

	@Override
	public ElementList getLeftElements(final PageParameter pp, final EProcessWorks qw) {
		if (qw == EProcessWorks.dept) {
			PermissionDept dept = pp.getDept(ID.of(pp.getParameter("deptId")));
			if (!dept.exists()) {
				dept = pp.getLdept();
			}
			if (dept.getParentId() != null && dept.getDeptChildren().size() > 0) {
				return ElementList.of(
						new Checkbox("idMyProcessWorks_DeptTPage_children", $m("MyProcessWorksTPage.2"))
								.setOnchange(
										"$Actions['MyProcessWorksTPage_tbl']('child=' + this.checked);"));
			}
		}
		return null;
	}

	@Override
	public FilterButtons getFilterButtons(final PageParameter pp, final EProcessWorks qw) {
		return null;
	}

	@Override
	public void doTablePagerInit(final PageParameter pp, final TablePagerBean tablePager,
			final EProcessWorks qw) {
		tablePager.addColumn(TablePagerColumn.ICON()).addColumn(AbstractWorksTPage.TC_TITLE())
				.addColumn(AbstractWorksTPage.TC_PNO())
				.addColumn(AbstractWorksTPage.TC_USER("userText", $m("ProcessMgrPage.0"))
						.setTextAlign(ETextAlign.left).setWidth(100))
				.addColumn(AbstractWorksTPage.TC_CREATEDATE().setWidth(100).setFormat("yy-MM-dd HH:mm"))
				.addColumn(TablePagerColumn.OPE(105));
	}

	@Override
	public IDataQuery<?> createDataObjectQuery(final ComponentParameter cp, final EProcessWorks qw) {
		return null;
	}

	@Override
	public Map<String, Object> getRowData(final ComponentParameter cp, final Object dataObject,
			final EProcessWorks qw) {
		final ProcessBean process = (ProcessBean) dataObject;
		final KVMap row = new KVMap();
		row.add(TablePagerColumn.ICON, WorkflowUtils.getStatusIcon(cp, process.getStatus()))
				.add("title", toTitleHTML(cp, process)).add("userText", toUserHTML(cp, process))
				.add("createDate", process.getCreateDate());
		row.add("pno", process.getPno());
		row.add(TablePagerColumn.OPE, toOpeHTML(cp, process));
		return row;
	}

	protected String toUserHTML(final ComponentParameter cp, final ProcessBean process) {
		final StringBuilder sb = new StringBuilder();
		sb.append(SpanElement.color555(process.getUserText()));
		sb.append(TagElement.br());
		sb.append(SpanElement.color999(cp.getPermission().getDept(process.getDeptId())));
		return sb.toString();
	}

	protected String toTitleHTML(final ComponentParameter cp, final ProcessBean process) {
		final StringBuilder sb = new StringBuilder();
		// final int c = Convert.toInt(process.getAttr("c"));
		// if (c > 0) {
		// t.append("[").append(c).append("] ");
		// }

		// final String deptTxt =
		// cp.getPermission().getDept(process.getDeptId()).toString();
		// sb.append("[").append(SpanElement.color777(deptTxt).setTitle(deptTxt)).append("]
		// ");
		sb.append(new LinkElement(WorkflowUtils.getProcessTitle(process))
				.setOnclick(
						"$Actions['MyProcessWorksTPage_workitem']('processId=" + process.getId() + "');")
				.setColor_gray(!StringUtils.hasText(process.getTitle())));
		return sb.toString();
	}

	protected String toOpeHTML(final ComponentParameter cp, final ProcessBean process) {
		final StringBuilder ope = new StringBuilder();
		ope.append(new ButtonElement($m("MyProcessWorksTPage.1")).setOnclick(
				"$Actions['MyProcessWorksTPage_detail']('processId=" + process.getId() + "');"));
		ope.append(SpanElement.SPACE)
				.append(new ButtonElement($m("MyRunningWorklistTbl.3"))
						.setOnclick("$Actions['MyProcessWorksTPage_workitem']('processId="
								+ process.getId() + "&monitor=true');"));
		return ope.toString();
	}

	@Override
	public String toString() {
		return $m("AbstractProcessWorksHandler.0") + " - " + getClass().getName();
	}

	public static IProcessWorksHandler getProcessWorksHandler(final PageParameter pp) {
		final ProcessModelBean pm = WorkflowUtils.getProcessModel(pp);
		IProcessWorksHandler hdl = null;
		if (pm != null) {
			hdl = AbstractProcessWorksHandler.regists.get(pm.getModelName());
		}
		if (hdl == null) {
			hdl = DefaultProcessWorksHandler.instance;
		}
		return hdl;
	}

	@Override
	public void doExcelExport(final ComponentParameter cp, final IDataQuery<?> dQuery,
			final AbstractTablePagerSchema tablePagerData, final TablePagerColumns columns)
			throws IOException {
		throw NotImplementedException.of(getClass(), "doExcelExport");
	}
}
