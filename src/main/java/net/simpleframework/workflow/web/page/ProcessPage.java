package net.simpleframework.workflow.web.page;

import java.io.IOException;
import java.util.Map;

import net.simpleframework.mvc.AbstractMVCPage;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.LinkElement;
import net.simpleframework.mvc.template.struct.NavigationButtons;
import net.simpleframework.mvc.template.t1.T1ResizedTemplatePage;
import net.simpleframework.workflow.engine.IWorkflowContextAware;
import net.simpleframework.workflow.web.component.processlist.ProcessListBean;
import net.simpleframework.workflow.web.page.t1.ProcessModelMgrPage;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class ProcessPage extends T1ResizedTemplatePage implements IWorkflowContextAware {

	@Override
	protected void onForward(final PageParameter pp) {
		super.onForward(pp);

		pp.addImportCSS(ProcessPage.class, "/process_mgr.css");

		addComponentBean(pp, "processMgr", ProcessListBean.class).setContainerId("idProcessList");
	}

	@Override
	protected String toHtml(final PageParameter pp, final Map<String, Object> variables,
			final String variable) throws IOException {
		return "<div class='ProcessPage'><div id='idProcessList'></div></div>";
	}

	@Override
	public NavigationButtons getNavigationBar(final PageParameter pp) {
		return super.getNavigationBar(pp).append(
				new LinkElement("#(ProcessModelMgrPage.0)").setHref(AbstractMVCPage
						.url(ProcessModelMgrPage.class)));
	}
}
