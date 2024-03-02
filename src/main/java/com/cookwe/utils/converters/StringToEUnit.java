package com.cookwe.utils.converters;

import com.cookwe.data.model.EUnit;
import com.cookwe.utils.errors.RestError;

public class StringToEUnit {
    public static EUnit convert(String unit) {
        try {
            return EUnit.valueOf(unit);
        } catch (IllegalArgumentException e) {
            throw RestError.UNIT_NOT_FOUND.get(unit);
        }
    }
}
