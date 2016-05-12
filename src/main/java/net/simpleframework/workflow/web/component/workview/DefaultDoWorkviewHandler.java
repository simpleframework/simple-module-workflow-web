package net.simpleframework.workflow.web.component.workview;

import static net.simpleframework.common.I18n.$m;

import java.util.List;

import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.common.ID;
import net.simpleframework.ctx.permission.PermissionConst;
import net.simpleframework.ctx.permission.PermissionUser;
import net.simpleframework.mvc.JavascriptForward;
import net.simpleframework.mvc.common.element.SpanElement;
import net.simpleframework.mvc.component.AbstractComponentHandler;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.workflow.engine.bean.WorkitemBean;
import net.simpleframework.workflow.engine.bean.WorkviewBean;
import net.simpleframework.workflow.web.WorkflowUtils;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class DefaultDoWorkviewHandler extends AbstractComponentHandler implements
		IDoWorkviewHandler {

	@Override
	public JavascriptForward doSent(final ComponentParameter cp, final boolean allowSent,
			final List<ID> ids) {
		final WorkitemBean workitem = WorkflowUtils.getWorkitemBean(cp);
		List<WorkviewBean> list = null;
		if (workitem != null) {
			list = wfvService.createWorkviews(WorkflowUtils.getWorkitemBean(cp), allowSent,
					ids.toArray(new ID[ids.size()]));
		} else {
			final WorkviewBean workview = WorkflowUtils.getWorkviewBean(cp);
			if (workview != null) {
				list = wfvService.createForwardWorkviews(WorkflowUtils.getWorkviewBean(cp), allowSent,
						ids.toArray(new ID[ids.size()]));
			}
		}
		return createJavascriptForward(cp, list);
	}

	protected JavascriptForward createJavascriptForward(final ComponentParameter cp,
			final List<WorkviewBean> list) {
		final JavascriptForward js = new JavascriptForward();
		js.append("alert('")
				.append(
						$m("DefaultDoWorkviewHandler.0", new SpanElement(list != null ? list.size() : 0)
								.setClassName("workview_select_num"))).append("');");
		return js;
	}

	@Override
	public IDataQuery<PermissionUser> getUsers(final ComponentParameter cp) {
		return null;
	}

	@Override
	public String[] getSelectedRoles(final ComponentParameter cp) {
		return new String[] { PermissionConst.ROLE_INDEPT };
	}
}
