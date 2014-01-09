package net.simpleframework.workflow.web.page.t1;

import net.simpleframework.mvc.AbstractMVCPage;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.component.ui.pager.db.AbstractDbTablePagerHandler;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class ActivityMgrPage extends AbstractWorkflowMgrPage {

	@Override
	protected void onForward(final PageParameter pp) {
		super.onForward(pp);
	}

	@Override
	protected Class<? extends AbstractMVCPage> getStatusDescPage() {
		return null;
	}

	@Override
	protected Class<? extends AbstractMVCPage> getUpdateLogPage() {
		return null;
	}

	public static class ActivityTbl extends AbstractDbTablePagerHandler {
	}
}
