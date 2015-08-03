package net.simpleframework.workflow.web.page;

import static net.simpleframework.common.I18n.$m;

import java.util.Map;

import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.ElementList;
import net.simpleframework.mvc.common.element.LinkButton;
import net.simpleframework.mvc.common.element.Option;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ui.pager.TablePagerBean;
import net.simpleframework.mvc.component.ui.pager.TablePagerColumn;
import net.simpleframework.workflow.engine.EDelegationSource;
import net.simpleframework.workflow.engine.EDelegationStatus;
import net.simpleframework.workflow.engine.bean.DelegationBean;
import net.simpleframework.workflow.web.WorkflowUtils;
import net.simpleframework.workflow.web.page.AbstractDelegateFormPage.WorkitemDelegateSetPage;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class UserDelegateListTPage extends MyDelegateListTPage {

	@Override
	protected void onForward(final PageParameter pp) throws Exception {
		super.onForward(pp);

		// 委托设置
		addAjaxRequest(pp, "UserDelegateListTPage_delegate_page", WorkitemDelegateSetPage.class);
		addWindowBean(pp, "UserDelegateListTPage_delegate")
				.setContentRef("UserDelegateListTPage_delegate_page")
				.setTitle($m("MyRunningWorklistTPage.4")).setHeight(300).setWidth(500);
	}

	@Override
	protected TablePagerBean addTablePagerBean(final PageParameter pp) {
		final TablePagerBean tablePager = addTablePagerBean(pp, "MyWorklistTPage_tbl",
				UserDelegateTbl.class);
		tablePager.addColumn(TablePagerColumn.ICON().setWidth(16));
		tablePager
				.addColumn(
						new TablePagerColumn("description", $m("WorkitemDelegateSetPage.3"))
								.setSort(false))
				.addColumn(TC_USERTEXT())
				.addColumn(TC_CREATEDATE())
				.addColumn(
						TC_STATUS().setFilterOptions(
								Option.from(EDelegationStatus.ready, EDelegationStatus.running,
										EDelegationStatus.complete, EDelegationStatus.abort)))
				.addColumn(TablePagerColumn.OPE().setWidth(70));
		return tablePager;
	}

	@Override
	public ElementList getLeftElements(final PageParameter pp) {
		return ElementList.of(LinkButton.of($m("Add")).setOnclick(
				"$Actions['UserDelegateListTPage_delegate']('delegationSource=user');"));
	}

	@Override
	public ElementList getRightElements(final PageParameter pp) {
		return ElementList.of(getDelegateTabs(pp));
	}

	public static class UserDelegateTbl extends MyWorkDelegateTbl {
		@Override
		public IDataQuery<?> createDataObjectQuery(final ComponentParameter cp) {
			return wfdService.queryDelegations(cp.getLoginId(), EDelegationSource.user);
		}

		@Override
		protected Map<String, Object> getRowData(final ComponentParameter cp, final Object dataObject) {
			final DelegationBean delegation = (DelegationBean) dataObject;
			final KVMap row = new KVMap();
			row.add("description", toTitle(delegation, delegation.getDescription()));
			row.add("userText", delegation.getUserText());
			row.add("createDate", delegation.getCreateDate());
			final EDelegationStatus status = delegation.getStatus();
			row.add("status", WorkflowUtils.toStatusHTML(cp, status));
			row.add(TablePagerColumn.OPE, toOpeHTML(cp, delegation));
			return row;
		}
	}
}