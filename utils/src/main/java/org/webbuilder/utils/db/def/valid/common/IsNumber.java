package org.webbuilder.utils.db.def.valid.common;


import org.webbuilder.utils.base.StringUtil;
import org.webbuilder.utils.db.exception.DataValidException;

/**
 * Created by 浩 on 2015-07-10 0010.
 */
public class IsNumber extends IsNotEmpty {

    @Override
    public void valid(String name, Object value) throws DataValidException {
        super.valid(name, value);
        if (!StringUtil.isNumber(value)) {
            throw new DataValidException(name);
        }
    }
}
