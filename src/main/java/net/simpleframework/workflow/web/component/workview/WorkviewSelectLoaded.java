package net.simpleframework.workflow.web.component.workview;

import static net.simpleframework.common.I18n.$m;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.simpleframework.common.ID;
import net.simpleframework.common.StringUtils;
import net.simpleframework.ctx.permission.PermissionRole;
import net.simpleframework.mvc.DefaultPageHandler;
import net.simpleframework.mvc.IForward;
import net.simpleframework.mvc.JavascriptForward;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.TextForward;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.base.ajaxrequest.AjaxRequestBean;
import net.simpleframework.mvc.component.base.ajaxrequest.DefaultAjaxRequestHandler;
import net.simpleframework.mvc.component.ext.userselect.UserSelectBean;
import net.simpleframework.mvc.component.ui.dictionary.DictionaryBean;
import net.simpleframework.mvc.component.ui.listbox.AbstractListboxHandler;
import net.simpleframework.mvc.component.ui.listbox.ListItem;
import net.simpleframework.mvc.component.ui.listbox.ListItems;
import net.simpleframework.mvc.component.ui.listbox.ListboxBean;
import net.simpleframework.mvc.component.ui.menu.EMenuEvent;
import net.simpleframework.mvc.component.ui.menu.MenuBean;
import net.simpleframework.mvc.component.ui.menu.MenuItem;
import net.simpleframework.mvc.ctx.permission.IPagePermissionHandler;
import net.simpleframework.workflow.engine.IWorkflowContextAware;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class WorkviewSelectLoaded extends DefaultPageHandler implements IWorkflowContextAware {

	@Override
	public void onBeforeComponentRender(final PageParameter pp) {
		super.onBeforeComponentRender(pp);

		final ComponentParameter nCP = DoWorkviewUtils.get(pp);
		final String componentName = nCP.getComponentName();

		// 用户选取
		pp.addComponentBean(componentName + "_userSelect", UserSelectBean.class).setMultiple(true)
				.setJsSelectCallback("return DoWorkview_user_selected(selects);");
		// 预设列表字典
		final ListboxBean listbox = (ListboxBean) pp.addComponentBean(componentName + "_roleList",
				ListboxBean.class).setHandlerClass(SelectedRolesHandler.class);
		final StringBuilder js = new StringBuilder();
		js.append("$Actions['").append(componentName).append("_roleDictSelect_OK']('")
				.append(DoWorkviewUtils.toParams(nCP)).append("&roleId=' + selects[0].id);");
		js.append("return true;");
		pp.addComponentBean(componentName + "_roleDictSelect", DictionaryBean.class)
				.addListboxRef(pp, listbox.getName()).setJsSelectCallback(js.toString())
				.setClearAction("false").setShowHelpTooltip(false)
				.setTitle($m("WorkviewSelectLoaded.2"));

		// 列表选取
		pp.addComponentBean(componentName + "_roleDictSelect_OK", AjaxRequestBean.class)
				.setHandlerMethod("doRoleDictSelect").setHandlerClass(UserListAction.class);

		// 列表
		pp.addComponentBean(componentName + "_ulist", AjaxRequestBean.class)
				.setHandlerMethod("doLoad").setHandlerClass(UserListAction.class);
		// 删除
		pp.addComponentBean(componentName + "_del", AjaxRequestBean.class)
				.setHandlerMethod("doDelete").setHandlerClass(UserListAction.class);
		// 选择上次发送
		pp.addComponentBean(componentName + "_lastUlist", AjaxRequestBean.class)
				.setHandlerMethod("doLastUlist").setHandlerClass(UserListAction.class);

		pp.addComponentBean(componentName + "_clearAll", AjaxRequestBean.class)
				.setHandlerMethod("doClearAll").setHandlerClass(UserListAction.class);

		// 保存
		pp.addComponentBean(componentName + "_save", AjaxRequestBean.class)
				.setConfirmMessage($m("WorkviewSelectLoaded.1")).setHandlerMethod("doSave")
				.setHandlerClass(UserListAction.class);

		// 添加菜单
		final MenuBean mb = (MenuBean) pp
				.addComponentBean("WorkviewSelectLoaded_addMenu", MenuBean.class)
				.setMenuEvent(EMenuEvent.click).setSelector("#idWorkviewSelectLoaded_addMenu");

		mb.addItem(
				MenuItem.of($m("DoWorkviewUtils.0")).setOnclick(
						DoWorkviewUtils.jsActions(nCP, "_userSelect")))
				.addItem(
						MenuItem.of($m("DoWorkviewUtils.1")).setOnclick(
								DoWorkviewUtils.jsActions(nCP, "_roleDictSelect")))
				.addItem(MenuItem.sep())
				.addItem(
						MenuItem.of($m("DoWorkviewUtils.6")).setOnclick(
								DoWorkviewUtils.jsActions(nCP, "_lastUlist")));
	}

	private final static String COOKIE_ULIST = "doworkview_ulist";

	public static class UserListAction extends DefaultAjaxRequestHandler {

		public IForward doLoad(final ComponentParameter cp) throws Exception {
			final ComponentParameter nCP = DoWorkviewUtils.get(cp);
			final Set<String> ulist = DoWorkviewUtils.getSessionUlist(nCP);
			final String[] arr = StringUtils.split(nCP.getParameter("userIds"), ";");
			if (arr != null) {
				for (final String s : arr) {
					ulist.add(s);
				}
			}
			return new TextForward(DoWorkviewUtils.toUserList(nCP));
		}

		public IForward doDelete(final ComponentParameter cp) throws Exception {
			final ComponentParameter nCP = DoWorkviewUtils.get(cp);
			final Set<String> ulist = DoWorkviewUtils.getSessionUlist(nCP);
			final String uid = nCP.getParameter("uid");
			for (final String id : ulist) {
				if (id.equals(uid)) {
					ulist.remove(id);
					break;
				}
			}
			return new JavascriptForward("DoWorkview_user_selected();");
		}

		public IForward doSave(final ComponentParameter cp) throws Exception {
			final ComponentParameter nCP = DoWorkviewUtils.get(cp);
			final IDoWorkviewHandler hdl = (IDoWorkviewHandler) nCP.getComponentHandler();
			final Set<String> ulist = DoWorkviewUtils.getSessionUlist(nCP);
			final JavascriptForward js = new JavascriptForward();
			if (ulist == null || ulist.size() == 0) {
				js.append("alert('").append($m("WorkviewSelectLoaded.0")).append("');");
			} else {
				final List<ID> list = new ArrayList<ID>();
				final IPagePermissionHandler permission = cp.getPermission();
				for (final String id : ulist) {
					final ID oid = permission.getUser(id).getId();
					if (oid != null) {
						list.add(oid);
					}
				}
				final JavascriptForward js2 = hdl.doSent(nCP, list);
				if (js2 != null) {
					js.append(js2);
				}
				DoWorkviewUtils.removeSessionUlist(nCP);
				js.append("$Actions['").append(nCP.getComponentName()).append("_win'].close();");
				js.append("document.setCookie('").append(COOKIE_ULIST).append("', '")
						.append(StringUtils.join(list, "#")).append("');");
			}
			return js;
		}

		public IForward doClearAll(final ComponentParameter cp) throws Exception {
			return new JavascriptForward("DoWorkview_user_selected(null, 'op="
					+ StringUtils.blank(cp.getParameter("op")) + "');");
		}

		public IForward doRoleDictSelect(final ComponentParameter cp) throws Exception {
			final ComponentParameter nCP = DoWorkviewUtils.get(cp);
			final PermissionRole role = nCP.getRole(nCP.toID("roleId"));
			final Iterator<ID> it = nCP.getPermission().users(cp, role.getId());
			final Set<String> ulist = DoWorkviewUtils.getSessionUlist(nCP);
			while (it.hasNext()) {
				ulist.add(it.next().toString());
			}
			return new JavascriptForward("DoWorkview_user_selected();");
		}

		public IForward doLastUlist(final ComponentParameter cp) throws Exception {
			final ComponentParameter nCP = DoWorkviewUtils.get(cp);
			final Set<String> ulist = DoWorkviewUtils.getSessionUlist(nCP);
			final String[] arr = StringUtils.split(
					URLDecoder.decode(StringUtils.blank(cp.getCookie(COOKIE_ULIST)), "utf-8"), "#");
			final JavascriptForward js = new JavascriptForward();
			if (arr.length == 0) {
				js.append("alert('").append($m("WorkviewSelectLoaded.3")).append("');");
			} else {
				for (final String id : arr) {
					ulist.add(id);
				}
				js.append("DoWorkview_user_selected();");
			}
			return js;
		}
	}

	public static class SelectedRolesHandler extends AbstractListboxHandler {

		@Override
		public ListItems getListItems(final ComponentParameter cp) {
			final ComponentParameter nCP = DoWorkviewUtils.get(cp);
			final IDoWorkviewHandler hdl = (IDoWorkviewHandler) nCP.getComponentHandler();
			final String[] roles = hdl.getSelectedRoles(nCP);
			if (roles != null) {
				final ListItems items = ListItems.of();
				final ListboxBean listbox = (ListboxBean) cp.componentBean;
				for (final String r : roles) {
					final PermissionRole role = cp.getRole(r);
					if (role.getId() != null) {
						items.append(new ListItem(listbox, role));
					}
				}
				return items;
			}
			return null;
		}
	}
}
