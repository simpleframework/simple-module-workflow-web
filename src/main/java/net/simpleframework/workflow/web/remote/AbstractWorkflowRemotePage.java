package net.simpleframework.workflow.web.remote;

import java.util.Properties;

import net.simpleframework.ctx.permission.PermissionConst;
import net.simpleframework.ctx.trans.Transaction;
import net.simpleframework.mvc.IForwardCallback.IJsonForwardCallback;
import net.simpleframework.mvc.JsonForward;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.template.AbstractTemplatePage;
import net.simpleframework.workflow.engine.IWorkflowContext;
import net.simpleframework.workflow.engine.IWorkflowServiceAware;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public abstract class AbstractWorkflowRemotePage extends AbstractTemplatePage implements
		IWorkflowServiceAware {

	@Override
	protected void onForward(final PageParameter pp) {
		super.onForward(pp);
	}

	@Override
	public String getRole(final PageParameter pp) {
		return PermissionConst.ROLE_ALL_ACCOUNT;
	}

	protected void copyTo(final PageParameter pp, final Properties properties, final String... keys) {
		if (keys == null) {
			return;
		}
		for (final String key : keys) {
			properties.setProperty(key, pp.getLocaleParameter(key));
		}
	}

	@Transaction(context = IWorkflowContext.class)
	public JsonForward doJsonForward(final IJsonForwardCallback callback) {
		final JsonForward json = new JsonForward();
		try {
			callback.doAction(json);
		} catch (final Throwable e) {
			json.put("error", mvcContext.getThrowableMessage(e));
		}
		return json;
	}
}
