package net.simpleframework.workflow.web.page;

import static net.simpleframework.common.I18n.$m;

import java.util.Map;

import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.ado.query.ListDataQuery;
import net.simpleframework.common.ID;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.mvc.JavascriptForward;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.ElementList;
import net.simpleframework.mvc.common.element.JS;
import net.simpleframework.mvc.common.element.LinkButton;
import net.simpleframework.mvc.common.element.LinkElement;
import net.simpleframework.mvc.common.element.SpanElement;
import net.simpleframework.mvc.common.element.TabButton;
import net.simpleframework.mvc.common.element.TabButtons;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ui.pager.AbstractTablePagerSchema;
import net.simpleframework.mvc.component.ui.pager.EPagerBarLayout;
import net.simpleframework.mvc.component.ui.pager.TablePagerBean;
import net.simpleframework.mvc.component.ui.pager.TablePagerColumn;
import net.simpleframework.mvc.component.ui.pager.db.GroupDbTablePagerHandler;
import net.simpleframework.workflow.engine.InitiateItem;
import net.simpleframework.workflow.engine.InitiateItems;
import net.simpleframework.workflow.engine.bean.ProcessBean;
import net.simpleframework.workflow.engine.bean.ProcessModelBean;
import net.simpleframework.workflow.engine.bean.WorkitemBean;
import net.simpleframework.workflow.schema.ProcessDocument;
import net.simpleframework.workflow.web.component.startprocess.DefaultStartProcessHandler;
import net.simpleframework.workflow.web.component.startprocess.StartProcessBean;
import net.simpleframework.workflow.web.page.t1.WorkflowFormPage;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class MyInitiateItemsTPage extends AbstractItemsTPage {

	@Override
	protected void onForward(final PageParameter pp) throws Exception {
		super.onForward(pp);
		// 发起流程
		addStartProcess(pp);

		addTablePagerBean(pp);
	}

	protected StartProcessBean addStartProcess(final PageParameter pp) {
		return (StartProcessBean) pp.addComponentBean("MyInitiateItemsTPage_startProcess",
				StartProcessBean.class)
		// .setConfirmMessage($m("MyInitiateItemsTPage.0"))
				.setHandlerClass(_StartProcessHandler.class);
	}

	@Override
	public ElementList getRightElements(final PageParameter pp) {
		return ElementList.of(getTabs(pp));
	}

	static SpanElement getTabs(final PageParameter pp) {
		return createTabsElement(pp, TabButtons.of(new TabButton($m("MyInitiateItemsTPage.5"),
				uFactory.getUrl(pp, MyInitiateItemsGroupTPage.class)), new TabButton(
				$m("MyInitiateItemsTPage.6"), uFactory.getUrl(pp, MyInitiateItemsTPage.class))));
	}

	protected TablePagerBean addTablePagerBean(final PageParameter pp) {
		final TablePagerBean tablePager = (TablePagerBean) addTablePagerBean(pp,
				"MyInitiateItemsTPage_tbl", MyInitiateItemsTbl.class).setGroupColumn("gc")
				.setPagerBarLayout(EPagerBarLayout.none);
		tablePager
				.addColumn(TC_ICON())
				.addColumn(new TablePagerColumn("modelText", $m("MyInitiateItemsTPage.1")))
				.addColumn(
						new TablePagerColumn("processCount", $m("MyInitiateItemsTPage.3"), 80).setFilter(
								false).setPropertyClass(Integer.class))
				.addColumn(
						new TablePagerColumn("version", $m("MyInitiateItemsTPage.4"), 80)
								.setFilter(false)).addColumn(TablePagerColumn.OPE(70));
		return tablePager;
	}

	public static class MyInitiateItemsTbl extends GroupDbTablePagerHandler {

		@Override
		public IDataQuery<?> createDataObjectQuery(final ComponentParameter cp) {
			final ID loginId = cp.getLoginId();
			final InitiateItems items = wfpmService.getInitiateItems(loginId).sort();
			return new ListDataQuery<InitiateItem>(items);
		}

		@Override
		public AbstractTablePagerSchema createTablePagerSchema() {
			return new DefaultTablePagerSchema() {
				private ProcessModelBean getProcessModelBean(final InitiateItem initiateItem) {
					return wfpmService.getBean(initiateItem.getModelId());
				}

				@Override
				public Object getVal(final Object dataObject, final String key) {
					final InitiateItem initiateItem = (InitiateItem) dataObject;
					if ("gc".equals(key)) {
						final ProcessModelBean processModel = getProcessModelBean(initiateItem);
						final String[] arr = StringUtils.split(processModel.getModelText(), ".");
						if (arr.length > 1) {
							return arr[0];
						} else {
							return $m("MyInitiateItemsGroupTPage.0");
						}
					} else if ("modelText".equals(key)) {
						return getProcessModelBean(initiateItem).getModelText();
					} else if ("processCount".equals(key)) {
						return getProcessModelBean(initiateItem).getProcessCount();
					} else if ("version".equals(key)) {
						final ProcessDocument doc = wfpmService
								.getProcessDocument(getProcessModelBean(initiateItem));
						return doc.getProcessNode().getVersion();
					}
					return super.getVal(dataObject, key);
				}
			};
		}

		@Override
		protected Map<String, Object> getRowData(final ComponentParameter cp, final Object dataObject) {
			final InitiateItem initiateItem = (InitiateItem) dataObject;
			final Object modelId = initiateItem.getModelId();
			final ProcessModelBean processModel = wfpmService.getBean(modelId);
			final KVMap row = new KVMap();
			final String mtxt = processModel.getModelText();
			final int p = mtxt.indexOf('.');
			row.add("modelText", new LinkElement(p > 0 ? mtxt.substring(p + 1) : mtxt)
					.setOnclick("$Actions['MyInitiateItemsTPage_startProcess']('modelId=" + modelId
							+ "');"));
			row.add("version", processModel.getModelVer());
			row.add("processCount", processModel.getProcessCount());
			final StringBuilder sb = new StringBuilder();
			sb.append(LinkButton.corner($m("MyInitiateItemsTPage.2")).setOnclick(
					"$Actions['MyInitiateItemsTPage_startProcess'].initiator_select('modelId=" + modelId
							+ "');"));
			row.put(TablePagerColumn.OPE, sb.toString());
			return row;
		}
	}

	public static class _StartProcessHandler extends DefaultStartProcessHandler {

		@Override
		public JavascriptForward onStartProcess(final ComponentParameter cp, final ProcessBean process) {
			final WorkitemBean workitem = wfpService.getFirstWorkitem(process);
			if (workitem != null) {
				final JavascriptForward js = new JavascriptForward();
				js.append(JS.loc(uFactory.getUrl(cp, WorkflowFormPage.class, workitem)));
				return js;
			} else {
				return null;
			}
		}
	}
}
