package net.simpleframework.workflow.web;

import net.simpleframework.ctx.IModuleContext;
import net.simpleframework.module.common.plugin.ModulePluginFactory;
import net.simpleframework.module.favorite.FavoriteRef;
import net.simpleframework.module.favorite.IFavoriteContent;
import net.simpleframework.module.favorite.web.plugin.AbstractWebFavoritePlugin;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.workflow.engine.IWorkflowContextAware;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class WorkflowFavoriteRef extends FavoriteRef implements IWorkflowContextAware {

	@Override
	public void onInit(final IModuleContext context) throws Exception {
		super.onInit(context);

		getModuleContext().getPluginRegistry().registPlugin(WorkflowWebFavoritePlugin.class);
	}

	public WorkflowWebFavoritePlugin plugin() {
		return ModulePluginFactory.get(WorkflowWebFavoritePlugin.class);
	}

	public static class WorkflowWebFavoritePlugin extends AbstractWebFavoritePlugin {
		@Override
		public String getText() {
			return "流程";
		}

		@Override
		public String getCategoryText(final Object categoryId) {
			return null;
		}

		@Override
		public IFavoriteContent getContent(final PageParameter pp, final Object contentId) {
			return null;
		}
	}
}
