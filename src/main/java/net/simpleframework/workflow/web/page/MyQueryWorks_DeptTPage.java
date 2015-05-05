package net.simpleframework.workflow.web.page;

import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.ElementList;
import net.simpleframework.mvc.common.element.SpanElement;
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
public class MyQueryWorks_DeptTPage extends MyQueryWorksTPage {

	@Override
	protected TablePagerBean addTablePagerBean(final PageParameter pp) {
		return (TablePagerBean) super.addTablePagerBean(pp).setHandlerClass(
				MyQueryWorks_DeptTbl.class);
	}

	@Override
	public ElementList getLeftElements(final PageParameter pp) {
		final ElementList el = ElementList.of();
		el.append(new SpanElement(pp.getLogin().getDept()).setClassName("worklist_dept"));
		return el;
	}

	@Override
	protected WorkitemBean getOpenWorkitem(final PageParameter pp, final ProcessBean process) {
		return wService.getWorkitems(process, null).iterator().next();
	}

	public static class MyQueryWorks_DeptTbl extends MyQueryWorksTbl {

		@Override
		public IDataQuery<?> createDataObjectQuery(final ComponentParameter cp) {
			return pService.getProcessListInDept(cp.getLogin().getDept().getId());
		}
	}
}
