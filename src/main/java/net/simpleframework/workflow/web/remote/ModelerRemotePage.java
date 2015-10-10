package net.simpleframework.workflow.web.remote;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.common.IoUtils;
import net.simpleframework.common.JsonUtils;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.ctx.permission.PermissionConst;
import net.simpleframework.mvc.IForward;
import net.simpleframework.mvc.IForwardCallback.IJsonForwardCallback;
import net.simpleframework.mvc.JsonForward;
import net.simpleframework.mvc.MVCUtils;
import net.simpleframework.mvc.PageMapping;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.workflow.engine.bean.ProcessModelBean;
import net.simpleframework.workflow.schema.ProcessDocument;
import net.simpleframework.workflow.schema.ProcessNode;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
@PageMapping(url = "/wf-remote-modeler")
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
	public String getPageRole(final PageParameter pp) {
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
				final IDataQuery<ProcessModelBean> query = wfpmService.getModelList();
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
				final ProcessModelBean model = wfpmService.doAddModel(pp.getLoginId(), document);
				json.put("id", model.getId().getValue());
				json.put("text", processNode.toString());
			}
		});
	}

	public IForward model(final PageParameter pp) {
		return doJsonForward(new IJsonForwardCallback() {
			@Override
			public void doAction(final JsonForward json) {
				final ProcessModelBean pm = wfpmService.getBean(pp.getParameter("id"));
				json.put("doc", wfpmService.getProcessDocument(pm).toString());
			}
		});
	}

	public IForward deleteModel(final PageParameter pp) {
		return doJsonForward(new IJsonForwardCallback() {
			@Override
			public void doAction(final JsonForward json) {
				wfpmService.delete(pp.getParameter("id"));
			}
		});
	}

	public IForward saveModel(final PageParameter pp) {
		return doJsonForward(new IJsonForwardCallback() {
			@Override
			public void doAction(final JsonForward json) {
				final ProcessModelBean bean = wfpmService.getBean(pp.getParameter("id"));
				final String doc = pp.getLocaleParameter("doc");
				wfpmService.doUpdateModel(bean, doc.toCharArray());
				json.put("result", Boolean.TRUE);
			}
		});
	}

	/**
	 * 获取所有执行者插件
	 * 
	 * @param pp
	 * @return
	 */
	public IForward participants(final PageParameter pp) {
		return doJsonForward(new IJsonForwardCallback() {
			@SuppressWarnings("unchecked")
			@Override
			public void doAction(final JsonForward json) {
				final File settingsFile = new File(MVCUtils.getRealPath("/WEB-INF/participants.json"));
				// File settingsFile = new File("participants.json");
				// if (!settingsFile.exists()) {
				// ClassUtils.getResourceAsStream("participants.json");
				// } else {
				// new FileInputStream(settingsFile);
				// }
				if (settingsFile.exists()) {
					FileInputStream iStream = null;
					try {
						final String jsons = IoUtils
								.getStringFromInputStream(iStream = new FileInputStream(settingsFile));
						if (StringUtils.hasText(jsons)) {
							json.put((Map<String, Object>) JsonUtils.toMap(jsons));
						}
					} catch (final IOException e) {
						e.printStackTrace();
					} finally {
						if (iStream != null) {
							try {
								iStream.close();
							} catch (final IOException e) {
							}
						}
					}
				}
			}
		});
	}
}