package org.webbuilder.bpm.service.bpm;

import com.alibaba.fastjson.JSON;
import org.webbuilder.bpm.cache.definition.ProcessDefinitionCache;
import org.webbuilder.bpm.po.bpm.WorkFlowFormEntity;
import org.webbuilder.utils.base.MD5;
import org.webbuilder.web.core.bean.GenericPo;
import org.webbuilder.web.core.exception.BusinessException;
import org.webbuilder.web.po.form.Form;
import org.webbuilder.web.service.form.CustomFormService;
import org.webbuilder.web.service.storage.StorageService;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.*;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.image.ProcessDiagramGenerator;
import org.activiti.spring.ProcessEngineFactoryBean;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 浩 on 2015-08-14 0014.
 */
@Service
public class BpmProcessService {

    @Resource
    protected RuntimeService runtimeService;

    @Resource
    protected TaskService taskService;

    @Resource
    protected HistoryService historyService;

    @Resource
    protected RepositoryService repositoryService;

    @Resource
    private StorageService storageService;

    @Autowired
    private ProcessEngineFactoryBean processEngineFactoryBean;

    @Autowired
    private ProcessEngineConfiguration processEngineConfiguration;

    @Resource
    private CustomFormService customFormService;

    @Autowired
    private IdentityService identityService;

    @Autowired
    private FormService formService;

    @Autowired
    private BpmTaskService bpmTaskService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 启动一个流程，并初始化自定义主表单数据
     *
     * @param work_flow_key 流程定义key
     * @param formId        主表单id
     * @param formEntity    主表单实体
     * @param variables     启动参数
     * @return 启动后的流程实例
     * @throws Exception 异常信息
     */
    @Transactional(rollbackFor = Throwable.class)
    public ProcessInstance startWorkflow(String work_flow_key,
                                         String formId,
                                         WorkFlowFormEntity formEntity,
                                         Map<String, Object> variables) throws Exception {
        logger.debug("start flow ", work_flow_key);
        Form form = customFormService.getForm(formId);
        if (null==form) {
            throw new BusinessException(String.format("不存在此表单:%d", formId));
        }
        formEntity.setU_id(GenericPo.createUID());//创建主键
        String businessKey = formEntity.getU_id();
        ProcessInstance processInstance = null;
        try {
            // 用来设置启动流程的人员ID，引擎会自动把用户ID保存到activiti:initiator中
            identityService.setAuthenticatedUserId(formEntity.getCreator_id());
            processInstance = runtimeService.startProcessInstanceByKey(work_flow_key, businessKey, variables);
            String processInstanceId = processInstance.getId();
            formEntity.setFlow_instance_id(processInstanceId);
            formEntity.setNow_step_id(bpmTaskService.selectNowTaskId(processInstanceId));
            formEntity.setNow_step_name(bpmTaskService.selectNowTaskName(processInstanceId));
            //保存基础表数据
            String dataId = customFormService.insert(formId, formEntity.init());
            variables.put("mainFormId", formId); //设置主表id
            variables.put("mainFormDataId", dataId);//设置主表数据主键
            runtimeService.setVariables(processInstanceId, variables);
            if (logger.isDebugEnabled())
                logger.debug("start process of {key={}, bkey={}, pid={}, variables={}}", new Object[]{work_flow_key, businessKey, processInstanceId, variables});
        } finally {
            identityService.setAuthenticatedUserId(null);
        }
        return processInstance;
    }

    /**
     * 根据流程定义id获取流程定义实例<br/>
     * 此方法使用了缓存，返回的{@link ProcessDefinition}实例不为activity默认的实例，而是{@link ProcessDefinitionCache},以保证缓存时正常序列化
     *
     * @param processDefinitionId 流程实例id
     * @return 流程定义实例
     * @throws Exception 异常信息
     */
    public ProcessDefinition getProcessDefinition(String processDefinitionId) throws Exception {
        return repositoryService.createProcessDefinitionQuery().processDefinitionId(processDefinitionId).singleResult();
    }

    /**
     * 输出一个流程实例，支持图片和xml格式
     *
     * @param resourceType      输出格式 ,image or xml
     * @param processInstanceId 流程实例id
     * @param outputStream      输出流
     * @throws Exception 异常欣喜
     */
    public void outputProcessInstance(String resourceType, String processInstanceId, OutputStream outputStream) throws Exception {
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        ProcessDefinition processDefinition = this.getProcessDefinition(processInstance.getProcessDefinitionId());
        String resourceName = "";
        if (resourceType.equals("image")) {
            resourceName = processDefinition.getDiagramResourceName();
        } else if (resourceType.equals("xml")) {
            resourceName = processDefinition.getResourceName();
        }
        InputStream resourceAsStream = repositoryService.getResourceAsStream(processDefinition.getDeploymentId(), resourceName);
        IOUtils.copy(resourceAsStream, outputStream);
    }

    /**
     * 获取带跟踪信息的图片
     *
     * @param executionId
     * @param outputStream
     * @throws Exception
     */
    public void traceProcessImage(String executionId, OutputStream outputStream) throws Exception {
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(executionId).singleResult();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processInstance.getProcessDefinitionId());
        List<String> activeActivityIds = runtimeService.getActiveActivityIds(executionId);
        processEngineConfiguration = processEngineFactoryBean.getProcessEngineConfiguration();
        Context.setProcessEngineConfiguration((ProcessEngineConfigurationImpl) processEngineConfiguration);
        ProcessDiagramGenerator diagramGenerator = processEngineConfiguration.getProcessDiagramGenerator();
        InputStream imageStream = diagramGenerator.generateDiagram(bpmnModel, "png", activeActivityIds);
        IOUtils.copy(imageStream, outputStream);
    }

}
