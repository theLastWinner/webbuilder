package org.webbuilder.bpm.service.bpm;

import org.webbuilder.web.core.bean.PageUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.*;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 模型服务类，用于管理工作流模型
 * Created by 浩 on 2015-08-13 0013.
 */
@Service
public class BpmModelService {
    @Resource
    private RuntimeService runtimeService;

    @Resource
    protected TaskService taskService;

    @Resource
    protected HistoryService historyService;

    @Resource
    protected RepositoryService repositoryService;

    private static final String CACHE_KEY = "bpm.model";

    /**
     * 删除一个模型
     *
     * @param modelId 模型id
     * @throws Exception 异常信息
     */
    public void deleteModel(String modelId) throws Exception {
        repositoryService.deleteModel(modelId);
    }

    /**
     * 导出模型为xml
     *
     * @param modelId      模型id
     * @param outputStream 输出流
     * @throws Exception 导出异常
     */
    public void exportModel(String modelId, OutputStream outputStream) throws Exception {
        Model modelData = repositoryService.getModel(modelId);
        BpmnJsonConverter jsonConverter = new BpmnJsonConverter();
        JsonNode editorNode = new ObjectMapper().readTree(repositoryService.getModelEditorSource(modelData.getId()));
        BpmnModel bpmnModel = jsonConverter.convertToBpmnModel(editorNode);
        BpmnXMLConverter xmlConverter = new BpmnXMLConverter();
        byte[] bpmnBytes = xmlConverter.convertToXML(bpmnModel);
        ByteArrayInputStream in = new ByteArrayInputStream(bpmnBytes);
        IOUtils.copy(in, outputStream);
    }

    /**
     * 部署模型
     *
     * @param modelId 模型id
     * @return 部署实例
     * @throws Exception 异常信息
     */
    public Deployment deployModel(String modelId) throws Exception {
        Model modelData = repositoryService.getModel(modelId);
        ObjectNode modelNode = (ObjectNode) new ObjectMapper().readTree(repositoryService.getModelEditorSource(modelData.getId()));
        byte[] bpmnBytes;
        BpmnModel model = new BpmnJsonConverter().convertToBpmnModel(modelNode);
        bpmnBytes = new BpmnXMLConverter().convertToXML(model);
        String processName = modelData.getName() + ".bpmn20.xml";
        return repositoryService.createDeployment().name(modelData.getName()).addString(processName, new String(bpmnBytes,"utf8")).deploy();
    }

    /**
     * 查询模型列表
     *
     * @return 模型集合
     */
    public List<Model> selectModelList() {
        return repositoryService.createModelQuery().list();
    }

    /**
     * 新建一个模型
     *
     * @param name        模型名称
     * @param key         模型key
     * @param description 模型描述
     * @return 模型实例
     * @throws Exception 异常信息
     */
    public Model createModel(String name, String key, String description) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode editorNode = objectMapper.createObjectNode();
        editorNode.put("id", "canvas");
        editorNode.put("resourceId", "canvas");
        ObjectNode stencilSetNode = objectMapper.createObjectNode();
        stencilSetNode.put("namespace", "http://b3mn.org/stencilset/bpmn2.0#");
        editorNode.put("stencilset", stencilSetNode);
        Model modelData = repositoryService.newModel();
        ObjectNode modelObjectNode = objectMapper.createObjectNode();
        modelObjectNode.put(ModelDataJsonConstants.MODEL_NAME, name);
        modelObjectNode.put(ModelDataJsonConstants.MODEL_REVISION, 1);
        description = StringUtils.defaultString(description);
        modelObjectNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION, description);
        modelData.setMetaInfo(modelObjectNode.toString());
        modelData.setName(name);
        modelData.setKey(StringUtils.defaultString(key));
        repositoryService.saveModel(modelData);
        repositoryService.addModelEditorSource(modelData.getId(), editorNode.toString().getBytes("utf-8"));
        return modelData;
    }

    public List<Object[]> processList(PageUtil pageUtil) {
        List<Object[]> objects = new ArrayList<>();
        int[] pageParams = new int[]{pageUtil.getStart(), pageUtil.getPageSize()};
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery().orderByDeploymentId().desc();
        List<ProcessDefinition> processDefinitionList = processDefinitionQuery.listPage(pageParams[0], pageParams[1]);
        for (ProcessDefinition processDefinition : processDefinitionList) {
            String deploymentId = processDefinition.getDeploymentId();
            Deployment deployment = repositoryService.createDeploymentQuery().deploymentId(deploymentId).singleResult();
            objects.add(new Object[]{processDefinition, deployment});
        }
        return objects;
    }

    public ProcessInstance startWorkflow(String bpmId, String userId, Map<String, Object> variables) {
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(bpmId, variables);
        return processInstance;
    }

    public List<ProcessInstance> test2() {
        ProcessInstanceQuery query = runtimeService.createProcessInstanceQuery().processDefinitionKey("myProcess_1").active();
        return query.list();
    }
}
