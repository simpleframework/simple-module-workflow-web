package net.simpleframework.workflow.web.page.org2;

import static net.simpleframework.common.I18n.$m;
import net.simpleframework.ctx.permission.PermissionDept;
import net.simpleframework.module.common.web.page.AbstractMgrTPage;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.ElementList;
import net.simpleframework.mvc.common.element.SpanElement;
import net.simpleframework.workflow.engine.IWorkflowContextAware;
import net.simpleframework.workflow.engine.impl.WorkflowContext;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class AbstractWorkflowMgrTPage extends AbstractMgrTPage implements IWorkflowContextAware {

	@Override
	protected void onForward(final PageParameter pp) {
		super.onForward(pp);

		pp.addImportCSS(AbstractWorkflowMgrTPage.class, "/wfmgrt.css");
	}

	@Override
	public String getRole(final PageParameter pp) {
		return WorkflowContext.ROLE_WORKFLOW_MANAGER;
	}

	@Override
	public ElementList getLeftElements(final PageParameter pp) {
		SpanElement oele;
		final PermissionDept org = getOrg(pp);
		if (org != null) {
			oele = new SpanElement(org.getText());
		} else {
			oele = new SpanElement($m("AbstractMgrTPage.0"));
		}
		final ElementList el = ElementList.of(oele.setClassName("org_txt"));
		// if (pp.getLogin().isManager()) {
		// el.append(SpanElement.SPACE).append(
		// new LinkElement($m("AbstractOrgMgrTPage.4"))
		// .setOnclick("$Actions['AbstractMgrTPage_orgSelect']();"));
		// }
		return el;
	}

	// @Override
	// public TabButtons getTabButtons(final PageParameter pp) {
	// return TabButtons.of(new TabButton("流程实例"));
	// }
}