package net.simpleframework.workflow.web.test;

import static net.simpleframework.common.I18n.$m;

import java.util.Map;

import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.BlockElement;
import net.simpleframework.mvc.common.element.RowField;
import net.simpleframework.mvc.common.element.TableRow;
import net.simpleframework.mvc.common.element.TableRows;
import net.simpleframework.workflow.engine.ProcessBean;
import net.simpleframework.workflow.web.page.AbstractWorkflowFormTPage;

public class FawenTestForm extends AbstractWorkflowFormTPage {

	@Override
	protected void onForward(final PageParameter pp) {
		super.onForward(pp);

		addWfCommentBean(pp).setContainerId("idFawenTestForm_comments");
	}

	@Override
	public void bindVariables(final PageParameter pp, final Map<String, Object> variables) {
	}

	@Override
	protected TableRows getTableRows(final PageParameter pp) {
		final ProcessBean process = getProcess(pp);
		wf_topic.setText(process.getTitle());

		final TableRow r1 = new TableRow(new RowField($m("AbstractWorkflowFormPage.2"), wf_topic));
		final TableRow r2 = new TableRow(new RowField($m("AbstractWorkflowFormPage.3"),
				wf_description));

		final TableRow r3 = new TableRow(new RowField("意见",
				new BlockElement().setId("idFawenTestForm_comments")));
		return TableRows.of(r1, r2, r3);
	}
}
