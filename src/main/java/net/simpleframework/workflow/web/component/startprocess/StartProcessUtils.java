package net.simpleframework.workflow.web.component.startprocess;

import static net.simpleframework.common.I18n.$m;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.simpleframework.common.ID;
import net.simpleframework.common.JsonUtils;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.logger.Log;
import net.simpleframework.common.logger.LogFactory;
import net.simpleframework.common.web.JavascriptUtils;
import net.simpleframework.ctx.permission.PermissionRole;
import net.simpleframework.mvc.JavascriptForward;
import net.simpleframework.mvc.MVCUtils;
import net.simpleframework.mvc.PageRequestResponse;
import net.simpleframework.mvc.common.element.BlockElement;
import net.simpleframework.mvc.common.element.LinkButton;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.workflow.engine.IWorkflowServiceAware;
import net.simpleframework.workflow.engine.InitiateItem;
import net.simpleframework.workflow.engine.ProcessBean;
import net.simpleframework.workflow.engine.ProcessModelBean;
import net.simpleframework.workflow.web.component.WfComponentUtils;
import net.simpleframework.workflow.web.component.WfComponentUtils.IJavascriptCallback;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public abstract class StartProcessUtils implements IWorkflowServiceAware {

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
		final String modelId = cp.getParameter((String) cp.getBeanProperty("modelIdParameterName"));
		if (StringUtils.hasText(modelId)) {
			processModel = mService.getBean(modelId);
		}
		if (processModel == null) {
			processModel = mService.getProcessModelByName(cp.getParameter("modelName"));
		}
		return mService.getInitiateItems(cp.getLoginId()).get(processModel);
	}

	public static String toParams(final ComponentParameter cp, final InitiateItem initiateItem) {
		final StringBuilder sb = new StringBuilder();
		if (initiateItem != null) {
			final String modelIdParameterName = (String) cp.getBeanProperty("modelIdParameterName");
			sb.append(modelIdParameterName).append("=").append(initiateItem.getModelId()).append("&");
		}
		sb.append(BEAN_ID).append("=").append(cp.hashId());
		return sb.toString();
	}

	public static void doForword(final ComponentParameter cp) throws Exception {
		WfComponentUtils.doForword(cp, new IJavascriptCallback() {
			@Override
			public void doJavascript(final JavascriptForward js) {
				final InitiateItem initiateItem = getInitiateItem(cp);
				if (initiateItem == null) {
					js.append("alert('").append($m("StartProcessUtils.0")).append("');");
				} else {
					try {
						final String confirmMessage = (String) cp.getBeanProperty("confirmMessage");
						if (StringUtils.hasText(confirmMessage)) {
							js.append("if (!confirm('").append(JavascriptUtils.escape(confirmMessage))
									.append("')) return;");
						}
						final String componentName = cp.getComponentName();
						js.append("$Actions['").append(componentName).append("_startProcess']('")
								.append(toParams(cp, initiateItem)).append("');");
					} catch (final Throwable th) {
						log.error(th);
						js.append("$error(").append(JsonUtils.toJSON(MVCUtils.createException(cp, th)))
								.append(");");
					}
				}
			}
		});
	}

	static JavascriptForward doStartProcess(final ComponentParameter nCP,
			final InitiateItem initiateItem) {
		// 设置选择的其他角色
		final PermissionRole role = nCP.getRole(ID.of(nCP.getParameter("initiator")));
		if (role != null) {
			initiateItem.setSelectedRoleId(role.getId());
		}

		// 发起流程实例
		final ProcessBean process = pService.doStartProcess(initiateItem);
		// 触发onStartProcess回调
		return ((IStartProcessHandler) nCP.getComponentHandler()).onStartProcess(nCP, process);
	}

	public static String toInitiatorHTML(final ComponentParameter cp) {
		final InitiateItem initiateItem = StartProcessUtils.getInitiateItem(cp);
		final StringBuilder sb = new StringBuilder();
		final Collection<ID> coll = initiateItem.roles();
		if (coll == null || coll.size() == 0) {
			sb.append(new BlockElement().setClassName("msg").setText($m("StartProcessUtils.1")));
		} else {
			for (final ID id : coll) {
				sb.append("<div class='ritem'>");
				sb.append(LinkButton.corner(cp.getRole(id)).setOnclick(
						"$Actions['InitiatorSelect_ok']('" + toParams(cp, initiateItem) + "&initiator="
								+ id + "');"));
				sb.append("</div>");
			}
		}
		return sb.toString();
	}

	private static Log log = LogFactory.getLogger(StartProcessUtils.class);
}
