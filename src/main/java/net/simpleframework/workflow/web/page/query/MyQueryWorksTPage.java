package net.simpleframework.workflow.web.page.query;

import static net.simpleframework.common.I18n.$m;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import net.simpleframework.ado.query.DataQueryUtils;
import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.common.ID;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.ctx.permission.PermissionDept;
import net.simpleframework.mvc.IForward;
import net.simpleframework.mvc.JavascriptForward;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.ButtonElement;
import net.simpleframework.mvc.common.element.ElementList;
import net.simpleframework.mvc.common.element.JS;
import net.simpleframework.mvc.common.element.LinkElement;
import net.simpleframework.mvc.common.element.SpanElement;
import net.simpleframework.mvc.common.element.TabButton;
import net.simpleframework.mvc.common.element.TabButtons;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.base.ajaxrequest.AjaxRequestBean;
import net.simpleframework.mvc.component.ui.pager.TablePagerBean;
import net.simpleframework.mvc.component.ui.pager.TablePagerColumn;
import net.simpleframework.mvc.component.ui.pager.db.AbstractDbTablePagerHandler;
import net.simpleframework.mvc.ctx.permission.IPagePermissionHandler;
import net.simpleframework.mvc.template.AbstractTemplatePage;
import net.simpleframework.workflow.engine.EProcessModelStatus;
import net.simpleframework.workflow.engine.EProcessStatus;
import net.simpleframework.workflow.engine.bean.ActivityBean;
import net.simpleframework.workflow.engine.bean.ProcessBean;
import net.simpleframework.workflow.engine.bean.ProcessModelBean;
import net.simpleframework.workflow.engine.bean.WorkitemBean;
import net.simpleframework.workflow.schema.AbstractTaskNode;
import net.simpleframework.workflow.web.IWorkflowWebContext;
import net.simpleframework.workflow.web.WorkflowUtils;
import net.simpleframework.workflow.web.page.AbstractItemsTPage;
import net.simpleframework.workflow.web.page.AbstractWorksTPage;
import net.simpleframework.workflow.web.page.query.MyQueryWorksTPages.MyQueryWorks_DeptTPage;
import net.simpleframework.workflow.web.page.query.MyQueryWorksTPages.MyQueryWorks_OrgTPage;
import net.simpleframework.workflow.web.page.query.MyQueryWorksTPages.MyQueryWorks_RoleTPage;
import net.simpleframework.workflow.web.page.t1.WorkflowFormPage;
import net.simpleframework.workflow.web.page.t1.WorkflowMonitorPage;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class MyQueryWorksTPage extends AbstractWorksTPage {

	@Override
	protected void onForward(final PageParameter pp) throws Exception {
		super.onForward(pp);
		pp.addImportCSS(AbstractItemsTPage.class, "/query_work.css");
		pp.addImportJavascript(AbstractItemsTPage.class, "/js/query_work.js");

		addTablePagerBean(pp);

		addAjaxRequest(pp, "MyQueryWorksTPage_workitem").setHandlerMethod("doWorkitem");

		// 工作列表窗口
		final AjaxRequestBean ajaxRequest = addAjaxRequest(pp, "MyQueryWorksTPage_detail_page",
				ProcessDetailPage.class);
		addWindowBean(pp, "MyQueryWorksTPage_detail", ajaxRequest).setWidth(400).setHeight(480)
				.setTitle($m("MyQueryWorksTPage.1"));
	}

	protected TablePagerBean addTablePagerBean(final PageParameter pp) {
		final TablePagerBean tablePager = addTablePagerBean(pp, "MyQueryWorksTPage_tbl",
				MyQueryWorksTbl.class);
		tablePager.addColumn(TC_TITLE()).addColumn(TC_PNO())
				.addColumn(TC_USER("userText", $m("ProcessMgrPage.0")))
				.addColumn(TC_CREATEDATE().setWidth(100).setFormat("yy-MM-dd HH:mm"))
				.addColumn(TC_STATUS(EProcessStatus.class).setColumnAlias("p.status"))
				.addColumn(TablePagerColumn.OPE(105));
		return tablePager;
	}

	@Override
	protected String getPageCSS(final PageParameter pp) {
		return "MyQueryWorksTPage";
	}

	public IForward doWorkitem(final ComponentParameter cp) {
		final ProcessBean process = WorkflowUtils.getProcessBean(cp);
		WorkitemBean workitem;
		if (process != null && (workitem = getOpenWorkitem(cp, process)) != null) {
			return new JavascriptForward(
					JS.loc(uFactory.getUrl(cp,
							(cp.getBoolParameter("monitor") ? WorkflowMonitorPage.class
									: WorkflowFormPage.class), workitem)));
		} else {
			return new JavascriptForward("alert('").append($m("MyQueryWorksTPage.7")).append("');");
		}
	}

	protected WorkitemBean getOpenWorkitem(final PageParameter pp, final ProcessBean process) {
		return wfwService.getWorkitems(process, pp.getLoginId()).iterator().next();
	}

	@Override
	public ElementList getLeftElements(final PageParameter pp) {
		final ElementList el = ElementList.of();
		final ProcessModelBean pm = WorkflowUtils.getProcessModel(pp);
		if (pm != null) {
			el.add(new SpanElement(pm.getModelText()));
			el.add(SpanElement.SPACE);
			el.append(LinkElement.style2($m("MyQueryWorksTPage.9")).setOnclick(
					"$Actions.reloc('modelId=');"));
		}
		return el;
	}

	@Override
	public ElementList getRightElements(final PageParameter pp) {
		final TabButtons tabs = TabButtons.of(new TabButton($m("MyQueryWorksTPage.4"), uFactory
				.getUrl(pp, MyQueryWorksTPage.class)));
		final IWorkflowWebContext ctx = (IWorkflowWebContext) workflowContext;
		if (pp.isLmember(ctx.getQueryWorks_DeptRole(pp))) {
			tabs.append(new TabButton(pp.getLdept(), uFactory.getUrl(pp, MyQueryWorks_DeptTPage.class)));
		}
		if (pp.isLmember(ctx.getQueryWorks_OrgRole(pp))) {
			tabs.append(new TabButton($m("MyQueryWorksTPage.5"), uFactory.getUrl(pp,
					MyQueryWorks_OrgTPage.class)));
		}
		tabs.append(new TabButton($m("MyQueryWorksTPage.6"), uFactory.getUrl(pp,
				MyQueryWorks_RoleTPage.class)));
		return ElementList.of(createTabsElement(pp, tabs));
	}

	@Override
	public String toCategoryHTML(final PageParameter pp) {
		final StringBuilder sb = new StringBuilder();
		final List<ProcessModelBean> models = DataQueryUtils.toList(wfpmService.getModelListByDomain(
				pp.getLdept().getDomainId(), EProcessModelStatus.deploy));
		wfpmService.sort(models);

		final Map<String, List<ProcessModelBean>> gmap = new LinkedHashMap<String, List<ProcessModelBean>>();
		for (final ProcessModelBean pm : models) {
			final String[] arr = StringUtils.split(pm.getModelText(), ".");
			String key;
			if (arr.length > 1) {
				key = arr[0];
			} else {
				key = $m("MyInitiateItemsGroupTPage.0");
			}
			List<ProcessModelBean> list = gmap.get(key);
			if (list == null) {
				gmap.put(key, list = new ArrayList<ProcessModelBean>());
			}
			list.add(pm);
		}

		for (final Map.Entry<String, List<ProcessModelBean>> e : gmap.entrySet()) {
			final String key = e.getKey();
			final List<ProcessModelBean> val = e.getValue();
			sb.append("<div class='gitem'>");
			sb.append(new SpanElement(key).setClassName("glbl"));
			final int size = val.size();
			if (size > 0) {
				sb.append(new SpanElement("(" + size + ")").setClassName("gsize"));
			}
			sb.append(" <div class='psub' style='display: none'>");
			sb.append(" <div class='psep'></div>");
			for (final ProcessModelBean pm : val) {
				sb.append("<div class='pitem'>");
				final String mtxt = pm.getModelText();
				final int p = mtxt.indexOf('.');
				sb.append(new LinkElement(p > 0 ? mtxt.substring(p + 1) : mtxt)
						.setOnclick("$Actions.reloc('modelId=" + pm.getId() + "');"));
				sb.append("</div>");
			}
			sb.append(" </div>");
			sb.append("</div>");
		}
		return sb.toString();
	}

	public static class MyQueryWorksTbl extends AbstractDbTablePagerHandler {
		@Override
		public IDataQuery<?> createDataObjectQuery(final ComponentParameter cp) {
			return wfpService.getProcessWlist(cp.getLoginId(), WorkflowUtils.getProcessModel(cp));
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
	}

	public static class ProcessDetailPage extends AbstractTemplatePage {

		class Tag {
			int c;

			String s;
		}

		@Override
		protected String toHtml(final PageParameter pp, final Map<String, Object> variables,
				final String currentVariable) throws IOException {
			final StringBuilder sb = new StringBuilder();
			final ID loginId = pp.getLoginId();
			final ProcessBean process = WorkflowUtils.getProcessBean(pp);
			sb.append("<div class='ProcessDetailPage'>");
			sb.append("<div class='ptitle'>").append(process).append("</div>");
			sb.append("<table class='form_tbl' cellspacing='1'>");
			sb.append("  <tr>");
			sb.append("    <td class='l'>#(MyQueryWorksTPage.13)</td>");
			sb.append("    <td class='v'>");
			final LinkedHashSet<String> dtags = new LinkedHashSet<String>();
			final LinkedHashMap<ID, Integer> utags = new LinkedHashMap<ID, Integer>();
			List<WorkitemBean> list = wfwService.getWorkitems(process, null);
			final IPagePermissionHandler hdl = pp.getPermission();
			for (int i = list.size() - 1; i >= 0; i--) {
				// 部门
				final WorkitemBean workitem = list.get(i);
				final PermissionDept dept = hdl.getDept(workitem.getDeptId2());
				dtags.add(dept.toString());

				// 用户
				final ID userId = workitem.getUserId2();
				if (userId.equals(loginId)) {
					continue;
				}
				final Integer oj = utags.get(userId);
				if (oj == null) {
					utags.put(userId, 1);
				} else {
					utags.put(userId, oj + 1);
				}
			}
			for (final String e : dtags) {
				sb.append("<span class='ptag'>").append(e).append("</span>");
			}
			sb.append("    </td>");
			sb.append("  </tr>");
			sb.append("  <tr>");
			sb.append("    <td class='l'>#(MyQueryWorksTPage.14)</td>");
			sb.append("    <td class='v'>");
			final LinkedHashMap<AbstractTaskNode, Integer> wtags = new LinkedHashMap<AbstractTaskNode, Integer>();
			list = wfwService.getWorkitems(process, loginId);
			for (int i = list.size() - 1; i >= 0; i--) {
				final ActivityBean activity = wfwService.getActivity(list.get(i));
				final AbstractTaskNode tasknode = wfaService.getTaskNode(activity);
				final Integer oj = wtags.get(tasknode);
				if (oj == null) {
					wtags.put(tasknode, 1);
				} else {
					wtags.put(tasknode, oj + 1);
				}
			}
			for (final Map.Entry<AbstractTaskNode, Integer> e : wtags.entrySet()) {
				sb.append("<span class='ptag'>");
				sb.append(e.getKey()).append(" (").append(e.getValue()).append(")");
				sb.append("</span>");
			}
			sb.append("    </td>");
			sb.append("  </tr>");
			sb.append("  <tr>");
			sb.append("    <td class='l'>#(MyQueryWorksTPage.15)</td>");
			sb.append("    <td class='v'>");
			for (final Map.Entry<ID, Integer> e : utags.entrySet()) {
				sb.append("<span class='ptag'>");
				sb.append(hdl.getUser(e.getKey())).append(" (").append(e.getValue()).append(")");
				sb.append("</span>");
			}
			sb.append("    </td>");
			sb.append("  </tr>");
			sb.append("</table>");
			sb.append("</div>");
			return sb.toString();
		}
	}
}