package org.webbuilder.web.core.bean;

import java.lang.annotation.*;

/**
 * springmvc 使用json作为参数绑定
 * Created by 浩 on 2015-09-29 0029.
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JsonParam {
    String value() default "";

    Class type() default Object.class;
}
