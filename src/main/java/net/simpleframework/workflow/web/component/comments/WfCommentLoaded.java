package net.simpleframework.workflow.web.component.comments;

import static net.simpleframework.common.I18n.$m;
import net.simpleframework.mvc.DefaultPageHandler;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.base.ajaxrequest.AjaxRequestBean;
import net.simpleframework.mvc.component.ui.window.WindowBean;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class WfCommentLoaded extends DefaultPageHandler {

	@Override
	public void onBeforeComponentRender(final PageParameter pp) {
		super.onBeforeComponentRender(pp);

		final ComponentParameter nCP = WfCommentUtils.get(pp);
		final String commentName = nCP.getComponentName();

		final String rpath = pp.getResourceHomePath(WfCommentLoaded.class);
		pp.addComponentBean(commentName + "_logPage", AjaxRequestBean.class).setUrlForward(
				rpath + "/jsp/wf_comment_log.jsp?" + WfCommentUtils.BEAN_ID + "=" + nCP.hashId());
		pp.addComponentBean(commentName + "_log_popup", WindowBean.class)
				.setContentRef(commentName + "_logPage").setPopup(true)
				.setTitle($m("WfCommentLoaded.0")).setHeight(450).setWidth(340);

		if (pp.isLmember(nCP.getBeanProperty("managerRole"))) {
			pp.addComponentBean(commentName + "_editPage", AjaxRequestBean.class).setUrlForward(
					rpath + "/jsp/wf_comment_edit.jsp?" + WfCommentUtils.BEAN_ID + "=" + nCP.hashId());
			pp.addComponentBean(commentName + "_edit", WindowBean.class)
					.setContentRef(commentName + "_editPage").setTitle($m("wf_comment_edit.0"))
					.setHeight(300).setWidth(450);
		}
	}
}