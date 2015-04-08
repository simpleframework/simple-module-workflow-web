package net.simpleframework.workflow.web.component.workview;

import static net.simpleframework.common.I18n.$m;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.simpleframework.common.ID;
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
import net.simpleframework.workflow.engine.IWorkflowServiceAware;
import net.simpleframework.workflow.engine.WorkitemBean;
import net.simpleframework.workflow.web.WorkflowUtils;
import net.simpleframework.workflow.web.component.WfComponentUtils;
import net.simpleframework.workflow.web.component.WfComponentUtils.IJavascriptCallback;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public abstract class DoWorkviewUtils implements IWorkflowContextAware, IWorkflowServiceAware {
	public static final String BEAN_ID = "doworkview_@bid";

	public static ComponentParameter get(final PageRequestResponse rRequest) {
		return ComponentParameter.get(rRequest, BEAN_ID);
	}

	public static ComponentParameter get(final HttpServletRequest request,
			final HttpServletResponse response) {
		return ComponentParameter.get(request, response, BEAN_ID);
	}

	public static String toParams(final ComponentParameter cp) {
		final StringBuilder sb = new StringBuilder();
		final WorkitemBean workitem = WorkflowUtils.getWorkitemBean(cp);
		if (workitem != null) {
			sb.append("workitemId=").append(workitem.getId()).append("&");
		}
		sb.append(BEAN_ID).append("=").append(cp.hashId());
		return sb.toString();
	}

	public static void doForword(final ComponentParameter cp) throws Exception {
		WfComponentUtils.doForword(cp, new IJavascriptCallback() {
			@Override
			public void doJavascript(final JavascriptForward js) {
				js.append("$Actions['").append(cp.getComponentName()).append("_win']('")
						.append(toParams(cp)).append("');");
			}
		});
	}

	static final String SESSION_ULIST = "_ulist";

	public static String toSelectHTML(final ComponentParameter cp) {
		final String componentName = cp.getComponentName();
		final StringBuilder sb = new StringBuilder();
		sb.append("<div class='wv_tt clearfix'>");
		sb.append(" <div class='left'>");
		sb.append(ElementList.of(LinkButton.menu($m("Add")).setId("idWorkviewSelectLoaded_addMenu")));
		sb.append(" </div>");
		sb.append(" <div class='right'>");
		sb.append(LinkButton.corner($m("DoWorkviewUtils.2")));
		sb.append(" </div>");
		sb.append("</div>");
		sb.append("<div class='wv_cc'>");
		sb.append(toUserList(cp));
		sb.append("</div>");
		sb.append("<div class='wv_bb'>");
		sb.append(
				ButtonElement.okBtn().setHighlight(true)
						.setOnclick("$Actions['" + componentName + "_save']('" + toParams(cp) + "');"))
				.append(SpanElement.SPACE);
		sb.append(ButtonElement.WINDOW_CLOSE);
		sb.append("</div>");
		return sb.toString();
	}

	@SuppressWarnings("unchecked")
	static String toUserList(final ComponentParameter cp) {
		final StringBuilder sb = new StringBuilder();
		final Set<String> ulist = (Set<String>) cp.getSessionAttr(SESSION_ULIST);
		if (ulist != null) {
			final WorkitemBean workitem = WorkflowUtils.getWorkitemBean(cp);
			final IPagePermissionHandler permission = cp.getPermission();
			final ID processId = workitem.getProcessId();
			final List<PermissionUser> slist = new ArrayList<PermissionUser>();
			for (final String id : ulist) {
				final PermissionUser user = permission.getUser(id);
				if (vService.getWorkviewBean(processId, id) != null) {
					slist.add(user);
				} else {
					sb.append(toItemHTML(cp, user, false));
				}
			}
			for (final PermissionUser user : slist) {
				sb.append(toItemHTML(cp, user, true));
			}
			sb.append("<script type='text/javascript'>DoWorkview_init();</script>");
		}
		return sb.toString();
	}

	private static String toItemHTML(final ComponentParameter cp, final PermissionUser user,
			final boolean sent) {
		final StringBuilder sb = new StringBuilder();
		sb.append("<div class='uitem");
		if (sent) {
			sb.append(" workview");
		}
		sb.append("'>");
		sb.append(" <div>").append(user).append(" (").append(user.getName()).append(")</div>");
		sb.append(" <div class='dept'>").append(user.getDept()).append("</div>");
		sb.append(" <div class='act' style='display: none;'>");
		sb.append("  <span class='del' onclick=\"$Actions['").append(cp.getComponentName())
				.append("_del']('uid=").append(user.getId()).append("&").append(toParams(cp))
				.append("');\"></span>");
		sb.append(" </div>");
		sb.append("</div>");
		return sb.toString();
	}
}
