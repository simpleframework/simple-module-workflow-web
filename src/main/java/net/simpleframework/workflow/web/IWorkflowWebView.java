package net.simpleframework.workflow.web;

import net.simpleframework.mvc.PageParameter;
import net.simpleframework.workflow.engine.IWorkflowView;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public interface IWorkflowWebView extends IWorkflowView {

	/**
	 * 获取view的url
	 * 
	 * @param pp
	 * @return
	 */
	String getForwardUrl(PageParameter pp);
}
