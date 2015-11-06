package net.simpleframework.workflow.web.page.query;

import static net.simpleframework.common.I18n.$m;

import java.util.HashMap;
import java.util.Map;

import net.simpleframework.ctx.IApplicationContext;
import net.simpleframework.ctx.hdl.AbstractScanHandler;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.Checkbox;
import net.simpleframework.mvc.common.element.ElementList;
import net.simpleframework.mvc.component.ui.pager.TablePagerBean;
import net.simpleframework.mvc.component.ui.pager.TablePagerColumn;
import net.simpleframework.workflow.engine.EProcessStatus;
import net.simpleframework.workflow.engine.bean.ProcessModelBean;
import net.simpleframework.workflow.web.page.AbstractWorksTPage;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public abstract class AbstractQueryWorksHandler extends AbstractScanHandler implements
		IQueryWorksHandler {

	static Map<String, IQueryWorksHandler> regists = new HashMap<String, IQueryWorksHandler>();

	@Override
	public void onScan(final IApplicationContext application) throws Exception {
		final String modelname = getModelName();
		final ProcessModelBean pm = wfpmService.getProcessModelByName(modelname);
		if (pm == null) {
			oprintln(new StringBuilder("[IQueryWorksHandler] ")
					.append($m("AbstractQueryWorksHandler.1")).append(" - ")
					.append(getClass().getName()));
			return;
		}

		if (regists.containsKey(modelname)) {
			oprintln(new StringBuilder("[IQueryWorksHandler, name: ").append(modelname).append("] ")
					.append($m("AbstractQueryWorksHandler.2")).append(" - ")
					.append(getClass().getName()));
			return;
		}
		regists.put(modelname, this);
	}

	@Override
	public void doTablePagerInit(final PageParameter pp, final TablePagerBean tablePager,
			final EQueryWorks qw) {
		tablePager
				.addColumn(AbstractWorksTPage.TC_TITLE())
				.addColumn(AbstractWorksTPage.TC_PNO())
				.addColumn(AbstractWorksTPage.TC_USER("userText", $m("ProcessMgrPage.0")))
				.addColumn(AbstractWorksTPage.TC_CREATEDATE().setWidth(100).setFormat("yy-MM-dd HH:mm"))
				.addColumn(
						AbstractWorksTPage.TC_STATUS(EProcessStatus.class).setColumnAlias("p.status"))
				.addColumn(TablePagerColumn.OPE(105));
	}

	@Override
	public ElementList getLeftElements(final PageParameter pp, final EQueryWorks qw) {
		if (qw == EQueryWorks.dept) {
			if (pp.getLdept().hasChild()) {
				return ElementList.of(new Checkbox("idMyQueryWorks_DeptTPage_children",
						$m("MyQueryWorksTPage.2"))
						.setOnchange("$Actions['MyQueryWorksTPage_tbl']('child=' + this.checked);"));
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return $m("AbstractQueryWorksHandler.0") + " - " + getClass().getName();
	}
}
