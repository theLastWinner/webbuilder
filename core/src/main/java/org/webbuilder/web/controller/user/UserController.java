package org.webbuilder.web.controller.user;

import org.webbuilder.web.core.aop.logger.AccessLogger;
import org.webbuilder.web.core.authorize.annotation.Authorize;
import org.webbuilder.web.core.bean.JsonParam;
import org.webbuilder.web.core.bean.PageUtil;
import org.webbuilder.web.core.bean.ResponseData;
import org.webbuilder.web.core.bean.ResponseMessage;
import org.webbuilder.web.po.role.Role;
import org.webbuilder.web.po.user.User;
import org.webbuilder.web.service.user.UserService;
import org.webbuilder.web.core.controller.GenericController;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 后台管理用户控制器，继承自GenericController,使用rest+json
 * Created by generator 2015-8-26 10:35:57
 */
@RestController
@RequestMapping(value = "/user")
@AccessLogger("用户管理")
@Authorize(role = Role.SYS_ROLE_ADMIN)
public class UserController extends GenericController<User, String> {

    //默认服务类
    @Resource
    private UserService userService;

    @Override
    public UserService getService() {
        return this.userService;
    }

    @Override
    @AccessLogger("获取列表")
    public ResponseData list(@JsonParam PageUtil pageUtil) {
        pageUtil.excludes("password");
        return super.list(pageUtil).excludes(User.class, "modules");
    }

    @Override
    @AccessLogger("获取用户详情")
    public Object info(@PathVariable("id") String id) {
        return new ResponseData(super.info(id)).excludes(User.class, "password", "modules");
    }

    @Override
    @AccessLogger("删除用户")
    public Object delete(@PathVariable("id") String id) {
        try {
            User user = getService().selectByPk(id);
            user.setStatus(-1);
            getService().update(user);
            return new ResponseMessage(true, "删除成功");
        } catch (Exception e) {
            return new ResponseMessage(false, e);
        }
    }

    @Override
    @AccessLogger("修改用户")
    public Object update(@PathVariable("id") String id, @RequestBody User object) {
        return super.update(id, object);
    }

    @Override
    @AccessLogger("新增用户")
    public Object add(@RequestBody User object) {
        return super.add(object);
    }

}
