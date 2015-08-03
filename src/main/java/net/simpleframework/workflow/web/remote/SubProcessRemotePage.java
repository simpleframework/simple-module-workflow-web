package net.simpleframework.workflow.web.remote;

import java.util.Properties;

import net.simpleframework.common.StringUtils;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.mvc.IForward;
import net.simpleframework.mvc.IForwardCallback.IJsonForwardCallback;
import net.simpleframework.mvc.JsonForward;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.workflow.engine.IMappingVal;
import net.simpleframework.workflow.engine.bean.ActivityBean;
import net.simpleframework.workflow.engine.bean.ProcessBean;
import net.simpleframework.workflow.engine.remote.IProcessRemote;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class SubProcessRemotePage extends AbstractWorkflowRemotePage {

	public IForward startProcess(final PageParameter pp) {

		return doJsonForward(new IJsonForwardCallback() {
			@Override
			public void doAction(final JsonForward json) {
				final Properties properties = new Properties();
				copyTo(pp, properties, IProcessRemote.SERVERURL, IProcessRemote.SUB_ACTIVITYID,
						IProcessRemote.VAR_MAPPINGS);

				// 子流程变量
				final KVMap variables = new KVMap();
				for (final String mapping : StringUtils.split(properties
						.getProperty(IProcessRemote.VAR_MAPPINGS))) {
					variables.add(mapping, pp.getLocaleParameter(mapping));
				}

				final ProcessBean process = wfpService.doStartProcess(
						wfpmService.getProcessModel(pp.getLocaleParameter(IProcessRemote.MODEL)),
						variables, properties, null);
				json.put(IProcessRemote.SUB_PROCESSID, process.getId());
			}
		});
	}

	public IForward checkProcess(final PageParameter pp) {
		return doJsonForward(new IJsonForwardCallback() {
			@Override
			public void doAction(final JsonForward json) {
				final ProcessBean sProcess = wfpService.getBean(pp
						.getLocaleParameter(IProcessRemote.SUB_PROCESSID));
				if (sProcess != null && wfpService.isFinalStatus(sProcess)) {
					wfpService.doBackToRemote(sProcess);
				}
				json.put("success", Boolean.TRUE);
			}
		});
	}

	public IForward subComplete(final PageParameter pp) {
		return doJsonForward(new IJsonForwardCallback() {
			@Override
			public void doAction(final JsonForward json) {
				final ActivityBean nActivity = wfaService.getBean(pp
						.getLocaleParameter(IProcessRemote.SUB_ACTIVITYID));
				wfaService.doSubComplete(nActivity, new IMappingVal() {
					@Override
					public Object val(final String mapping) {
						return pp.getLocaleParameter(mapping);
					}
				});
				json.put("success", Boolean.TRUE);
			}
		});
	}
}