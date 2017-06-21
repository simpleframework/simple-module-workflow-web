package net.simpleframework.workflow.web.component.startprocess;

import static net.simpleframework.common.I18n.$m;

import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.simpleframework.common.JsonUtils;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.logger.Log;
import net.simpleframework.common.logger.LogFactory;
import net.simpleframework.common.web.JavascriptUtils;
import net.simpleframework.ctx.permission.PermissionDept;
import net.simpleframework.ctx.permission.PermissionRole;
import net.simpleframework.mvc.JavascriptForward;
import net.simpleframework.mvc.MVCUtils;
import net.simpleframework.mvc.PageRequestResponse;
import net.simpleframework.mvc.common.element.BlockElement;
import net.simpleframework.mvc.common.element.LinkButton;
import net.simpleframework.mvc.common.element.SpanElement;
import net.simpleframework.mvc.component.AbstractComponentRender;
import net.simpleframework.mvc.component.AbstractComponentRender.IJavascriptCallback;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.workflow.engine.IWorkflowContextAware;
import net.simpleframework.workflow.engine.InitiateItem;
import net.simpleframework.workflow.engine.bean.ProcessBean;
import net.simpleframework.workflow.engine.bean.ProcessModelBean;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public abstract class StartProcessUtils implements IWorkflowContextAware {

	public static final String BEAN_ID = "startprocess_@bid";

	public static ComponentParameter get(final PageRequestResponse rRequest) {
		return ComponentParameter.get(rRequest, BEAN_ID);
	}

	public static ComponentParameter get(final HttpServletRequest request,
			final HttpServletResponse response) {
		return ComponentParameter.get(request, response, BEAN_ID);
	}

	public static InitiateItem getInitiateItem(final ComponentParameter cp) {
		ProcessModelBean processModel = null;
		final String modelId = cp.getParameter("modelId");
		if (StringUtils.hasText(modelId)) {
			processModel = wfpmService.getBean(modelId);
		}
		if (processModel == null) {
			processModel = wfpmService.getProcessModelByName(cp.getParameter("modelName"));
		}
		return wfpmService.getInitiateItems(processModel,cp.getLoginId()).get(processModel);
	}

	public static String toParams(final ComponentParameter cp, final InitiateItem initiateItem) {
		final StringBuilder sb = new StringBuilder();
		if (initiateItem != null) {
			sb.append("modelId=").append(initiateItem.getModelId()).append("&");
		}
		sb.append(BEAN_ID).append("=").append(cp.hashId());
		return sb.toString();
	}

	public static void doForword(final ComponentParameter cp) throws Exception {
		AbstractComponentRender.doJavascriptForward(cp, new IJavascriptCallback() {
			@Override
			public void doJavascript(final JavascriptForward js) {
				final InitiateItem initiateItem = getInitiateItem(cp);
				if (initiateItem == null) {
					js.append("alert('").append($m("StartProcessUtils.0")).append("');");
				} else {
					final String componentName = cp.getComponentName();
					final List<PermissionRole> roles = initiateItem.roles();
					if (roles.size() > 1) {
						// 选择角色
						js.append("$Actions['").append(componentName).append("_initiatorSelect']('")
								.append(toParams(cp, initiateItem)).append("');");
					} else {
						final String confirmMessage = (String) cp.getBeanProperty("confirmMessage");
						if (StringUtils.hasText(confirmMessage) && !initiateItem.isTransitionManual()) {
							js.append("if (!confirm('").append(JavascriptUtils.escape(confirmMessage))
									.append("')) return;");
						}
						js.append("$Actions['").append(componentName).append("_startProcess']('")
								.append(toParams(cp, initiateItem)).append("');");
					}
				}
			}
		});
	}

	static JavascriptForward doStartProcess(final ComponentParameter nCP,
			final InitiateItem initiateItem) {
		try {
			// 设置选择的其他角色
			final PermissionRole role = nCP.getRole(nCP.toID("roleId"));
			if (role.exists()) {
				initiateItem.getParticipant().setRoleId(role.getId());
			}
			final PermissionDept dept = nCP.getDept(nCP.toID("deptId"));
			if (dept.exists()) {
				initiateItem.getParticipant().setDeptId(dept.getId());
			}

			// 发起流程实例
			final ProcessBean process = wfpService.doStartProcess(initiateItem);
			// 触发onStartProcess回调
			return ((IStartProcessHandler) nCP.getComponentHandler()).onStartProcess(nCP, process);
		} catch (final Throwable th) {
			log.error(th);
			return (JavascriptForward) new JavascriptForward("$error(")
					.append(JsonUtils.toJSON(MVCUtils.createException(nCP, th))).append(");")
					.setAttr("_throwable", th);
		}
	}

	public static String toInitiatorHTML(final ComponentParameter cp) {
		final InitiateItem initiateItem = StartProcessUtils.getInitiateItem(cp);
		final StringBuilder sb = new StringBuilder();
		final Collection<PermissionRole> coll = initiateItem.roles();
		if (coll == null || coll.size() == 0) {
			sb.append(new BlockElement().setClassName("msg").setText($m("StartProcessUtils.1")));
		} else {
			for (final PermissionRole role : coll) {
				sb.append("<div class='ritem clearfix'>");
				sb.append(" <div class='left'>");
				sb.append(role).append("<br>");
				sb.append(SpanElement.color777("(" + role.getDept() + ")"));
				sb.append(" </div>");
				sb.append(" <div class='right'>");
				sb.append(LinkButton.corner($m("StartProcessUtils.2"))
						.setOnclick("$Actions['InitiatorSelect_ok']('" + toParams(cp, initiateItem)
								+ "&roleId=" + role.getId() + "&deptId=" + role.getDept().getId() + "');"));
				sb.append(" </div>");
				sb.append("</div>");
			}
		}
		return sb.toString();
	}

	private static Log log = LogFactory.getLogger(StartProcessUtils.class);
}
