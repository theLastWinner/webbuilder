package org.webbuilder.web.core.websocket.cmd;

import com.alibaba.fastjson.JSON;
import org.webbuilder.utils.base.DateTimeUtils;

/**
 * Created by 浩 on 2015-09-08 0008.
 */
public class CmdResponse {
    private String cmd;

    private boolean success;

    private Object data;

    public CmdResponse() {
    }

    public CmdResponse(boolean success, Object data) {
        this("unknow", success, data);
    }

    public CmdResponse(String cmd, boolean success, Object data) {
        this.cmd = cmd;
        this.success = success;
        this.data = data;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return JSON.toJSONStringWithDateFormat(this, DateTimeUtils.YEAR_MONTH_DAY_HOUR_MINUTE_SECOND);
    }
}
