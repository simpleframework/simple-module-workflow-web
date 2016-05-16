package net.simpleframework.workflow.web.page.list.stat;

import static net.simpleframework.common.I18n.$m;

import java.util.Calendar;
import java.util.Date;

import net.simpleframework.common.BeanUtils;
import net.simpleframework.common.Convert;
import net.simpleframework.common.NumberUtils;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.ElementList;
import net.simpleframework.mvc.common.element.SpanElement;
import net.simpleframework.mvc.common.element.TabButton;
import net.simpleframework.mvc.common.element.TabButtons;
import net.simpleframework.mvc.component.ext.highchart.DataObj;
import net.simpleframework.mvc.component.ext.highchart.HcChart;
import net.simpleframework.mvc.component.ext.highchart.HcLegend;
import net.simpleframework.mvc.component.ext.highchart.HcSeries;
import net.simpleframework.mvc.component.ext.highchart.HcTooltip;
import net.simpleframework.mvc.component.ext.highchart.HcXAxis;
import net.simpleframework.mvc.component.ext.highchart.HcYAxis;
import net.simpleframework.mvc.component.ext.highchart.HighchartBean;
import net.simpleframework.workflow.engine.EWorkitemStatus;
import net.simpleframework.workflow.engine.bean.UserStatBean;
import net.simpleframework.workflow.web.page.list.AbstractItemsTPage;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class MyWorkstatTPage extends AbstractItemsTPage {

	@Override
	protected void onForward(final PageParameter pp) throws Exception {
		super.onForward(pp);

		addStatChart(pp);
	}

	public static void addStatChart(final PageParameter pp) {
		final HighchartBean hc1 = (HighchartBean) pp
				.addComponentBean("MyWorkstatTPage_chart", HighchartBean.class)
				.setChart(
						new HcChart().setHeight(300).setBackgroundColor("#FFF").setMarginTop(20)
								.setMarginRight(30)).setTitle("")
				.setLegend(new HcLegend().setEnabled(false)).setContainerId("idMyWorkstatTPage_chart");
		hc1.setxAxis(new HcXAxis());
		hc1.setyAxis(new HcYAxis().setTitle($m("MyWorkstatTPage.0")).setMin(0).setMax(100));
		final StringBuilder sb = new StringBuilder();
		sb.append($m("MyWorkstatTPage.2")).append("{point.complete}<br>")
				.append($m("MyWorkstatTPage.3")).append("{point.all}<br>")
				.append($m("MyWorkstatTPage.4")).append("<span class='num'>{point.y}%</span>");
		hc1.setTooltip(new HcTooltip().setUseHTML(true)
				.setHeaderFormat($m("MyWorkstatTPage.1") + "{point.key}<br>")
				.setPointFormat(sb.toString()));

		hc1.getxAxis().setCategories(
				new String[] { null, null, null, null, null, $m("MyWorkstatTPage.6"),
						$m("MyWorkstatTPage.5") });

		final Calendar cal = Calendar.getInstance();
		final HcSeries h = new HcSeries();
		for (int i = 0; i < 7; i++) {
			final Date nDate = cal.getTime();
			final String lbl = Convert.toDateString(nDate, "MM-dd");
			final int[] arr = wfusService.getComplete_AllWorkitems(pp.getLoginId(), nDate);
			final float p = arr[1] == 0 ? 100 : ((float) arr[0] / arr[1]) * 100;
			final DataObj data = new DataObj(lbl, NumberUtils.toFloat(p)).addAttribute("complete",
					arr[0]).addAttribute("all", arr[1]);
			// if (arr[1] == 0) {
			// data.setColor("#f00");
			// }
			h.getData().add(0, data.toVal());
			cal.add(Calendar.DATE, -1);
		}

		hc1.addSeries(h);
	}

	static SpanElement getStatTabs(final PageParameter pp) {
		return createTabsElement(pp, TabButtons.of(
				new TabButton($m("MyWorkstatTPage.7"), uFactory.getUrl(pp, MyWorkstatTPage.class)),
				new TabButton($m("MyWorkstatTPage.11"), uFactory.getUrl(pp, MyWorklogsTPage.class))));
	}

	@Override
	public ElementList getRightElements(final PageParameter pp) {
		return ElementList.of(getStatTabs(pp));
	}

	protected String toStat1HTML(final PageParameter pp) {
		final UserStatBean userStat = wfusService.getUserStat(pp.getLoginId());
		final StringBuilder sb = new StringBuilder();
		sb.append("<div class='stat1 clearfix'>");
		sb.append(" <div class='topic'>#(MyWorkstatTPage.8)</div>");
		sb.append(" <div class='col'>");
		sb.append("  <div class='num'>").append(wfusService.getAllWorkitems(userStat))
				.append("</div>");
		sb.append("  <div class='lbl'>#(MyWorkstatTPage.9)</div>");
		sb.append(" </div>");
		for (final EWorkitemStatus s : EWorkitemStatus.values()) {
			sb.append(" <div class='col'>");
			sb.append("  <div class='num'>")
					.append(BeanUtils.getProperty(userStat, "workitem_" + s.name())).append("</div>");
			sb.append("  <div class='lbl'>").append(s).append("</div>");
			sb.append(" </div>");
		}
		sb.append("</div>");
		return sb.toString();
	}

	protected String toStat2HTML(final PageParameter pp) {
		final StringBuilder sb = new StringBuilder();
		sb.append("<div class='stat2 clearfix'>");
		sb.append(" <div class='topic'>#(MyWorkstatTPage.10)</div>");
		sb.append(" <div id='idMyWorkstatTPage_chart'></div>");
		sb.append("</div>");
		return sb.toString();
	}

	@Override
	protected String toListHTML(final PageParameter pp) {
		final StringBuilder sb = new StringBuilder();
		sb.append("<div class='MyWorkstatTPage'>");
		sb.append(toStat1HTML(pp));
		sb.append(toStat2HTML(pp));
		sb.append("</div>");
		return sb.toString();
	}
}
