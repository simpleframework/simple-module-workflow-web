package net.simpleframework.workflow.web.page.list.stat;

import static net.simpleframework.common.I18n.$m;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import net.simpleframework.ado.ColumnData;
import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.common.Convert;
import net.simpleframework.common.DateUtils;
import net.simpleframework.common.ID;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.TimePeriod;
import net.simpleframework.module.log.ILogContextAware;
import net.simpleframework.module.log.bean.AbstractEntityTblLogBean;
import net.simpleframework.module.log.bean.EntityDeleteLog;
import net.simpleframework.module.log.bean.EntityInsertLog;
import net.simpleframework.module.log.bean.EntityUpdateLog;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.ElementList;
import net.simpleframework.mvc.common.element.LinkElement;
import net.simpleframework.mvc.common.element.SpanElement;
import net.simpleframework.workflow.engine.EActivityStatus;
import net.simpleframework.workflow.engine.EDelegationSource;
import net.simpleframework.workflow.engine.EDelegationStatus;
import net.simpleframework.workflow.engine.bean.ActivityBean;
import net.simpleframework.workflow.engine.bean.DelegationBean;
import net.simpleframework.workflow.engine.bean.WorkitemBean;
import net.simpleframework.workflow.schema.AbstractTaskNode;
import net.simpleframework.workflow.web.page.list.AbstractItemsTPage;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
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
		final ArrayList<LinkElement> al = new ArrayList<>();
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

	private final String TBL_PROCESS = wfpService.getTablename();
	private final String TBL_ACTIVITY = wfaService.getTablename();
	private final String TBL_WORKITEM = wfwService.getTablename();
	private final String TBL_DELEGATION = wfdService.getTablename();

	private String getLogs(final PageParameter pp, final TimePeriod period) {
		final StringBuilder sb = new StringBuilder();
		final ID loginId = pp.getLoginId();

		final List<AbstractEntityTblLogBean> logs = new ArrayList<>();
		AbstractEntityTblLogBean log;
		// 删除日志
		final IDataQuery<EntityDeleteLog> dqd = _logDeleteService.queryLogs(loginId,
				new String[] { TBL_PROCESS, TBL_DELEGATION }, period, ColumnData.EMPTY);
		while ((log = dqd.next()) != null) {
			logs.add(log);
		}

		// 插入日志
		final IDataQuery<EntityInsertLog> dqi = _logInsertService.queryLogs(loginId,
				new String[] { TBL_PROCESS, TBL_DELEGATION }, period, ColumnData.EMPTY);
		while ((log = dqi.next()) != null) {
			logs.add(log);
		}

		// 更新日志
		// ------------------------------------- 流程日志
		IDataQuery<EntityUpdateLog> dqu = _logUpdateService.queryLogs(loginId, TBL_PROCESS, "title",
				period, ColumnData.EMPTY);
		while ((log = dqu.next()) != null) {
			logs.add(log);
		}
		// ------------------------------------- 环节日志
		dqu = _logUpdateService.queryLogs(loginId, TBL_ACTIVITY, "status", period, ColumnData.EMPTY);
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
		dqu = _logUpdateService.queryLogs(loginId, TBL_WORKITEM, period, ColumnData.EMPTY);
		while ((log = dqu.next()) != null) {
			final String valName = ((EntityUpdateLog) log).getValName();
			if ("topMark".equals(valName) || "retakeId".equals(valName)) {
				logs.add(log);
			}
		}
		// ------------------------------------- 委托日志
		dqu = _logUpdateService.queryLogs(loginId, TBL_DELEGATION, period, ColumnData.EMPTY);
		while ((log = dqu.next()) != null) {
			logs.add(log);
		}

		// 更新日志
		Collections.sort(logs, new Comparator<AbstractEntityTblLogBean>() {
			@Override
			public int compare(final AbstractEntityTblLogBean o1, final AbstractEntityTblLogBean o2) {
				return o2.getCreateDate().compareTo(o1.getCreateDate());
			}
		});

		sb.append(toLogsHTML(pp, logs));
		return sb.toString();
	}

	private String toLogsHTML(final PageParameter pp, final List<AbstractEntityTblLogBean> logs) {
		final StringBuilder sb = new StringBuilder();
		for (final AbstractEntityTblLogBean log : logs) {
			sb.append("<div class='clearfix'>");
			sb.append(" <div class='timec left'>");
			sb.append("  <span class='dot1'></span>");
			sb.append("  <span class='dot'></span>");
			sb.append(Convert.toDateString(log.getCreateDate(), "HH:mm"));
			sb.append(" </div>");
			sb.append(" <div class='cc left'>");
			String logHTML = null;
			if (log instanceof EntityInsertLog) {
				logHTML = toInsertLogHTML(pp, (EntityInsertLog) log);
			} else if (log instanceof EntityDeleteLog) {
				logHTML = toDeleteLogHTML(pp, (EntityDeleteLog) log);
			} else if (log instanceof EntityUpdateLog) {
				logHTML = toUpdateLogHTML(pp, (EntityUpdateLog) log);
			}
			if (StringUtils.hasText(logHTML)) {
				sb.append(logHTML);
			}
			sb.append(" </div>");
			sb.append("</div>");
		}
		return sb.toString();
	}

	private String toInsertLogHTML(final PageParameter pp, final EntityInsertLog log) {
		final StringBuilder sb = new StringBuilder();
		final String tblName = log.getTblName();
		if (TBL_PROCESS.equals(tblName)) {
			sb.append(act("启动新工作")).append(log.getDescription());
		} else if (TBL_DELEGATION.equals(tblName)) {
			sb.append(act("设置委托"));
			final DelegationBean delegation = wfdService.getBean(log.getBeanId());
			if (delegation != null) {
				sb.append(toDelegationHTML(pp, delegation));
			} else {
				sb.append(log.getDescription());
			}
		}
		return sb.toString();
	}

	private String toDeleteLogHTML(final PageParameter pp, final EntityDeleteLog log) {
		final StringBuilder sb = new StringBuilder();
		final String tblName = log.getTblName();
		if (TBL_PROCESS.equals(tblName)) {
			sb.append(tag("删除未发送工作", "#f00")).append(log.getDescription());
		} else if (TBL_DELEGATION.equals(tblName)) {
			sb.append(act("删除委托")).append(log.getDescription());
		}
		return sb.toString();
	}

	private String toUpdateLogHTML(final PageParameter pp, final EntityUpdateLog log) {
		final StringBuilder sb = new StringBuilder();
		final String tblName = log.getTblName();
		final String valName = log.getValName();
		if (TBL_PROCESS.equals(tblName)) {
			if ("title".equals(valName)) {
				sb.append(toValChangeHTML($m("MyWorklogsTPage.0"), log));
			}
		} else if (TBL_ACTIVITY.equals(tblName)) {
			if ("status".equals(valName)) {
				final EActivityStatus toVal = Convert.toEnum(EActivityStatus.class, log.getToVal());
				if (toVal == EActivityStatus.complete) {
					sb.append(tag($m("MyWorklogsTPage.3"), "#060"));
					final ActivityBean activity = wfaService.getBean(log.getBeanId());
					if (activity != null) {
						sb.append(wfpService.getBean(activity.getProcessId()));
						sb.append(toNextActivitiesHTML(pp, activity, true));
					} else {
						sb.append(log.getDescription());
					}
				} else if (toVal == EActivityStatus.fallback) {
					sb.append(tag($m("MyWorklogsTPage.6"), "#a00"));
					final ActivityBean activity = wfaService.getBean(log.getBeanId());
					if (activity != null) {
						sb.append(wfpService.getBean(activity.getProcessId())).append("<br>");
						sb.append(SPACE);
						sb.append(activity).append(rArr)
								.append(wfaService.getBean(activity.getPreviousId()));
					} else {
						sb.append(log.getDescription());
					}
				}
			}
		} else if (TBL_WORKITEM.equals(tblName)) {
			final WorkitemBean workitem = wfwService.getBean(log.getBeanId());
			if ("topMark".equals(valName)) {
				sb.append(act(Convert.toBool(log.getToVal()) ? $m("MyWorklogsTPage.1")
						: $m("MyWorklogsTPage.2")));
				if (workitem != null) {
					sb.append(wfaService.getBean(workitem.getActivityId())).append(" / ")
							.append(wfpService.getBean(workitem.getProcessId()));
				}
			} else if ("retakeId".equals(valName)) {
				sb.append(tag($m("MyWorklogsTPage.5"), "#c00"));
				if (workitem != null) {
					sb.append(wfpService.getBean(workitem.getProcessId()));
					sb.append(
							toNextActivitiesHTML(pp, wfaService.getBean(workitem.getActivityId()), false));
				}
			}
			if (workitem == null) {
				sb.append(log.getDescription());
			}
		} else if (TBL_DELEGATION.equals(tblName)) {
			if ("status".equals(valName)) {
				final DelegationBean delegation = wfdService.getBean(log.getBeanId());
				if (delegation != null) {
					final EDelegationStatus toVal = Convert.toEnum(EDelegationStatus.class,
							log.getToVal());
					if (toVal == EDelegationStatus.running || toVal == EDelegationStatus.refuse) {
						sb.append(toVal == EDelegationStatus.running ? act("接受委托") : act("拒绝委托"));
						sb.append(wfwService.getBean(delegation.getSourceId())).append(rArr)
								.append(delegation.getUserText());
					} else if (toVal == EDelegationStatus.abort) {
						sb.append(act("取消委托"));
						sb.append(toDelegationHTML(pp, delegation));
					}
				} else {
					sb.append(log.getDescription());
				}
			}
		}
		return sb.toString();
	}

	private String toDelegationHTML(final PageParameter pp, final DelegationBean delegation) {
		final StringBuilder sb = new StringBuilder();
		if (delegation.getDelegationSource() == EDelegationSource.workitem) {
			sb.append("工作项委托").append("<br>").append(SPACE);
			sb.append(wfwService.getBean(delegation.getSourceId()));
		} else {
			sb.append("用户委托").append("<br>").append(SPACE);
			sb.append(pp.getUser(delegation.getSourceId()));
		}
		sb.append(rArr).append(delegation.getUserText());
		return sb.toString();
	}

	private String toNextActivitiesHTML(final PageParameter pp, final ActivityBean activity,
			final boolean rarr) {
		final StringBuilder sb = new StringBuilder("<br>");
		int j = 0;
		for (final ActivityBean nActivity : wfaService.getNextActivities(activity)) {
			if (j++ > 0) {
				sb.append("<br>");
			}
			sb.append(SPACE);
			sb.append(activity.getTasknodeText()).append(rarr ? rArr : lArr)
					.append(nActivity.getTasknodeText()).append(" ( ");
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
		sb.append(tag(title, "#06c"));
		sb.append("<br>").append(SPACE);
		sb.append(log.getFromVal()).append(rArr).append(log.getToVal());
		return sb.toString();
	}

	private SpanElement act(final String txt) {
		return tag(txt, "#333");
	}

	private SpanElement tag(final String txt, final String color) {
		return new SpanElement("[" + txt + "]").setStyle("margin-right: 8px;").setColor(color);
	}

	private static String rArr = " &raquo; ";
	private static String lArr = " &laquo; ";
	private static SpanElement SPACE = SpanElement.SPACE(40);
}
