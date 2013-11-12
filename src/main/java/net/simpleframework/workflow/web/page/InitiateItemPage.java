package net.simpleframework.workflow.web.page;

import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.SpanElement;
import net.simpleframework.mvc.common.element.TabButtons;
import net.simpleframework.mvc.template.struct.NavigationButtons;
import net.simpleframework.mvc.template.t1.T1ResizedTemplatePage;
import net.simpleframework.workflow.engine.IWorkflowContextAware;
import net.simpleframework.workflow.web.component.modellist.ModelListBean;
import net.simpleframework.workflow.web.component.modellist.MyModelListHandler;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public class InitiateItemPage extends T1ResizedTemplatePage implements IWorkflowContextAware {

	@Override
	protected void addComponents(final PageParameter pp) {
		super.addComponents(pp);

		addComponentBean(pp, "initiateItemMgr", ModelListBean.class).setContainerId(
				"idInitiateItemPage").setHandleClass(MyModelListHandler.class);
	}

	@Override
	protected void addImportCSS(final PageParameter pp) {
		super.addImportCSS(pp);

		pp.addImportCSS(InitiateItemPage.class, "/my_worklist.css");
	}

	@Override
	public NavigationButtons getNavigationBar(final PageParameter pp) {
		// uriFor(ModelerRemotePage.class)
		return super.getNavigationBar(pp).append(new SpanElement("#(MyWorklistPage.1)"));
	}

	@Override
	protected TabButtons getTabButtons(final PageParameter pp) {
		return get(MyWorklistPage.class).getTabButtons(pp);
	}
}