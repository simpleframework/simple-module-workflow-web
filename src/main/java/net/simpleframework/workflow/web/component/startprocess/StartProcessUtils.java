package net.simpleframework.workflow.web.component.startprocess;

import static net.simpleframework.common.I18n.$m;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.simpleframework.common.JsonUtils;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.web.JavascriptUtils;
import net.simpleframework.ctx.permission.PermissionRole;
import net.simpleframework.mvc.JavascriptForward;
import net.simpleframework.mvc.MVCUtils;
import net.simpleframework.mvc.PageRequestResponse;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.workflow.engine.IProcessModelService;
import net.simpleframework.workflow.engine.IWorkflowContextAware;
import net.simpleframework.workflow.engine.InitiateItem;
import net.simpleframework.workflow.engine.ProcessBean;
import net.simpleframework.workflow.engine.ProcessModelBean;

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
		final IProcessModelService service = context.getProcessModelService();
		ProcessModelBean processModel = null;
		final String modelIdParameterName = (String) cp.getBeanProperty("modelIdParameterName");
		final String modelId = cp.getParameter(modelIdParameterName);
		if (StringUtils.hasText(modelId)) {
			processModel = service.getBean(modelId);
		}
		if (processModel == null) {
			processModel = service.getProcessModelByName(cp.getParameter("modelName"));
		}
		return processModel != null ? service.getInitiateItems(cp.getLoginId()).get(processModel)
				: null;
	}

	public static String toParams(final ComponentParameter cp, final InitiateItem initiateItem) {
		final StringBuilder sb = new StringBuilder();
		if (initiateItem != null) {
			final String modelIdParameterName = (String) cp.getBeanProperty("modelIdParameterName");
			sb.append(modelIdParameterName).append("=").append(initiateItem.getModelId()).append("&");
		}
		sb.append(StartProcessUtils.BEAN_ID).append("=").append(cp.hashId());
		return sb.toString();
	}

	public static void doStartProcess(final HttpServletRequest request,
			final HttpServletResponse response) throws IOException {
		final ComponentParameter cp = get(request, response);
		final JavascriptForward js = new JavascriptForward();
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
				js.append("$error(").append(JsonUtils.toJSON(MVCUtils.createException(cp, th)))
						.append(");");
			}
		}
		final Writer out = cp.getResponseWriter();
		out.write(JavascriptUtils.wrapFunction(js.toString()));
		out.flush();
	}

	static JavascriptForward doStartProcess(final ComponentParameter nCP,
			final InitiateItem initiateItem) {
		// 设置选择的其他角色
		final PermissionRole role = nCP.getPermission().getRole(nCP.getParameter("initiator"));
		if (role != null) {
			initiateItem.setSelectedRoleId(role.getId());
		}

		// 发起流程实例
		final ProcessBean process = context.getProcessService().startProcess(initiateItem);
		// 触发onStartProcess回调
		return ((IStartProcessHandler) nCP.getComponentHandler()).onStartProcess(nCP, process);
	}
}
