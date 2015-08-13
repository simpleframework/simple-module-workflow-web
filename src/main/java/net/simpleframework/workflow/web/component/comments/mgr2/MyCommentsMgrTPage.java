package net.simpleframework.workflow.web.component.comments.mgr2;

import java.io.IOException;
import java.util.Map;

import net.simpleframework.module.common.web.page.AbstractMgrTPage;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.workflow.engine.IWorkflowServiceAware;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class MyCommentsMgrTPage extends AbstractMgrTPage implements IWorkflowServiceAware {

	@Override
	protected String toHtml(final PageParameter pp, final Map<String, Object> variables,
			final String currentVariable) throws IOException {
		return "Test";
	}
}