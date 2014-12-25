package net.simpleframework.workflow.web.component.comments;

import net.simpleframework.mvc.DefaultPageHandler;
import net.simpleframework.mvc.IForward;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.TextForward;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.base.ajaxrequest.AjaxRequestBean;
import net.simpleframework.mvc.component.base.ajaxrequest.DefaultAjaxRequestHandler;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class WfCommentLogLoaded extends DefaultPageHandler {

	@Override
	public void onBeforeComponentRender(final PageParameter pp) {
		super.onBeforeComponentRender(pp);

		final ComponentParameter nCP = WfCommentUtils.get(pp);
		final String commentName = nCP.getComponentName();

		pp.addComponentBean(commentName + "_logTab", AjaxRequestBean.class)
				.setHandlerClass(LobTabAction.class).setAttr("$wfcomment", nCP.componentBean);
	}

	public static class LobTabAction extends DefaultAjaxRequestHandler {
		@Override
		public IForward ajaxProcess(final ComponentParameter cp) throws Exception {
			final ComponentParameter nCP = ComponentParameter.getByAttri(cp, "$wfcomment");
			return new TextForward(WfCommentUtils.toLogsHTML(nCP));
		}
	}
}