package net.simpleframework.workflow.web.page.list.process;

import java.io.IOException;
import java.util.Map;

import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.ElementList;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ui.pager.AbstractTablePagerSchema;
import net.simpleframework.mvc.component.ui.pager.TablePagerBean;
import net.simpleframework.mvc.component.ui.pager.TablePagerColumns;
import net.simpleframework.mvc.template.struct.FilterButtons;
import net.simpleframework.workflow.engine.IWorkflowContextAware;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public interface IProcessWorksHandler extends IWorkflowContextAware {

	/**
	 * 获取要操作的流程模型
	 * 
	 * @return
	 */
	String[] getModelNames();

	/**
	 * 表格初始化
	 * 
	 * @param pp
	 * @param tablePager
	 * @param qw
	 */
	void doTablePagerInit(PageParameter pp, TablePagerBean tablePager, EProcessWorks qw);

	/**
	 * 表格数据源
	 * 
	 * @param cp
	 * @param qw
	 * @return
	 */
	IDataQuery<?> createDataObjectQuery(ComponentParameter cp, EProcessWorks qw);

	/**
	 * 获取行数据
	 * 
	 * @param cp
	 * @param dataObject
	 * @param qw
	 * @return
	 */
	Map<String, Object> getRowData(ComponentParameter cp, Object dataObject, EProcessWorks qw);

	/**
	 * 获取左侧元素列表
	 * 
	 * @param pp
	 * @param qw
	 * @return
	 */
	ElementList getLeftElements(PageParameter pp, EProcessWorks qw);

	FilterButtons getFilterButtons(PageParameter pp, EProcessWorks qw);

	/**
	 * 导出excel文件
	 * 
	 * @param cp
	 * @param dQuery
	 * @param tablePagerData
	 * @param columns
	 * @throws IOException
	 */
	void doExcelExport(ComponentParameter cp, IDataQuery<?> dQuery,
			AbstractTablePagerSchema tablePagerData, TablePagerColumns columns) throws IOException;

	public static enum EProcessWorks {
		my,

		dept,

		org,

		role
	}
}
