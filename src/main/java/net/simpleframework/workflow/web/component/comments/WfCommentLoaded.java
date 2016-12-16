package net.simpleframework.workflow.web.component.comments;

import net.simpleframework.mvc.DefaultPageHandler;
import net.simpleframework.mvc.IForward;
import net.simpleframework.mvc.JavascriptForward;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.base.ajaxrequest.DefaultAjaxRequestHandler;
import net.simpleframework.workflow.engine.IWorkflowContextAware;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class WfCommentLoaded extends DefaultPageHandler implements IWorkflowContextAware {

	@Override
	public void onBeforeComponentRender(final PageParameter pp) {
		super.onBeforeComponentRender(pp);

		final ComponentParameter nCP = WfCommentUtils.get(pp);
		((IWfCommentHandler) nCP.getComponentHandler()).onComponentsCreated(nCP);
	}

	public static class WfCommentAction extends DefaultAjaxRequestHandler {

		public IForward doDel(final ComponentParameter cp) throws Exception {
			wfcService.delete(cp.getParameter("commentId"));
			return JavascriptForward.RELOC;
		}
	}
}