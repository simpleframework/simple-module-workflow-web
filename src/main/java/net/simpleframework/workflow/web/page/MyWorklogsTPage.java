package net.simpleframework.workflow.web.page;

import static net.simpleframework.common.I18n.$m;

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
import net.simpleframework.common.web.html.HtmlConst;
import net.simpleframework.common.web.html.HtmlUtils;
import net.simpleframework.module.log.AbstractEntityTblLogBean;
import net.simpleframework.module.log.EntityDeleteLog;
import net.simpleframework.module.log.EntityInsertLog;
import net.simpleframework.module.log.EntityUpdateLog;
import net.simpleframework.module.log.ILogContextAware;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.ElementList;
import net.simpleframework.mvc.common.element.LinkElement;
import net.simpleframework.mvc.common.element.SpanElement;
import net.simpleframework.workflow.engine.EActivityStatus;
import net.simpleframework.workflow.engine.bean.ActivityBean;
import net.simpleframework.workflow.engine.bean.WorkitemBean;
import net.simpleframework.workflow.schema.AbstractTaskNode;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class MyWorklogsTPage extends AbstractItemsTPage implements ILogContextAware {

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
					uFactory.getUrl(pp, MyWorklogsTPage.class, "t=" + (nDate.getTime() / 1000)));
			if (t > 0 && mmdd.equals(Convert.toDateString(new Date(t * 1000), "MM-dd"))) {
				le.addClassName("simple_btn2_selected");
				sEle = le;
			}
			al.add(le);
			cal.add(Calendar.DATE, -1);
		}
		if (sEle == null) {
			sEle = al.get(0);
			sEle.addClassName("simple_btn2_selected");
			t = System.currentTimeMillis() / 1000;
		}

		sb.append(StringUtils.join(al, SpanElement.SEP(10).toString()));
		sb.append(" </div>");
		sb.append(" <div class='logs'>");
		final Calendar[] period = DateUtils.getDateInterval(t > 0 ? new Date(t * 1000) : null);
		sb.append(getLogs(pp, new TimePeriod(period[0].getTime(), period[1].getTime())));
		sb.append(" </div>");
		sb.append("</div>");
		return sb.toString();
	}

	private String getLogs(final PageParameter pp, final TimePeriod period) {
		final StringBuilder sb = new StringBuilder();
		final ID loginId = pp.getLoginId();

		AbstractEntityTblLogBean log;
		// ------------------------------------- 流程日志
		// 插入日志
		final IDataQuery<EntityInsertLog> dqi = _logInsertService.queryLogs(loginId,
				wfpService.getTablename(), period);
		final List<AbstractEntityTblLogBean> logs = new ArrayList<AbstractEntityTblLogBean>();
		while ((log = dqi.next()) != null) {
			logs.add(log);
		}
		// 删除日志
		final IDataQuery<EntityDeleteLog> dqd = _logDeleteService.queryLogs(loginId,
				wfpService.getTablename(), period);
		while ((log = dqd.next()) != null) {
			logs.add(log);
		}
		// 查找标题更新
		IDataQuery<EntityUpdateLog> dqu = _logUpdateService.queryLogs(loginId,
				wfpService.getTablename(), "title", period);
		while ((log = dqu.next()) != null) {
			logs.add(log);
		}

		// ------------------------------------- 环节日志

		dqu = _logUpdateService.queryLogs(loginId, wfaService.getTablename(), "status", period);
		while ((log = dqu.next()) != null) {
			final ActivityBean activity = wfaService.getBean(log.getBeanId());
			if (activity != null && activity.getTasknodeType() == AbstractTaskNode.TT_USER) {
				final EActivityStatus toVal = Convert.toEnum(EActivityStatus.class,
						((EntityUpdateLog) log).getToVal());
				if (toVal == EActivityStatus.complete || toVal == EActivityStatus.fallback) {
					logs.add(log);
				}
			}
		}

		// ------------------------------------- 工作项日志
		dqu = _logUpdateService.queryLogs(loginId, wfwService.getTablename(), period);
		while ((log = dqu.next()) != null) {
			final String valName = ((EntityUpdateLog) log).getValName();
			if ("topMark".equals(valName) || "retakeId".equals(valName)) {
				logs.add(log);
			}
		}

		// 更新日志
		Collections.sort(logs, new Comparator<AbstractEntityTblLogBean>() {
			@Override
			public int compare(final AbstractEntityTblLogBean o1, final AbstractEntityTblLogBean o2) {
				return o2.getCreateDate().compareTo(o1.getCreateDate());
			}
		});

		sb.append(toLogsHTML(logs));
		return sb.toString();
	}

	private String toLogsHTML(final List<AbstractEntityTblLogBean> logs) {
		final StringBuilder sb = new StringBuilder();
		final String pTbl = wfpService.getTablename();
		final String aTbl = wfaService.getTablename();
		final String wTbl = wfwService.getTablename();
		for (final AbstractEntityTblLogBean log : logs) {
			sb.append("<div class='clearfix'>");
			sb.append(" <div class='timec left'>");
			sb.append("  <span class='dot1'></span>");
			sb.append("  <span class='dot'></span>");
			sb.append(Convert.toDateString(log.getCreateDate(), "HH:mm"));
			sb.append(" </div>");
			sb.append(" <div class='cc left'>");
			String desc = log.getDescription();
			if (StringUtils.hasText(desc)) {
				desc = StringUtils.replace(desc, " ", HtmlConst.NBSP);
				desc = HtmlUtils.convertHtmlLines(desc);
				if (log instanceof EntityDeleteLog) {
					sb.append(SpanElement.colorf00(desc));
				} else {
					sb.append(desc);
				}
			} else {
				if (log instanceof EntityUpdateLog) {
					final EntityUpdateLog ulog = (EntityUpdateLog) log;
					final String tblName = ulog.getTblName();
					final String valName = ulog.getValName();
					if (pTbl.equals(tblName)) {
						if ("title".equals(valName)) {
							sb.append(toValChangeHTML($m("MyWorklogsTPage.0"), ulog));
						}
					} else if (aTbl.equals(tblName)) {
						if ("status".equals(valName)) {
							final EActivityStatus toVal = Convert.toEnum(EActivityStatus.class,
									ulog.getToVal());
							if (toVal == EActivityStatus.complete) {
								sb.append(toTaskCompleteHTML(ulog));
							} else if (toVal == EActivityStatus.fallback) {
								sb.append(SpanElement.colora00($m("MyWorklogsTPage.6")));
								final ActivityBean activity = wfaService.getBean(log.getBeanId());
								if (activity != null) {
									sb.append(" [ ");
									sb.append(wfpService.getBean(activity.getProcessId()));
									sb.append(" ]");
								}
							}
						}
					} else if (wTbl.equals(tblName)) {
						if ("topMark".equals(valName)) {
							if (Convert.toBool(ulog.getToVal())) {
								sb.append($m("MyWorklogsTPage.1"));
							} else {
								sb.append($m("MyWorklogsTPage.2"));
							}
						} else if ("retakeId".equals(valName)) {
							sb.append(SpanElement.colora00($m("MyWorklogsTPage.5")));
						}
						final WorkitemBean workitem = wfwService.getBean(log.getBeanId());
						if (workitem != null) {
							sb.append(" [ ");
							sb.append(wfpService.getBean(workitem.getProcessId()));
							sb.append(" ]");
						}
					}
				}
			}
			sb.append(" </div>");
			sb.append("</div>");
		}
		return sb.toString();
	}

	private String toTaskCompleteHTML(final EntityUpdateLog log) {
		final StringBuilder sb = new StringBuilder();
		final ActivityBean activity = wfaService.getBean(log.getBeanId());
		sb.append(SpanElement.color060($m("MyWorklogsTPage.3"))).append(" [ ").append(activity)
				.append(" ]").append("<br>");
		int j = 0;
		for (final ActivityBean nActivity : wfaService.getNextActivities(activity)) {
			if (j++ > 0) {
				sb.append("<br>");
			}
			sb.append(SpanElement.SPACE(20)).append(" -> ");
			sb.append(nActivity).append(" ( ");
			int i = 0;
			for (final WorkitemBean workitem : wfwService.getWorkitems(nActivity)) {
				if (i++ > 0) {
					sb.append(", ");
				}
				sb.append(workitem.getUserText());
			}
			sb.append(" )");
		}
		return sb.toString();
	}

	private String toValChangeHTML(final String title, final EntityUpdateLog log) {
		final StringBuilder sb = new StringBuilder();
		sb.append(title).append("<br>").append(SpanElement.SPACE(20));
		sb.append(SpanElement.color777(toVal(log.getFromVal()))).append(" -> ")
				.append(toVal(log.getToVal()));
		return sb.toString();
	}

	private Object toVal(final Object val) {
		return val != null ? val : $m("MyWorklogsTPage.4");
	}
}
