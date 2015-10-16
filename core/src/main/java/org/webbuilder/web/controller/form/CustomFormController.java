package org.webbuilder.web.controller.form;

import com.alibaba.fastjson.JSON;
import org.webbuilder.web.core.bean.PageUtil;
import org.webbuilder.web.core.bean.ResponseData;
import org.webbuilder.web.core.bean.ResponseMessage;
import org.webbuilder.web.service.form.CustomFormService;
import org.webbuilder.web.service.storage.StorageService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * 自定义表单数据访问接口
 * Created by 浩 on 2015-08-01 0001.
 */
@Controller
@RequestMapping(value = "/cf")
public class CustomFormController {

    @Resource
    private CustomFormService customFormService;

    @RequestMapping(value = "/{form_id}", method = RequestMethod.GET, produces = ResponseMessage.CONTENT_TYPE_JSON)
    @ResponseBody
    public Object list(@PathVariable("form_id") String form_id, PageUtil pageUtil,
                       @RequestParam(value = "includes", defaultValue = "[]") String includes,
                       @RequestParam(value = "excludes", defaultValue = "[]") String excludes) {
        // 获取条件查询
        try {
            pageUtil.getIncludes().addAll(JSON.parseObject(includes, HashSet.class));
            pageUtil.getExcludes().addAll(JSON.parseObject(excludes, HashSet.class));
            Map data = customFormService.selectPager(form_id, pageUtil);
            ResponseData responseData = new ResponseData(data);
            responseData.excludes(Map.class, "ROWNUM_");

            return responseData;
        } catch (Exception e) {
            return new ResponseMessage(false, e);
        }
    }

    @RequestMapping(value = "/{form_id}/{id}", method = RequestMethod.GET, produces = ResponseMessage.CONTENT_TYPE_JSON)
    @ResponseBody
    public Object info(@PathVariable("form_id") String form_id, @PathVariable("id") String id) {
        try {
            Object data = customFormService.selectByPk(form_id, id);
            ResponseData responseData = new ResponseData(new ResponseMessage(true, data));
            responseData.excludes(Map.class, "ROWNUM_");
            return responseData;
        } catch (Exception e) {
            return new ResponseMessage(false, e);
        }
    }

    @RequestMapping(value = "/{form_id}/total", method = RequestMethod.GET, produces = ResponseMessage.CONTENT_TYPE_JSON)
    @ResponseBody
    public Object total(@PathVariable("form_id") String form_id, PageUtil pageUtil) {
        try {
            // 获取条件查询
            return new ResponseMessage(true, customFormService.total(form_id, pageUtil.params()));
        } catch (Exception e) {
            return new ResponseMessage(false, e);
        }
    }

    @RequestMapping(value = "/{form_id}", method = RequestMethod.POST, produces = ResponseMessage.CONTENT_TYPE_JSON)
    @ResponseBody
    public Object add(@PathVariable("form_id") String form_id,
                      @RequestBody Map<String, Object> data) {
        try {
            String id = customFormService.insert(form_id, data);
            return new ResponseMessage(true, id);
        } catch (Exception e) {
            return new ResponseMessage(false, e);
        }
    }

    @RequestMapping(value = "/{form_id}/{id}", method = RequestMethod.PUT, produces = ResponseMessage.CONTENT_TYPE_JSON)
    @ResponseBody
    public Object update(@PathVariable("form_id") String form_id,
                         @PathVariable("id") String id,
                         @RequestBody Map<String, Object> data) {
        try {
            int i = customFormService.update(form_id, id, data);
            return new ResponseMessage(true, i);
        } catch (Exception e) {
            return new ResponseMessage(false, e);
        }
    }

}
