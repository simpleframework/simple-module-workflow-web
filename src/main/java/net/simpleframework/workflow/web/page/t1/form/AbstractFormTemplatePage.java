package net.simpleframework.workflow.web.page.t1.form;

import java.util.Map;

import net.simpleframework.common.Convert;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.web.HttpUtils;
import net.simpleframework.ctx.permission.PermissionConst;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.SessionCache;
import net.simpleframework.mvc.common.element.ElementList;
import net.simpleframework.mvc.common.element.LinkButton;
import net.simpleframework.mvc.template.t1.T1FormTemplatePage;
import net.simpleframework.workflow.web.page.IWorkflowPageAware;
import net.simpleframework.workflow.web.page.list.worklist.MyRunningWorklistTPage;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class AbstractFormTemplatePage extends T1FormTemplatePage implements IWorkflowPageAware {

	@Override
	protected void onForward(final PageParameter pp) throws Exception {
		super.onForward(pp);

		pp.addImportCSS(AbstractFormTemplatePage.class, "/form.css");
	}

	@Override
	public ElementList getLeftElements(final PageParameter pp) {
		final ElementList el = ElementList.of(getBackBtn(pp));
		return el;
	}

	@SuppressWarnings("unchecked")
	public LinkButton getBackBtn(final PageParameter pp) {
		final LinkButton backBtn = LinkButton.backBtn();

		String referer = pp.getRequestHeader("Referer");
		String href = null;
		if (StringUtils.hasText(referer)) {
			String path = HttpUtils.stripContextPath(pp.request,
					HttpUtils.stripAbsoluteUrl(referer));
			// 返回首页
			if ("".equals(path) || "/".equals(path) || "/home".equals(path)) {
				if("".equals(path)) path="/";
				backBtn.setHref((href = path));//不能全设为/，因为/可能为共公首页，/home才是个人首页
			} else {
				if (referer.contains("/workflow/")
						&& !(referer.contains("/workflow/form") || referer.contains("/workflow/view"))) {
					referer = HttpUtils.addParameters(referer, "pageNumber=__del");
					Map<String, Object> attri = (Map<String, Object>) pp
							.getSessionAttr("attributes_MyWorklistTPage_tbl");
					if (attri == null) {
						attri = (Map<String, Object>) pp
								.getSessionAttr("attributes_MyProcessWorksTPage_tbl");
					}
					if (attri != null) {
						final int pageNumber = Convert.toInt(attri.get("pageNumber"));
						if (pageNumber > 1) {
							referer = HttpUtils.addParameters(referer, "pageNumber=" + pageNumber);
						}
					}
					backBtn.setHref((href = referer));
				}
			}
		}

		if (href == null) {
			referer = (String) SessionCache.lget("_Referer");
			if (referer != null) {
				backBtn.setHref(referer);
			} else {
				backBtn.setHref(getDefaultBackUrl(pp));
			}
		} else {
			SessionCache.lput("_Referer", href);
		}
		return backBtn;
	}

	protected String getDefaultBackUrl(final PageParameter pp) {
		return uFactory.getUrl(pp, MyRunningWorklistTPage.class);
	}

	@Override
	public String getPageRole(final PageParameter pp) {
		return PermissionConst.ROLE_ALL_ACCOUNT;
	}
}
