package net.simpleframework.workflow.web.component.workview;

import java.util.List;

import net.simpleframework.common.ID;
import net.simpleframework.mvc.JavascriptForward;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.workflow.engine.WorkviewBean;
import net.simpleframework.workflow.web.WorkflowUtils;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class DefaultDoWorkview2Handler extends DefaultDoWorkviewHandler {

	@Override
	public JavascriptForward doSent(final ComponentParameter cp, final List<ID> ids) {
		final List<WorkviewBean> list = vService.createForwardWorkviews(
				WorkflowUtils.getWorkviewBean(cp), ids.toArray(new ID[ids.size()]));
		return createJavascriptForward(cp, list);
	}
}
