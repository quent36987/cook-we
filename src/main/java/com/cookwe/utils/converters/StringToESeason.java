package com.cookwe.utils.converters;

import com.cookwe.data.model.ESeason;
import com.cookwe.utils.errors.RestError;

import java.util.List;

public class StringToESeason {
    public static ESeason convert(String season) {
        try {
            return ESeason.valueOf(season);
        } catch (IllegalArgumentException e) {
            throw RestError.SEASON_NOT_FOUND.get(season);
        }
    }

    public static List<ESeason> convertList(List<String> seasons) {
        return seasons.stream()
                .map(StringToESeason::convert)
                .toList();
    }
}
