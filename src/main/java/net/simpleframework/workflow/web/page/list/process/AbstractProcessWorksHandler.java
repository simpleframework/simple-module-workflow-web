package net.simpleframework.workflow.web.page.list.process;

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
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public abstract class AbstractProcessWorksHandler extends AbstractScanHandler implements
		IProcessWorksHandler {

	static Map<String, IProcessWorksHandler> regists = new HashMap<String, IProcessWorksHandler>();

	@Override
	public void onScan(final IApplicationContext application) throws Exception {
		final String modelname = getModelName();
		final ProcessModelBean pm = wfpmService.getProcessModelByName(modelname);
		if (pm == null) {
			oprintln(new StringBuilder("[IProcessWorksHandler] ")
					.append($m("AbstractProcessWorksHandler.1")).append(" - ")
					.append(getClass().getName()));
			return;
		}

		if (regists.containsKey(modelname)) {
			oprintln(new StringBuilder("[IProcessWorksHandler, name: ").append(modelname).append("] ")
					.append($m("AbstractProcessWorksHandler.2")).append(" - ")
					.append(getClass().getName()));
			return;
		}
		regists.put(modelname, this);
	}

	@Override
	public void doTablePagerInit(final PageParameter pp, final TablePagerBean tablePager,
			final EProcessWorks qw) {
		tablePager
				.addColumn(AbstractWorksTPage.TC_TITLE())
				.addColumn(AbstractWorksTPage.TC_PNO())
				.addColumn(AbstractWorksTPage.TC_USER("userText", $m("ProcessMgrPage.0")))
				.addColumn(AbstractWorksTPage.TC_CREATEDATE().setWidth(100).setFormat("yy-MM-dd HH:mm"))
				.addColumn(
						AbstractWorksTPage.TC_STATUS(EProcessStatus.class).setColumnAlias("p.status"))
				.addColumn(TablePagerColumn.OPE(105)).setShowLineNo(true);
		;
	}

	@Override
	public ElementList getLeftElements(final PageParameter pp, final EProcessWorks qw) {
		if (qw == EProcessWorks.dept) {
			if (pp.getLdept().getDeptChildren().size() > 0) {
				return ElementList.of(new Checkbox("idMyProcessWorks_DeptTPage_children",
						$m("MyProcessWorksTPage.2"))
						.setOnchange("$Actions['MyProcessWorksTPage_tbl']('child=' + this.checked);"));
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return $m("AbstractProcessWorksHandler.0") + " - " + getClass().getName();
	}
}
