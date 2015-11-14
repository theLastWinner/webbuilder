package org.webbuilder.sql.support.common;

import org.webbuilder.sql.TableMetaData;
import org.webbuilder.sql.exception.TriggerException;
import org.webbuilder.sql.trigger.TriggerResult;

import java.util.Map;

/**
 * Created by 浩 on 2015-11-14 0014.
 */
public abstract class TriggerExecutor {

    public abstract TableMetaData getTableMetaData();


    public Object tryExecuteTrigger(String triggerName, Map<String, Object> root) throws Exception {
        return tryExecuteTrigger(triggerName, root, false);
    }

    public Object tryExecuteTrigger(String triggerName, Map<String, Object> root, boolean skipError) throws Exception {
        try {
            if (getTableMetaData().triggerSupport(triggerName)) {
                TriggerResult res = getTableMetaData().on(triggerName, root);
                if (!res.isSuccess()) {
                    throw new TriggerException(res.getMessage());
                }
                return res.getData();
            }
        } catch (Exception e) {
            if (!skipError) throw e;
        }
        return null;
    }
}
