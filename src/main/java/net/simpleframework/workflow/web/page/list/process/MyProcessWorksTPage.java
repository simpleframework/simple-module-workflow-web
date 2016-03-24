package net.simpleframework.workflow.web.page.list.process;

import static net.simpleframework.common.I18n.$m;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.simpleframework.ado.query.DataQueryUtils;
import net.simpleframework.common.StringUtils;
import net.simpleframework.mvc.AbstractMVCPage;
import net.simpleframework.mvc.IForward;
import net.simpleframework.mvc.JavascriptForward;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.ElementList;
import net.simpleframework.mvc.common.element.JS;
import net.simpleframework.mvc.common.element.LinkElement;
import net.simpleframework.mvc.common.element.SpanElement;
import net.simpleframework.mvc.common.element.SupElement;
import net.simpleframework.mvc.common.element.TabButton;
import net.simpleframework.mvc.common.element.TabButtons;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.base.ajaxrequest.AjaxRequestBean;
import net.simpleframework.mvc.component.ui.pager.TablePagerBean;
import net.simpleframework.mvc.component.ui.pager.db.AbstractDbTablePagerHandler;
import net.simpleframework.workflow.engine.EProcessModelStatus;
import net.simpleframework.workflow.engine.bean.ProcessBean;
import net.simpleframework.workflow.engine.bean.ProcessModelBean;
import net.simpleframework.workflow.engine.bean.WorkitemBean;
import net.simpleframework.workflow.web.IWorkflowWebContext;
import net.simpleframework.workflow.web.WorkflowUtils;
import net.simpleframework.workflow.web.page.AbstractWorksTPage;
import net.simpleframework.workflow.web.page.list.process.IProcessWorksHandler.EProcessWorks;
import net.simpleframework.workflow.web.page.list.process.MyProcessWorksTPages.MyProcessWorks_DeptTPage;
import net.simpleframework.workflow.web.page.list.process.MyProcessWorksTPages.MyProcessWorks_OrgTPage;
import net.simpleframework.workflow.web.page.list.process.MyProcessWorksTPages.MyProcessWorks_RoleTPage;
import net.simpleframework.workflow.web.page.list.process.MyProcessWorksTbl.MyProcessWorks_DeptTbl;
import net.simpleframework.workflow.web.page.list.process.MyProcessWorksTbl.MyProcessWorks_OrgTbl;
import net.simpleframework.workflow.web.page.list.process.MyProcessWorksTbl.MyProcessWorks_RoleTbl;
import net.simpleframework.workflow.web.page.t1.form.WorkflowFormPage;
import net.simpleframework.workflow.web.page.t1.form.WorkflowMonitorPage;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class MyProcessWorksTPage extends AbstractWorksTPage {

	@Override
	protected void onForward(final PageParameter pp) throws Exception {
		super.onForward(pp);
		pp.addImportCSS(MyProcessWorksTPage.class, "/process_work.css");
		pp.addImportJavascript(MyProcessWorksTPage.class, "/js/process_work.js");

		addTablePagerBean(pp);

		addAjaxRequest(pp, "MyProcessWorksTPage_workitem").setHandlerMethod("doWorkitem");

		// 工作列表窗口
		final AjaxRequestBean ajaxRequest = addAjaxRequest(pp, "MyProcessWorksTPage_detail_page",
				ProcessDetailPage.class);
		addWindowBean(pp, "MyProcessWorksTPage_detail", ajaxRequest).setWidth(400).setHeight(480)
				.setTitle($m("MyProcessWorksTPage.1"));
	}

	@Override
	protected String getPageCSS(final PageParameter pp) {
		return "MyProcessWorksTPage";
	}

	protected EProcessWorks qw;
	protected Class<? extends AbstractDbTablePagerHandler> tblClass;
	{
		if (MyProcessWorks_DeptTPage.class.isAssignableFrom(getClass())) {
			qw = EProcessWorks.dept;
			tblClass = MyProcessWorks_DeptTbl.class;
		} else if (MyProcessWorks_OrgTPage.class.isAssignableFrom(getClass())) {
			qw = EProcessWorks.org;
			tblClass = MyProcessWorks_OrgTbl.class;
		} else if (MyProcessWorks_RoleTPage.class.isAssignableFrom(getClass())) {
			qw = EProcessWorks.role;
			tblClass = MyProcessWorks_RoleTbl.class;
		} else {
			qw = EProcessWorks.my;
			tblClass = MyProcessWorksTbl.class;
		}
	}

	protected TablePagerBean addTablePagerBean(final PageParameter pp) {
		final TablePagerBean tablePager = addTablePagerBean(pp, "MyProcessWorksTPage_tbl", tblClass);
		AbstractProcessWorksHandler.getProcessWorksHandler(pp).doTablePagerInit(pp, tablePager, qw);
		return tablePager;
	}

	@Override
	public ElementList getLeftElements(final PageParameter pp) {
		return AbstractProcessWorksHandler.getProcessWorksHandler(pp).getLeftElements(pp, qw);
	}

	public IForward doWorkitem(final ComponentParameter cp) {
		final ProcessBean process = WorkflowUtils.getProcessBean(cp);
		WorkitemBean workitem;
		if (process != null && (workitem = getOpenWorkitem(cp, process)) != null) {
			return new JavascriptForward(JS.loc(
					uFactory.getUrl(cp, (cp.getBoolParameter("monitor") ? WorkflowMonitorPage.class
							: WorkflowFormPage.class), workitem), true));
		} else {
			return new JavascriptForward("alert('").append($m("MyProcessWorksTPage.7")).append("');");
		}
	}

	protected WorkitemBean getOpenWorkitem(final PageParameter pp, final ProcessBean process) {
		return wfwService.getWorkitems(process, pp.getLoginId()).iterator().next();
	}

	@Override
	public ElementList getRightElements(final PageParameter pp) {
		final ProcessModelBean pm = WorkflowUtils.getProcessModel(pp);
		String params = null;
		if (pm != null) {
			params = "modelId=" + pm.getId();
		}
		final TabButtons tabs = TabButtons.of(new TabButton($m("MyProcessWorksTPage.4"), uFactory
				.getUrl(pp, MyProcessWorksTPage.class, params)));
		final IWorkflowWebContext ctx = (IWorkflowWebContext) workflowContext;
		if (pp.isLmember(ctx.getProcessWorks_DeptRole(pp))) {
			tabs.append(new TabButton(pp.getLdept(), uFactory.getUrl(pp,
					MyProcessWorks_DeptTPage.class, params)));
		}
		if (pp.isLmember(ctx.getProcessWorks_OrgRole(pp))) {
			tabs.append(new TabButton($m("MyProcessWorksTPage.5"), uFactory.getUrl(pp,
					MyProcessWorks_OrgTPage.class, params)));
		}
		tabs.append(new TabButton($m("MyProcessWorksTPage.6"), uFactory.getUrl(pp,
				MyProcessWorks_RoleTPage.class, params)));
		return ElementList.of(createTabsElement(pp, tabs));
	}

	@Override
	public String toCategoryHTML(final PageParameter pp) {
		final StringBuilder sb = new StringBuilder();
		final List<ProcessModelBean> models = DataQueryUtils.toList(wfpmService.getModelListByDomain(
				pp.getLDomainId(), EProcessModelStatus.deploy));
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

		final Map<String, Map<String, List<ProcessModelBean>>> gmap2 = new LinkedHashMap<String, Map<String, List<ProcessModelBean>>>();
		for (final Map.Entry<String, List<ProcessModelBean>> e : gmap.entrySet()) {
			final String key = e.getKey();
			final Map<String, List<ProcessModelBean>> m = new LinkedHashMap<String, List<ProcessModelBean>>();
			List<ProcessModelBean> glist = null;
			for (final ProcessModelBean pm : e.getValue()) {
				final String pgroup = wfpmService.getProcessDocument(pm).getProcessNode().getPgroup();
				if (StringUtils.hasText(pgroup)) {
					List<ProcessModelBean> list = m.get(pgroup);
					if (list == null) {
						m.put(pgroup, list = new ArrayList<ProcessModelBean>());
					}
					list.add(pm);
				} else {
					if (glist == null) {
						glist = new ArrayList<ProcessModelBean>();
					}
					glist.add(pm);
				}
			}
			if (glist != null) {
				m.put($m("MyProcessWorksTPage.17"), glist);
			}
			gmap2.put(key, m);
		}

		sb.append("<div class='gtitle'>").append($m("MyProcessWorksTPage.16")).append("</div>");
		sb.append("<div class='gtree'>");
		final ProcessModelBean cur = WorkflowUtils.getProcessModel(pp);
		for (final Map.Entry<String, Map<String, List<ProcessModelBean>>> e : gmap2.entrySet()) {
			final String key = e.getKey();
			sb.append("<div class='gitem");
			if (cur != null && cur.getModelText().startsWith(key)) {
				sb.append(" cur");
			}
			sb.append("'>");
			sb.append(new SpanElement(key).setClassName("glbl"));
			final int size = gmap.get(key).size();
			if (size > 0) {
				sb.append(new SupElement("(" + size + ")").addClassName("gsize"));
			}
			sb.append(" <div class='psub'>");
			for (final Map.Entry<String, List<ProcessModelBean>> e2 : e.getValue().entrySet()) {
				sb.append("<div class='pgroup'>");
				sb.append(e2.getKey());
				sb.append("</div>");
				for (final ProcessModelBean pm : e2.getValue()) {
					sb.append("<div class='pitem'>");
					final String mtxt = pm.getModelText();
					final int p = mtxt.indexOf('.');
					sb.append(new LinkElement(p > 0 ? mtxt.substring(p + 1) : mtxt)
							.setOnclick("$Actions.reloc('modelId=" + pm.getId() + "');"));
					sb.append("</div>");
				}
			}
			sb.append(" </div>");
			sb.append("</div>");
		}
		sb.append("</div>");
		return sb.toString();
	}

	@SuppressWarnings("unchecked")
	@Override
	public String toToolbarHTML(final PageParameter pp) {
		final StringBuilder sb = new StringBuilder();
		final ProcessModelBean pm = WorkflowUtils.getProcessModel(pp);
		if (pm != null) {
			sb.append("<div class='modeltxt clearfix'>");
			sb.append(new SpanElement(pm.getModelText()).setClassName("pm"));
			sb.append(" (");
			sb.append(LinkElement.style2($m("MyProcessWorksTPage.9")).setHref(
					uFactory.getUrl(pp, (Class<? extends AbstractMVCPage>) getOriginalClass())));
			sb.append(")");
			sb.append("</div>");
		}
		sb.append(super.toToolbarHTML(pp));
		return sb.toString();
	}
}