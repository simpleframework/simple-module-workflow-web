package net.simpleframework.workflow.web.test;

import static net.simpleframework.common.I18n.$m;

import java.util.Map;

import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.BlockElement;
import net.simpleframework.mvc.common.element.ElementList;
import net.simpleframework.mvc.common.element.InputElement;
import net.simpleframework.mvc.common.element.RowField;
import net.simpleframework.mvc.common.element.SpanElement;
import net.simpleframework.mvc.common.element.TableRow;
import net.simpleframework.mvc.common.element.TableRows;
import net.simpleframework.workflow.engine.bean.ProcessBean;
import net.simpleframework.workflow.engine.bean.WorkitemBean;
import net.simpleframework.workflow.web.component.comments.WfCommentBean.EGroupBy;
import net.simpleframework.workflow.web.page.AbstractWorkflowFormTPage;

public class TestForm extends AbstractWorkflowFormTPage {

	@Override
	protected void onForward(final PageParameter pp) {
		super.onForward(pp);

		addWfCommentBean(pp).setGroupBy(EGroupBy.dept).setContainerId("idTestForm_comments");
		addDoWorkviewBean(pp);
	}

	@Override
	public void onSaveForm(final PageParameter pp, final WorkitemBean workitem) {
		super.onSaveForm(pp, workitem);
	}

	@Override
	public ElementList getRightElements(final PageParameter pp) {
		return super.getRightElements(pp).append(SpanElement.SPACE).append(createDoWorkviewBtn(pp));
	}

	@Override
	public void bindVariables(final PageParameter pp, final Map<String, Object> variables) {
		// variables.put("m", pp.getIntParameter("wf_days"));
	}

	@Override
	protected TableRows getTableRows(final PageParameter pp) {
		final ProcessBean process = getProcessBean(pp);
		final InputElement wf_days = new InputElement("wf_days");
		final TableRow r1 = new TableRow(new RowField($m("AbstractWorkflowFormPage.2"),
				getInput_topic(pp).setText(process.getTitle())),
				new RowField("请假天数", wf_days).setElementsStyle("width: 200px;"));
		final TableRow r2 = new TableRow(new RowField($m("AbstractWorkflowFormPage.3"),
				getInput_description(pp)));
		final TableRow r3 = new TableRow(new RowField("意见",
				new BlockElement().setId("idTestForm_comments")));
		return TableRows.of(r1, r3, r2);
	}
}
