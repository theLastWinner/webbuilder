package org.webbuilder.web.core.aop.logger;

import java.lang.annotation.*;

/**
 * 访问日志注解
 * Created by 浩 on 2015-09-11 0011.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface AccessLogger {
    /**
     * 日志描述
     */
    String value();
}
