package net.simpleframework.workflow.web.component.modellist;

import net.simpleframework.mvc.component.ui.pager.AbstractTablePagerSchema;
import net.simpleframework.mvc.component.ui.pager.db.AbstractDbTablePagerHandler;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public abstract class AbstractModelListHandler extends AbstractDbTablePagerHandler {

	@Override
	public AbstractTablePagerSchema createTablePagerSchema() {
		return new ModelTablePagerSchema();
	}

	protected class ModelTablePagerSchema extends DefaultDbTablePagerSchema {
	}
}
