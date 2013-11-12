package net.simpleframework.workflow.web.test;

import java.util.Map;

import net.simpleframework.mvc.IPageHandler.PageSelector;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.workflow.engine.ProcessBean;
import net.simpleframework.workflow.engine.WorkitemBean;
import net.simpleframework.workflow.web.page.MyWorklistForm;

public class BXTest extends MyWorklistForm {

	@Override
	protected void onSave(final Map<String, String> parameters, final WorkitemBean workitem) {
		// getConnection();
		final String title = parameters.get("wf_topic");
		System.out.println("wf_topic: " + title);

		updateProcessTitle(workitem, title); // db
	}

	@Override
	protected void onLoad(final PageParameter pp, final Map<String, Object> dataBinding,
			final PageSelector selector, final WorkitemBean workitem) {
		final ProcessBean process = getProcess(workitem);
		dataBinding.put("wf_topic", process.getTitle());
		//
		dataBinding.put("wf_m", context.getProcessService().getVariable(process, "m"));
	}
}
