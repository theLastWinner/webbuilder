package org.webbuilder.web.core.logger;

import org.webbuilder.web.po.logger.LogInfo;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by 浩 on 2015-09-11 0011.
 */
public interface LoggerService extends Serializable {

    void log(LogInfo logInfo) throws Exception;

    List<LogInfo> search(Map<String, Object> conditions) throws Exception;

    int total(Map<String, Object> conditions) throws Exception;

}
