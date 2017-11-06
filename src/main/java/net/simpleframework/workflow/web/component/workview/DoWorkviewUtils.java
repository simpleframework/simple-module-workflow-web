package net.simpleframework.workflow.web.component.workview;

import static net.simpleframework.common.I18n.$m;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.simpleframework.common.Convert;
import net.simpleframework.common.ID;
import net.simpleframework.common.web.HttpUtils;
import net.simpleframework.common.web.JavascriptUtils;
import net.simpleframework.ctx.permission.PermissionUser;
import net.simpleframework.mvc.JavascriptForward;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.PageRequestResponse;
import net.simpleframework.mvc.common.element.ButtonElement;
import net.simpleframework.mvc.common.element.Checkbox;
import net.simpleframework.mvc.common.element.ElementList;
import net.simpleframework.mvc.common.element.LinkButton;
import net.simpleframework.mvc.common.element.SpanElement;
import net.simpleframework.mvc.component.AbstractComponentRender;
import net.simpleframework.mvc.component.AbstractComponentRender.IJavascriptCallback;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.ctx.permission.IPagePermissionHandler;
import net.simpleframework.workflow.engine.IWorkflowContextAware;
import net.simpleframework.workflow.engine.bean.AbstractWorkitemBean;
import net.simpleframework.workflow.web.WorkflowUtils;

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

	public static String toParams(final ComponentParameter cp) {
		final StringBuilder sb = new StringBuilder();
		AbstractWorkitemBean workitem;
		if ((workitem = WorkflowUtils.getWorkitemBean(cp)) != null) {
			sb.append("workitemId=").append(workitem.getId()).append("&");
		} else if ((workitem = WorkflowUtils.getWorkviewBean(cp)) != null) {
			sb.append("workviewId=").append(workitem.getId()).append("&");
		}
		sb.append(BEAN_ID).append("=").append(cp.hashId());
		return sb.toString();
	}

	static String jsActions(final ComponentParameter cp, final String postfix) {
		return jsActions(cp, postfix, null);
	}

	static String jsActions(final ComponentParameter cp, final String postfix, final String params) {
		final StringBuilder sb = new StringBuilder();
		sb.append("$Actions['").append(cp.getComponentName()).append(postfix).append("']('");
		if (params != null) {
			sb.append(HttpUtils.addParameters(toParams(cp), params));
		} else {
			sb.append(toParams(cp));
		}
		sb.append("');");
		return sb.toString();
	}

	public static void doForword(final ComponentParameter cp) throws Exception {
		AbstractComponentRender.doJavascriptForward(cp, new IJavascriptCallback() {
			@Override
			public void doJavascript(final JavascriptForward js) {
				js.append(jsActions(cp, "_win"));
			}
		});
	}

	static final String SESSION_ULIST = "_ulist";

	@SuppressWarnings("unchecked")
	static Set<String> getSessionUlist(final PageParameter pp) {
		Set<String> ulist = (Set<String>) pp.getSessionAttr(SESSION_ULIST);
		if (ulist == null) {
			pp.setSessionAttr(SESSION_ULIST, ulist = new LinkedHashSet<>());
		}
		return ulist;
	}

	public static void removeSessionUlist(final PageParameter pp) {
		pp.removeSessionAttr(SESSION_ULIST);
	}

	public static String toSelectHTML(final ComponentParameter cp) {
		final StringBuilder sb = new StringBuilder();
		sb.append("<div class='wv_tt clearfix'>");
		sb.append(" <div class='left'>");
		sb.append(ElementList.of(LinkButton.menu($m("Add")).setId("idWorkviewSelectLoaded_addMenu")));
		sb.append(" </div>");
		sb.append(" <div class='right'>");
		sb.append(ElementList.of(LinkButton.of($m("DoWorkviewUtils.4")).addClassName("clearall")
				.setOnclick(jsActions(cp, "_clearAll", "op=clearAll"))));
		sb.append(" </div>");
		sb.append("</div>");
		sb.append("<div class='wv_cc'>");
		sb.append(toUserList(cp));
		sb.append("</div>");
		sb.append("<div class='wv_bb clearfix'>");
		sb.append(" <div class='left'>");
		sb.append(new Checkbox("idDoWorkviewUtils_opt1", $m("DoWorkviewUtils.7")).setChecked(true));
		sb.append(" </div>");
		sb.append(" <div class='right'>");

		final StringBuilder js = new StringBuilder();
		js.append("$Actions['").append(cp.getComponentName()).append("_save']('").append(toParams(cp))
				.append("&allowSent=' + $('idDoWorkviewUtils_opt1').checked);");
		sb.append(
				new ButtonElement($m("DoWorkviewUtils.5")).setHighlight(true).setOnclick(js.toString()))
				.append(SpanElement.SPACE);
		sb.append(ButtonElement.closeBtn());
		sb.append(" </div>");
		sb.append("</div>");
		return sb.toString();

	}

	static String toUserList(final ComponentParameter cp) {
		final StringBuilder sb = new StringBuilder();
		final String op = cp.getParameter("op");
		if ("clearAll".equals(op)) {
			removeSessionUlist(cp);
		}

		final Set<String> ulist = getSessionUlist(cp);
		if (ulist.size() > 0) {
			AbstractWorkitemBean workitem = WorkflowUtils.getWorkitemBean(cp);
			if (workitem == null) {
				workitem = WorkflowUtils.getWorkviewBean(cp);
			}
			final IPagePermissionHandler permission = cp.getPermission();
			final ID processId = workitem.getProcessId();
			final List<PermissionUser> slist = new ArrayList<>();
			for (final String id : ulist) {
				final PermissionUser user = permission.getUser(id);
				if (wfvService.getWorkviewBean(processId, id) != null) {
					slist.add(user);
				} else {
					if (user.exists()) {
						sb.append(toItemHTML(cp, user, false));
					}
				}
			}
			if ("clearAll2".equals(op)) {
				for (final PermissionUser user : slist) {
					ulist.remove(Convert.toString(user.getId()));
				}
				slist.clear();
			}
			if (slist.size() > 0) {
				sb.append("<div class='uitem2'>");
				sb.append(" <span>").append($m("DoWorkviewUtils.3")).append("</span>");
				sb.append(" <a class='simple_btn2' onclick=\"")
						.append(jsActions(cp, "_clearAll", "op=clearAll2")).append("\">")
						.append($m("DoWorkviewUtils.2")).append("</a>");
				sb.append("</div>");
				for (final PermissionUser user : slist) {
					if (user.exists()) {
						sb.append(toItemHTML(cp, user, true));
					}
				}
			}
		}
		sb.append(JavascriptUtils.wrapScriptTag("DoWorkview_init();"));
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
		if (!sent) {
			sb.append(" <div class='act' style='display: none;'>");
			sb.append("  <span class='del' onclick=\"")
					.append(jsActions(cp, "_del", "uid=" + user.getId())).append("\"></span>");
			sb.append(" </div>");
		}
		sb.append("</div>");
		return sb.toString();
	}
}
