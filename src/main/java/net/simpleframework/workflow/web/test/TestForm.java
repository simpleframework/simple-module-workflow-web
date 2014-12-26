package net.simpleframework.workflow.web.test;

import static net.simpleframework.common.I18n.$m;

import java.util.Map;

import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.InputElement;
import net.simpleframework.mvc.common.element.RowField;
import net.simpleframework.mvc.common.element.TableRow;
import net.simpleframework.mvc.common.element.TableRows;
import net.simpleframework.workflow.engine.ProcessBean;
import net.simpleframework.workflow.engine.WorkitemBean;
import net.simpleframework.workflow.web.page.AbstractWorkflowFormTPage;

public class TestForm extends AbstractWorkflowFormTPage {

	@Override
	public void onSaveForm(final PageParameter pp, final WorkitemBean workitem) {
		super.onSaveForm(pp, workitem);
	}

	@Override
	public void bindVariables(final PageParameter pp, final Map<String, Object> variables) {
		variables.put("m", pp.getIntParameter("wf_days"));
	}

	@Override
	protected TableRows getTableRows(final PageParameter pp) {
		final ProcessBean process = getProcess(pp);

		final InputElement wf_days = new InputElement("wf_days");
		final TableRow r1 = new TableRow(new RowField($m("AbstractWorkflowFormPage.2"), getInput_topic(pp).setText(process.getTitle())),
				new RowField("请假天数", wf_days).setElementsStyle("width: 200px;"));
		final TableRow r2 = new TableRow(new RowField($m("AbstractWorkflowFormPage.3"),
				getInput_description(pp)));
		return TableRows.of(r1, r2);
	}
}
