package net.simpleframework.workflow.web.page.t1.form;

import static net.simpleframework.common.I18n.$m;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.simpleframework.common.StringUtils;
import net.simpleframework.common.web.JavascriptUtils;
import net.simpleframework.ctx.permission.PermissionUser;
import net.simpleframework.mvc.PageMapping;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.BlockElement;
import net.simpleframework.mvc.common.element.JS;
import net.simpleframework.mvc.common.element.LinkButton;
import net.simpleframework.mvc.common.element.SpanElement;
import net.simpleframework.workflow.engine.EWorkitemStatus;
import net.simpleframework.workflow.engine.bean.ActivityBean;
import net.simpleframework.workflow.engine.bean.WorkitemBean;
import net.simpleframework.workflow.schema.AbstractTaskNode;
import net.simpleframework.workflow.schema.ProcessDocument;
import net.simpleframework.workflow.schema.ProcessNode;
import net.simpleframework.workflow.web.WorkflowUtils;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
@PageMapping(url = "/workflow/completeInfo")
public class WorkflowCompleteInfoPage extends AbstractWorkflowFormPage {

	private List<ActivityBean> removeMergeNodes(final List<ActivityBean> nextActivities) {
		final List<ActivityBean> l = new ArrayList<ActivityBean>();
		if (nextActivities != null) {
			for (final ActivityBean next : nextActivities) {
				if (next.getTasknodeType() == AbstractTaskNode.TT_MERGE) {
					l.addAll(removeMergeNodes(wfaService.getLastNextActivities(next)));
				} else {
					l.add(next);
				}
			}
		}
		return l;
	}

	@Override
	public LinkButton getBackBtn(final PageParameter pp) {
		return super.getBackBtn(pp).setStyle("display: none;");
	}

	@Override
	protected String toHtml(final PageParameter pp, final Map<String, Object> variables,
			final String currentVariable) throws IOException {
		String backtime = getTaskNodeProperty(pp, "node.complete.backtime");
		if (StringUtils.isBlank(backtime)) {
			final String backnodes = getProcessNodeProperty(pp, "process.complete.backnodes");
			final AbstractTaskNode node = getTaskNode(pp);
			if (StringUtils.hasText(backnodes)
					&& ("," + backnodes + ",").indexOf("," + node.getName() + ",") > -1) {
				backtime = "0";// 直接返回
			}
		}
		boolean bt = false;
		if (StringUtils.hasText(backtime)) {
			bt = true;
		}
		final StringBuilder sb = new StringBuilder();
		sb.append("<div class='WorkflowCompleteInfoPage'>");
		sb.append(" <div class='l1'>");
		sb.append($m("WorkflowCompleteInfoPage.0"));
		if (bt) {
			sb.append(SpanElement.colorf00("").setStyle("color:#f00;padding-left:50px;")
					.setId("backtimetxt"));
		}
		final String backhref = getBackBtn(pp).getHref();
		sb.append(LinkButton.closeBtn().corner().setHref(backhref).setClassName("right"));
		sb.append(" </div>");
		sb.append(" <div class='l2'>");
		final List<ActivityBean> nextActivities = removeMergeNodes(wfaService
				.getLastNextActivities(wfwService.getActivity(WorkflowUtils.getWorkitemBean(pp))));
		if (nextActivities.size() > 0) {
			sb.append("<table>");
			for (final ActivityBean next : nextActivities) {
				if (next.getTasknodeType() == AbstractTaskNode.TT_MERGE) {
				}
				sb.append("<tr>");
				sb.append("<td class='task'>").append(next.getTasknodeText()).append("</td>");
				sb.append("<td>");
				int i = 0;
				for (final WorkitemBean workitem2 : wfwService.getWorkitems(next,
						EWorkitemStatus.running, EWorkitemStatus.delegate)) {
					if (i++ > 0) {
						sb.append(", ");
					}
					sb.append(workitem2.getUserText2());
					final Object uId = workitem2.getUserId2();
					final PermissionUser oUser = pp.getUser(uId);
					if (oUser.exists()) {
						sb.append(" (").append(oUser.getName()).append(")");
					}
					if (!uId.equals(workitem2.getUserId())) {
						sb.append("<br><span class='desc'>")
								.append($m("WorkflowCompleteInfoPage.2", workitem2.getUserText()))
								.append("</span>");
					}
				}
				sb.append("</td></tr>");
			}
			sb.append("</table>");
		} else {
			sb.append(
					new BlockElement().setClassName("winfo").setText($m("WorkflowCompleteInfoPage.1")));
		}
		sb.append(" </div>");
		sb.append("</div>");

		if (bt) {
			sb.append(JavascriptUtils
					.wrapScriptTag("var backtxtele=document.getElementById('backtimetxt');var countback="
							+ Integer.parseInt(backtime)
							+ ";function settime_back(){backtxtele.innerHTML=countback;if(countback==0){"
							+ JS.loc(backhref)
							+ "}else{countback--;setTimeout(settime_back,1000);}} settime_back();"));
		}
		return sb.toString();
	}

	protected AbstractTaskNode getTaskNode(final PageParameter pp) {
		return WorkflowUtils.getTaskNode(pp);
	}

	protected String getTaskNodeProperty(final PageParameter pp, final String key) {
		final AbstractTaskNode node = getTaskNode(pp);
		return node == null ? null : node.getProperty(key);
	}

	protected ProcessNode getProcessNode(final PageParameter pp) {
		return pp.getRequestCache("$ProcessNode", new CacheV<ProcessNode>() {
			@Override
			public ProcessNode get() {
				final ProcessDocument doc = wfpService
						.getProcessDocument(WorkflowUtils.getProcessBean(pp));
				return doc == null ? null : doc.getProcessNode();
			}
		});
	}

	protected String getProcessNodeProperty(final PageParameter pp, final String key) {
		final ProcessNode node = getProcessNode(pp);
		return node == null ? null : node.getProperty(key);
	}
}
