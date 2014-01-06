package net.simpleframework.workflow.web.page;

import static net.simpleframework.common.I18n.$m;

import java.io.IOException;
import java.util.Map;

import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.LinkElement;
import net.simpleframework.mvc.common.element.SpanElement;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.template.struct.NavigationButtons;
import net.simpleframework.mvc.template.t1.T1ResizedLCTemplatePage;
import net.simpleframework.workflow.engine.EWorkitemStatus;
import net.simpleframework.workflow.engine.IWorkflowContextAware;
import net.simpleframework.workflow.web.component.worklist.DefaultWorklistHandler;
import net.simpleframework.workflow.web.component.worklist.WorklistUtils;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class _MyWorklistPage extends T1ResizedLCTemplatePage implements IWorkflowContextAware {

	@Override
	protected void onForward(final PageParameter pp) {
		super.onForward(pp);

		pp.addImportCSS(_MyWorklistPage.class, "/my_worklist.css");
	}

	@Override
	protected String toHtml(final PageParameter pp, final Map<String, Object> variables,
			final String variable) throws IOException {
		if ("content_left".equals(variable)) {
			return "<div id='idMyWorklistTree' style='padding: 6px;'></div>";
		} else if ("content_center".equals(variable)) {
			return "<div class='MyWorklistPage_Center' style='padding: 6px;'><div id='idMyWorklistPage'></div></div>";
		}
		return null;
	}

	@Override
	public NavigationButtons getNavigationBar(final PageParameter pp) {
		return super.getNavigationBar(pp).append(new SpanElement("#(MyWorklistTPage.0)"));
	}

	// @Override
	// protected TabButtons getTabButtons(final PageParameter pp) {
	// return TabButtons.of(
	// new TabButton("#(MyWorklistTPage.0)",
	// AbstractMVCPage.url(_MyWorklistPage.class)),
	// new TabButton("#(MyWorklistTPage.1)",
	// AbstractMVCPage.url(_InitiateItemPage.class)));
	// }

	public static class MyWorklist extends DefaultWorklistHandler {

		@Override
		public String getTitle(final ComponentParameter cp) {
			final StringBuilder sb = new StringBuilder();
			final EWorkitemStatus status = WorklistUtils.getWorkitemStatus(cp);
			if (status != null) {
				sb.append(new LinkElement($m("MyWorklistTPage.0"))
						.setOnclick("$Actions['myWorklist']('status=false');"));
			} else {
				sb.append($m("MyWorklistTPage.0"));
			}
			if (status != null) {
				sb.append(SpanElement.NAV).append(status);
			}
			return sb.toString();
		}
	}
}
