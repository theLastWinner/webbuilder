package org.webbuilder.sql.trigger;

import org.webbuilder.sql.exception.TriggerException;

import java.util.Map;

/**
 * Created by 浩 on 2015-11-14 0014.
 */
public interface Trigger {
    String getName();

    void init() throws TriggerException;

    TriggerResult execute(Map<String, Object> root) throws TriggerException;
}
