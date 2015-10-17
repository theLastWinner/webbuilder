package org.webbuilder.utils.db.def.valid;

import org.webbuilder.utils.base.StringUtil;
import org.webbuilder.utils.db.def.valid.common.*;
import org.webbuilder.utils.db.exception.DataValidException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by 浩 on 2015-07-10 0010.
 */
public class ValidatorFactory {
    private ValidatorFactory() {
    }

    private static final Map<String, Validator> validator_mapper = new ConcurrentHashMap<>();

    public static final Validator NOT_NULL = new IsNotNull();
    public static final Validator NOT_EMPTY = new IsNotEmpty();
    public static final Validator IS_NUMBER = new IsNumber();
    public static final Validator IS_DATE = new IsDate();
    public static final Validator IS_EMAIL = new IsEmail();
    public static final Validator IS_INT = new IsInt();
    public static final Validator IS_DOUBLE = new IsDouble();
    public static final Validator IS_PHONE = new IsPhone();


    static {
        regist("int", ValidatorFactory.IS_INT);
        regist("double", ValidatorFactory.IS_DOUBLE);
        regist("number", ValidatorFactory.IS_NUMBER);
        regist("float", ValidatorFactory.IS_DOUBLE);
        regist("date", ValidatorFactory.IS_DATE);
        regist("email", ValidatorFactory.IS_EMAIL);
        regist("notnull", ValidatorFactory.NOT_NULL);
        regist("phone", ValidatorFactory.IS_PHONE);
    }

    public static final Validator MIN_LENGTH(int min) {
        return LENGTH(min, Integer.MAX_VALUE);
    }

    public static final Validator MAX_LENGTH(int max) {
        return LENGTH(0, max);
    }

    public static final Validator LENGTH(int min, int max) {
        return new LengthRange(min, max);
    }

    public static final Validator VALUE_RANGE(double min, double max) {
        return new NumberValueRange(min, max);
    }

    public static final Validator MIN_VALUE_RANGE(double min) {
        return VALUE_RANGE(min, Integer.MAX_VALUE);
    }

    public static final Validator MAX_VALUE_RANGE(double max) {
        return VALUE_RANGE(Double.MIN_VALUE, max);
    }

    public static final Validator validator(String type) {
        Validator validator = validator_mapper.get(type);
        if (validator == null) {
            if (type.startsWith("rangeLength")) {
                String[] info = type.split("[:]")[1].split("[,]");
                int min = StringUtil.toInt(info[0].trim());
                int max = StringUtil.toInt(info[1].trim());
                validator = LENGTH(min, max);
            } else if (type.startsWith("range")) {
                String[] info = type.split("[:]")[1].split("[,]");
                int min = StringUtil.toInt(info[0].trim());
                int max = StringUtil.toInt(info[1].trim());
                validator = VALUE_RANGE(min, max);
            } else if (type.startsWith("maxLength")) {
                String[] info = type.split("[:]");
                int max = StringUtil.toInt(info[1]);
                validator = MAX_LENGTH(max);
            } else if (type.startsWith("minLength")) {
                String[] info = type.split("[:]");
                int min = StringUtil.toInt(info[1]);
                validator = MIN_LENGTH(min);
            }
        }
        if (validator != null)
            regist(type, validator);
        return validator;
    }

    public static final Validator regist(String type, Validator validator) {
        return validator_mapper.put(type, validator);
    }

}
