package net.simpleframework.workflow.web.page.t1;

import java.util.Map;

import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.ado.query.ListDataQuery;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.ctx.common.xml.XmlDocument;
import net.simpleframework.ctx.script.MVEL2Template;
import net.simpleframework.mvc.PageMapping;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ui.pager.TablePagerBean;
import net.simpleframework.workflow.engine.ActivityBean;
import net.simpleframework.workflow.engine.ProcessBean;
import net.simpleframework.workflow.engine.WorkitemBean;
import net.simpleframework.workflow.graph.GraphUtils;
import net.simpleframework.workflow.web.page.WorkflowUtils;

import org.w3c.dom.Element;

import com.mxgraph.canvas.mxICanvas;
import com.mxgraph.canvas.mxSvgCanvas;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.util.mxCellRenderer.CanvasFactory;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxDomUtils;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxGraph;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
@PageMapping(url = "/workflow/monitor/graph")
public class WorkflowGraphMonitorPage extends WorkflowMonitorPage {

	@Override
	protected TablePagerBean addTablePagerBean(final PageParameter pp) {
		return (TablePagerBean) super.addTablePagerBean(pp).setHandleClass(_ActivityTbl2.class);
	}

	@Override
	protected String toTabHTML(final PageParameter pp) {
		final StringBuilder sb = new StringBuilder();
		final WorkitemBean workitem = WorkflowUtils.getWorkitemBean(pp);
		final mxGraph graph = GraphUtils.createGraph(pService.getProcessDocument(wService
				.getProcessBean(workitem)));

		final mxSvgCanvas canvas = (mxSvgCanvas) mxCellRenderer.drawCells(graph, null, 1, null,
				new CanvasFactory() {
					@Override
					public mxICanvas createCanvas(final int width, final int height) {
						return new mxSvgCanvas(mxDomUtils.createSvgDocument(width, height)) {
							@Override
							public Element drawShape(final int x, final int y, final int w, final int h,
									final Map<String, Object> style) {
								final Element ele = super.drawShape(x, y, w, h, style);
								final String taskId = (String) style.get("taskid");
								if (StringUtils.hasText(taskId)) {
									ele.setAttribute("taskid", taskId);
								}
								return ele;
							}

							@Override
							public String getImageForStyle(final Map<String, Object> style) {
								final String filename = mxUtils.getString(style, mxConstants.STYLE_IMAGE);
								final StringBuilder sb = new StringBuilder();
								sb.append(pp.getResourceHomePath(GraphUtils.class)).append("/images");
								sb.append(filename.substring(filename.lastIndexOf("/")));
								return sb.toString();
							}
						};
					}
				});
		sb.append(MVEL2Template.replace(
				new KVMap().add("svg", new XmlDocument(canvas.getDocument())),
				WorkflowGraphMonitorPage.class, "WorkflowGraphMonitorPage_svg.html"));
		return sb.toString();
	}

	public static class _ActivityTbl2 extends _ActivityTbl {

		@Override
		public IDataQuery<?> createDataObjectQuery(final ComponentParameter cp) {
			final ProcessBean process = pService.getBean(cp.getParameter("processId"));
			if (process != null) {
				cp.addFormParameter("processId", process.getId());
			}
			final String taskid = cp.getParameter("taskid");
			cp.addFormParameter("taskid", taskid);
			return StringUtils.hasText(taskid) ? new ListDataQuery<ActivityBean>(
					aService.getActivities(process, taskid)) : null;
		}
	}
}
