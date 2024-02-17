package com.cookwe.utils.converters;

import com.cookwe.data.model.ESeason;
import com.cookwe.utils.errors.RestError;

public class StringToESeason {
    public static ESeason convert(String season) {
        try {
            return ESeason.valueOf(season);
        } catch (IllegalArgumentException e) {
            throw RestError.SEASON_NOT_FOUND.get(season);
        }

    }
}
