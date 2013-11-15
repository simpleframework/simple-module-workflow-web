package net.simpleframework.workflow.web.component.modellist;

import net.simpleframework.common.StringUtils;
import net.simpleframework.ctx.common.xml.XmlElement;
import net.simpleframework.mvc.PageDocument;
import net.simpleframework.mvc.component.ComponentUtils;
import net.simpleframework.mvc.component.ui.pager.TablePagerBean;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class ModelListBean extends TablePagerBean {

	public ModelListBean(final PageDocument pageDocument, final XmlElement element) {
		super(pageDocument, element);
		setShowLineNo(true);
	}

	public ModelListBean(final PageDocument pageDocument) {
		this(pageDocument, null);
	}

	@Override
	public String getDataPath() {
		return ComponentUtils.getResourceHomePath(ModelListBean.class) + "/jsp/model_list.jsp";
	}

	@Override
	public String getHandleClass() {
		return StringUtils.text(super.getHandleClass(), DefaultModelListHandler.class.getName());
	}
}
