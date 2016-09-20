package net.simpleframework.workflow.web.page.list.delegate;

import static net.simpleframework.common.I18n.$m;

import java.util.Map;

import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.AbstractElement;
import net.simpleframework.mvc.common.element.ElementList;
import net.simpleframework.mvc.common.element.LinkButton;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ui.pager.TablePagerBean;
import net.simpleframework.mvc.component.ui.pager.TablePagerColumn;
import net.simpleframework.mvc.component.ui.window.WindowBean;
import net.simpleframework.workflow.engine.EDelegationSource;
import net.simpleframework.workflow.engine.bean.DelegationBean;
import net.simpleframework.workflow.web.page.list.delegate.AbstractDelegateFormPage.WorkitemDelegateSetPage;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class UserDelegateListTPage extends MyDelegateListTPage {

	@Override
	protected void addComponents(final PageParameter pp) {
		super.addComponents(pp);

		// 委托设置
		addAjaxRequest(pp, "UserDelegateListTPage_delegate_page", WorkitemDelegateSetPage.class);
		addWindowBean(pp, "UserDelegateListTPage_delegate")
				.setContentRef("UserDelegateListTPage_delegate_page")
				.setTitle($m("MyRunningWorklistTPage.4")).setHeight(300).setWidth(500);
	}

	@Override
	protected WindowBean addDelegateView(final PageParameter pp) {
		return super.addDelegateView(pp).setHeight(300);
	}

	@Override
	protected TablePagerBean addTablePagerBean(final PageParameter pp) {
		final TablePagerBean tablePager = addTablePagerBean(pp, "MyWorklistTPage_tbl",
				UserDelegateTbl.class);
		tablePager.addColumn(TablePagerColumn.ICON())
				.addColumn(new TablePagerColumn("description", $m("WorkitemDelegateSetPage.3"))
						.setSort(false))
				.addColumn(TC_USERTEXT())
				.addColumn(TC_CREATEDATE().setColumnText($m("MyDelegateListTPage.1")))
				.addColumn(TC_DDATE()).addColumn(TablePagerColumn.OPE(70));
		return tablePager;
	}

	@Override
	public ElementList getLeftElements(final PageParameter pp) {
		return ElementList.of(LinkButton.of($m("Add"))
				.setOnclick("$Actions['UserDelegateListTPage_delegate']('delegationSource=user');"));
	}

	public static class UserDelegateTbl extends MyWorkDelegateTbl {
		@Override
		public IDataQuery<?> createDataObjectQuery(final ComponentParameter cp) {
			return wfdService.queryDelegations(cp.getLoginId(), EDelegationSource.user);
		}

		@Override
		protected Map<String, Object> getRowData(final ComponentParameter cp,
				final Object dataObject) {
			final DelegationBean delegation = (DelegationBean) dataObject;
			final KVMap row = new KVMap();
			final AbstractElement<?> img = createImageMark(cp, delegation);
			if (img != null) {
				row.add(TablePagerColumn.ICON, img);
			}
			row.add("description", toTitle(delegation, delegation.getDescription()));
			row.add("userText", delegation.getUserText());
			row.add("createDate", delegation.getCreateDate());
			row.add("dseDate", toDseDateHTML(cp, delegation));
			row.add(TablePagerColumn.OPE, toOpeHTML(cp, delegation));
			return row;
		}
	}
}