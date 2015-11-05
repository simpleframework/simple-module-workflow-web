package net.simpleframework.workflow.web.page.query;

import static net.simpleframework.common.I18n.$m;

import java.util.HashMap;
import java.util.Map;

import net.simpleframework.ctx.IApplicationContext;
import net.simpleframework.ctx.hdl.AbstractScanHandler;
import net.simpleframework.workflow.WorkflowException;
import net.simpleframework.workflow.engine.bean.ProcessModelBean;

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
		// if (regists.containsKey(no)) {
		// throw WorkflowException.of("[IWfNoticeTypeHandler, no: " + no + "] "
		// + $m("AbstractWfNoticeTypeHandler.0"));
		// }
		// regists.put(no, this);
		final ProcessModelBean pm = getProcessModel();
		if (pm == null) {
			throw WorkflowException.of("");
		}
	}

	@Override
	public String toString() {
		return $m("AbstractQueryWorksHandler.0") + " - " + getClass().getName();
	}
}
