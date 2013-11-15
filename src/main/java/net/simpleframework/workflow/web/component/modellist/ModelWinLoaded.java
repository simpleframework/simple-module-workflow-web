package net.simpleframework.workflow.web.component.modellist;

import java.util.Map;

import net.simpleframework.mvc.DefaultPageHandler;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.workflow.engine.IWorkflowContextAware;
import net.simpleframework.workflow.engine.ProcessModelBean;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class ModelWinLoaded extends DefaultPageHandler implements IWorkflowContextAware {

	public void optLoad(final PageParameter pp, final Map<String, Object> dataBinding,
			final PageSelector selector) {
		final ProcessModelBean processModel = context.getModelService().getBean(
				pp.getParameter(ProcessModelBean.modelId));
		if (processModel != null) {
			dataBinding.put("model_status", processModel.getStatus().ordinal());
		}
	}
}
