package net.simpleframework.workflow.web.component.workview;

import static net.simpleframework.common.I18n.$m;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.component.AbstractComponentRegistry;
import net.simpleframework.mvc.component.ComponentBean;
import net.simpleframework.mvc.component.ComponentName;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ComponentRender;
import net.simpleframework.mvc.component.ComponentResourceProvider;
import net.simpleframework.mvc.component.base.ajaxrequest.AjaxRequestBean;
import net.simpleframework.mvc.component.ui.window.WindowBean;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
@ComponentName(DoWorkviewRegistry.DOWORKVIEW)
@ComponentBean(DoWorkviewBean.class)
@ComponentRender(DoWorkviewRender.class)
@ComponentResourceProvider(DoWorkviewResourceProvider.class)
public class DoWorkviewRegistry extends AbstractComponentRegistry {

	public static final String DOWORKVIEW = "wf_do_workview";

	@Override
	public DoWorkviewBean createComponentBean(final PageParameter pp, final Object attriData) {
		final DoWorkviewBean doWorkview = (DoWorkviewBean) super.createComponentBean(pp, attriData);

		final ComponentParameter nCP = ComponentParameter.get(pp, doWorkview);
		final String componentName = nCP.getComponentName();

		final AjaxRequestBean ajaxRequest = pp.addComponentBean(componentName + "_win_page",
				AjaxRequestBean.class).setUrlForward(
				getComponentResourceProvider().getResourceHomePath() + "/jsp/workview_select.jsp");
		pp.addComponentBean(componentName + "_win", WindowBean.class)
				.setContentRef(ajaxRequest.getName()).setWidth(340).setHeight(460)
				.setTitle($m("DoWorkviewRegistry.0"));

		return doWorkview;
	}
}
