package net.simpleframework.workflow.web.page.t1;

import net.simpleframework.mvc.PageMapping;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.TabButtons;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
@PageMapping(url = "/workflow/monitor")
public class WorkflowMonitorPage extends AbstractWorkflowFormPage {

	@Override
	public TabButtons getTabButtons(PageParameter pp) {
		return ((AbstractWorkflowFormPage) singleton(getUrlsFactory().getWorkflowFormClass()))
				.getTabButtons(pp);
	}
}