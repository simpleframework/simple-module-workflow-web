package net.simpleframework.workflow.web.component.fallback;

import java.util.Collection;

import net.simpleframework.mvc.JavascriptForward;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.IComponentHandler;
import net.simpleframework.workflow.schema.UserNode;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public interface IActivityFallbackHandler extends IComponentHandler {

	Collection<UserNode> getUserNodes(ComponentParameter cp);

	JavascriptForward doFallback(ComponentParameter cp, String usernode, boolean isNextActivity);
}
