package org.webbuilder.bpm.pojo.bpm;

import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Task;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * Created by 浩 on 2015-08-14 0014.
 */
public class TaskInfo implements Serializable {

    public static final String TYPE_TODO = "todo";

    public static final String TYPE_CLAIM = "claim";
    private String id;

    private String name;

    private Date createDate;

    private ProcessDefinition definition;

    private String formId;

    private String dataId;

    private Object mainFormData;

    private String pid;

    private String processInstanceId;

    private String type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public ProcessDefinition getDefinition() {
        return definition;
    }

    public void setDefinition(ProcessDefinition definition) {
        this.definition = definition;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public TaskInfo initFromProtoType(Task task) {
        this.setId(task.getId());
        this.setName(task.getName());
        this.setCreateDate(task.getCreateTime());
        this.setProcessInstanceId(task.getProcessInstanceId());
        this.setPid(task.getParentTaskId());
        Map<String, Object> var = task.getProcessVariables();
        this.setFormId((String) var.get("mainFormId"));
        this.setDataId((String) var.get("mainFormDataId"));
        return this;
    }


    public static TaskInfo buildFromProtoType(Task task) {
        return new TaskInfo().initFromProtoType(task);
    }

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public Object getMainFormData() {
        return mainFormData;
    }

    public void setMainFormData(Object mainFormData) {
        this.mainFormData = mainFormData;
    }
}
