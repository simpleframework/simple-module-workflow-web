package net.simpleframework.workflow.web.component.workview;

import static net.simpleframework.common.I18n.$m;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.simpleframework.common.web.JavascriptUtils;
import net.simpleframework.mvc.JavascriptForward;
import net.simpleframework.mvc.PageRequestResponse;
import net.simpleframework.mvc.common.element.ElementList;
import net.simpleframework.mvc.common.element.LinkButton;
import net.simpleframework.mvc.common.element.SpanElement;
import net.simpleframework.mvc.common.element.TabButton;
import net.simpleframework.mvc.common.element.TabButtons;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.workflow.engine.IWorkflowContextAware;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public abstract class DoWorkviewUtils implements IWorkflowContextAware {
	public static final String BEAN_ID = "doworkview_@bid";

	public static ComponentParameter get(final PageRequestResponse rRequest) {
		return ComponentParameter.get(rRequest, BEAN_ID);
	}

	public static ComponentParameter get(final HttpServletRequest request,
			final HttpServletResponse response) {
		return ComponentParameter.get(request, response, BEAN_ID);
	}

	public static void doForword(final ComponentParameter cp) throws IOException {
		final JavascriptForward js = new JavascriptForward();
		js.append("$Actions['").append(cp.getComponentName()).append("_win']('")
				.append(cp.getParamsString()).append("');");
		final Writer out = cp.getResponseWriter();
		out.write(JavascriptUtils.wrapFunction(js.toString()));
		out.flush();
	}

	public static String toSelectHTML(final ComponentParameter cp) {
		final String componentName = cp.getComponentName();

		final StringBuilder sb = new StringBuilder();
		sb.append("<div class='wv_tt'>");
		sb.append(ElementList.of(
				LinkButton.of($m("DoWorkviewUtils.0")).setOnclick(
						"$Actions['" + componentName + "_userSelect']();"), SpanElement.SPACE,
				LinkButton.of($m("DoWorkviewUtils.1"))));
		sb.append(TabButtons.of(new TabButton($m("DoWorkviewUtils.2"))).toString(cp));
		sb.append("</div>");
		sb.append("<div class='wv_cc'>");
		sb.append("</div>");
		return sb.toString();
	}
}
