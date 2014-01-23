package net.simpleframework.workflow.web.page.t1;

import java.util.Map;

import net.simpleframework.mvc.PageMapping;
import net.simpleframework.mvc.PageParameter;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
@PageMapping(url = "/workflow/completeInfo")
public class WorkflowCompleteInfoPage extends AbstractWorkflowFormPage {

	@Override
	public Map<String, Object> createVariables(PageParameter pp) {
		// final WorkitemBean workitem =
		// AbstractWorkflowFormTPage.getWorkitemBean(pp);
		return super.createVariables(pp);
	}
}
