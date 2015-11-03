package org.webbuilder.utils.script.engine.common.listener;

import org.webbuilder.utils.script.engine.listener.ExecuteEvent;
import org.webbuilder.utils.script.engine.listener.ScriptExecuteListener;

/**
 * Created by 浩 on 2015-10-27 0027.
 */
public interface CommonScriptExecuteListener extends ScriptExecuteListener {
    void onExecute(ExecuteEvent event);
}
