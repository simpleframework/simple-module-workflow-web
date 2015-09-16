package net.simpleframework.workflow.web.page;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.common.Convert;
import net.simpleframework.common.DateUtils;
import net.simpleframework.common.ID;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.TimePeriod;
import net.simpleframework.module.log.AbstractEntityTblLogBean;
import net.simpleframework.module.log.EntityDeleteLog;
import net.simpleframework.module.log.EntityInsertLog;
import net.simpleframework.module.log.EntityUpdateLog;
import net.simpleframework.module.log.ILogContextAware;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.ElementList;
import net.simpleframework.mvc.common.element.LinkElement;
import net.simpleframework.mvc.common.element.SpanElement;

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
		final IDataQuery<EntityUpdateLog> dq3 = _logUpdateService.queryLogs(loginId,
				wfaService.getTablename(), period);
		while ((log = dq3.next()) != null) {
			logs.add(log);
		}

		// 更新日志
		Collections.sort(logs, new Comparator<AbstractEntityTblLogBean>() {
			@Override
			public int compare(final AbstractEntityTblLogBean o1, final AbstractEntityTblLogBean o2) {
				return o2.getCreateDate().compareTo(o1.getCreateDate());
			}
		});

		for (final AbstractEntityTblLogBean log2 : logs) {
			sb.append("<div>");
			sb.append(Convert.toDateString(log2.getCreateDate(), "HH:mm"));
			sb.append(log2.getDescription());
			sb.append("</div>");
		}
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
		final Calendar cal = Calendar.getInstance();
		long t = pp.getLongParameter("t");
		final ArrayList<LinkElement> al = new ArrayList<LinkElement>();
		LinkElement sEle = null;
		for (int i = 0; i < 7; i++) {
			final Date nDate = cal.getTime();
			final String mmdd = Convert.toDateString(nDate, "MM-dd");
			final String lbl = i == 0 ? "今天" : (i == 1 ? "昨天" : mmdd);
			final LinkElement le = LinkElement.style2(lbl).setHref(
					uFactory.getUrl(pp, MyWorklogsTPage.class, "t=" + nDate.getTime()));
			if (t > 0 && mmdd.equals(Convert.toDateString(new Date(t), "MM-dd"))) {
				le.addClassName("simple_btn2_selected");
				sEle = le;
			}
			al.add(le);
			cal.add(Calendar.DATE, -1);
		}
		if (sEle == null) {
			sEle = al.get(0);
			sEle.addClassName("simple_btn2_selected");
			t = System.currentTimeMillis();
		}

		sb.append(StringUtils.join(al, SpanElement.SEP(10).toString()));
		sb.append(" </div>");
		sb.append(" <div class='logs'>");
		final Calendar[] period = DateUtils.getDateInterval(t > 0 ? new Date(t) : null);
		sb.append(getLogs(pp, new TimePeriod(period[0].getTime(), period[1].getTime())));
		sb.append(" </div>");
		sb.append("</div>");
		return sb.toString();
	}
}
