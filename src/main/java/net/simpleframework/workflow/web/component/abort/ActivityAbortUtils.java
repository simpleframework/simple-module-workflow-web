package net.simpleframework.workflow.web.component.abort;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.simpleframework.common.web.JavascriptUtils;
import net.simpleframework.mvc.JavascriptForward;
import net.simpleframework.mvc.PageRequestResponse;
import net.simpleframework.mvc.component.ComponentParameter;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public abstract class ActivityAbortUtils {

	public static final String BEAN_ID = "activityabort_@bid";

	public static ComponentParameter get(final PageRequestResponse rRequest) {
		return ComponentParameter.get(rRequest, BEAN_ID);
	}

	public static ComponentParameter get(final HttpServletRequest request,
			final HttpServletResponse response) {
		return ComponentParameter.get(request, response, BEAN_ID);
	}

	public static void doActivityAbort(final ComponentParameter cp) throws IOException {
		final JavascriptForward js = new JavascriptForward();
		js.append("$Actions['").append(cp.getComponentName()).append("_win']();");
		final Writer out = cp.getResponseWriter();
		out.write(JavascriptUtils.wrapFunction(js.toString()));
		out.flush();
	}
}