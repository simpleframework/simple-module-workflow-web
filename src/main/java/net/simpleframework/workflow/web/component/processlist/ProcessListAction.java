package net.simpleframework.workflow.web.component.processlist;

import net.simpleframework.common.Convert;
import net.simpleframework.mvc.IForward;
import net.simpleframework.mvc.JavascriptForward;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.workflow.engine.EProcessAbortPolicy;
import net.simpleframework.workflow.engine.EProcessStatus;
import net.simpleframework.workflow.engine.IProcessService;
import net.simpleframework.workflow.engine.ProcessBean;
import net.simpleframework.workflow.web.component.AbstractListAction;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public class ProcessListAction extends AbstractListAction {

	public IForward doSuspend(final ComponentParameter cp) {
		final JavascriptForward js = new JavascriptForward();
		final IProcessService service = context.getProcessService();
		final ProcessBean process = service.getBean(cp.getParameter(ProcessBean.processId));
		service.suspend(process, process.getStatus() == EProcessStatus.suspended);
		jsRefreshAction(cp, js);
		return js;
	}

	public IForward doDelete(final ComponentParameter cp) {
		final JavascriptForward js = new JavascriptForward();
		context.getProcessService().delete(cp.getParameter(ProcessBean.processId));
		jsRefreshAction(cp, js);
		return js;
	}

	public IForward doAbort(final ComponentParameter cp) {
		final JavascriptForward js = new JavascriptForward();
		final IProcessService service = context.getProcessService();
		final ProcessBean process = service.getBean(cp.getParameter(ProcessBean.processId));
		service.abort(process,
				Convert.toEnum(EProcessAbortPolicy.class, cp.getParameter("process_abort_policy")));
		js.append("$Actions['process_list_abort_window'].close();");
		jsRefreshAction(cp, js);
		return js;
	}
}
