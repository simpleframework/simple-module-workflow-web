package net.simpleframework.workflow.web.page;

import static net.simpleframework.common.I18n.$m;
import net.simpleframework.common.web.JavascriptUtils;
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
public abstract class AbstractFormTableRowTPage<T extends AbstractWorkitemBean> extends
		FormTableRow_TabsTemplatePage implements IWorkflowContextAware {

	public String getForwardUrl(final PageParameter pp) {
		return url(getClass());
	}

	protected DoWorkviewBean addDoWorkviewBean(final PageParameter pp) {
		return addComponentBean(pp, "AbstractWorkflowFormPage_doWorkview", DoWorkviewBean.class);
	}

	protected AbstractElement<?> createDoWorkviewBtn(final PageParameter pp) {
		final T item = getWorkitemBean(pp);
		return LinkButton.of($m("AbstractWorkflowFormTPage.1")).setOnclick(
				"$Actions['AbstractWorkflowFormPage_doWorkview']('workitemId="
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

	@Override
	protected String toFormHTML(final PageParameter pp) {
		final StringBuilder sb = new StringBuilder();
		super.toFormHTML(pp);
		final StringBuilder js = new StringBuilder();
		js.append("var topb = $('.form_content .FormTableRowTemplatePage>.tool_bar'); if (!topb) return;");
		js.append("var w = parseInt(topb.getStyle('width'));");
		js.append("Event.observe(window, 'scroll', function() {");
		js.append(" var scrollTop = document.documentElement.scrollTop || document.body.scrollTop || 0;");
		js.append(" if (scrollTop > 0) {");
		js.append("   topb.addClassName('scroll');");
		js.append("   topb.up().addClassName('scroll');");
		js.append("   if (!topb._scroll) { topb.setStyle('width: ' + w + 'px;'); topb._scroll = true; }");
		js.append(" } else {");
		js.append("   topb.removeClassName('scroll');");
		js.append("   topb.up().removeClassName('scroll');");
		js.append("   topb._scroll = null;");
		js.append(" }");
		js.append("});");
		sb.append(JavascriptUtils.wrapScriptTag(js.toString(), true));
		return sb.toString();
	}
}
