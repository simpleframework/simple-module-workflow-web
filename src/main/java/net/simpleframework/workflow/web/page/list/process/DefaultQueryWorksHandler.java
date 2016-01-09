package net.simpleframework.workflow.web.page.list.process;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class DefaultQueryWorksHandler extends AbstractQueryWorksHandler {

	static IQueryWorksHandler instance = new DefaultQueryWorksHandler();

	@Override
	public String getModelName() {
		return null;
	}
}
