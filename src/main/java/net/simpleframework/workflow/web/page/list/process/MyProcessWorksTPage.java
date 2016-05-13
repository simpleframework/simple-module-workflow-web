package net.simpleframework.workflow.web.page.list.process;

import static net.simpleframework.common.I18n.$m;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.simpleframework.ado.query.DataQueryUtils;
import net.simpleframework.common.ID;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.web.HttpUtils;
import net.simpleframework.ctx.permission.PermissionDept;
import net.simpleframework.mvc.AbstractMVCPage;
import net.simpleframework.mvc.IForward;
import net.simpleframework.mvc.JavascriptForward;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.ElementList;
import net.simpleframework.mvc.common.element.ImageElement;
import net.simpleframework.mvc.common.element.JS;
import net.simpleframework.mvc.common.element.LinkElement;
import net.simpleframework.mvc.common.element.SpanElement;
import net.simpleframework.mvc.common.element.TabButton;
import net.simpleframework.mvc.common.element.TabButtons;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.base.ajaxrequest.AjaxRequestBean;
import net.simpleframework.mvc.component.ui.menu.EMenuEvent;
import net.simpleframework.mvc.component.ui.menu.MenuBean;
import net.simpleframework.mvc.component.ui.menu.MenuItem;
import net.simpleframework.mvc.component.ui.menu.MenuItems;
import net.simpleframework.mvc.component.ui.pager.TablePagerBean;
import net.simpleframework.mvc.component.ui.pager.db.AbstractDbTablePagerHandler;
import net.simpleframework.mvc.template.struct.CategoryItem;
import net.simpleframework.mvc.template.struct.CategoryItems;
import net.simpleframework.mvc.template.struct.FilterButtons;
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
	public FilterButtons getFilterButtons(final PageParameter pp) {
		return AbstractProcessWorksHandler.getProcessWorksHandler(pp).getFilterButtons(pp, qw);
	}

	@Override
	public ElementList getLeftElements(final PageParameter pp) {
		return AbstractProcessWorksHandler.getProcessWorksHandler(pp).getLeftElements(pp, qw);
	}

	public IForward doWorkitem(final ComponentParameter cp) {
		final ProcessBean process = WorkflowUtils.getProcessBean(cp);
		WorkitemBean workitem;
		if (process != null && (workitem = getOpenWorkitem(cp, process)) != null) {
			if (cp.getBoolParameter("monitor")) {
				return new JavascriptForward(JS.loc(
						uFactory.getUrl(cp, WorkflowMonitorPage.class, workitem), true));
			} else {
				return new JavascriptForward(JS.loc(uFactory.getUrl(cp, WorkflowFormPage.class,
						workitem)));
			}
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
			final String url = uFactory.getUrl(pp, MyProcessWorks_DeptTPage.class, params);
			final StringBuilder txt = new StringBuilder();
			PermissionDept dept = pp.getDept(ID.of(pp.getParameter("deptId")));
			if (!dept.exists()) {
				dept = pp.getLdept();
			}
			txt.append(dept);
			final List<PermissionDept> depts = pp.getLogin().depts();
			if (depts.size() > 1) {
				final MenuBean menu = (MenuBean) pp
						.addComponentBean("MyProcessWorksTPage_depts_menu", MenuBean.class)
						.setMenuEvent(EMenuEvent.mouseenter)
						.setSelector(".MyProcessWorksTPage .tool_bar img.depts-menu");
				final MenuItems items = menu.getMenuItems();
				for (final PermissionDept _dept : depts) {
					if (_dept.equals(dept)) {
						continue;
					}
					items.add(MenuItem.of(_dept.getText()).setOnclick(
							"$Actions.loc('" + HttpUtils.addParameters(url, "deptId=" + _dept.getId())
									+ "');"));
				}
				txt.append(new ImageElement(pp.getCssResourceHomePath(MyProcessWorksTPage.class)
						+ "/images/down.png").setClassName("depts-menu"));
			}
			tabs.append(new TabButton(txt, url));
		}
		if (pp.isLmember(ctx.getProcessWorks_OrgRole(pp))) {
			tabs.append(new TabButton($m("MyProcessWorksTPage.5"), uFactory.getUrl(pp,
					MyProcessWorks_OrgTPage.class, params)));
		}
		return ElementList.of(createTabsElement(pp, tabs));
	}

	@SuppressWarnings("unchecked")
	@Override
	protected String toCategoryHTML(final PageParameter pp) {
		final StringBuilder sb = new StringBuilder();
		final List<ProcessModelBean> models = DataQueryUtils.toList(wfpmService.getModelListByDomain(
				pp.getLDomainId(), EProcessModelStatus.deploy));
		wfpmService.sort(models);

		final String CONST_OTHER = $m("MyInitiateItemsGroupTPage.0");
		final Map<String, List<ProcessModelBean>> gmap = new LinkedHashMap<String, List<ProcessModelBean>>();
		for (final ProcessModelBean pm : models) {
			final String[] arr = StringUtils.split(pm.getModelText(), ".");
			final String key = arr.length > 1 ? arr[0] : CONST_OTHER;
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
				m.put(CONST_OTHER, glist);
			}
			gmap2.put(key, m);
		}

		List<ProcessModelBean> l = null;
		String pgroup = null;
		final ProcessModelBean cur = WorkflowUtils.getProcessModel(pp);
		if (cur != null) {
			final String[] arr = StringUtils.split(cur.getModelText(), ".");
			final String key = arr.length > 1 ? arr[0] : CONST_OTHER;
			final Map<String, List<ProcessModelBean>> m2 = gmap2.get(key);
			if (m2 != null) {
				pgroup = wfpmService.getProcessDocument(cur).getProcessNode().getPgroup();
				if (!StringUtils.hasText(pgroup)) {
					pgroup = CONST_OTHER;
				}
				l = m2.get(pgroup);
			}
		}

		if (l != null) {
			final CategoryItems items = CategoryItems.of();
			for (final ProcessModelBean pm : l) {
				items.add(new _CategoryItem(pm).setHref(
						uFactory.getUrl(pp, MyProcessWorksTPage.class, "modelId=" + pm.getId()))
						.setSelected(cur.getId().equals(pm.getId())));
			}
			if (!l.contains(cur)) {
				items.add(new _CategoryItem(cur).setHref(
						uFactory.getUrl(pp, MyProcessWorksTPage.class, "modelId=" + cur.getId()))
						.setSelected(true));
			}

			sb.append("<div class='gtitle'>");
			sb.append(new LinkElement($m("MyProcessWorksTPage.9")).setHref(uFactory.getUrl(pp,
					(Class<? extends AbstractMVCPage>) getOriginalClass())));
			sb.append(SpanElement.NAV(3)).append(pgroup);
			sb.append("</div>");
			sb.append(items);
			return sb.toString();
		}

		sb.append("<div class='gtitle'>").append($m("MyProcessWorksTPage.16")).append("</div>");
		sb.append("<div class='gtree'>");
		for (final Map.Entry<String, Map<String, List<ProcessModelBean>>> e : gmap2.entrySet()) {
			final String key = e.getKey();
			sb.append("<div class='gitem");
			if (cur != null && cur.getModelText().startsWith(key)) {
				sb.append(" cur");
			}
			sb.append("'>");
			sb.append(new SpanElement(key).setClassName("glbl"));
			// final int size = gmap.get(key).size();
			// if (size > 0) {
			// sb.append(new SupElement("(" + size + ")").addClassName("gsize"));
			// }
			sb.append(" <div class='psub'>");
			int i = 0;
			for (final Map.Entry<String, List<ProcessModelBean>> e2 : e.getValue().entrySet()) {
				sb.append("<div class='pgroup'");
				if (i++ == 0) {
					sb.append(" style='border-top: 0'");
				}
				sb.append(">").append(new SpanElement(e2.getKey()));
				for (final ProcessModelBean pm : e2.getValue()) {
					sb.append("<div class='pitem'>");
					sb.append(new LinkElement(WorkflowUtils.getShortMtext(pm)).setOnclick("$Actions.loc('"
							+ uFactory.getUrl(pp, MyProcessWorksTPage.class, "modelId=" + pm.getId())
							+ "');"));
					sb.append("</div>");
				}
				sb.append("</div>");
			}
			sb.append(" </div>");
			sb.append("</div>");
		}
		sb.append("</div>");
		return sb.toString();
	}

	class _CategoryItem extends CategoryItem {
		_CategoryItem(final ProcessModelBean pm) {
			super(WorkflowUtils.getShortMtext(pm));
		}

		@Override
		protected SpanElement toIconElement() {
			return new SpanElement().setClassName("pm-txt").setTitle(getTitle());
		}
	}

	@Override
	public String toToolbarHTML(final PageParameter pp) {
		final StringBuilder sb = new StringBuilder();
		final FilterButtons btns = getFilterButtons(pp);
		if (btns != null && btns.size() > 0) {
			sb.append("<div class='model-filter'>").append(btns).append("</div>");
		}
		sb.append(super.toToolbarHTML(pp));
		return sb.toString();
	}
}