package net.simpleframework.workflow.web.component.action.startprocess;

import static net.simpleframework.common.I18n.$m;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.simpleframework.common.StringUtils;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.mvc.PageRequestResponse;
import net.simpleframework.mvc.UrlForward;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ComponentUtils;
import net.simpleframework.mvc.component.base.ajaxrequest.AjaxRequestUtils;
import net.simpleframework.mvc.ctx.permission.IPagePermissionHandler;
import net.simpleframework.workflow.engine.IProcessModelService;
import net.simpleframework.workflow.engine.IWorkflowContextAware;
import net.simpleframework.workflow.engine.InitiateItem;
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

	public static InitiateItem getInitiateItem(final PageRequestResponse rRequest) {
		final IProcessModelService service = context.getModelService();
		ProcessModelBean processModel = null;
		final String modelId = rRequest.getParameter(ProcessModelBean.modelId);
		if (StringUtils.hasText(modelId)) {
			processModel = service.getBean(modelId);
		}
		if (processModel == null) {
			processModel = service.getProcessModelByName(rRequest.getParameter("modelName"));
		}
		return processModel != null ? service.getInitiateItems(
				((IPagePermissionHandler) context.getParticipantService()).getLoginId(rRequest)).get(
				processModel) : null;
	}

	public static void doStartProcess(final HttpServletRequest request,
			final HttpServletResponse response) throws IOException {
		final ComponentParameter cp = get(request, response);
		final KVMap kv = new KVMap();
		final InitiateItem initiateItem = getInitiateItem(cp);
		if (initiateItem == null) {
			kv.add("exception", $m("StartProcessUtils.0"));
		} else {
			try {
				((IStartProcessHandler) cp.getComponentHandler()).doInit(cp, initiateItem);
				initiateItem.doTransitions();

				final boolean transitionManual = initiateItem.isTransitionManual();
				final boolean initiateRoles = initiateItem.getInitiateRoles().size() > 1;
				if (transitionManual || initiateRoles) {
					kv.add(
							"responseText",
							UrlForward.getResponseText(cp,
									ComponentUtils.getResourceHomePath(StartProcessBean.class)
											+ "/jsp/start_process_route.jsp"));
					kv.add("transitionManual", transitionManual);
					kv.add("initiateRoles", initiateRoles);
				} else {
					final String confirmMessage = (String) cp.getBeanProperty("confirmMessage");
					if (StringUtils.hasText(confirmMessage)) {
						kv.add(
								"responseText",
								UrlForward.getResponseText(cp,
										ComponentUtils.getResourceHomePath(StartProcessBean.class)
												+ "/jsp/start_process_route2.jsp"));
						kv.add("confirmMessage", confirmMessage);
					} else {
						final String jsCallback = jsStartProcessCallback(cp, initiateItem);
						if (StringUtils.hasText(jsCallback)) {
							kv.add("jsCallback", jsCallback);
						}
					}
				}
			} catch (final Throwable th) {
				kv.add("exception", AjaxRequestUtils.createException(cp, th));
			}
		}
		final Writer out = cp.getResponseWriter();
		out.write(kv.toJSON());
		out.flush();
	}

	static String jsStartProcessCallback(final ComponentParameter nCP,
			final InitiateItem initiateItem) {
		final IStartProcessHandler hdl = (IStartProcessHandler) nCP.getComponentHandler();
		return hdl
				.jsStartProcessCallback(nCP, context.getProcessService().startProcess(initiateItem));
	}
}
