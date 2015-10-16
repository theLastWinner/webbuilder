package org.webbuilder.utils.db.def.valid.common;

import org.webbuilder.utils.db.def.valid.Validator;
import org.webbuilder.utils.db.exception.DataValidException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 浩 on 2015-07-10 0010.
 */
public class IsPhone implements Validator {
     Pattern pattern = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");

    public void valid(String errorMsg, Object value) throws DataValidException {
        Matcher m = pattern.matcher(value.toString());
        if (!m.matches()) {
            throw new DataValidException(errorMsg);
        }
    }
}
