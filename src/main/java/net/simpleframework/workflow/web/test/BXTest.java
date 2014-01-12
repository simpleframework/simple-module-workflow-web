package net.simpleframework.workflow.web.test;

import java.util.Map;

import net.simpleframework.workflow.engine.WorkitemBean;
import net.simpleframework.workflow.web.AbstractWorkflowFormPage;

public class BXTest extends AbstractWorkflowFormPage {

	@Override
	protected void onSave(final Map<String, String> parameters, final WorkitemBean workitem) {
		// getConnection();
		final String title = parameters.get("wf_topic");
		System.out.println("wf_topic: " + title);

		// updateProcessTitle(workitem, title); // db
	}

	@Override
	public String getTitle() {
		return null;
	}

	// @Override
	// protected void onLoad(final PageParameter pp, final Map<String, Object>
	// dataBinding,
	// final PageSelector selector, final WorkitemBean workitem) {
	// final ProcessBean process = getProcess(workitem);
	// dataBinding.put("wf_topic", process.getTitle());
	// //
	// dataBinding.put("wf_m", context.getProcessService().getVariable(process,
	// "m"));
	// }
}
