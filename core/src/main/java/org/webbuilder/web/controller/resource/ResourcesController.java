package org.webbuilder.web.controller.resource;

import org.webbuilder.web.core.aop.logger.AccessLogger;
import org.webbuilder.web.core.authorize.annotation.Authorize;
import org.webbuilder.web.core.bean.ResponseData;
import org.webbuilder.web.core.bean.ResponseMessage;
import org.webbuilder.web.po.config.Config;
import org.webbuilder.web.po.resource.Resources;
import org.webbuilder.web.po.role.Role;
import org.webbuilder.web.service.config.ConfigService;
import org.webbuilder.web.service.resource.ResourcesService;
import org.webbuilder.web.core.controller.GenericController;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;

/**
 * 资源控制器，继承自GenericController,使用rest+json
 * Created by generator 2015-8-28 13:01:29
 *
 * @author 浩
 * @version 1.0
 * @UpdateRemark 2015年8月31日，重写{@link ResourcesController#delete(String)}方法，删除资源时需要授权认证
 */
@Controller
@RequestMapping(value = "/resources")
@AccessLogger("资源")
public class ResourcesController extends GenericController<Resources, String> {

    //默认服务类
    @Resource
    private ResourcesService resourcesService;

    @Override
    public ResourcesService getService() {
        return this.resourcesService;
    }

    /**
     * 重写 {@link GenericController#delete(Object)} 新增注解: @Authorize(role = Role.SYS_ROLE_ADMIN),只有拥有系统管理员权限的用户才能执行此操作
     *
     * @param id 要删除的id标识
     * @return
     */
    @Override
    @Authorize(role = Role.SYS_ROLE_ADMIN)
    public Object delete(@PathVariable("id") String id) {
        return super.delete(id);
    }

    /**
     * 判断资源文件是否存在并检测其健康状况
     *
     * @param id 资源文件id
     * @return 查询结果
     */
    @RequestMapping(value = "/{id:^[0-9a-zA-Z]*$}", method = RequestMethod.GET)
    @ResponseBody
    @AccessLogger("获取资源信息")
    public Object info(@PathVariable("id") String id) {
        try {
            Resources resources;
            //如果id长度为32，则尝试通过md5获取
            if (id.length() == 32) {
                resources = getService().selectByMd5(id);
                if (resources == null)
                    resources = getService().selectByPk(id);
            } else
                resources = resourcesService.selectByPk(id);
            if (resources == null) {
                return new ResponseMessage(false, "资源不存在！", "404");
            } else {
                if (resources.getStatus() != 1)
                    return new ResponseMessage(false, "拒绝访问！", "502");
                return new ResponseMessage(true, resources);
            }
        } catch (Exception e) {
            return new ResponseMessage(false, e);
        }
    }


}
