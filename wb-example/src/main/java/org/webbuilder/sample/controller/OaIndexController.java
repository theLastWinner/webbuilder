package org.webbuilder.sample.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.webbuilder.web.core.utils.WebUtil;
import org.webbuilder.web.service.config.ConfigService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 浩 on 2015-10-09 0009.
 */
@Controller
public class OaIndexController {

    @Resource
    private ConfigService configService;

    @RequestMapping(value = "/**")
    public ModelAndView index(HttpServletRequest request, HttpServletResponse response) {
//        User user = WebUtil.getLoginUser(request);
//        if (user == null) {
//            try {
//                request.getRequestDispatcher("/login.html").forward(request, response);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return null;
//        }
        String path = request.getRequestURI();
        String content = request.getContextPath();
        if (path.startsWith(content)) {
            path = path.substring(content.length() + 1);
        }
        if (path.contains("."))
            path = path.split("[.]")[0];
        ModelAndView modelAndView = new ModelAndView(path);
        Map<String, Object> sessionAttr = new HashMap<>();
        Enumeration<String> enumeration = request.getSession().getAttributeNames();
        while (enumeration.hasMoreElements()) {
            String name = enumeration.nextElement();
            sessionAttr.put(name, request.getSession().getAttribute(name));
        }
        modelAndView.addObject("param", WebUtil.getParams(request));
        modelAndView.addObject("basePath", WebUtil.getBasePath(request));
        modelAndView.addObject("config", configService);
        return modelAndView;
    }
}
