package net.simpleframework.workflow.web.component.comments;

import static net.simpleframework.common.I18n.$m;

import java.util.Date;

import net.simpleframework.mvc.DefaultPageHandler;
import net.simpleframework.mvc.IForward;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.TextForward;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.base.ajaxrequest.AjaxRequestBean;
import net.simpleframework.mvc.component.base.ajaxrequest.DefaultAjaxRequestHandler;
import net.simpleframework.workflow.engine.IWorkflowContextAware;
import net.simpleframework.workflow.engine.comment.IWfCommentLogService;
import net.simpleframework.workflow.engine.comment.WfCommentLog;
import net.simpleframework.workflow.engine.comment.WfCommentLog.ELogType;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class WfCommentLogLoaded extends DefaultPageHandler implements IWorkflowContextAware {

	@Override
	public void onBeforeComponentRender(final PageParameter pp) {
		super.onBeforeComponentRender(pp);

		final ComponentParameter nCP = WfCommentUtils.get(pp);
		final String commentName = nCP.getComponentName();

		pp.addComponentBean(commentName + "_logTab", AjaxRequestBean.class).setHandlerMethod("doTab")
				.setHandlerClass(LobTabAction.class).setAttr("$wfcomment", nCP.componentBean);

		pp.addComponentBean(commentName + "_logDel", AjaxRequestBean.class).setHandlerMethod("doDel")
				.setHandlerClass(LobTabAction.class).setAttr("$wfcomment", nCP.componentBean);
		pp.addComponentBean(commentName + "_logCopy", AjaxRequestBean.class)
				.setHandlerMethod("doCopy").setHandlerClass(LobTabAction.class)
				.setAttr("$wfcomment", nCP.componentBean);
	}

	public static class LobTabAction extends DefaultAjaxRequestHandler {

		public IForward doTab(final ComponentParameter cp) throws Exception {
			final ComponentParameter nCP = ComponentParameter.getByAttri(cp, "$wfcomment");
			return new TextForward(WfCommentUtils.toLogsHTML(nCP));
		}

		public IForward doDel(final ComponentParameter cp) throws Exception {
			final ComponentParameter nCP = ComponentParameter.getByAttri(cp, "$wfcomment");
			if (workflowContext.getCommentLogService().delete(nCP.getParameter("logid")) == 1) {
				return new TextForward("true");
			} else {
				return null;
			}
		}

		public IForward doCopy(final ComponentParameter cp) throws Exception {
			final ComponentParameter nCP = ComponentParameter.getByAttri(cp, "$wfcomment");
			final IWfCommentLogService lService = workflowContext.getCommentLogService();
			final WfCommentLog log = lService.getBean(nCP.getParameter("logid"));
			final WfCommentLog log2 = lService.createBean();
			log2.setUserId(log.getUserId());
			log2.setCreateDate(new Date());
			log2.setCommentId(log.getCommentId());
			log2.setCcomment(log.getCcomment());
			log2.setLogType(ELogType.collection);
			lService.insert(log2);
			return new TextForward($m("WfCommentLogLoaded.0"));
		}
	}
}