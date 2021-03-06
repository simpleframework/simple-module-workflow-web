package net.simpleframework.workflow.web.page.list.process;

import static net.simpleframework.common.I18n.$m;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.simpleframework.ado.query.DataQueryUtils;
import net.simpleframework.common.Base64;
import net.simpleframework.common.ID;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.web.HttpUtils;
import net.simpleframework.ctx.permission.PermissionDept;
import net.simpleframework.mvc.IForward;
import net.simpleframework.mvc.JavascriptForward;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.AbstractElement;
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
import net.simpleframework.mvc.component.ui.pager.ITablePagerHandler;
import net.simpleframework.mvc.component.ui.pager.TablePagerBean;
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

	// protected EProcessWorks qw;
	// protected Class<? extends AbstractDbTablePagerHandler> tblClass;
	// {
	// if (MyProcessWorks_DeptTPage.class.isAssignableFrom(getClass())) {
	// qw = EProcessWorks.dept;
	// tblClass = MyProcessWorks_DeptTbl.class;
	// } else if (MyProcessWorks_OrgTPage.class.isAssignableFrom(getClass())) {
	// qw = EProcessWorks.org;
	// tblClass = MyProcessWorks_OrgTbl.class;
	// } else if (MyProcessWorks_RoleTPage.class.isAssignableFrom(getClass())) {
	// qw = EProcessWorks.role;
	// tblClass = MyProcessWorks_RoleTbl.class;
	// } else {
	// qw = EProcessWorks.my;
	// tblClass = MyProcessWorksTbl.class;
	// }
	// }

	public Class<? extends ITablePagerHandler> getTableHandler() {
		return MyProcessWorksTbl.class;
	}

	public EProcessWorks getEProcessWorks() {
		return EProcessWorks.my;
	}

	protected TablePagerBean addTablePagerBean(final PageParameter pp) {
		return addTablePagerBean(pp, "MyProcessWorksTPage_tbl");
	}

	public TablePagerBean addTablePagerBean(final PageParameter pp, final String name) {
		final TablePagerBean tablePager = addTablePagerBean(pp, name, getTableHandler());
		AbstractProcessWorksHandler.getProcessWorksHandler(pp).doTablePagerInit(pp, tablePager,
				getEProcessWorks());
		return tablePager;
	}

	@Override
	public FilterButtons getFilterButtons(final PageParameter pp) {
		return AbstractProcessWorksHandler.getProcessWorksHandler(pp).getFilterButtons(pp,
				getEProcessWorks());
	}

	@Override
	public ElementList getLeftElements(final PageParameter pp) {
		return AbstractProcessWorksHandler.getProcessWorksHandler(pp).getLeftElements(pp,
				getEProcessWorks());
	}

	public IForward doWorkitem(final ComponentParameter cp) {
		final ProcessBean process = WorkflowUtils.getProcessBean(cp);
		WorkitemBean workitem;
		if (process != null && (workitem = getOpenWorkitem(cp, process)) != null) {
			if (cp.getBoolParameter("monitor")) {
				return new JavascriptForward(
						JS.loc(uFactory.getUrl(cp, WorkflowMonitorPage.class, workitem), true));
			} else {
				return new JavascriptForward(
						JS.loc(uFactory.getUrl(cp, WorkflowFormPage.class, workitem)));
			}
		} else {
			return JavascriptForward.alert($m("MyProcessWorksTPage.7"));
		}
	}

	protected WorkitemBean getOpenWorkitem(final PageParameter pp, final ProcessBean process) {
		final List<WorkitemBean> items = wfwService.getWorkitems(process, pp.getLoginId());
		return null != items && items.size() > 0 ? items.iterator().next()
				: wfwService.getWorkitems(process, null).iterator().next();
	}

	protected TabButton getMyProcessTabButton(final PageParameter pp, final String params) {
		return new TabButton($m("MyProcessWorksTPage.4"),
				uFactory.getUrl(pp, MyProcessWorksTPage.class, params));
	}

	protected TabButton getDeptProcessTabButton(final PageParameter pp, final String params) {
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
				items.add(MenuItem.of(_dept.getText()).setOnclick("$Actions.loc('"
						+ HttpUtils.addParameters(url, "deptId=" + _dept.getId()) + "');"));
			}
			txt.append(new ImageElement(
					pp.getCssResourceHomePath(MyProcessWorksTPage.class) + "/images/down.png")
							.setClassName("depts-menu"));
		}
		return new TabButton(txt, url);
	}

	public String getTabParams(final PageParameter pp) {
		final ProcessModelBean pm = WorkflowUtils.getProcessModel(pp);
		final StringBuilder params = new StringBuilder();
		if (pm != null) {
			params.append("modelId=").append(pm.getId());
		} else {
			final String _gstr = pp.getParameter("pgroup");
			if (StringUtils.hasText(_gstr)) {
				params.append("pgroup=").append(HttpUtils.encodeUrl(_gstr));
			}
		}
		return params.toString();
	}

	@Override
	public ElementList getRightElements(final PageParameter pp) {
		final String params = getTabParams(pp);

		final TabButtons tabs = TabButtons.of(getMyProcessTabButton(pp, params));
		final IWorkflowWebContext ctx = (IWorkflowWebContext) workflowContext;
		if (pp.isLmember(ctx.getProcessWorks_DeptRole(pp))) {
			tabs.append(getDeptProcessTabButton(pp, params));
		}
		if (pp.isLmember(ctx.getProcessWorks_OrgRole(pp))) {
			tabs.append(new TabButton($m("MyProcessWorksTPage.5"),
					uFactory.getUrl(pp, MyProcessWorks_OrgTPage.class, params)));
		}
		return ElementList.of(createTabsElement(pp, tabs));
	}

	static final String CONST_OTHER = $m("MyInitiateItemsGroupTPage.0");

	public static Map<String, List<ProcessModelBean>> getProcessModelMap(final PageParameter pp) {
		final List<ProcessModelBean> models = DataQueryUtils.toList(
				wfpmService.getModelListByDomain(pp.getLDomainId(), EProcessModelStatus.deploy));
		wfpmService.sort(models);

		final Map<String, List<ProcessModelBean>> gmap = new LinkedHashMap<>();
		for (final ProcessModelBean pm : models) {
			final String[] arr = StringUtils.split(pm.getModelText(), ".");
			final String key = arr.length > 1 ? arr[0] : CONST_OTHER;
			List<ProcessModelBean> list = gmap.get(key);
			if (list == null) {
				gmap.put(key, list = new ArrayList<>());
			}
			list.add(pm);
		}
		return gmap;
	}

	public static Map<String, Map<String, List<ProcessModelBean>>> getProcessModelMap2(
			final PageParameter pp) {
		final Map<String, List<ProcessModelBean>> gmap = getProcessModelMap(pp);
		final Map<String, Map<String, List<ProcessModelBean>>> gmap2 = new LinkedHashMap<>();
		for (final Map.Entry<String, List<ProcessModelBean>> e : gmap.entrySet()) {
			final String key = e.getKey();
			final Map<String, List<ProcessModelBean>> m = new LinkedHashMap<>();
			List<ProcessModelBean> glist = null;
			for (final ProcessModelBean pm : e.getValue()) {
				final String pgroup = wfpmService.getProcessDocument(pm).getProcessNode().getPgroup();
				if (StringUtils.hasText(pgroup)) {
					List<ProcessModelBean> list = m.get(pgroup);
					if (list == null) {
						m.put(pgroup, list = new ArrayList<>());
					}
					list.add(pm);
				} else {
					if (glist == null) {
						glist = new ArrayList<>();
					}
					glist.add(pm);
				}
			}
			if (glist != null) {
				m.put(CONST_OTHER, glist);
			}
			gmap2.put(key, m);
		}
		return gmap2;
	}

	public static String[] getPgroups(final PageParameter pp) {
		final String _gstr = pp.getParameter("pgroup");
		if (StringUtils.hasText(_gstr)) {
			return StringUtils.split(Base64.decodeToString(pp.getParameter("pgroup")));
		}
		return null;
	}

	@Override
	protected String toCategoryHTML(final PageParameter pp) {
		final StringBuilder sb = new StringBuilder();
		final Map<String, Map<String, List<ProcessModelBean>>> gmap2 = getProcessModelMap2(pp);
		final ProcessModelBean cur = WorkflowUtils.getProcessModel(pp);
		final String[] pgroups = getPgroups(pp);
		if (cur != null || (pgroups != null && pgroups.length == 2)) {
			List<ProcessModelBean> l = null;
			String key = null;
			String pgroup = null;
			Map<String, List<ProcessModelBean>> m2 = null;
			if (cur != null) {
				final String[] arr = StringUtils.split(cur.getModelText(), ".");
				key = arr.length > 1 ? arr[0] : CONST_OTHER;
				m2 = gmap2.get(key);
				pgroup = wfpmService.getProcessDocument(cur).getProcessNode().getPgroup();
				if (!StringUtils.hasText(pgroup)) {
					pgroup = CONST_OTHER;
				}
			} else {
				key = pgroups[0];
				m2 = gmap2.get(key);
				pgroup = pgroups[1];
			}
			if (m2 != null) {
				l = m2.get(pgroup);
			}

			final CategoryItems items = CategoryItems.of();
			if (l != null) {
				for (final ProcessModelBean pm : l) {
					final _CategoryItem item = (_CategoryItem) new _CategoryItem(pm).setHref(
							uFactory.getUrl(pp, MyProcessWorksTPage.class, "modelId=" + pm.getId()));
					if (cur != null) {
						item.setSelected(cur.getId().equals(pm.getId()));
					}
					items.add(item);
				}
			}

			if (cur != null && (l == null || !l.contains(cur))) {
				items.add(new _CategoryItem(cur)
						.setHref(uFactory.getUrl(pp, MyProcessWorksTPage.class, "modelId=" + cur.getId()))
						.setSelected(true));
			}

			sb.append("<div class='gtitle'>");
			sb.append(new LinkElement($m("MyProcessWorksTPage.9"))
					.setHref(uFactory.getUrl(pp, getOriginalClass())));

			AbstractElement<?> ele;
			if (cur != null) {
				ele = new LinkElement(pgroup).setHref(uFactory.getUrl(pp, MyProcessWorksTPage.class,
						"pgroup=" + HttpUtils.encodeUrl(Base64.encodeToString(key + ";" + pgroup))));
			} else {
				ele = new SpanElement(pgroup);
			}
			sb.append(SpanElement.NAV(3)).append(ele);
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

				sb.append(">").append(new SpanElement(e2.getKey())
						.setOnclick(JS.loc(uFactory.getUrl(pp, MyProcessWorksTPage.class, "pgroup="
								+ HttpUtils.encodeUrl(Base64.encodeToString(key + ";" + e2.getKey()))))));
				for (final ProcessModelBean pm : e2.getValue()) {
					sb.append("<div class='pitem'>");
					sb.append(new LinkElement(WorkflowUtils.getShortMtext(pm)).setOnclick(JS.loc(
							uFactory.getUrl(pp, MyProcessWorksTPage.class, "modelId=" + pm.getId()))));
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

		_CategoryItem(final String title) {
			super(title);
		}

		@Override
		protected SpanElement toIconElement() {
			return new SpanElement().setClassName("pm-txt");
		}

		@Override
		public AbstractElement<?> toItemElement(final String itemClass) {
			return super.toItemElement(itemClass).setTitle(getTitle());
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