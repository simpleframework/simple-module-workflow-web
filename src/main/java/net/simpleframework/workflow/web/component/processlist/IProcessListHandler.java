package net.simpleframework.workflow.web.component.processlist;

import net.simpleframework.mvc.component.ui.pager.ITablePagerHandler;
import net.simpleframework.workflow.engine.ProcessBean;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public interface IProcessListHandler extends ITablePagerHandler {

	String jsActivityListAction(ProcessBean processBean);
}
