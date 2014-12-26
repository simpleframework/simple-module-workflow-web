package net.simpleframework.workflow.web.component.comments;

import net.simpleframework.mvc.DefaultPageHandler;
import net.simpleframework.mvc.IForward;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.TextForward;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.base.ajaxrequest.AjaxRequestBean;
import net.simpleframework.mvc.component.base.ajaxrequest.DefaultAjaxRequestHandler;
import net.simpleframework.workflow.engine.IWorkflowContextAware;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class WfCommentLogLoaded extends DefaultPageHandler implements IWorkflowContextAware {

	@Override
	public void onBeforeComponentRender(final PageParameter pp) {
		super.onBeforeComponentRender(pp);

		final ComponentParameter nCP = WfCommentUtils.get(pp);
		final String commentName = nCP.getComponentName();

		pp.addComponentBean(commentName + "_logTab", AjaxRequestBean.class).setHandlerMethod("doTab")
				.setHandlerClass(LobTabAction.class).setAttr("$wfcomment", nCP.componentBean);

		pp.addComponentBean(commentName + "_logDel", AjaxRequestBean.class).setHandlerMethod("doDel")
				.setHandlerClass(LobTabAction.class).setAttr("$wfcomment", nCP.componentBean);
	}

	public static class LobTabAction extends DefaultAjaxRequestHandler {

		public IForward doTab(final ComponentParameter cp) throws Exception {
			final ComponentParameter nCP = ComponentParameter.getByAttri(cp, "$wfcomment");
			return new TextForward(WfCommentUtils.toLogsHTML(nCP));
		}

		public IForward doDel(final ComponentParameter cp) throws Exception {
			final ComponentParameter nCP = ComponentParameter.getByAttri(cp, "$wfcomment");
			if (workflowContext.getCommentLogService().delete(nCP.getParameter("logid")) == 1) {
				return new TextForward("true");
			} else {
				return null;
			}
		}
	}
}