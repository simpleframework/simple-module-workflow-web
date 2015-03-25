package net.simpleframework.workflow.web.page.org2;

import static net.simpleframework.common.I18n.$m;

import java.io.IOException;
import java.util.Map;

import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.PageRequestResponse.IVal;
import net.simpleframework.mvc.common.element.ElementList;
import net.simpleframework.mvc.common.element.LinkButton;
import net.simpleframework.mvc.common.element.TabButton;
import net.simpleframework.mvc.common.element.TabButtons;
import net.simpleframework.workflow.engine.ProcessBean;
import net.simpleframework.workflow.web.WorkflowUrlsFactory;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class ActivityMgrTPage extends AbstractWorkflowMgrTPage {

	@Override
	protected String toHtml(final PageParameter pp, final Map<String, Object> variables,
			final String currentVariable) throws IOException {
		final StringBuilder sb = new StringBuilder();
		sb.append("<div class='tbar'>");
		final ProcessBean process = getProcessBean(pp);
		sb.append(ElementList.of(LinkButton.backBtn().setOnclick(
				"$Actions.loc('"
						+ getUrlsFactory().getUrl(pp, ProcessMgrTPage.class,
								"modelId=" + (process != null ? process.getModelId() : "")) + "')")));
		sb.append("</div>");
		sb.append("<div id='idActivityMgrTPage_tbl'>");
		sb.append("</div>");
		return sb.toString();
	}

	@Override
	public TabButtons getTabButtons(final PageParameter pp) {
		final WorkflowUrlsFactory urlsFactory = getUrlsFactory();
		final ProcessBean process = getProcessBean(pp);
		final String params = "processId=" + (process != null ? process.getId() : "");
		return TabButtons.of(
				new TabButton($m("ActivityMgrPage.7"), urlsFactory.getUrl(pp, ActivityMgrTPage.class,
						params)),
				new TabButton($m("ActivityMgrPage.8"), urlsFactory.getUrl(pp,
						ActivityGraphMgrTPage.class, params)));
	}

	protected static ProcessBean getProcessBean(final PageParameter pp) {
		return pp.getCache("@ProcessBean", new IVal<ProcessBean>() {
			@Override
			public ProcessBean get() {
				return workflowContext.getProcessService().getBean(pp.getParameter("processId"));
			}
		});
	}
}
