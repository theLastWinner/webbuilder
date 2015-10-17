package org.webbuilder.bpm.controller.bpm;

import com.alibaba.fastjson.JSON;
import org.webbuilder.bpm.po.bpm.WorkFlowFormEntity;
import org.webbuilder.bpm.pojo.bpm.TaskInfo;
import org.webbuilder.bpm.service.bpm.BpmProcessService;
import org.webbuilder.bpm.service.bpm.BpmTaskService;
import org.webbuilder.utils.base.StringUtil;
import org.webbuilder.web.core.aop.logger.AccessLogger;
import org.webbuilder.web.core.authorize.annotation.Authorize;
import org.webbuilder.web.core.bean.ResponseData;
import org.webbuilder.web.core.bean.ResponseMessage;
import org.webbuilder.web.core.utils.WebUtil;
import org.webbuilder.web.po.user.User;
import org.webbuilder.web.service.form.CustomFormService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by 浩 on 2015-08-17 0017.
 */
@RequestMapping(value = "/bpm/process", produces = ResponseMessage.CONTENT_TYPE_JSON)
@AccessLogger("工作流")
@Authorize
@RestController
public class BpmProcessController {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    protected BpmTaskService bpmTaskService;

    @Resource
    protected TaskService taskService;

    @Resource
    protected BpmProcessService bpmProcessService;

    @Resource
    protected CustomFormService customFormService;


    @RequestMapping(value = "/task/{id}/formKey")
    @AccessLogger("任务所需填写表单")
    public Object taskFormIdList(@PathVariable("id") String taskId) {
        try {
            Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
            String ids[] = StringUtil.isNullOrEmpty(task.getFormKey()) ? new String[0] : task.getFormKey().split("[,]");
            return new ResponseMessage(true, ids);
        } catch (Exception e) {
            return new ResponseMessage(false, e);
        }
    }


    @RequestMapping(value = "/todo")
    @AccessLogger("待完成任务列表")
    public Object todo(@RequestParam(value = "includes", defaultValue = "[]") String includes,
                       @RequestParam(value = "excludes", defaultValue = "[]") String excludes) {
        User user = WebUtil.getLoginUser();
        try {
            List<TaskInfo> taskInfos = bpmTaskService.todoList(user.getU_id());
            List<String> includes_list = JSON.parseObject(includes, LinkedList.class);
            List<String> excludes_list = JSON.parseObject(excludes, LinkedList.class);
            if (includes_list.size() > 0 || excludes_list.size() > 0) {
                initFormData(taskInfos, includes_list, excludes_list);
            }
            return new ResponseData(new ResponseMessage(true, taskInfos))
                    .includes(ProcessDefinition.class, "id", "name", "key", "description")
                    .excludes(Map.class, "ROWNUM_");
        } catch (Exception e) {
            return new ResponseMessage(false, e);
        }
    }


    @RequestMapping(value = "/claim")
    @AccessLogger("待签收任务列表")
    public Object claim(@RequestParam(value = "includes", defaultValue = "[]") String includes,
                        @RequestParam(value = "excludes", defaultValue = "[]") String excludes) {
        User user = WebUtil.getLoginUser();
        try {
            List<String> includes_list = JSON.parseObject(includes, LinkedList.class);
            List<String> excludes_list = JSON.parseObject(excludes, LinkedList.class);
            List<TaskInfo> taskInfos = bpmTaskService.claimList(user.getU_id());
            if (includes_list.size() > 0 || excludes_list.size() > 0) {
                initFormData(taskInfos, includes_list, excludes_list);
            }
            return new ResponseData(new ResponseMessage(true, taskInfos))
                    .includes(ProcessDefinition.class, "id", "name", "key", "description")
                    .excludes(Map.class, "ROWNUM_");
        } catch (Exception e) {
            return new ResponseMessage(false, e);
        }
    }

    private void initFormData(List<TaskInfo> taskInfos, List<String> includes, List<String> excludes) {
        for (TaskInfo taskInfo : taskInfos) {
            if (StringUtil.isNullOrEmpty(taskInfo.getFormId()) || StringUtil.isNullOrEmpty(taskInfo.getDataId())) {
                logger.error("task {} not init form info.", taskInfo.getName());
            }
            try {
                Map data = customFormService.selectByPk(taskInfo.getFormId(), taskInfo.getDataId(), includes, excludes);
                taskInfo.setMainFormData(data);
            } catch (Exception e) {
                logger.error("task {} data info error.", e);
            }
        }
    }

    @RequestMapping(value = "/claim/{id}")
    @AccessLogger("签收任务")
    public Object claim(@PathVariable("id") String taskId) {
        User user = WebUtil.getLoginUser();
        try {
            bpmTaskService.claim(taskId, user.getU_id());
            return new ResponseMessage(true, "提交成功!");
        } catch (Exception e) {
            return new ResponseMessage(false, e);
        }
    }

    @RequestMapping(value = "/{id}/complete")
    @AccessLogger("完成任务")
    public Object complete(@PathVariable("id") String taskId,
                           @RequestParam(value = "var", defaultValue = "{}") String var,
                           @RequestParam(value = "formData", defaultValue = "{}") String formData) {
        User user = WebUtil.getLoginUser();
        Map<String, Object> varMap = JSON.parseObject(var);
        Map<String, Object> formDataMap = JSON.parseObject(formData);
        try {
            bpmTaskService.complete(taskId, user.getU_id(), varMap, formDataMap);
            return new ResponseMessage(true, "提交成功!");
        } catch (Exception e) {
            return new ResponseMessage(false, e);
        }
    }

    @RequestMapping(value = "/{processKey}/start")
    @AccessLogger("启动任务")
    public Object start(@PathVariable("processKey") String processKey,
                        @RequestParam("formId") String formId,
                        @RequestParam(value = "formData", defaultValue = "{}") String formData,
                        @RequestParam(value = "var", defaultValue = "{}") String var) {
        User user = WebUtil.getLoginUser();
        Map<String, Object> varMap = JSON.parseObject(var);
        Map<String, Object> formDataMap = JSON.parseObject(formData);
        try {
            WorkFlowFormEntity flowFormEntity = new WorkFlowFormEntity();
            flowFormEntity.putAll(formDataMap);
            flowFormEntity.setCreator_id(user.getU_id());
            bpmProcessService.startWorkflow(processKey, formId, flowFormEntity, varMap);
            return new ResponseMessage(true, "启动流程成功!");
        } catch (Exception e) {
            return new ResponseMessage(false, e);
        }
    }

}
