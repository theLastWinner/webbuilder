package org.webbuilder.web.core.websocket.message;

import com.alibaba.fastjson.JSON;
import org.webbuilder.utils.base.MD5;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by 浩 on 2015-09-08 0008.
 */
public class Message implements Serializable, Comparable<Message> {
    private String u_id;

    private String cmd;

    private String from;

    private String to;

    private String content;

    private Date send_date;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getSend_date() {
        return send_date;
    }

    public void setSend_date(Date send_date) {
        this.send_date = send_date;
    }

    @Override
    public String toString() {
        return JSON.toJSONStringWithDateFormat(this, "yyyy-MM-dd HH:mm:ss");
    }

    public String getU_id() {
        if (u_id == null)
            u_id = MD5.encode(String.valueOf(System.nanoTime()).concat(String.valueOf(Math.random())));
        return u_id;
    }

    public void setU_id(String u_id) {
        this.u_id = u_id;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    @Override
    public int compareTo(Message o) {
        if (getSend_date().getTime() > o.getSend_date().getTime()) return 1;
        return 0;
    }
}
