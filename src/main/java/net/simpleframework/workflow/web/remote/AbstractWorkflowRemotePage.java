package net.simpleframework.workflow.web.remote;

import java.util.Properties;

import net.simpleframework.common.web.HttpUtils;
import net.simpleframework.ctx.permission.IPermissionConst;
import net.simpleframework.mvc.IForwardCallback.IJsonForwardCallback;
import net.simpleframework.mvc.JsonForward;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.template.AbstractTemplatePage;
import net.simpleframework.workflow.engine.IWorkflowContextAware;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public abstract class AbstractWorkflowRemotePage extends AbstractTemplatePage implements
		IWorkflowContextAware {

	@Override
	protected void onForward(final PageParameter pp) {
		super.onForward(pp);
	}

	@Override
	public String getRole(final PageParameter pp) {
		return IPermissionConst.ROLE_ALL_ACCOUNT;
	}

	protected String parameter(final PageParameter pp, final String key) {
		return HttpUtils.toLocaleString(pp.getParameter(key), "utf-8");
	}

	protected void copyTo(final PageParameter pp, final Properties properties, final String... keys) {
		if (keys == null) {
			return;
		}
		for (final String key : keys) {
			properties.setProperty(key, parameter(pp, key));
		}
	}

	public JsonForward doJsonForward(final IJsonForwardCallback callback) {
		final JsonForward json = new JsonForward();
		try {
			callback.doAction(json);
		} catch (final Throwable e) {
			json.put("error", ctx.getThrowableMessage(e));
		}
		return json;
	}
}
