package net.simpleframework.workflow.web.component.modellist;

import java.io.IOException;
import java.io.InputStreamReader;

import net.simpleframework.common.ID;
import net.simpleframework.mvc.AbstractUrlForward;
import net.simpleframework.mvc.IMultipartFile;
import net.simpleframework.mvc.component.ComponentHandlerException;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.base.submit.AbstractSubmitHandler;
import net.simpleframework.mvc.ctx.permission.IPagePermissionHandler;
import net.simpleframework.workflow.engine.IWorkflowContextAware;
import net.simpleframework.workflow.schema.ProcessDocument;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class ModelUploadAction extends AbstractSubmitHandler implements IWorkflowContextAware {

	@Override
	public AbstractUrlForward submit(final ComponentParameter cp) {
		final IMultipartFile multipartFile = getMultipartFile(cp, "ml_upload");
		final ID loginId = ((IPagePermissionHandler) context.getParticipantService()).getLoginId(cp);
		try {
			final ProcessDocument document = new ProcessDocument(new InputStreamReader(
					multipartFile.getInputStream()));
			context.getModelService().addModel(loginId, document);
			return AbstractUrlForward
					.componentUrl(ModelListBean.class, "/jsp/model_upload_result.jsp");
		} catch (final IOException e) {
			throw ComponentHandlerException.of(e);
		}
	}
}
