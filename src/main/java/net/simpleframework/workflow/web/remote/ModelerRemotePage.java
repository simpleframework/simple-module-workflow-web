package net.simpleframework.workflow.web.remote;

import java.util.ArrayList;
import java.util.Map;

import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.ctx.permission.PermissionConst;
import net.simpleframework.mvc.IForward;
import net.simpleframework.mvc.IForwardCallback.IJsonForwardCallback;
import net.simpleframework.mvc.JsonForward;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.workflow.engine.ProcessModelBean;
import net.simpleframework.workflow.schema.ProcessDocument;
import net.simpleframework.workflow.schema.ProcessNode;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class ModelerRemotePage extends AbstractWorkflowRemotePage {
	/**
	 * 设计器登录验证
	 * 
	 * @param pageParameter
	 * @return
	 */
	public IForward login(final PageParameter pp) {
		final String login = pp.getParameter("login");
		final String password = pp.getParameter("password");
		return doJsonForward(new IJsonForwardCallback() {
			@Override
			public void doAction(final JsonForward json) {
				pp.getPermission().login(pp, login, password, null);
				json.put("jsessionid", pp.getSessionId());
			}
		});
	}

	@Override
	public String getRole(final PageParameter pp) {
		return "login".equals(pp.getParameter("method")) ? PermissionConst.ROLE_ANONYMOUS
				: PermissionConst.ROLE_ALL_ACCOUNT;
	}

	/**
	 * 获取所有模型
	 * 
	 * @param pageParameter
	 * @return
	 */
	public IForward models(final PageParameter pp) {
		return doJsonForward(new IJsonForwardCallback() {
			@Override
			public void doAction(final JsonForward json) {
				final ArrayList<Map<?, ?>> models = new ArrayList<Map<?, ?>>();
				ProcessModelBean pm;
				final IDataQuery<ProcessModelBean> query = mService.getModelList();
				while ((pm = query.next()) != null) {
					models.add(new KVMap().add("text", pm.toString()).add("id", pm.getId()).map());
				}
				json.put("models", models);
			}
		});
	}

	/**
	 * 新建模型
	 * 
	 * @param pageParameter
	 * @return
	 */
	public IForward newModel(final PageParameter pp) {
		return doJsonForward(new IJsonForwardCallback() {
			@Override
			public void doAction(final JsonForward json) {
				final ProcessDocument document = new ProcessDocument();
				final ProcessNode processNode = document.getProcessNode();
				processNode.setName(pp.getLocaleParameter("name"));
				processNode.setDescription(pp.getLocaleParameter("description"));
				final ProcessModelBean model = mService.doAddModel(pp.getLoginId(), document);
				json.put("id", model.getId().getValue());
				json.put("text", processNode.toString());
			}
		});
	}

	public IForward model(final PageParameter pp) {
		return doJsonForward(new IJsonForwardCallback() {
			@Override
			public void doAction(final JsonForward json) {
				final ProcessModelBean pm = mService.getBean(pp.getParameter("id"));
				json.put("doc", mService.getProcessDocument(pm).toString());
			}
		});
	}

	public IForward deleteModel(final PageParameter pp) {
		return doJsonForward(new IJsonForwardCallback() {
			@Override
			public void doAction(final JsonForward json) {
				mService.delete(pp.getParameter("id"));
			}
		});
	}

	public IForward saveModel(final PageParameter pp) {
		return doJsonForward(new IJsonForwardCallback() {
			@Override
			public void doAction(final JsonForward json) {
				final ProcessModelBean bean = mService.getBean(pp.getParameter("id"));
				final String doc = pp.getLocaleParameter("doc");
				mService.doUpdateModel(bean, doc.toCharArray());
				json.put("result", Boolean.TRUE);
			}
		});
	}
}