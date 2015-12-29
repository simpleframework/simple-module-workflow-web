package net.simpleframework.workflow.web.page;

import static net.simpleframework.common.I18n.$m;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import net.simpleframework.common.Convert;
import net.simpleframework.common.ID;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.common.web.JavascriptUtils;
import net.simpleframework.ctx.trans.Transaction;
import net.simpleframework.mvc.AbstractMVCPage;
import net.simpleframework.mvc.IForward;
import net.simpleframework.mvc.JavascriptForward;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.AbstractElement;
import net.simpleframework.mvc.common.element.ElementList;
import net.simpleframework.mvc.common.element.Icon;
import net.simpleframework.mvc.common.element.InputElement;
import net.simpleframework.mvc.common.element.JS;
import net.simpleframework.mvc.common.element.RowField;
import net.simpleframework.mvc.common.element.SpanElement;
import net.simpleframework.mvc.common.element.TableRow;
import net.simpleframework.mvc.common.element.TableRows;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.base.validation.EValidatorMethod;
import net.simpleframework.mvc.component.base.validation.ValidationBean;
import net.simpleframework.mvc.component.base.validation.Validator;
import net.simpleframework.mvc.template.lets.FormTableRowTemplatePage;
import net.simpleframework.workflow.engine.EWorkitemStatus;
import net.simpleframework.workflow.engine.IWorkflowContext;
import net.simpleframework.workflow.engine.WorkitemComplete;
import net.simpleframework.workflow.engine.bean.ActivityBean;
import net.simpleframework.workflow.engine.bean.ProcessBean;
import net.simpleframework.workflow.engine.bean.WorkitemBean;
import net.simpleframework.workflow.schema.AbstractTaskNode;
import net.simpleframework.workflow.web.IWorkflowWebForm;
import net.simpleframework.workflow.web.WorkflowUtils;
import net.simpleframework.workflow.web.component.comments.IWfCommentHandler;
import net.simpleframework.workflow.web.component.comments.WfCommentBean;
import net.simpleframework.workflow.web.component.comments.WfCommentUtils;
import net.simpleframework.workflow.web.component.complete.WorkitemCompleteBean;
import net.simpleframework.workflow.web.page.t1.form.WorkflowCompleteInfoPage;
import net.simpleframework.workflow.web.page.t1.form.WorkflowFormPage;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public abstract class AbstractWorkflowFormTPage extends AbstractFormTableRowTPage<WorkitemBean>
		implements IWorkflowWebForm, IWorkflowPageAware {
	@Override
	protected boolean isPage404(final PageParameter pp) {
		return getWorkitemBean(pp) == null;
	}

	@Override
	protected void onForward(final PageParameter pp) throws Exception {
		super.onForward(pp);

		// 自动保存
		addAutoSaveComponentBean(pp);
		// 完成
		addWorkitemCompleteComponentBean(pp);

		// 验证
		addFormValidationBean(pp);

		// 更新工作项
		final WorkitemBean workitem = getWorkitemBean(pp);
		if (workitem != null) {
			final ID processId = workitem.getProcessId();

			// 重置新到意见
			wfcuService.resetCommentUser(workitem.getUserId(), processId);
			if (!workitem.isReadMark()) {
				wfwService.doReadMark(workitem);
			}

			// 更新
			final String k = "views_" + processId;
			final Object o = pp.getSessionAttr(k);
			if (o == null) {
				wfpService.doUpdateViews(wfpService.getBean(processId));
				pp.setSessionAttr(k, Boolean.TRUE);
			}
		}
	}

	protected void addAutoSaveComponentBean(final PageParameter pp) {
		addAjaxRequest(pp, "AbstractWorkflowFormTPage_autosave").setHandlerMethod("doAutosave")
				.setSelector(getFormSelector());
	}

	protected WorkitemCompleteBean addWorkitemCompleteComponentBean(final PageParameter pp) {
		// 完成
		return (WorkitemCompleteBean) addComponentBean(pp, "AbstractWorkflowFormPage_completeAction",
				WorkitemCompleteBean.class).setSelector(getFormSelector()).setParameters(
				"_isSendAction=false");
	}

	protected WfCommentBean addWfCommentBean(final PageParameter pp) {
		return addComponentBean(pp, "AbstractWorkflowFormPage_wfComment", WfCommentBean.class)
				.setEditable(!isReadonly(getWorkitemBean(pp)));
	}

	@Override
	protected ValidationBean addFormValidationBean(final PageParameter pp) {
		return super.addFormValidationBean(pp).addValidators(
				new Validator(EValidatorMethod.required, "#" + getParamKey_title()));
	}

	@Transaction(context = IWorkflowContext.class)
	public void onSaveForm(final PageParameter pp, final WorkitemBean workitem) {
		final ProcessBean process = getProcessBean(pp);

		wfpService.doUpdateKV(process, new KVMap().add("title", pp.getParameter(getParamKey_title()))
				.add("pno", pp.getParameter(getParamKey_pno())));

		// 添加评论
		doCommentSave(pp);
	}

	protected void doCommentSave(final PageParameter pp) {
		final ComponentParameter nCP = WfCommentUtils.get(pp);
		if (nCP.componentBean != null) {
			((IWfCommentHandler) nCP.getComponentHandler()).onSave(nCP);
		}
	}

	@Override
	public JavascriptForward onComplete(final PageParameter pp,
			final WorkitemComplete workitemComplete) {
		final WorkitemBean workitem = getWorkitemBean(pp);
		onSaveForm(pp, workitem);
		pp.removeSessionAttr("time_" + workitem.getId());
		return new JavascriptForward(JS.loc(uFactory.getUrl(pp, WorkflowCompleteInfoPage.class,
				workitem)));
	}

	@Override
	public JavascriptForward onSave(final ComponentParameter cp) throws Exception {
		final WorkitemBean workitem = getWorkitemBean(cp);
		onSaveForm(cp, workitem);
		cp.setSessionAttr("time_" + workitem.getId(), new Date());
		return new JavascriptForward(JS.loc(uFactory.getUrl(cp, WorkflowFormPage.class, workitem)));
	}

	@Override
	public ElementList getLeftElements(final PageParameter pp) {
		final WorkitemBean workitem = getWorkitemBean(pp);
		final ElementList el = ElementList.of();
		if (!isReadonly(workitem)) {
			final SpanElement sEle = new SpanElement().setId("idAbstractWorkflowFormTPage_saveInfo")
					.setStyle("line-height: 2;color: green");
			el.add(sEle);
			final Date date = (Date) pp.getSessionAttr("time_" + workitem.getId());
			if (date != null) {
				sEle.setText($m("AbstractWorkflowFormTPage.0", Convert.toDateString(date, "HH:mm")));
			}
		} else {
			el.add(SpanElement.strongText(WorkflowUtils.getProcessTitle(WorkflowUtils.getProcessBean(
					pp, workitem))));
		}
		return el;
	}

	protected AbstractElement<?> createSaveBtn(final PageParameter pp) {
		return VALIDATION_BTN2($m("AbstractWorkflowFormPage.0")).setOnclick(getSaveAction(null));
	}

	protected AbstractElement<?> createCompleteBtn(final PageParameter pp) {
		return VALIDATION_BTN2($m("AbstractWorkflowFormPage.1")).setIconClass(Icon.check)
				.setHighlight(true)
				.setOnclick("$Actions['AbstractWorkflowFormPage_completeAction']();");
	}

	@Override
	public ElementList getRightElements(final PageParameter pp) {
		final WorkitemBean workitem = getWorkitemBean(pp);
		final ElementList el = ElementList.of();
		if (!isReadonly(workitem)) {
			el.append(createSaveBtn(pp), SpanElement.SPACE, createCompleteBtn(pp));
		}
		return el;
	}

	protected String getParamKey_title() {
		return "wf_topic";
	}

	protected String getParamKey_pno() {
		return "wf_pno";
	}

	protected String getParamKey_description() {
		return "wf_description";
	}

	protected InputElement getInput_topic(final PageParameter pp) {
		return new InputElement(getParamKey_title());
	}

	protected InputElement getInput_description(final PageParameter pp) {
		return InputElement.textarea(getParamKey_description()).setRows(5);
	}

	@Override
	protected TableRows getTableRows(final PageParameter pp) {
		final ProcessBean process = getProcessBean(pp);
		final TableRow r1 = new TableRow(new RowField($m("AbstractWorkflowFormPage.2"),
				getInput_topic(pp).setText(process.getTitle())));
		final TableRow r2 = new TableRow(new RowField($m("AbstractWorkflowFormPage.3"),
				getInput_description(pp)));
		return TableRows.of(r1, r2);
	}

	@Override
	protected ElementList getFormElements(final PageParameter pp) {
		return ElementList.of(InputElement.hidden().setName("workitemId").setValue(pp));
	}

	@Override
	public String toTableRowsString(final PageParameter pp) {
		final TableRows tableRows = getTableRows(pp);
		if (tableRows != null) {
			return tableRows.setReadonly(isReadonly(getWorkitemBean(pp))).toString();
		}
		return null;
	}

	protected boolean isReadonly(final WorkitemBean workitem) {
		if (null == workitem) {
			return true;
		}
		final EWorkitemStatus status = workitem.getStatus();
		return status != EWorkitemStatus.running && status != EWorkitemStatus.suspended
				&& status != EWorkitemStatus.delegate;
	}

	@Override
	protected WorkitemBean getWorkitemBean(final PageParameter pp) {
		return WorkflowUtils.getWorkitemBean(pp);
	}

	protected ActivityBean getActivityBean(final PageParameter pp) {
		return WorkflowUtils.getActivityBean(pp);
	}

	protected AbstractTaskNode getTaskNode(final PageParameter pp) {
		return WorkflowUtils.getTaskNode(pp);
	}

	protected String getTaskNodeProperty(final PageParameter pp, final String key) {
		final AbstractTaskNode node = getTaskNode(pp);
		return node == null ? null : node.getProperty(key);
	}

	public void onAutosaveForm(final PageParameter pp, final WorkitemBean workitem) {
		// 保存意见
		doCommentSave(pp);
	}

	public IForward doAutosave(final ComponentParameter cp) {
		final WorkitemBean workitem = getWorkitemBean(cp);
		onAutosaveForm(cp, workitem);
		final Date date = new Date();
		cp.setSessionAttr("time_" + workitem.getId(), date);
		return toAutosaveJavascriptForward(cp, date);
	}

	protected JavascriptForward toAutosaveJavascriptForward(final ComponentParameter cp,
			final Date date) {
		final JavascriptForward js = new JavascriptForward();
		js.append("var act = $Actions['AbstractWorkflowFormTPage_autosave'];");
		js.append("act.CHANGE_MARK = false;");
		js.append("$('idAbstractWorkflowFormTPage_saveInfo').innerHTML = '")
				.append($m("AbstractWorkflowFormTPage.2", Convert.toDateString(date, "HH:mm")))
				.append("';");
		return js;
	}

	protected int getAutosaveFrequency() {
		return 60;
	}

	@Override
	protected String toHtml(final PageParameter pp,
			final Class<? extends AbstractMVCPage> pageClass, final Map<String, Object> variables,
			final String currentVariable) throws IOException {
		String html = super.toHtml(pp, pageClass, variables, currentVariable);
		if (FormTableRowTemplatePage.class.equals(pageClass)) {
			final StringBuffer js = new StringBuffer();
			js.append("var _form = $('#").append(getBlockId()).append(" form');");
			js.append("if (_form) {");
			js.append("	var act = $Actions['AbstractWorkflowFormTPage_autosave'];");
			js.append(" var _func = function() { act.CHANGE_MARK = true; };");
			js.append(" var eles = _form.select('#ta_wfcomment');"); // _form.getElements()
			js.append(" eles.invoke('observe', 'input', _func).invoke('observe', 'propertychange', _func);");
			js.append("}");

			// 添加确定退出
			// js.append("window.onbeforeunload = function(event) {");
			// js.append(" if (CHANGE_MARK) { return '确定退出吗'; }");
			// js.append("};");

			// 添加自动保存代码
			js.append("new PeriodicalExecuter(function(executer) {");
			js.append("	var act = $Actions['AbstractWorkflowFormTPage_autosave'];");
			js.append(" if (act.CHANGE_MARK) { act(); }");
			js.append("}, ").append(getAutosaveFrequency()).append(");");

			html += JavascriptUtils.wrapScriptTag(js.toString(), true);
		}
		return html;
	}
}