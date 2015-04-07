package net.simpleframework.workflow.web.component;

import java.io.Writer;

import net.simpleframework.common.web.JavascriptUtils;
import net.simpleframework.ctx.permission.PermissionConst;
import net.simpleframework.mvc.IForward;
import net.simpleframework.mvc.JavascriptForward;
import net.simpleframework.mvc.UrlForward;
import net.simpleframework.mvc.component.ComponentParameter;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public abstract class WfComponentUtils {

	public static void doForword(final ComponentParameter cp, final IJavascriptCallback callback)
			throws Exception {
		final JavascriptForward js = new JavascriptForward();
		cp.setHttpRequest();
		final IForward forward = cp.getPermission().accessForward(
				cp,
				cp.componentBean != null ? cp.getBeanProperty("role")
						: PermissionConst.ROLE_ALL_ACCOUNT);
		if (forward instanceof UrlForward) {
			js.append("$Actions.loc('").append(((UrlForward) forward).getUrl()).append("');");
		} else {
			if (callback != null) {
				callback.doJavascript(js);
			}
		}
		final Writer out = cp.getResponseWriter();
		out.write(JavascriptUtils.wrapFunction(js.toString()));
		out.flush();
	}

	public static interface IJavascriptCallback {

		void doJavascript(JavascriptForward js) throws Exception;
	}
}
