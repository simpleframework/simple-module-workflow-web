package net.simpleframework.workflow.web.page.query;

import static net.simpleframework.common.I18n.$m;

import java.util.HashMap;
import java.util.Map;

import net.simpleframework.ctx.IApplicationContext;
import net.simpleframework.ctx.hdl.AbstractScanHandler;
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
		final ProcessModelBean pm = getProcessModel();
		if (pm == null) {
			oprintln(new StringBuilder("[IQueryWorksHandler] ")
					.append($m("AbstractQueryWorksHandler.1")).append(" - ")
					.append(getClass().getName()));
			return;
		}
		String modelname;
		if (regists.containsKey(modelname = pm.getModelName())) {
			oprintln(new StringBuilder("[IQueryWorksHandler, name: ").append(modelname).append("] ")
					.append($m("AbstractQueryWorksHandler.2")).append(" - ")
					.append(getClass().getName()));
			return;
		}
		regists.put(pm.getModelName(), this);
	}

	@Override
	public String toString() {
		return $m("AbstractQueryWorksHandler.0") + " - " + getClass().getName();
	}
}
