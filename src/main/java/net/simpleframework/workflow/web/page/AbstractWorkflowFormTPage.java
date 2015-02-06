package net.simpleframework.workflow.web.page;

import static net.simpleframework.common.I18n.$m;

import java.util.Date;

import net.simpleframework.common.Convert;
import net.simpleframework.ctx.trans.Transaction;
import net.simpleframework.mvc.JavascriptForward;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.PageParameter.IVal;
import net.simpleframework.mvc.common.element.AbstractElement;
import net.simpleframework.mvc.common.element.ElementList;
import net.simpleframework.mvc.common.element.Icon;
import net.simpleframework.mvc.common.element.InputElement;
import net.simpleframework.mvc.common.element.RowField;
import net.simpleframework.mvc.common.element.SpanElement;
import net.simpleframework.mvc.common.element.TableRow;
import net.simpleframework.mvc.common.element.TableRows;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.base.validation.EValidatorMethod;
import net.simpleframework.mvc.component.base.validation.ValidationBean;
import net.simpleframework.mvc.component.base.validation.Validator;
import net.simpleframework.mvc.template.lets.FormTableRowTemplatePage;
import net.simpleframework.workflow.engine.ActivityBean;
import net.simpleframework.workflow.engine.EWorkitemStatus;
import net.simpleframework.workflow.engine.IWorkflowContext;
import net.simpleframework.workflow.engine.IWorkflowServiceAware;
import net.simpleframework.workflow.engine.ProcessBean;
import net.simpleframework.workflow.engine.ProcessModelBean;
import net.simpleframework.workflow.engine.WorkitemBean;
import net.simpleframework.workflow.engine.WorkitemComplete;
import net.simpleframework.workflow.schema.AbstractTaskNode;
import net.simpleframework.workflow.schema.ProcessNode;
import net.simpleframework.workflow.web.IWorkflowWebContext;
import net.simpleframework.workflow.web.IWorkflowWebForm;
import net.simpleframework.workflow.web.WorkflowUtils;
import net.simpleframework.workflow.web.component.comments.IWfCommentHandler;
import net.simpleframework.workflow.web.component.comments.WfCommentBean;
import net.simpleframework.workflow.web.component.comments.WfCommentUtils;
import net.simpleframework.workflow.web.component.complete.WorkitemCompleteBean;
import net.simpleframework.workflow.web.page.t1.WorkflowCompleteInfoPage;
import net.simpleframework.workflow.web.page.t1.WorkflowFormPage;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public abstract class AbstractWorkflowFormTPage extends FormTableRowTemplatePage implements
		IWorkflowWebForm, IWorkflowServiceAware {

	@Override
	protected boolean isPage404(final PageParameter pp) {
		return getWorkitemBean(pp) == null;
	}

	@Override
	protected void onForward(final PageParameter pp) {
		super.onForward(pp);

		// 完成
		addWorkitemCompleteComponentBean(pp);

		// 验证
		addFormValidationBean(pp);

		final WorkitemBean workitem = getWorkitemBean(pp);
		if (workitem != null && !workitem.isReadMark()) {
			wService.doReadMark(workitem);
		}
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
				new Validator(EValidatorMethod.required, "#" + getP() + "topic"));
	}

	@Transaction(context = IWorkflowContext.class)
	public void onSaveForm(final PageParameter pp, final WorkitemBean workitem) {
		final ProcessBean process = getProcess(pp);
		pService.doUpdateTitle(process, pp.getParameter(getP() + "topic"));

		// 添加了评论
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
		return new JavascriptForward("$Actions.loc('").append(
				((IWorkflowWebContext) workflowContext).getUrlsFactory().getUrl(pp,
						WorkflowCompleteInfoPage.class, workitem)).append("');");
	}

	@Override
	public JavascriptForward onSave(final ComponentParameter cp) throws Exception {
		final WorkitemBean workitem = getWorkitemBean(cp);
		onSaveForm(cp, workitem);
		cp.setSessionAttr("time_" + workitem.getId(), new Date());
		return new JavascriptForward("$Actions.loc('").append(
				((IWorkflowWebContext) workflowContext).getUrlsFactory().getUrl(cp,
						WorkflowFormPage.class, workitem)).append("');");
	}

	protected ProcessBean getProcess(final PageParameter pp) {
		return pp.getCache("$ProcessBean", new IVal<ProcessBean>() {
			@Override
			public ProcessBean get() {
				return aService.getProcessBean(getActivityBean(pp));
			}
		});
	}

	protected ProcessModelBean getProcessModel(final PageParameter pp) {
		return pp.getCache("$ProcessModelBean", new IVal<ProcessModelBean>() {
			@Override
			public ProcessModelBean get() {
				return mService.getBean(getProcess(pp).getModelId());
			}
		});
	}

	@Override
	public String getFormForward(final PageParameter pp) {
		return url(getClass());
	}

	@Override
	public ElementList getLeftElements(final PageParameter pp) {
		final ElementList el = ElementList.of();
		final WorkitemBean workitem = getWorkitemBean(pp);
		final Date date = (Date) pp.getSessionAttr("time_" + workitem.getId());
		if (date != null) {
			el.add(new SpanElement($m("AbstractWorkflowFormTPage.0",
					Convert.toDateString(date, "HH:mm"))).setColor("green"));
		}
		return el;
	}

	protected AbstractElement<?> createSaveBtn() {
		return VALIDATION_BTN2($m("AbstractWorkflowFormPage.0")).setOnclick(getSaveAction(null));
	}

	protected AbstractElement<?> createCompleteBtn() {
		return VALIDATION_BTN2($m("AbstractWorkflowFormPage.1")).setIconClass(Icon.check).setOnclick(
				"$Actions['AbstractWorkflowFormPage_completeAction']();");
	}

	@Override
	public ElementList getRightElements(final PageParameter pp) {
		final WorkitemBean workitem = getWorkitemBean(pp);
		final ElementList el = ElementList.of();
		if (!isReadonly(workitem)) {
			el.append(createSaveBtn(), createCompleteBtn());
		}
		return el;
	}

	@Override
	public boolean isButtonsOnTop(final PageParameter pp) {
		return true;
	}

	protected String getP() {
		return "wf_";
	}

	protected InputElement getInput_topic(final PageParameter pp) {
		return new InputElement(getP() + "topic");
	}

	protected InputElement getInput_description(final PageParameter pp) {
		return InputElement.textarea(getP() + "description").setRows(5);
	}

	@Override
	protected TableRows getTableRows(final PageParameter pp) {
		final ProcessBean process = getProcess(pp);
		final TableRow r1 = new TableRow(new RowField($m("AbstractWorkflowFormPage.2"),
				getInput_topic(pp).setText(process.getTitle())));
		final TableRow r2 = new TableRow(new RowField($m("AbstractWorkflowFormPage.3"),
				getInput_description(pp)));
		return TableRows.of(r1, r2);
	}

	@Override
	public String toTableRowsString(final PageParameter pp) {
		final StringBuilder sb = new StringBuilder();
		final WorkitemBean workitem = getWorkitemBean(pp);
		final TableRows tableRows = getTableRows(pp).setReadonly(isReadonly(workitem));
		sb.append(InputElement.hidden().setName("workitemId").setValue(pp));
		if (tableRows != null) {
			sb.append(tableRows);
		}
		return sb.toString();
	}

	protected boolean isReadonly(final WorkitemBean workitem) {
		if (null == workitem) {
			return true;
		}
		final EWorkitemStatus status = workitem.getStatus();
		return status != EWorkitemStatus.running && status != EWorkitemStatus.suspended
				&& status != EWorkitemStatus.delegate;
	}

	protected WorkitemBean getWorkitemBean(final PageParameter pp) {
		return WorkflowUtils.getWorkitemBean(pp);
	}

	protected ActivityBean getActivityBean(final PageParameter pp) {
		final WorkitemBean workitem = getWorkitemBean(pp);
		if (null == workitem) {
			return null;
		}
		return pp.getCache("$ActivityBean", new IVal<ActivityBean>() {
			@Override
			public ActivityBean get() {
				return wService.getActivity(workitem);
			}
		});
	}

	protected ProcessNode getProcessNode(final PageParameter pp) {
		final WorkitemBean workitem = getWorkitemBean(pp);
		if (null == workitem) {
			return null;
		}
		return pp.getCache("$ProcessNode", new IVal<ProcessNode>() {
			@Override
			public ProcessNode get() {
				return pService.getProcessDocument(getProcess(pp)).getProcessNode();
			}
		});
	}

	protected AbstractTaskNode getTaskNode(final PageParameter pp) {
		if (null == getActivityBean(pp)) {
			return null;
		}
		return pp.getCache("$TaskNode", new IVal<AbstractTaskNode>() {
			@Override
			public AbstractTaskNode get() {
				return aService.getTaskNode(getActivityBean(pp));
			}
		});
	}

	protected String getProcessProperty(final PageParameter pp, final String key) {
		if (null == getProcessNode(pp)) {
			return null;
		}
		return getProcessNode(pp).getProperty(key);
	}

	protected String getTaskNodeProperty(final PageParameter pp, final String key) {
		if (null == getTaskNode(pp)) {
			return null;
		}
		return getTaskNode(pp).getProperty(key);
	}
}