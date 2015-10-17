package org.webbuilder.web.core.logger;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.util.StatusPrinter;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Created by 浩 on 2015-09-11 0011.
 */
public class LoggerConfig {

    public static void loadConfigure(byte[] config) throws Exception {
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        JoranConfigurator configurator = new JoranConfigurator();
        configurator.setContext(lc);
        lc.reset();
        InputStream inputStream = new ByteArrayInputStream(config);
        configurator.doConfigure(inputStream);
        StatusPrinter.printInCaseOfErrorsOrWarnings(lc);
    }
}
