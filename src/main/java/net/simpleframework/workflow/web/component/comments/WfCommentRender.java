package net.simpleframework.workflow.web.component.comments;

import net.simpleframework.mvc.component.ComponentHtmlRenderEx;
import net.simpleframework.mvc.component.ComponentParameter;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class WfCommentRender extends ComponentHtmlRenderEx {

	@Override
	protected String getRelativePath(final ComponentParameter cp) {
		final StringBuilder sb = new StringBuilder();
		sb.append("/jsp/wf_comment.jsp?").append(WfCommentUtils.BEAN_ID);
		sb.append("=").append(cp.hashId());
		return sb.toString();
	}
}
