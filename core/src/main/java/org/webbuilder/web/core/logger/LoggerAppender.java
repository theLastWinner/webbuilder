package org.webbuilder.web.core.logger;

import java.io.Serializable;

/**
 * Created by 浩 on 2015-08-17 0017.
 */
public interface LoggerAppender extends Serializable {
    String getName();

    void append(String log);
}
