package org.webbuilder.utils.db.def.valid.common;


import org.webbuilder.utils.db.exception.DataValidException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 浩 on 2015-07-10 0010.
 */
public class IsEmail extends IsNotEmpty {

    private static final Pattern pattern = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");//复杂匹配

    @Override
    public void valid(String errorMsg, Object value) throws DataValidException {
        super.valid(errorMsg, value);
        Matcher m = pattern.matcher(value.toString());
        if (!m.matches()) {
            throw new DataValidException(errorMsg);
        }
    }
}
