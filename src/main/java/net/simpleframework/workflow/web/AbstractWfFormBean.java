package net.simpleframework.workflow.web;

import net.simpleframework.ado.bean.AbstractDateAwareBean;
import net.simpleframework.ado.bean.IDescriptionBeanAware;
import net.simpleframework.common.ID;

public abstract class AbstractWfFormBean extends AbstractDateAwareBean
implements IDescriptionBeanAware  {
	private String description;

	private ID processid;
	private ID orgid;
	private ID deptid;
	private String title;

	public ID getProcessid() {
		return processid;
	}

	public void setProcessid(ID processid) {
		this.processid = processid;
	}

	public ID getOrgid() {
		return orgid;
	}

	public void setOrgid(ID orgid) {
		this.orgid = orgid;
	}

	public ID getDeptid() {
		return deptid;
	}

	public void setDeptid(ID deptid) {
		this.deptid = deptid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
	}
}
