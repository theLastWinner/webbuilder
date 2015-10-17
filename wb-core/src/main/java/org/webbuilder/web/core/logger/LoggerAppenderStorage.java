package org.webbuilder.web.core.logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by 浩 on 2015-08-17 0017.
 */
public class LoggerAppenderStorage {
    private static final Map<String, LoggerAppender> STORAGE = new ConcurrentHashMap<>();

    public static List<LoggerAppender> getAllAppender() {
        return new ArrayList<>(STORAGE.values());
    }

    public static void registAppender(LoggerAppender appender) {
        STORAGE.put(appender.getName(), appender);
    }

    public static void cancelAppender(String name) {
        STORAGE.remove(name);
    }
}
