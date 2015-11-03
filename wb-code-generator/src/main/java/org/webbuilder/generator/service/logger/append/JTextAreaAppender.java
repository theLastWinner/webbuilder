package org.webbuilder.generator.service.logger.append;

import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.OutputStreamAppender;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.*;


/**
 * Created by 浩 on 2015-07-29 0029.
 */
public class JTextAreaAppender<E> extends OutputStreamAppender<E> {

    private static JTextArea area = null;

    private OutputStream outputStream = new ByteArrayOutputStream(){
        @Override
        public synchronized void write(byte b[],int off,int len) {
            appendText(new String(b));
        }
    };

    public static void registerArea(JTextArea area){
        JTextAreaAppender.area=area;
    }

    public void appendText(String str){
        if(area!=null) {
            area.append(str);
            int length = area.getText().length();
            area.setCaretPosition(length);
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
