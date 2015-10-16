package org.webbuilder.utils.db.def.parser.annotation;

import java.lang.annotation.*;

/**
 * Created by 浩 on 2015-07-20 0020.
 */
@Target({ElementType.FIELD})
@Retention(value = RetentionPolicy.RUNTIME)
@Inherited
public @interface FieldMeta {
    String name() default "";

    String alias();

    String remark() default "";

    boolean notNull() default false;

    String defaultValue() default "";

    boolean primaryKey() default false;

    boolean index() default false;

    boolean canUpdate() default true;

    String dataType() default "";

    int length() default 256;

    String vtype() default "";
}
