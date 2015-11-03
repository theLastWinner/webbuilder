package org.webbuilder.web.core.authorize;

import com.alibaba.fastjson.JSON;
import org.webbuilder.web.core.authorize.annotation.Authorize;
import org.webbuilder.web.core.bean.ResponseMessage;
import org.webbuilder.web.core.utils.WebUtil;
import org.webbuilder.web.po.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.socket.server.support.WebSocketHttpRequestHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.*;

/**
 * Created by 浩 on 2015-08-25 0025.
 */
public class AuthorizeInterceptor implements HandlerInterceptor {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public static String loginPage = "login.html";

    private String loginPath = loginPage;

    private String ajaxLoginMsg = new ResponseMessage(false, "请先登录!", "-1").toString();

    private List<String> includes = new LinkedList<>();

    private List<String> excludes = new LinkedList<>();

    private final PathMatcher matcher = new AntPathMatcher();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof WebSocketHttpRequestHandler) {

        }
        if (!(handler instanceof HandlerMethod)) return true;
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        //Controller中指定的RequestMapping
        RequestMapping mapping = handlerMethod.getBeanType().getAnnotation(RequestMapping.class);
        String[] basePath = mapping == null ? new String[0] : mapping.value();
        //Method中指定的RequestMapping
        mapping = handlerMethod.getMethodAnnotation(RequestMapping.class);
        String[] absPath, paths = mapping.value();
        List<String> absPathList = new ArrayList<>();
        for (int i = 0, len = basePath.length; i < len; i++) {
            if (paths.length == 0) {
                absPathList.add(basePath[i]);
            } else
                for (int i1 = 0, len2 = paths.length; i1 < len2; i1++) {
                    absPathList.add(basePath[i].concat(paths[i1]));
                }
        }
        absPath = absPathList.toArray(new String[absPathList.size()]);
        Authorize authorize = handlerMethod.getMethodAnnotation(Authorize.class);
        Authorize classAuth = handlerMethod.getBeanType().getAnnotation(Authorize.class);
        AuthorizeInfo authorizeInfo = new AuthorizeInfo();
        if (classAuth != null) {
            authorizeInfo.getRoles().addAll(Arrays.asList(classAuth.role()));
            authorizeInfo.getLevel().addAll(Arrays.asList(classAuth.level()));
            authorizeInfo.getExpression().addAll(Arrays.asList(classAuth.expression()));
            authorizeInfo.getModules().addAll(Arrays.asList(classAuth.module()));
            authorizeInfo.setMod(classAuth.mod());
            authorizeInfo.setApi(classAuth.api());
        }
        if (authorize != null) {
            authorizeInfo.getRoles().addAll(Arrays.asList(authorize.role()));
            authorizeInfo.getLevel().addAll(Arrays.asList(authorize.level()));
            authorizeInfo.getExpression().addAll(Arrays.asList(authorize.expression()));
            authorizeInfo.getModules().addAll(Arrays.asList(authorize.module()));
            if (authorize.api() != authorizeInfo.isApi())
                authorizeInfo.setApi(authorize.api());
            if (authorize.mod() != authorizeInfo.getMod())
                authorizeInfo.setMod(authorize.mod());
        }

        boolean doAuthorize = (classAuth != null || authorize != null);//如果进行了注解，代表必须授权
        //判断指定了必须授权的地址
        if (!doAuthorize) {
            if (includes.size() > 0) {
                for (int i = 0, len = absPath.length; i < len; i++) {
                    //需要权限控制
                    if (doAuthorize = matchPath(includes, absPath[i])) break;
                }
            }
        }
        try {
            //不进行控制
            if (!doAuthorize) {
                return true;
            }
            //判断进行排除验证的地址
            for (int i = 0, len = absPath.length; i < len; i++) {
                if (matchPath(excludes, absPath[i])) {
                    if (logger.isInfoEnabled()) {
                        logger.info(String.format("path %s in excludes ", absPath[i]));
                    }
                    return true;
                }
            }
            return validAuthorize(request, response, authorizeInfo);
        } finally {

        }
    }

    //验证授权
    private boolean validAuthorize(HttpServletRequest request, HttpServletResponse response, AuthorizeInfo authorize) throws Exception {
        boolean authorized = false;
        User user = WebUtil.getLoginUser(request);
        String msg = ajaxLoginMsg;
        boolean responseJson = false;
        if (user != null) {
            //进行用户登录权限控制
            if (authorize != null) {
                authorized = authorize.doAuth(user);
            }
            if (!authorized) {
                msg = new ResponseMessage(false, "无访问权限!", "403").toString();
                logger.error(String.format("访问未授权资源:%s", JSON.toJSON(WebUtil.requestInfo(request))));
                response.setStatus(403);
                responseJson = true;
            }
        }
        if (authorize.isApi() && !authorized) {
            //进行api授权
            responseJson = true;
        }
        if (!authorized) {
            String requestType = request.getHeader("X-Requested-With");
            if ("XMLHttpRequest".equals(requestType) || responseJson) {
                // AJAX请求 响应json格式的数据
                response.setContentType(ResponseMessage.CONTENT_TYPE_JSON);
                response.getWriter().print(msg);
                return false;
            } else {
                String uri = WebUtil.getBasePath(request).concat(WebUtil.getUri(request, true));
                String url = loginPath.concat(loginPath.contains("?") ? "&" : "?").concat("url=").concat(URLEncoder.encode(uri, "utf8"));
                if (logger.isInfoEnabled()) {
                    logger.info(String.format("not login,redirect:", url));
                }
                response.sendRedirect(WebUtil.getBasePath(request).concat(url));
                return false;
            }
        }
        return authorized;
    }

    private boolean matchPath(List<String> paths, String path) {
        for (String exclude : paths) {
            if (matcher.match(exclude, path))
                return true;
        }
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object handler, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception e) throws Exception {
    }

    public List<String> getIncludes() {
        return includes;
    }

    public void setIncludes(List<String> includes) {
        this.includes = includes;
    }

    public List<String> getExcludes() {
        return excludes;
    }

    public void setExcludes(List<String> excludes) {
        this.excludes = excludes;
    }

    public String getLoginPath() {
        return loginPath;
    }

    public void setLoginPath(String loginPath) {
        this.loginPath = loginPage = loginPath;
    }

    public String getAjaxLoginMsg() {
        return ajaxLoginMsg;
    }

    public void setAjaxLoginMsg(String ajaxLoginMsg) {
        this.ajaxLoginMsg = ajaxLoginMsg;
    }

    private class AuthorizeInfo {
        Set<String> roles = new HashSet<>();
        Set<String> modules = new HashSet<>();
        Set<String> level = new HashSet<>();
        Set<String> expression = new HashSet<>();
        Authorize.MOD mod = Authorize.MOD.INTERSECTION;
        boolean api;
        public boolean doAuth(User user) {
            boolean success = false;
            if (user == null) return false;
            //优先模块验证
            if (modules.size() != 0) {
                if (level.size() != 0) {
                    m:
                    for (String module : modules) {
                        for (String lv : level) {
                            success = user.hasAccessModuleLevel(module, lv);
                            if (mod == Authorize.MOD.INTERSECTION) {
                                if (success)//只要有一个true就成功
                                    break m;
                            } else {
                                if (!success)//只要有一个false就失败
                                    break m;
                            }
                        }
                    }
                } else {
                    //未设置level
                    for (String module : modules) {
                        success = user.hasAccessModule(module);
                        if (mod == Authorize.MOD.INTERSECTION) {
                            if (success)
                                break;
                        } else {
                            if (!success)
                                break;
                        }
                    }
                }
            } else if (roles.size() > 0) {
                //角色验证
                for (String role : roles) {
                    success = user.hasAccessRole(role);
                    if (mod == Authorize.MOD.INTERSECTION) {
                        if (success)//只要有一个true就成功
                            break;
                    } else {
                        if (!success)//只要有一个false就失败
                            break;
                    }
                }
                //} else if (excludes.size() > 0) {
                //表达式验证 尚未提供支持
                //
            } else {
                //只需要登陆即可
                return true;
            }
            return success;
        }

        public Set<String> getRoles() {
            return roles;
        }

        public void setRoles(Set<String> roles) {
            this.roles = roles;
        }

        public Set<String> getModules() {
            return modules;
        }

        public void setModules(Set<String> modules) {
            this.modules = modules;
        }

        public Set<String> getLevel() {
            return level;
        }

        public void setLevel(Set<String> level) {
            this.level = level;
        }

        public Set<String> getExpression() {
            return expression;
        }

        public void setExpression(Set<String> expression) {
            this.expression = expression;
        }

        public boolean isApi() {
            return api;
        }

        public void setApi(boolean api) {
            this.api = api;
        }

        public Authorize.MOD getMod() {
            return mod;
        }

        public void setMod(Authorize.MOD mod) {
            this.mod = mod;
        }
    }
}
