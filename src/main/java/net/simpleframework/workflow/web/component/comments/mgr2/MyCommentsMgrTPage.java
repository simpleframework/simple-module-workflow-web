package net.simpleframework.workflow.web.component.comments.mgr2;

import static net.simpleframework.common.I18n.$m;

import java.io.IOException;
import java.util.Map;

import net.simpleframework.module.common.web.page.AbstractMgrTPage;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.TabButton;
import net.simpleframework.mvc.common.element.TabButtons;
import net.simpleframework.workflow.engine.IWorkflowServiceAware;
import net.simpleframework.workflow.web.component.comments.IWfCommentHandler;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class MyCommentsMgrTPage extends AbstractMgrTPage implements IWorkflowServiceAware {

	@Override
	protected void onForward(final PageParameter pp) throws Exception {
		super.onForward(pp);

		pp.addImportCSS(IWfCommentHandler.class, "/my_comment.css");
	}

	@Override
	public TabButtons getTabButtons(final PageParameter pp) {
		return TabButtons.of(new TabButton($m("MyCommentsMgrTPage.0")));
	}

	@Override
	protected String toHtml(final PageParameter pp, final Map<String, Object> variables,
			final String currentVariable) throws IOException {
		return "Test";
	}
}