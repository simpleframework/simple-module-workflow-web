package net.simpleframework.workflow.web.component;

import net.simpleframework.common.StringUtils;
import net.simpleframework.ctx.permission.PermissionConst;
import net.simpleframework.mvc.component.AbstractComponentBean;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public abstract class AbstractWfActionBean extends AbstractComponentBean {
	/* action角色 */
	private String role;

	public String getRole() {
		return StringUtils.hasText(role) ? role : PermissionConst.ROLE_ALL_ACCOUNT;
	}

	public void setRole(final String role) {
		this.role = role;
	}
}
