package org.webbuilder.web.core.logger;

import ch.qos.logback.core.OutputStreamAppender;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Created by 浩 on 2015-08-17 0017.
 */
public class LoggerAppenderAdapter<E> extends OutputStreamAppender<E> {

    private OutputStream outputStream = new ByteArrayOutputStream() {
        @Override
        public synchronized void write(byte b[], int off, int len) {
            appendText(new String(b));
        }
    };


    public void appendText(String str) {
        List<LoggerAppender> appenders = LoggerAppenderStorage.getAllAppender();
        for (LoggerAppender appender : appenders) {
            appender.append(str);
        }
    }

    @Override
    protected void append(E eventObject) {
        super.append(eventObject);

    }

    @Override
    public void start() {
        try {
            if (outputStream == null)
                outputStream = new ByteArrayOutputStream();
            setOutputStream(outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.start();
    }

    @Override
    public void stop() {
        super.stop();
    }

}
