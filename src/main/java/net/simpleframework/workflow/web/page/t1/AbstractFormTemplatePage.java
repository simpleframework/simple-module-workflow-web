package net.simpleframework.workflow.web.page.t1;

import net.simpleframework.common.StringUtils;
import net.simpleframework.ctx.permission.PermissionConst;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.ElementList;
import net.simpleframework.mvc.common.element.LinkButton;
import net.simpleframework.mvc.template.t1.T1FormTemplatePage;
import net.simpleframework.workflow.web.page.IWorkflowPageAware;
import net.simpleframework.workflow.web.page.MyRunningWorklistTPage;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class AbstractFormTemplatePage extends T1FormTemplatePage implements IWorkflowPageAware {

	@Override
	public ElementList getLeftElements(final PageParameter pp) {
		final ElementList el = ElementList.of(getBackBtn(pp));
		return el;
	}

	public LinkButton getBackBtn(final PageParameter pp) {
		final LinkButton backBtn = LinkButton.backBtn();
		String referer = pp.getRequestHeader("Referer");
		if (StringUtils.hasText(referer) && referer.contains("/workflow/my/")) {
			backBtn.setHref(referer);
			pp.setSessionAttr("_Referer", referer);
		} else {
			referer = (String) pp.getSessionAttr("_Referer");
			if (referer != null) {
				backBtn.setHref(referer);
			} else {
				backBtn.setHref(uFactory.getUrl(pp, MyRunningWorklistTPage.class));
			}
		}
		return backBtn;
	}

	@Override
	public String getRole(final PageParameter pp) {
		return PermissionConst.ROLE_ALL_ACCOUNT;
	}
}
