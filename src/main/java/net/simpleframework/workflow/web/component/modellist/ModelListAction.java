package net.simpleframework.workflow.web.component.modellist;

import net.simpleframework.common.Convert;
import net.simpleframework.mvc.IForward;
import net.simpleframework.mvc.JavascriptForward;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.workflow.engine.EProcessModelStatus;
import net.simpleframework.workflow.engine.IProcessModelService;
import net.simpleframework.workflow.engine.ProcessModelBean;
import net.simpleframework.workflow.web.component.AbstractListAction;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class ModelListAction extends AbstractListAction {

	public IForward deleteModel(final ComponentParameter cp) {
		final JavascriptForward js = new JavascriptForward();
		context.getModelService().delete(cp.getParameter(ProcessModelBean.modelId));
		jsRefreshAction(cp, js);
		return js;
	}

	public IForward optSave(final ComponentParameter cp) {
		final JavascriptForward js = new JavascriptForward();
		final IProcessModelService service = context.getModelService();
		final ProcessModelBean processModel = service.getBean(cp
				.getParameter(ProcessModelBean.modelId));
		service.setStatus(processModel,
				Convert.toEnum(EProcessModelStatus.class, cp.getParameter("model_status")));
		jsRefreshAction(cp, js);
		js.append("$Actions['ml_opt_window'].close();");
		return js;
	}
}
