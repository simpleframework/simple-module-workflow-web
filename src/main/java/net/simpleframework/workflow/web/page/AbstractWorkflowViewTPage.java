package net.simpleframework.workflow.web.page;

import net.simpleframework.mvc.PageParameter;
import net.simpleframework.workflow.engine.bean.WorkviewBean;
import net.simpleframework.workflow.web.IWorkflowWebView;
import net.simpleframework.workflow.web.WorkflowUtils;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public abstract class AbstractWorkflowViewTPage extends AbstractFormTableRowTPage<WorkviewBean>
		implements IWorkflowWebView {

	@Override
	protected void onForward(final PageParameter pp) {
		super.onForward(pp);

		final WorkviewBean workview = getWorkitemBean(pp);
		if (workview != null) {
			// 标记已读
			vService.doReadMark(workview);
		}
	}

	@Override
	protected WorkviewBean getWorkitemBean(final PageParameter pp) {
		return WorkflowUtils.getWorkviewBean(pp);
	}
}
