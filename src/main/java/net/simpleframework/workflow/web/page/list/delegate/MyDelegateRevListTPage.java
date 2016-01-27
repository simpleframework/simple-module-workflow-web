package net.simpleframework.workflow.web.page.list.delegate;

import static net.simpleframework.common.I18n.$m;
import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.SpanElement;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ui.pager.TablePagerBean;
import net.simpleframework.mvc.component.ui.pager.TablePagerColumn;
import net.simpleframework.workflow.engine.bean.DelegationBean;
import net.simpleframework.workflow.engine.bean.WorkitemBean;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class MyDelegateRevListTPage extends MyDelegateListTPage {

	@Override
	protected TablePagerBean addTablePagerBean(final PageParameter pp) {
		return (TablePagerBean) super.addTablePagerBean(pp).setHandlerClass(
				MyWorkDelegateRevTbl.class);
	}

	@Override
	protected void addComponents(final PageParameter pp) {
		// 取消
		addAbortComponent(pp);
	}

	@Override
	protected TablePagerColumn TC_USERTEXT() {
		return super.TC_USERTEXT().setColumnText($m("MyDelegateListTPage.9"));
	}

	@Override
	protected TablePagerColumn TC_CREATEDATE2() {
		return super.TC_CREATEDATE2().setColumnText($m("MyDelegateListTPage.10"));
	}

	public static class MyWorkDelegateRevTbl extends MyWorkDelegateTbl {
		@Override
		public IDataQuery<?> createDataObjectQuery(final ComponentParameter cp) {
			return wfdService.queryRevDelegations(cp.getLoginId());
		}

		@Override
		protected String toUsertextHTML(final ComponentParameter cp, final DelegationBean delegation,
				final WorkitemBean workitem) {
			String userText = workitem.getUserText();
			if (!workitem.getUserId().equals(delegation.getOuserId())) {
				userText += "<br>"
						+ SpanElement.color777($m("MyDelegateListTPage.7", delegation.getOuserText()));
			}
			return userText;
		}

		@Override
		protected boolean isShowDownmenu(final ComponentParameter cp) {
			return false;
		}
	}
}
