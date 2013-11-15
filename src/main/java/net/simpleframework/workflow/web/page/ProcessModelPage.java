package net.simpleframework.workflow.web.page;

import java.io.IOException;
import java.util.Map;

import net.simpleframework.mvc.AbstractMVCPage;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.SpanElement;
import net.simpleframework.mvc.template.struct.NavigationButtons;
import net.simpleframework.mvc.template.t1.T1ResizedTemplatePage;
import net.simpleframework.workflow.engine.IWorkflowContextAware;
import net.simpleframework.workflow.engine.ProcessModelBean;
import net.simpleframework.workflow.web.component.modellist.DefaultModelListHandler;
import net.simpleframework.workflow.web.component.modellist.ModelListBean;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class ProcessModelPage extends T1ResizedTemplatePage implements IWorkflowContextAware {

	@Override
	protected void addComponents(final PageParameter pp) {
		super.addComponents(pp);

		addComponentBean(pp, "processModelMgr", ModelListBean.class).setContainerId(
				"idProcessModelList").setHandleClass(Model.class);
	}

	@Override
	protected void addImportCSS(final PageParameter pp) {
		super.addImportCSS(pp);

		pp.addImportCSS(ProcessModelPage.class, "/process_mgr.css");
	}

	@Override
	protected String toHtml(final PageParameter pp, final Map<String, Object> variables,
			final String variable) throws IOException {
		return "<div class='ProcessModelPage'><div id='idProcessModelList'></div></div>";
	}

	@Override
	public NavigationButtons getNavigationBar(final PageParameter pp) {
		return super.getNavigationBar(pp).append(new SpanElement("#(ProcessModelPage.0)"));
	}

	public String jsProcessListAction(final ProcessModelBean processModel) {
		return "$Actions.loc('"
				+ AbstractMVCPage.url(ProcessPage.class,
						ProcessModelBean.modelId + "=" + processModel.getId()) + "');";
	}

	public static class Model extends DefaultModelListHandler {

		@Override
		public String jsProcessListAction(final ProcessModelBean processModel) {
			return AbstractMVCPage.get(ProcessModelPage.class).jsProcessListAction(processModel);
		}
	}
}
