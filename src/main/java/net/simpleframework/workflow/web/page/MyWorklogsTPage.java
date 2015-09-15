package net.simpleframework.workflow.web.page;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.common.DateUtils;
import net.simpleframework.common.ID;
import net.simpleframework.common.TimePeriod;
import net.simpleframework.module.log.AbstractEntityTblLogBean;
import net.simpleframework.module.log.EntityDeleteLog;
import net.simpleframework.module.log.EntityInsertLog;
import net.simpleframework.module.log.ILogContextAware;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.ElementList;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class MyWorklogsTPage extends AbstractItemsTPage implements ILogContextAware {

	private String getLogs(final PageParameter pp, final TimePeriod period) {
		final StringBuilder sb = new StringBuilder();
		final ID loginId = pp.getLoginId();

		// 插入日志
		final IDataQuery<EntityInsertLog> dq1 = _logInsertService.queryLogs(loginId,
				wfpService.getTablename(), period);
		AbstractEntityTblLogBean log;
		final List<AbstractEntityTblLogBean> logs = new ArrayList<AbstractEntityTblLogBean>();
		while ((log = dq1.next()) != null) {
			logs.add(log);
		}

		// 删除日志
		final IDataQuery<EntityDeleteLog> dq2 = _logDeleteService.queryLogs(loginId,
				wfpService.getTablename(), period);
		while ((log = dq2.next()) != null) {
			logs.add(log);
		}

		// 更新日志

		return sb.toString();
	}

	@Override
	public ElementList getRightElements(final PageParameter pp) {
		return ElementList.of(MyWorkstatTPage.getStatTabs(pp));
	}

	@Override
	protected String toListHTML(final PageParameter pp) {
		final StringBuilder sb = new StringBuilder();
		sb.append("<div class='MyWorklogsTPage'>");
		sb.append(" <div class='topbar clearfix'>");
		sb.append(" </div>");
		sb.append(" <div class='logs'>");
		final Calendar[] cal = DateUtils.getTodayInterval();
		sb.append(getLogs(pp, new TimePeriod(cal[0].getTime(), cal[1].getTime())));
		sb.append(" </div>");
		sb.append("</div>");
		return sb.toString();
	}
}
