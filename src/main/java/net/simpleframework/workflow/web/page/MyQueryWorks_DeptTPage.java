package net.simpleframework.workflow.web.page;

import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ui.pager.TablePagerBean;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class MyQueryWorks_DeptTPage extends MyQueryWorksTPage {

	@Override
	protected TablePagerBean addTablePagerBean(final PageParameter pp) {
		return addTablePagerBean(pp, "MyQueryWorksTPage_tbl", MyQueryWorks_DeptTbl.class);
	}

	public static class MyQueryWorks_DeptTbl extends MyQueryWorksTbl {

		@Override
		public IDataQuery<?> createDataObjectQuery(final ComponentParameter cp) {
			return pService.getProcessListInDept(cp.getLogin().getDept().getId());
		}
	}
}