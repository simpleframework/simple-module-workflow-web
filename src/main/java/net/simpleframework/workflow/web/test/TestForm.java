package net.simpleframework.workflow.web.test;

import java.util.Map;

import net.simpleframework.common.coll.KVMap;
import net.simpleframework.mvc.IPageHandler.PageSelector;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.workflow.engine.WorkitemBean;
import net.simpleframework.workflow.web.page.MyWorklistForm;

public class TestForm extends MyWorklistForm {

	@Override
	protected void onSave(final Map<String, String> parameters, final WorkitemBean workitem) {
		final String title = parameters.get("wf_topic");
		System.out.println("wf_topic: " + title);
		updateProcessTitle(workitem, title);
	}

	@Override
	protected void onLoad(final PageParameter pp, final Map<String, Object> dataBinding,
			final PageSelector selector, final WorkitemBean workitem) {
		dataBinding.put("wf_topic", getProcess(workitem).getTitle());
	}

	@Override
	public void bindVariables(final KVMap variables) {
		// variables.put("t1", 103);
	}

	@Override
	public String getTitle() {
		return "测试。。。";
	}
}
