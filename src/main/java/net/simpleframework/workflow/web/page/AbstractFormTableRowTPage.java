package net.simpleframework.workflow.web.page;

import static net.simpleframework.common.I18n.$m;

import net.simpleframework.common.StringUtils;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.AbstractElement;
import net.simpleframework.mvc.common.element.LinkButton;
import net.simpleframework.mvc.template.lets.FormTableRow_TabsTemplatePage;
import net.simpleframework.workflow.engine.IWorkflowContextAware;
import net.simpleframework.workflow.engine.bean.AbstractWorkitemBean;
import net.simpleframework.workflow.engine.bean.ProcessBean;
import net.simpleframework.workflow.engine.bean.ProcessModelBean;
import net.simpleframework.workflow.schema.ProcessDocument;
import net.simpleframework.workflow.schema.ProcessNode;
import net.simpleframework.workflow.web.WorkflowUtils;
import net.simpleframework.workflow.web.component.workview.DoWorkviewBean;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public abstract class AbstractFormTableRowTPage<T extends AbstractWorkitemBean>
		extends FormTableRow_TabsTemplatePage implements IWorkflowContextAware {

	public String getForwardUrl(final PageParameter pp) {
		return url(getClass());
	}

	protected DoWorkviewBean addDoWorkviewBean(final PageParameter pp) {
		return addComponentBean(pp, "AbstractWorkflowFormPage_doWorkview", DoWorkviewBean.class);
	}

	protected AbstractElement<?> createDoWorkviewBtn(final PageParameter pp) {
		final T item = getWorkitemBean(pp);
		return LinkButton.of($m("AbstractWorkflowFormTPage.1"))
				.setOnclick("$Actions['AbstractWorkflowFormPage_doWorkview']('workitemId="
						+ (null != item ? item.getId() : "") + "');");
	}

	@Override
	public String getLabelWidth(final PageParameter pp) {
		return "85px";
	}

	@Override
	public boolean isButtonsOnTop(final PageParameter pp) {
		return true;
	}

	protected abstract T getWorkitemBean(final PageParameter pp);

	protected ProcessBean getProcessBean(final PageParameter pp) {
		return WorkflowUtils.getProcessBean(pp);
	}

	protected ProcessModelBean getProcessModel(final PageParameter pp) {
		return WorkflowUtils.getProcessModel(pp);
	}

	protected ProcessNode getProcessNode(final PageParameter pp) {
		return pp.getRequestCache("$ProcessNode", new CacheV<ProcessNode>() {
			@Override
			public ProcessNode get() {
				final ProcessDocument doc = wfpService.getProcessDocument(getProcessBean(pp));
				return doc == null ? null : doc.getProcessNode();
			}
		});
	}

	protected String getProcessNodeProperty(final PageParameter pp, final String key) {
		final ProcessNode node = getProcessNode(pp);
		return node == null ? null : node.getProperty(key);
	}

	protected String getFormTopic(final PageParameter pp) {
		return null;
	}

	@Override
	public String toToolbarHTML(final PageParameter pp) {
		final StringBuilder sb = new StringBuilder();
		sb.append(super.toToolbarHTML(pp));
		final String ftopic = getFormTopic(pp);
		if (StringUtils.hasText(ftopic)) {
			sb.append("<div class='form_topic'>").append(ftopic).append("</div>");
		}
		return sb.toString();
	}
}
