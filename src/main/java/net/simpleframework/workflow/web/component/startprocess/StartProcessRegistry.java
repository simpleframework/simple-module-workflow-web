package net.simpleframework.workflow.web.component.startprocess;

import net.simpleframework.mvc.IForward;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.LinkButton;
import net.simpleframework.mvc.component.AbstractComponentBean;
import net.simpleframework.mvc.component.AbstractComponentRegistry;
import net.simpleframework.mvc.component.ComponentBean;
import net.simpleframework.mvc.component.ComponentName;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ComponentRender;
import net.simpleframework.mvc.component.ComponentResourceProvider;
import net.simpleframework.mvc.component.base.ajaxrequest.AjaxRequestBean;
import net.simpleframework.mvc.component.base.ajaxrequest.DefaultAjaxRequestHandler;
import net.simpleframework.mvc.component.ui.listbox.AbstractListboxHandler;
import net.simpleframework.mvc.component.ui.listbox.ListItem;
import net.simpleframework.mvc.component.ui.listbox.ListItems;
import net.simpleframework.mvc.component.ui.window.WindowBean;
import net.simpleframework.workflow.engine.IWorkflowContextAware;
import net.simpleframework.workflow.engine.InitiateItem;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
@ComponentName(StartProcessRegistry.STARTPROCESS)
@ComponentBean(StartProcessBean.class)
@ComponentRender(StartProcessRender.class)
@ComponentResourceProvider(StartProcessResourceProvider.class)
public class StartProcessRegistry extends AbstractComponentRegistry implements
		IWorkflowContextAware {
	public static final String STARTPROCESS = "wf_start_process";

	@Override
	public AbstractComponentBean createComponentBean(final PageParameter pp, final Object attriData) {
		final StartProcessBean startProcess = (StartProcessBean) super.createComponentBean(pp,
				attriData);

		final ComponentParameter nCP = ComponentParameter.get(pp, startProcess);
		final String componentName = nCP.getComponentName();

		// 启动流程
		pp.addComponentBean(componentName + "_startProcess", AjaxRequestBean.class)
				.setHandleMethod("doStartProcess").setHandleClass(StartProcessHandler.class)
				.setAttr("_startProcess", startProcess);

		// 角色选择
		final AjaxRequestBean ajaxRequest = pp.addComponentBean(componentName
				+ "__initiatorSelect_list", AjaxRequestBean.class);
		pp.addComponentBean(componentName + "_initiatorSelect", WindowBean.class)
				.setContentRef(ajaxRequest.getName()).setTitle("以其它角色启动").setWidth(320).setHeight(400);

		return startProcess;
	}

	public static class StartProcessHandler extends DefaultAjaxRequestHandler {

		public IForward doStartProcess(final ComponentParameter cp) throws Exception {
			final StartProcessBean startProcess = (StartProcessBean) cp.componentBean
					.getAttr("_startProcess");
			final ComponentParameter nCP = ComponentParameter.get(cp, startProcess);
			final InitiateItem initiateItem = StartProcessUtils.getInitiateItem(nCP);
			// 设置选择的其他角色
			// final String initiator = nCP.getParameter("initiator");
			// if (StringUtils.hasText(initiator)) {
			// final ID selected = ID.of(initiator);
			// initiateItem.setSelectedRoleId(selected);
			// }
			return StartProcessUtils.doStartProcess(nCP, initiateItem);
		}
	}

	public static class InitiatorDictList extends AbstractListboxHandler {
		@Override
		public ListItems getListItems(final ComponentParameter cp) {
			return ListItems.of(
					new ListItem(null, new LinkButton("a浇洒接口毒素").setOnclick("$Actions['']();")),
					new ListItem(null, "bbb"));
		}
	}
}
