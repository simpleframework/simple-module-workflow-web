package net.simpleframework.workflow.web.page.t1;

import static net.simpleframework.common.I18n.$m;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.mxgraph.canvas.mxICanvas;
import com.mxgraph.canvas.mxSvgCanvas;
import com.mxgraph.canvas.mxVmlCanvas;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.util.mxCellRenderer.CanvasFactory;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxDomUtils;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxGraph;

import net.simpleframework.common.StringUtils;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.ctx.common.xml.XmlDocument;
import net.simpleframework.ctx.script.MVEL2Template;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.AbstractElement;
import net.simpleframework.mvc.common.element.SpanElement;
import net.simpleframework.workflow.engine.IWorkflowContextAware;
import net.simpleframework.workflow.engine.bean.ActivityBean;
import net.simpleframework.workflow.engine.bean.ProcessBean;
import net.simpleframework.workflow.engine.bean.ProcessModelBean;
import net.simpleframework.workflow.graph.GraphUtils;
import net.simpleframework.workflow.web.WorkflowUtils;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public abstract class WorkflowGraphUtils implements IWorkflowContextAware {

	public static String toGraphHTML(final PageParameter pp, final ProcessBean process,
			final String trHTML) {
		return toGraphHTML(pp, process,
				new KVMap().add("tbl", "WorkflowGraphMonitorPage_tbl").add("trHTML", trHTML));
	}

	public static String toTrHTML(final PageParameter pp) {
		final StringBuilder sb = new StringBuilder();
		sb.append($m("WorkflowGraphMonitorPage.0"))
				.append(SpanElement.color($m("WorkflowGraphMonitorPage.1"), "#c00")).append(", ")
				.append(SpanElement.color($m("WorkflowGraphMonitorPage.2"), "green")).append(", ")
				.append(SpanElement.color($m("WorkflowGraphMonitorPage.3"), "#777"));
		return sb.toString();
	}

	public static String toGraphHTML(final PageParameter pp, final ProcessBean process,
			final Map<String, Object> variables) {
		if (process == null) {
			return "";
		}

		final mxGraph graph = GraphUtils.createGraph(wfpService.getProcessDocument(process));
		final List<ActivityBean> list = wfaService.getActivities(process);
		final Map<String, Boolean> state = new HashMap<>();
		for (final ActivityBean activity : list) {
			final String tasknodeId = activity.getTasknodeId();
			if (state.get(tasknodeId) == null) {
				state.put(tasknodeId, true);
			}
			if (!wfaService.isFinalStatus(activity)) {
				state.put(tasknodeId, false);
			}
		}

		Object gObj;
		if (isVML(pp)) {
			final mxVmlCanvas canvas = (mxVmlCanvas) mxCellRenderer.drawCells(graph, null, 1, null,
					new CanvasFactory() {
						@Override
						public mxICanvas createCanvas(final int width, final int height) {
							final Document document = mxDomUtils.createDocument();
							final Element root = document.createElement("div");
							root.setAttribute("style",
									"position: relative; width:" + width + "px;height:" + height + "px");
							document.appendChild(root);
							return new mxVmlCanvas(document) {
								@Override
								public void appendVmlElement(final Element node) {
									document.getDocumentElement().appendChild(node);
								}

								@Override
								public Element drawShape(final int x, final int y, final int w, final int h,
										final Map<String, Object> style) {
									final Element ele = super.drawShape(x, y, w, h, style);
									_setShape(ele, style);
									return ele;
								}

								@Override
								public Element drawText(final String text, final int x, final int y,
										final int w, final int h, final Map<String, Object> style) {
									final Element ele = super.drawText(text, x, y, w, h, style);
									_setText(ele, style, state, false);
									return ele;
								}

								@Override
								public String getImageForStyle(final Map<String, Object> style) {
									return _getImageForStyle(pp, style);
								}
							};
						}
					});
			gObj = StringUtils.replace(new XmlDocument(canvas.getDocument()).toString(), "v:img",
					"v:image");
		} else {
			final mxSvgCanvas canvas = (mxSvgCanvas) mxCellRenderer.drawCells(graph, null, 1, null,
					new CanvasFactory() {
						@Override
						public mxICanvas createCanvas(final int width, final int height) {
							return new mxSvgCanvas(mxDomUtils.createSvgDocument(width, height)) {
								@Override
								public Element drawShape(final int x, final int y, final int w, final int h,
										final Map<String, Object> style) {
									final Element ele = super.drawShape(x, y, w, h, style);
									_setShape(ele, style);
									return ele;
								}

								@Override
								public Object drawText(final String text, final int x, final int y,
										final int w, final int h, final Map<String, Object> style) {
									final Element ele = (Element) super.drawText(text, x, y, w, h, style);
									_setText(ele, style, state, true);
									return ele;
								}

								@Override
								public String getImageForStyle(final Map<String, Object> style) {
									return _getImageForStyle(pp, style);
								}
							};
						}
					});
			gObj = new XmlDocument(canvas.getDocument());
		}

		final KVMap _variables = new KVMap();
		if (variables != null) {
			_variables.putAll(variables);
		}

		final ProcessModelBean pModel = wfpService.getProcessModel(process);
		return MVEL2Template.replace(
				_variables.add("graph", gObj).add("pm",
						WorkflowUtils.getShortMtext(pModel) + " (" + pModel.getModelVer() + ")"),
				WorkflowGraphUtils.class, "WorkflowGraphMonitorPage_svg.html");
	}

	public static boolean isVML(final PageParameter pp) {
		final Boolean ie = pp.isIE8("<=");
		return ie != null && ie;
	}

	private static String _getImageForStyle(final PageParameter pp,
			final Map<String, Object> style) {
		final String filename = mxUtils.getString(style, mxConstants.STYLE_IMAGE);
		final StringBuilder sb = new StringBuilder();
		sb.append(pp.getResourceHomePath(GraphUtils.class)).append("/images");
		sb.append(filename.substring(filename.lastIndexOf("/")));
		return sb.toString();
	}

	private static void _setShape(final Element ele, final Map<String, Object> style) {
		if (ele == null) {
			return;
		}
		ele.setAttribute("class", "tasknode");
		final String taskId = (String) style.get("taskid");
		if (StringUtils.hasText(taskId)) {
			ele.setAttribute("taskid", taskId);
		}
	}

	private static void _setText(Element ele, final Map<String, Object> style,
			final Map<String, Boolean> state, final boolean svg) {
		if (ele == null) {
			return;
		}
		ele.setAttribute("class", "tasktext");
		final String taskId = (String) style.get("taskid");
		if (StringUtils.hasText(taskId)) {
			ele.setAttribute("taskid", taskId);
		}
		Boolean sfinal;
		if (svg) {
			if (StringUtils.hasText(taskId) && (sfinal = state.get(taskId)) != null) {
				if (sfinal) {
					ele.setAttribute("fill", "#c00");
				} else {
					ele.setAttribute("fill", "green");
					ele.setAttribute("font-weight", "bold");
				}
			} else {
				ele.setAttribute("fill", "#777");
			}
		} else {
			// table
			ele = (Element) ele.getFirstChild();
			if (ele != null) {
				ele = (Element) ele.getFirstChild();
				if (ele != null) {
					final Map<String, String> styles = AbstractElement
							.toStyle(ele.getAttribute("style"));
					if (StringUtils.hasText(taskId) && (sfinal = state.get(taskId)) != null) {
						if (sfinal) {
							styles.put("color", "#c00");
						} else {
							styles.put("color", "green");
							styles.put("font-weight", "bold");
						}
					} else {
						styles.put("color", "#777");
					}
					ele.setAttribute("style", AbstractElement.joinStyle(styles));
				}
			}
		}
	}
}
