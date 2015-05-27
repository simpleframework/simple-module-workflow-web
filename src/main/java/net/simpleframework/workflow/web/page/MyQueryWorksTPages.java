package net.simpleframework.workflow.web.page;

import static net.simpleframework.common.I18n.$m;
import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.Checkbox;
import net.simpleframework.mvc.common.element.ElementList;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ui.pager.TablePagerBean;
import net.simpleframework.workflow.engine.bean.ProcessBean;
import net.simpleframework.workflow.engine.bean.WorkitemBean;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public abstract class MyQueryWorksTPages {

	public static class MyQueryWorks_OrgTPage extends MyQueryWorksTPage {
		@Override
		protected TablePagerBean addTablePagerBean(final PageParameter pp) {
			return (TablePagerBean) super.addTablePagerBean(pp).setHandlerClass(
					MyQueryWorks_OrgTbl.class);
		}

		@Override
		protected WorkitemBean getOpenWorkitem(final PageParameter pp, final ProcessBean process) {
			return wService.getWorkitems(process, null).iterator().next();
		}

		public static class MyQueryWorks_OrgTbl extends MyQueryWorksTbl {
			@Override
			public IDataQuery<?> createDataObjectQuery(final ComponentParameter cp) {
				return pService.getProcessListInDomain(cp.getLogin().getDomainId());
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
		public ElementList getLeftElements(final PageParameter pp) {
			final ElementList el = ElementList.of();
			if (pp.getLdept().hasChild()) {
				el.add(new Checkbox("idMyQueryWorks_DeptTPage_children", $m("MyQueryWorksTPage.2"))
						.setOnchange("$Actions['MyQueryWorksTPage_tbl']('child=true');"));
			}
			return el;
		}

		public static class MyQueryWorks_DeptTbl extends MyQueryWorksTbl {
			@Override
			public IDataQuery<?> createDataObjectQuery(final ComponentParameter cp) {
				final boolean child = cp.getBoolParameter("child");
				cp.addFormParameter("child", child);
				return pService.getProcessListInDept(cp.getLdept().getId(), child);
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
}
