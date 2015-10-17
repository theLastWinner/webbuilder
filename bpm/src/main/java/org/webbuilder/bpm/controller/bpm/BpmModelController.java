package org.webbuilder.bpm.controller.bpm;

import org.webbuilder.bpm.service.bpm.BpmModelService;
import org.webbuilder.web.core.aop.logger.AccessLogger;
import org.webbuilder.web.core.bean.ResponseMessage;
import org.activiti.engine.repository.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 工作流模型控制器
 * Created by 浩 on 2015-08-14 0014.
 */
@Controller
@RequestMapping(value = "/bpm/model",produces = ResponseMessage.CONTENT_TYPE_JSON)
@ResponseBody
@AccessLogger("工作流模型")
public class BpmModelController {

    @Resource
    private BpmModelService bpmModelService;

    /**
     * 工作流模型列表
     *
     * @return 模型列表
     */
    @RequestMapping(method = RequestMethod.GET)
    @AccessLogger("查询所有模型")
    public List<Model> list() {
        List<Model> models = bpmModelService.selectModelList();
        return models;
    }

    /**
     * 添加一个模型
     *
     * @param data 模型数据，由 name,key,description组成的map集合
     * @return 执行结果响应信息:{success:true,data:@modelInfo}
     */
    @RequestMapping(method = RequestMethod.POST)
    @AccessLogger("添加一个模型")
    public ResponseMessage add(@RequestBody Map<String, String> data) {
        try {
            Model model = bpmModelService.createModel(data.get("name"), data.get("key"), data.get("description"));
            return new ResponseMessage(true, model);
        } catch (Exception e) {
            return new ResponseMessage(false, e);
        }
    }

    /**
     * 删除一个模型
     *
     * @param modelId 要删除模型的id
     * @return 执行结果响应信息
     */
    @RequestMapping(value = "/{modelId}", method = RequestMethod.DELETE)
    @AccessLogger("删除模型")
    public ResponseMessage delete(@PathVariable(value = "modelId") String modelId) {
        try {
            bpmModelService.deleteModel(modelId);
            return new ResponseMessage(true, "删除成功");
        } catch (Exception e) {
            return new ResponseMessage(false, e);
        }
    }

    /**
     * 部署流程
     *
     * @param modelId 模型id
     * @return 执行结果响应信息
     */
    @AccessLogger("部署流程")
    @RequestMapping(value = "/{modelId}/deploy")
    public ResponseMessage deploy(@PathVariable(value = "modelId") String modelId) {
        try {
            bpmModelService.deployModel(modelId);
            return new ResponseMessage(true, "部署成功");
        } catch (Exception e) {
            return new ResponseMessage(false, e);
        }
    }
}
