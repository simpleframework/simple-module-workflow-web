package net.simpleframework.workflow.web.component.modellist;

import net.simpleframework.common.coll.ArrayUtils;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.component.IComponentRegistry;
import net.simpleframework.mvc.component.ui.pager.TablePagerResourceProvider;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class ModelListResourceProvider extends TablePagerResourceProvider {

	public ModelListResourceProvider(final IComponentRegistry componentRegistry) {
		super(componentRegistry);
	}

	@Override
	public String[] getCssPath(final PageParameter pp) {
		return ArrayUtils.add(super.getCssPath(pp), getCssResourceHomePath(pp) + "/model_list.css");
	}
}
