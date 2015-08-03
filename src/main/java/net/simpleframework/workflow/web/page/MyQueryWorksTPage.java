package net.simpleframework.workflow.web.page;

import static net.simpleframework.common.I18n.$m;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.common.ID;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.ctx.permission.PermissionDept;
import net.simpleframework.mvc.IForward;
import net.simpleframework.mvc.JavascriptForward;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.ButtonElement;
import net.simpleframework.mvc.common.element.ETextAlign;
import net.simpleframework.mvc.common.element.ElementList;
import net.simpleframework.mvc.common.element.LinkButton;
import net.simpleframework.mvc.common.element.LinkElement;
import net.simpleframework.mvc.common.element.SpanElement;
import net.simpleframework.mvc.common.element.TabButton;
import net.simpleframework.mvc.common.element.TabButtons;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.base.ajaxrequest.AjaxRequestBean;
import net.simpleframework.mvc.component.ui.pager.EPagerBarLayout;
import net.simpleframework.mvc.component.ui.pager.TablePagerBean;
import net.simpleframework.mvc.component.ui.pager.TablePagerColumn;
import net.simpleframework.mvc.component.ui.pager.db.AbstractDbTablePagerHandler;
import net.simpleframework.mvc.ctx.permission.IPagePermissionHandler;
import net.simpleframework.mvc.template.AbstractTemplatePage;
import net.simpleframework.mvc.template.lets.OneTableTemplatePage;
import net.simpleframework.workflow.engine.EProcessModelStatus;
import net.simpleframework.workflow.engine.EProcessStatus;
import net.simpleframework.workflow.engine.bean.ActivityBean;
import net.simpleframework.workflow.engine.bean.ProcessBean;
import net.simpleframework.workflow.engine.bean.ProcessModelBean;
import net.simpleframework.workflow.engine.bean.WorkitemBean;
import net.simpleframework.workflow.schema.AbstractTaskNode;
import net.simpleframework.workflow.web.IWorkflowWebContext;
import net.simpleframework.workflow.web.WorkflowUtils;
import net.simpleframework.workflow.web.page.MyQueryWorksTPages.MyQueryWorks_DeptTPage;
import net.simpleframework.workflow.web.page.MyQueryWorksTPages.MyQueryWorks_OrgTPage;
import net.simpleframework.workflow.web.page.MyQueryWorksTPages.MyQueryWorks_RoleTPage;
import net.simpleframework.workflow.web.page.t1.WorkflowFormPage;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class MyQueryWorksTPage extends AbstractItemsTPage {

	@Override
	protected void onForward(final PageParameter pp) throws Exception {
		super.onForward(pp);

		addTablePagerBean(pp);

		addAjaxRequest(pp, "MyQueryWorksTPage_workitem").setHandlerMethod("doWorkitem");

		// 工作列表窗口
		AjaxRequestBean ajaxRequest = addAjaxRequest(pp, "MyQueryWorksTPage_detail_page",
				ProcessDetailPage.class);
		addWindowBean(pp, "MyQueryWorksTPage_detail", ajaxRequest).setWidth(400).setHeight(480)
				.setTitle($m("MyQueryWorksTPage.1"));

		// 流程选择
		ajaxRequest = addAjaxRequest(pp, "MyQueryWorksTPage_pmselect_page",
				ProcessModelSelectPage.class);
		addWindowBean(pp, "MyQueryWorksTPage_pmselect", ajaxRequest).setPopup(true).setWidth(680)
				.setHeight(450).setTitle($m("MyQueryWorksTPage.9"));
	}

	protected TablePagerBean addTablePagerBean(final PageParameter pp) {
		final TablePagerBean tablePager = addTablePagerBean(pp, "MyQueryWorksTPage_tbl",
				MyQueryWorksTbl.class);
		tablePager.addColumn(TC_TITLE()).addColumn(TC_PNO())
				.addColumn(new TablePagerColumn("userText", $m("ProcessMgrPage.0"), 100))
				.addColumn(TC_CREATEDATE())
				.addColumn(TC_STATUS(EProcessStatus.class).setColumnAlias("p.status"))
				.addColumn(TablePagerColumn.OPE().setWidth(70));
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
			return new JavascriptForward("$Actions.loc('"
					+ uFactory.getUrl(cp, WorkflowFormPage.class, workitem) + "');");
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
			el.append(
					new LinkElement("取消过滤").setClassName("simple_btn2").setOnclick(
							"location.href = location.href.addParameter('modelId=');")).append(
					SpanElement.SPACE);
		}
		el.append(new LinkElement(pm != null ? pm.getModelText() : $m("MyQueryWorksTPage.8"))
				.setClassName("simple_btn2").setOnclick("$Actions['MyQueryWorksTPage_pmselect']();"));
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

	public static class MyQueryWorksTbl extends AbstractDbTablePagerHandler {
		@Override
		public IDataQuery<?> createDataObjectQuery(final ComponentParameter cp) {
			return wfpService.getProcessWlist(cp.getLoginId(), WorkflowUtils.getProcessModel(cp));
		}

		@Override
		protected Map<String, Object> getRowData(final ComponentParameter cp, final Object dataObject) {
			final ProcessBean process = (ProcessBean) dataObject;
			final KVMap row = new KVMap();

			row.add("title", toTitleHTML(cp, process)).add("userText", process.getUserText())
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
			sb.append("    <td class='l'>参与的部门</td>");
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
			sb.append("    <td class='l'>我参与的工作</td>");
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
			sb.append("    <td class='l'>其他参与人员</td>");
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
					.addColumn(TablePagerColumn.OPE().setWidth(80));
		}
	}

	public static class ProcessModelSelectTbl extends AbstractDbTablePagerHandler {
		@Override
		public IDataQuery<?> createDataObjectQuery(final ComponentParameter cp) {
			return wfpmService.getModelList(EProcessModelStatus.deploy);
		}

		@Override
		protected Map<String, Object> getRowData(final ComponentParameter cp, final Object dataObject) {
			final ProcessModelBean pm = (ProcessModelBean) dataObject;
			final KVMap data = new KVMap();
			data.add("modelText", pm.getModelText()).add("modelVer", pm.getModelVer())
					.add(TablePagerColumn.OPE, toOpeHTML(cp, pm));
			return data;
		}

		protected String toOpeHTML(final ComponentParameter cp, final ProcessModelBean pm) {
			final StringBuilder ope = new StringBuilder();
			ope.append(LinkButton.corner($m("MyQueryWorksTPage.12")).setOnclick(
					"location.href = location.href.addParameter('modelId=" + pm.getId() + "');"));
			return ope.toString();
		}
	}
}