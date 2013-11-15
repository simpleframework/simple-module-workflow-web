package net.simpleframework.workflow.web.component.action.startprocess;

import net.simpleframework.ctx.common.xml.XmlElement;
import net.simpleframework.mvc.PageDocument;
import net.simpleframework.mvc.component.AbstractComponentBean;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class StartProcessBean extends AbstractComponentBean {

	private String confirmMessage;

	public StartProcessBean(final PageDocument pageDocument, final XmlElement element) {
		super(pageDocument, element);
	}

	@Override
	public boolean isRunImmediately() {
		return false;
	}

	public String getConfirmMessage() {
		return confirmMessage;
	}

	public StartProcessBean setConfirmMessage(final String confirmMessage) {
		this.confirmMessage = confirmMessage;
		return this;
	}
}
