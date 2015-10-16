package org.webbuilder.web.controller.login;

import org.webbuilder.utils.base.MD5;
import org.webbuilder.utils.base.StringUtil;
import org.webbuilder.utils.http.HttpUtils;
import org.webbuilder.utils.storage.Storage;
import org.webbuilder.web.core.aop.logger.AccessLogger;
import org.webbuilder.web.core.authorize.AuthorizeInterceptor;
import org.webbuilder.web.core.bean.ResponseMessage;
import org.webbuilder.web.core.exception.BusinessException;
import org.webbuilder.web.core.utils.http.session.HttpSessionManager;
import org.webbuilder.web.core.utils.http.session.HttpSessionManagerContainer;
import org.webbuilder.web.core.utils.http.session.impl.StorageHttpSessionManager;
import org.webbuilder.web.core.utils.WebUtil;
import org.webbuilder.web.po.user.User;
import org.webbuilder.web.service.config.ConfigService;
import org.webbuilder.web.service.storage.StorageService;
import org.webbuilder.web.service.user.UserService;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by 浩 on 2015-08-26 0026.
 */
@RestController
@RequestMapping(value = "/login")
@AccessLogger("授权")
public class LoginController {

    private org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private UserService userService;

    @Resource
    private StorageService storageService;

    @Resource
    private ConfigService configService;

    @Resource
    private HttpSessionManager httpSessionManager;

    @RequestMapping(value = "/exit", method = RequestMethod.POST)
    @AccessLogger("退出")
    public Object exit(HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");
            if (user != null) {
                session.removeAttribute("user");
                httpSessionManager.removeUser(user.getU_id());
            }
        } catch (Exception e) {
            return new ResponseMessage(false, e);
        }
        return new ResponseMessage(true, "退出成功");
    }

    /**
     * 验证码方式登陆
     */
    @RequestMapping(value = "/v2", method = RequestMethod.POST)
    @AccessLogger("验证码方式")
    public Object loginV2(@RequestParam("username") String username,
                          @RequestParam("password") String password,
                          @RequestParam("vcode") String vcode,
                          HttpServletRequest request) {
        try {
            if (!vcode.equals(request.getSession().getAttribute("vcode"))) {
                return new ResponseMessage(false, "验证码错误", "401");
            }
            User db = userService.selectByUserName(username);
            if (db == null) {
                return new ResponseMessage(false, "用户不存在", "404");
            }
            if (!MD5.encode(password).equals(db.getPassword())) {
                throw new BusinessException("密码错误!");
            } else {
                db.initRoleInfo();
                request.getSession().setAttribute("user", db);//设置登录用户
            }
        } catch (Exception e) {
            return new ResponseMessage(false, e);
        }
        return new ResponseMessage(true, "登陆成功");
    }

    /**
     * 不使用验证码登录，多次密码错误进行ip+用户名限制，达到限制次数后禁止登陆（即便再次输入正确的密码）
     */
    @RequestMapping(method = RequestMethod.GET)
    @AccessLogger("限制登陆次数方式")
    public Object login(@RequestParam("username") String username,
                        @RequestParam("password") String password,
                        HttpServletRequest request) {
        try {
            User db = userService.selectByUserName(username);
            if (db == null) {
                return new ResponseMessage(false, "用户不存在", "404");
            }
            String ip = WebUtil.getIpAddr(request);
            Storage<String, HashMap> storage = storageService.getStorage("user_login_valid", HashMap.class);
            String cacheKey = String.format("%s>%s", ip, db.getUsername());
            HashMap map = storage.get(cacheKey);
            Long last_login_time;
            Integer err_number = 0;
            //密码错误最多次数,默认5次
            int max_err_number = configService.getInt("login_config", "max_err_number", 5);
            //封ip+用户名的时间，默认30分钟
            int closure_time = configService.getInt("login_config", "closure_time", 30);
            //用户输错过密码
            if (map != null) {
                last_login_time = (Long) map.get("last_err_time");//最后一次密码错误时间
                err_number = (Integer) map.get("err_number");//密码错误次数
                if (last_login_time == null) last_login_time = System.currentTimeMillis();
                if (err_number == null) err_number = 0;
                //密码错误次数已超过阈值
                if (err_number >= max_err_number) {
                    long minute = (closure_time) - ((System.currentTimeMillis() - last_login_time) / 1000 / 60);
                    throw new BusinessException(String.format("请%s分钟后再试!", minute));
                }
            } else {
                map = new HashMap();
            }
            if (!MD5.encode(password).equals(db.getPassword())) {
                last_login_time = System.currentTimeMillis();
                map.put("last_err_time", last_login_time);
                map.put("err_number", ++err_number);
                storage.put(cacheKey, map);
                String msg = String.format("密码错误,你还有%s次机会!", max_err_number - err_number);
                if (max_err_number - err_number == 0)
                    msg = "密码错误次数太多，请稍后再试!";
                throw new BusinessException(msg);
            } else {
                db.initRoleInfo();
                HttpSession session = request.getSession();
                exit(session);//退出登录
                //踢出已经登陆的用户
                String sessionId = httpSessionManager.getSessionIdByUserId(db.getU_id());
                if (sessionId != null && !sessionId.equals(session.getId()))
                    httpSessionManager.removeSession(sessionId);
                session.setAttribute("user", db);//设置登录用户
                //添加新的用户
                httpSessionManager.addUser(db.getU_id(), session);
                storage.remove(cacheKey);//删除错误记录
            }
        } catch (Exception e) {
            return new ResponseMessage(false, e);
        }
        return new ResponseMessage(true, "登陆成功");
    }

    /**
     * 单点登录回掉，登录成功后，授权系统会调用此地址，传入被授权的token，
     * <br/>通过token和accessKey去授权系统提取已登陆用户的信息，并对本系统中用户信息的绑定
     *
     * @param token   被授权的token
     * @param url     登陆成功后跳转的地址
     * @param request HttpServletRequest
     * @return 重定向地址
     */
    @RequestMapping(value = "/sso/callback", method = RequestMethod.GET)
    @AccessLogger("单点登陆回掉")
    public String sso(@RequestParam("token") String token, String url, HttpServletRequest request) {
        String ssoAccessHost = configService.get("sso", "host", "http://localhost:8080/sso/");
        String ssoAccessUrl = ssoAccessHost.concat(configService.get("sso", "uri", "login/%s/%s"));
        String accessKey = configService.get("sso", "accessKey", "");
        if (StringUtil.isNullOrEmpty(url)) {
            url = WebUtil.getBasePath(request);
        }
        Map<String, String> param = new LinkedHashMap<>();
        param.put("token", token);
        param.put("accessKey", accessKey);
        try {
            ssoAccessUrl = String.format(ssoAccessUrl, accessKey, token);
            String data = HttpUtils.doPost(ssoAccessUrl, param);
            ResponseMessage message = ResponseMessage.fromJson(data);
            if (message.isSuccess()) {
                Map<String, Object> userInfo = (Map) message.getData();
                //提取sso系统中的用户信息

            } else {
                return "redirect:".concat(AuthorizeInterceptor.loginPage);
            }
        } catch (Exception e) {
            logger.error("sso login error!", e);
            return "redirect:".concat(AuthorizeInterceptor.loginPage);
        }
        return "redirect:".concat(url);
    }

}
