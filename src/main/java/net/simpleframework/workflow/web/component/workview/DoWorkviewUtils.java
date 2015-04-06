package net.simpleframework.workflow.web.component.workview;

import static net.simpleframework.common.I18n.$m;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.simpleframework.ctx.permission.PermissionUser;
import net.simpleframework.mvc.JavascriptForward;
import net.simpleframework.mvc.PageRequestResponse;
import net.simpleframework.mvc.common.element.ButtonElement;
import net.simpleframework.mvc.common.element.ElementList;
import net.simpleframework.mvc.common.element.LinkButton;
import net.simpleframework.mvc.common.element.SpanElement;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.ctx.permission.IPagePermissionHandler;
import net.simpleframework.workflow.engine.IWorkflowContextAware;
import net.simpleframework.workflow.web.component.WfComponentUtils;
import net.simpleframework.workflow.web.component.WfComponentUtils.IJavascriptCallback;

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

	public static void doForword(final ComponentParameter cp) throws Exception {
		WfComponentUtils.doForword(cp, new IJavascriptCallback() {
			@Override
			public void doJavascript(final JavascriptForward js) {
				js.append("$Actions['").append(cp.getComponentName()).append("_win']('")
						.append(cp.getParamsString()).append("');");
			}
		});
	}

	static final String SESSION_ULIST = "_ulist";

	public static String toSelectHTML(final ComponentParameter cp) {
		final String componentName = cp.getComponentName();
		final StringBuilder sb = new StringBuilder();
		sb.append("<div class='wv_tt'>");
		sb.append(ElementList.of(
				LinkButton.of($m("DoWorkviewUtils.0")).setOnclick(
						"$Actions['" + componentName + "_userSelect']();"), SpanElement.SPACE,
				LinkButton.of($m("DoWorkviewUtils.1"))));
		sb.append("</div>");
		sb.append("<div class='wv_cc'>");
		sb.append(toUserList(cp));
		sb.append("</div>");
		sb.append("<div class='wv_bb'>");
		sb.append(ButtonElement.okBtn().setHighlight(true)).append(SpanElement.SPACE);
		sb.append(ButtonElement.WINDOW_CLOSE);
		sb.append("</div>");
		return sb.toString();
	}

	@SuppressWarnings("unchecked")
	static String toUserList(final ComponentParameter cp) {
		final StringBuilder sb = new StringBuilder();
		final Set<String> ulist = (Set<String>) cp.getSessionAttr(SESSION_ULIST);
		if (ulist != null) {
			final String componentName = cp.getComponentName();
			final IPagePermissionHandler permission = cp.getPermission();
			for (final String id : ulist) {
				final PermissionUser user = permission.getUser(id);
				sb.append("<div class='uitem'>");
				sb.append(" <div>").append(user).append(" (").append(user.getName()).append(")</div>");
				sb.append(" <div class='dept'>").append(user.getDept()).append("</div>");
				sb.append(" <div class='act' style='display: none;'>");
				sb.append("  <span class='del' onclick=\"$Actions['").append(componentName)
						.append("_del']('uid=").append(user.getId()).append("');\"></span>");
				sb.append(" </div>");
				sb.append("</div>");
			}
			sb.append("<script type='text/javascript'>DoWorkview_init();</script>");
		}
		return sb.toString();
	}
}
