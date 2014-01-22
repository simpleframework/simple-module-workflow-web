package net.simpleframework.workflow.web.test;

import java.util.Map;

import net.simpleframework.workflow.web.AbstractWorkflowFormPage;

public class TestForm extends AbstractWorkflowFormPage {

	// @Override
	// protected void onSave(final Map<String, String> parameters, final
	// WorkitemBean workitem) {
	// final String title = parameters.get("wf_topic");
	// System.out.println("wf_topic: " + title);
	// updateProcessTitle(workitem, title);
	// }

	// @Override
	// protected void onLoad(final PageParameter pp, final Map<String, Object>
	// dataBinding,
	// final PageSelector selector, final WorkitemBean workitem) {
	// dataBinding.put("wf_topic", getProcess(workitem).getTitle());
	// }

	@Override
	public void bindVariables(final Map<String, Object> variables) {
		// variables.put("t1", 103);
	}
}
