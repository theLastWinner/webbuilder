package org.webbuilder.web.core.logger;

import org.webbuilder.web.po.logger.LogInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Created by 浩 on 2015-09-11 0011.
 */
public class LogBackLoggerService implements LoggerService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void log(LogInfo logInfo) throws Exception {
        if (logger.isInfoEnabled()) {
            logger.info(logInfo.toString());
        }
    }

    @Override
    public List<LogInfo> search(Map<String, Object> conditions) throws Exception {
        return null;
    }

    @Override
    public int total(Map<String, Object> conditions) throws Exception {
        return 0;
    }
}
