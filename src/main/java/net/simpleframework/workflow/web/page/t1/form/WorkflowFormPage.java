package net.simpleframework.workflow.web.page.t1.form;

import static net.simpleframework.common.I18n.$m;

import java.io.IOException;
import java.util.Map;

import net.simpleframework.ctx.permission.PermissionUser;
import net.simpleframework.mvc.PageMapping;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.AbstractElement;
import net.simpleframework.mvc.common.element.BlockElement;
import net.simpleframework.mvc.common.element.ElementList;
import net.simpleframework.mvc.common.element.SpanElement;
import net.simpleframework.mvc.common.element.TabButtons;
import net.simpleframework.workflow.engine.bean.ActivityBean;
import net.simpleframework.workflow.engine.bean.ProcessBean;
import net.simpleframework.workflow.engine.bean.ProcessModelBean;
import net.simpleframework.workflow.engine.bean.WorkitemBean;
import net.simpleframework.workflow.web.IWorkflowWebForm;
import net.simpleframework.workflow.web.WorkflowUtils;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
@PageMapping(url = "/workflow/form")
public class WorkflowFormPage extends AbstractWorkflowFormPage {

	@Override
	protected String toHtml(final PageParameter pp, final Map<String, Object> variables,
			final String currentVariable) throws IOException {
		final StringBuilder sb = new StringBuilder();
		sb.append("<div class='WorkflowFormPage'>");
		final IWorkflowWebForm workflowForm = getWorkflowForm(pp);
		if (workflowForm != null) {
			sb.append(pp.includeUrl(workflowForm.getForwardUrl(pp)));
		} else {
			sb.append(SpanElement.warnText($m("WorkflowFormPage.2")));
		}
		sb.append("</div>");
		return sb.toString();
	}

	@Override
	public String getTitle(final PageParameter pp) {
		final ProcessBean process = WorkflowUtils.getProcessBean(pp);
		return process != null ? WorkflowUtils.getProcessTitle(process) : super.getTitle(pp);
	}

	@Override
	public ElementList getLeftElements(final PageParameter pp) {
		final ElementList el = super.getLeftElements(pp);
		final StringBuilder sb = new StringBuilder();
		final ProcessModelBean pm = WorkflowUtils.getProcessModel(pp);
		sb.append(WorkflowUtils.getShortMtext(pm));
		sb.append(" / ");
		final ActivityBean activity = WorkflowUtils.getActivityBean(pp);
		sb.append(activity.getTasknodeText());
		final String userFrom = WorkflowUtils.getUserFrom(activity, ", ");
		if (userFrom != null) {
			sb.append(" (").append($m("WorkflowFormPage.3")).append(userFrom).append(")");
		}
		el.append(new BlockElement().setClassName("taskinfo").addHtml(sb.toString()));
		return el;
	}

	@Override
	protected AbstractElement<?> getLoginElement(final PageParameter pp) {
		final PermissionUser login = pp.getLogin();
		if (login.exists()) {
			final StringBuilder sb = new StringBuilder(login.toString()).append(" (");
			final WorkitemBean workitem = WorkflowUtils.getWorkitemBean(pp);
			if (null != workitem && null != workitem.getDeptId()) {
				sb.append(pp.getDept(workitem.getDeptId()));
			} else {
				sb.append(pp.getLdept());
			}
			sb.append(")");
			return new SpanElement(sb.toString(), "login");
		}
		return null;
	}

	@Override
	public TabButtons getTabButtons(final PageParameter pp) {
		final WorkitemBean workitem = WorkflowUtils.getWorkitemBean(pp);
		return TabButtons.of(createFormTab(pp, workitem),
				createMonitorTab(pp, workitem).setTabIndex(1));
	}
}
