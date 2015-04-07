package net.simpleframework.workflow.web.component.workview;

import java.util.LinkedHashSet;
import java.util.Set;

import net.simpleframework.common.StringUtils;
import net.simpleframework.mvc.DefaultPageHandler;
import net.simpleframework.mvc.IForward;
import net.simpleframework.mvc.JavascriptForward;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.TextForward;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.base.ajaxrequest.AjaxRequestBean;
import net.simpleframework.mvc.component.base.ajaxrequest.DefaultAjaxRequestHandler;
import net.simpleframework.mvc.component.ext.userselect.UserSelectBean;
import net.simpleframework.workflow.engine.IWorkflowServiceAware;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class WorkviewSelectLoaded extends DefaultPageHandler implements IWorkflowServiceAware {

	@Override
	public void onBeforeComponentRender(final PageParameter pp) {
		super.onBeforeComponentRender(pp);

		final ComponentParameter nCP = DoWorkviewUtils.get(pp);
		final String componentName = nCP.getComponentName();

		// 用户选取
		pp.addComponentBean(componentName + "_userSelect", UserSelectBean.class).setMultiple(true)
				.setJsSelectCallback("return DoWorkview_user_selected(selects)");

		// 列表
		pp.addComponentBean(componentName + "_ulist", AjaxRequestBean.class)
				.setHandlerMethod("doLoad").setHandlerClass(UserListAction.class);
		// 删除
		pp.addComponentBean(componentName + "_del", AjaxRequestBean.class)
				.setHandlerMethod("doDelete").setHandlerClass(UserListAction.class);
		// 保存
		pp.addComponentBean(componentName + "_save", AjaxRequestBean.class)
				.setHandlerMethod("doSave").setHandlerClass(UserListAction.class);
	}

	public static class UserListAction extends DefaultAjaxRequestHandler {

		@SuppressWarnings("unchecked")
		public IForward doLoad(final ComponentParameter cp) throws Exception {
			final ComponentParameter nCP = DoWorkviewUtils.get(cp);
			Set<String> ulist = (Set<String>) nCP.getSessionAttr(DoWorkviewUtils.SESSION_ULIST);
			if (ulist == null) {
				nCP.setSessionAttr(DoWorkviewUtils.SESSION_ULIST, ulist = new LinkedHashSet<String>());
			}
			final String[] arr = StringUtils.split(nCP.getParameter("userIds"), ";");
			if (arr != null) {
				for (final String s : arr) {
					ulist.add(s);
				}
			}
			return new TextForward(DoWorkviewUtils.toUserList(nCP));
		}

		@SuppressWarnings("unchecked")
		public IForward doDelete(final ComponentParameter cp) throws Exception {
			final ComponentParameter nCP = DoWorkviewUtils.get(cp);
			final Set<String> ulist = (Set<String>) nCP.getSessionAttr(DoWorkviewUtils.SESSION_ULIST);
			if (ulist != null) {
				final String uid = nCP.getParameter("uid");
				for (final String id : ulist) {
					if (id.equals(uid)) {
						ulist.remove(id);
						break;
					}
				}
			}
			return new JavascriptForward("DoWorkview_user_selected();");
		}

		public IForward doSave(final ComponentParameter cp) throws Exception {
			final ComponentParameter nCP = DoWorkviewUtils.get(cp);
			final IDoWorkviewHandler hdl = (IDoWorkviewHandler) nCP.getComponentHandler();
			System.out.println(hdl);
			return null;
		}
	}
}
