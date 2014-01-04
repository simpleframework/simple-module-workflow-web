package net.simpleframework.workflow.web.page.t1;

import java.io.IOException;
import java.util.Map;

import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.template.AbstractTemplatePage;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class ProcessModelUploadPage extends AbstractTemplatePage {

	@Override
	protected void onForward(PageParameter pp) {
		super.onForward(pp);

	}

	@Override
	protected String toHtml(PageParameter pp, Map<String, Object> variables, String currentVariable)
			throws IOException {
		StringBuilder sb = new StringBuilder();
		return sb.toString();
	}
}
