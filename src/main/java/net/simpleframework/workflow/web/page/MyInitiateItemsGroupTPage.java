package net.simpleframework.workflow.web.page;

import static net.simpleframework.common.I18n.$m;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.simpleframework.common.StringUtils;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.ElementList;
import net.simpleframework.mvc.common.element.SpanElement;
import net.simpleframework.workflow.engine.InitiateItem;
import net.simpleframework.workflow.engine.InitiateItems;
import net.simpleframework.workflow.engine.bean.ProcessModelBean;
import net.simpleframework.workflow.schema.ProcessNode;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class MyInitiateItemsGroupTPage extends MyInitiateItemsTPage {

	@Override
	protected void onForward(final PageParameter pp) throws Exception {
		super.onForward(pp);
		// 发起流程
		addStartProcess(pp);
	}

	@Override
	public ElementList getRightElements(final PageParameter pp) {
		return ElementList.of(MyInitiateItemsTPage.getTabs(pp));
	}

	@Override
	protected String toListHTML(final PageParameter pp) {
		final StringBuilder sb = new StringBuilder();
		final InitiateItems items = wfpmService.getInitiateItems(pp.getLoginId());
		Collections.sort(items, new Comparator<InitiateItem>() {
			@Override
			public int compare(final InitiateItem item1, final InitiateItem item2) {
				final ProcessModelBean pm1 = wfpmService.getBean(item1.getModelId());
				final ProcessModelBean pm2 = wfpmService.getBean(item2.getModelId());
				if (pm1 != null && pm2 != null) {
					final ProcessNode pn1 = wfpmService.getProcessDocument(pm1).getProcessNode();
					final ProcessNode pn2 = wfpmService.getProcessDocument(pm2).getProcessNode();
					return pn1.getOorder() > pn2.getOorder() ? 1 : -1;
				}
				return 0;
			}
		});
		final Map<String, List<InitiateItem>> gmap = new LinkedHashMap<String, List<InitiateItem>>();
		for (final InitiateItem item : items) {
			final ProcessModelBean pm = wfpmService.getBean(item.getModelId());
			final String[] arr = StringUtils.split(pm.getModelText(), ".");
			String key;
			if (arr.length > 1) {
				key = arr[0];
			} else {
				key = $m("MyInitiateItemsGroupTPage.0");
			}
			List<InitiateItem> list = gmap.get(key);
			if (list == null) {
				gmap.put(key, list = new ArrayList<InitiateItem>());
			}
			list.add(item);
		}

		final String[] COLORS = new String[] { "#166CA5", "#953735", "#01B0F1", "#767719", "#F99D52" };
		int i = 0;
		sb.append("<div class='MyInitiateItemsGroupTPage clearfix'>");
		for (final Map.Entry<String, List<InitiateItem>> e : gmap.entrySet()) {
			final String key = e.getKey();
			final List<InitiateItem> val = e.getValue();
			sb.append("<div class='lblock'>");
			sb.append(" <div class='lt' style='border-bottom-color:")
					.append(COLORS[Math.min(i++, COLORS.length - 1)]).append("'>");
			sb.append("  <span class='lbl'>").append(key).append("</span>");
			sb.append("  <span class='size'>").append("(").append(val.size()).append(")")
					.append("</span>");
			sb.append("</div>");
			sb.append(" <div class='lc'>");
			for (final InitiateItem item : val) {
				final Object modelId = item.getModelId();
				final ProcessModelBean processModel = wfpmService.getBean(modelId);
				final String mtxt = processModel.getModelText();
				final int p = mtxt.indexOf('.');
				sb.append("<div class='litem'>");
				sb.append(new SpanElement(p > 0 ? mtxt.substring(p + 1) : mtxt)
						.setOnclick("$Actions['MyInitiateItemsTPage_startProcess']('modelId=" + modelId
								+ "');"));
				sb.append("</div>");
			}
			sb.append(" </div>");
			sb.append("</div>");
		}
		sb.append("</div>");
		return sb.toString();
	}
}
