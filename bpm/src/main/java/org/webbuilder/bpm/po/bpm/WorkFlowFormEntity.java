package org.webbuilder.bpm.po.bpm;

import java.util.LinkedHashMap;

/**
 * 工作流 流程表单实体
 * Created by 浩 on 2015-09-16 0016.
 */
public class WorkFlowFormEntity extends LinkedHashMap<String, Object> {

    private String u_id;

    //流程实例id
    private String flow_instance_id;

    //流程启动者
    private String creator_id;

    //当前流程环节
    private String now_step_id;

    //当前流程环节
    private String now_step_name;

    public WorkFlowFormEntity init() {
        put("flow_instance_id", getFlow_instance_id());
        put("creator_id", getCreator_id());
        put("now_step_id", getNow_step_id());
        put("now_step_name", getNow_step_name());
        put("u_id", getU_id());
        return this;
    }

    @Override
    public Object put(String key, Object value) {
        if ("u_id".equals(key)) {
            setU_id(String.valueOf(value));
        }
        return super.put(key, value);
    }

    public String getU_id() {
        return u_id;
    }

    public void setU_id(String u_id) {
        this.u_id = u_id;
    }

    public String getFlow_instance_id() {
        return flow_instance_id;
    }

    public void setFlow_instance_id(String flow_instance_id) {
        this.flow_instance_id = flow_instance_id;
    }

    public String getCreator_id() {
        return creator_id;
    }

    public void setCreator_id(String creator_id) {
        this.creator_id = creator_id;
    }

    public String getNow_step_id() {
        return now_step_id;
    }

    public void setNow_step_id(String now_step_id) {
        this.now_step_id = now_step_id;
    }

    public String getNow_step_name() {
        return now_step_name;
    }

    public void setNow_step_name(String now_step_name) {
        this.now_step_name = now_step_name;
    }
}
