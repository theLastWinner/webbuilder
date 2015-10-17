package org.webbuilder.utils.db.def.valid.common;


import org.webbuilder.utils.base.StringUtil;
import org.webbuilder.utils.db.exception.DataValidException;

/**
 * Created by 浩 on 2015-07-10 0010.
 */
public class NumberValueRange extends IsNumber {
    private double min = 0;

    private double max = 0;

    public NumberValueRange(double min, double max) {
        this.max = max;
        this.min = min;
    }

    @Override
    public void valid(String errorMsg, Object value) throws DataValidException {
        super.valid(errorMsg, value);
        double val = StringUtil.toDouble(value);
        if (val > max||val<min) {
            throw new DataValidException(errorMsg);
        }
    }
}
