package org.webbuilder.bpm.service.bpm;

import com.alibaba.fastjson.JSON;
import org.webbuilder.bpm.pojo.bpm.TaskInfo;
import org.webbuilder.utils.base.StringUtil;
import org.webbuilder.web.core.exception.BusinessException;
import org.webbuilder.web.po.form.Form;
import org.webbuilder.web.service.form.CustomFormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * 工作流任务服务类
 * Created by 浩 on 2015-08-14 0014.
 */
@Service
public class BpmTaskService {

    @Resource
    protected TaskService taskService;

    @Resource
    protected HistoryService historyService;

    @Resource
    protected RepositoryService repositoryService;

    @Resource
    private BpmProcessService bpmProcessService;

    @Autowired
    private CustomFormService customFormService;

    @Resource
    private RuntimeService runtimeService;

    public List<Task> selectNowTask(String processInstanceId) {
        return taskService.createTaskQuery().processInstanceId(processInstanceId).list();
    }

    public String selectNowTaskName(String processInstanceId) {
        List<Task> tasks = selectNowTask(processInstanceId);
        if (tasks.size() == 1)
            return tasks.get(0).getName();
        else {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < tasks.size(); i++) {
                if (i != 0)
                    builder.append(",");
                builder.append(tasks.get(i).getName());
            }
            return builder.toString();
        }

    }

    public String selectNowTaskId(String processInstanceId) {
        List<Task> tasks = selectNowTask(processInstanceId);
        if (tasks.size() == 1)
            return tasks.get(0).getId();
        else {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < tasks.size(); i++) {
                if (i != 0)
                    builder.append(",");
                builder.append(tasks.get(i).getId());
            }
            return builder.toString();
        }
    }

    /**
     * 完成任务（环节）
     *
     * @param taskId    任务id
     * @param userId    用户id
     * @param variables 完成参数
     * @param formData  需要保持的表单数据，如 {form1:{id:"",name:""},form2:[{id:"",name:"}]}
     * @throws Exception 异常信息
     */
    @Transactional(rollbackFor = Throwable.class)
    public void complete(String taskId, String userId, Map<String, Object> variables, Map<String, Object> formData) throws Exception {
        Task task = taskService.createTaskQuery().taskId(taskId).includeProcessVariables().singleResult();
        if (task == null)
            throw new BusinessException("任务不存在！");
        String processInstanceId = task.getProcessInstanceId();
        task.getTaskDefinitionKey();
        String assignee = task.getAssignee();
        if (null == assignee)
            throw new BusinessException("请先签收任务!");
        if (!userId.equals(assignee)) {
            throw new BusinessException("只能完成自己的任务!");
        }
        //保存表单数据
        List<Map<String, Object>> formDataInfo = new LinkedList<>();
        String formKey = task.getFormKey();
        if (!StringUtil.isNullOrEmpty(formKey)) {
            String[] shouldSaveForm = formKey.split("[,]");
            //列出需要保持数据的自定义表单
            for (String formId : shouldSaveForm) {
                Form form = customFormService.getForm(formId);
                Object data = formData.get(formId);
                if (form == null) {
                    throw new BusinessException(String.format("不存在表单:%s", formId));
                }
                if (data == null) {
                    throw new BusinessException(String.format("请填写表单(%s)数据", form.getName()));
                }
                Map<String, Object> dataInfo = new LinkedHashMap<>();
                formDataInfo.add(dataInfo);
                if (data instanceof Map) {//单条数据
                    Map<String, Object> tmp = (Map) data;
                    tmp.put("flow_instance_id", processInstanceId);
                    tmp.put("creator_id", userId);
                    tmp.put("task_id", task.getId());
                    String id = customFormService.insert(formId, tmp);
                    dataInfo.put("formId", formId);
                    dataInfo.put("dataId", id);
                } else if (data instanceof Collection) { //多条数据
                    Collection<Map> tmps = (Collection) data;
                    List<String> dataIds = new LinkedList<>();
                    dataInfo.put("formId", formId);
                    dataInfo.put("dataId", dataIds);
                    for (Map tmp : tmps) {
                        tmp.put("flow_instance_id", processInstanceId);
                        tmp.put("task_id", task.getId());
                        tmp.put("creator_id", userId);
                        String id = customFormService.insert(formId, tmp);
                        dataIds.add(id);
                    }
                } else {
                    throw new BusinessException(String.format("表单(%s)数据格式错误!", formId));
                }
            }
        }
        //保存自定义表单关联的数据
        taskService.setVariableLocal(taskId, "formDataInfo", JSON.toJSONString(formDataInfo));
        taskService.complete(taskId, variables);//完成此任务
        //修改主表单中：当前环节信息
        Map<String, Object> processVariables = task.getProcessVariables();
        String formId = (String) processVariables.get("mainFormId");
        String dataId = (String) processVariables.get("mainFormDataId");
        if (formId == null || dataId == null) {
            throw new BusinessException(String.format("获取主表单失败，请联系管理员:formId:%s,dataId:%s", formId, dataId));
        }
        Map<String, Object> update_date = new LinkedHashMap<>();
        update_date.put("now_step_id", this.selectNowTaskId(processInstanceId));
        update_date.put("now_step_name", this.selectNowTaskId(processInstanceId));
        customFormService.update(formId, dataId, update_date);

    }

    /**
     * 领取（签收）任务
     *
     * @param taskId 任务id
     * @param userId 用户id
     * @throws Exception 异常信息
     */
    public void claim(String taskId, String userId) throws Exception {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null)
            throw new BusinessException("获取任务失败");
        if (!StringUtil.isNullOrEmpty(task.getAssignee())) {
            throw new BusinessException("该任务已被签收!");
        }
        //判断是否能签收
        //进行签收
        taskService.claim(taskId, userId);
    }

    /**
     * 已签收待办理的任务
     *
     * @param userId 用户id
     * @return 任务信息
     * @throws Exception
     */
    public List<TaskInfo> todoList(String userId) throws Exception {
        List<TaskInfo> list = new ArrayList<>();
        // 已经签收的任务
        List<Task> todoList = taskService.createTaskQuery().taskAssignee(userId).includeProcessVariables().active().list();
        for (Task task : todoList) {
            String processDefinitionId = task.getProcessDefinitionId();
            ProcessDefinition processDefinition = bpmProcessService.getProcessDefinition(processDefinitionId);
            TaskInfo singleTask = packageTaskInfo(task, processDefinition);
            singleTask.setType(TaskInfo.TYPE_TODO);//已签收
            list.add(singleTask);
        }
        return list;
    }

    /**
     * 等待签收的任务
     *
     * @param userId 用户id
     * @return 任务信息
     * @throws Exception
     */
    public List<TaskInfo> claimList(String userId) throws Exception {
        List<TaskInfo> list = new ArrayList<>();
        // 等待签收的任务
        List<Task> todoList = taskService.createTaskQuery().taskCandidateUser(userId).includeProcessVariables().active().list();
        for (Task task : todoList) {
            String processDefinitionId = task.getProcessDefinitionId();
            ProcessDefinition processDefinition = bpmProcessService.getProcessDefinition(processDefinitionId);
            TaskInfo singleTask = packageTaskInfo(task, processDefinition);
            singleTask.setType(TaskInfo.TYPE_CLAIM);//未签收
            list.add(singleTask);
        }
        return list;
    }

    /**
     * 将task原型转为需要的任务信息
     *
     * @param task              原型
     * @param processDefinition 流程定义实例
     * @return 任务信息
     */
    private TaskInfo packageTaskInfo(Task task, ProcessDefinition processDefinition) {
        TaskInfo taskInfo = TaskInfo.buildFromProtoType(task);
        taskInfo.setDefinition(processDefinition);
        return taskInfo;
    }

}
