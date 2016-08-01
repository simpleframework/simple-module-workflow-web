package net.simpleframework.workflow.web.component.comments;

import net.simpleframework.mvc.DefaultPageHandler;
import net.simpleframework.mvc.IForward;
import net.simpleframework.mvc.JavascriptForward;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.base.ajaxrequest.AjaxRequestBean;
import net.simpleframework.mvc.component.base.ajaxrequest.DefaultAjaxRequestHandler;
import net.simpleframework.workflow.engine.IWorkflowContextAware;
import net.simpleframework.workflow.engine.comment.WfComment;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class WfCommentEditLoaded extends DefaultPageHandler implements IWorkflowContextAware {

	@Override
	public void onBeforeComponentRender(final PageParameter pp) {
		super.onBeforeComponentRender(pp);

		final ComponentParameter nCP = WfCommentUtils.get(pp);
		final String commentName = nCP.getComponentName();

		pp.addComponentBean(commentName + "_save", AjaxRequestBean.class).setHandlerMethod("doSave")
				.setSelector(".wf_comment_edit").setHandlerClass(EditAction.class)
				.setAttr("$wfcomment", nCP.componentBean);
	}

	public static class EditAction extends DefaultAjaxRequestHandler {

		public IForward doSave(final ComponentParameter cp) throws Exception {
			final WfComment comment = wfcService.getBean(cp.getParameter("commentId"));
			comment.setCcomment(cp.getParameter("ce_ccomment"));
			wfcService.update(new String[] { "ccomment" }, comment);
			return JavascriptForward.RELOC;
		}
	}
}
