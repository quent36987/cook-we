package com.cookwe.utils.converters;

import com.cookwe.data.model.EType;
import com.cookwe.utils.errors.RestError;

import java.util.List;

public class StringToEType {
    public static EType convert(String type) {
        try {
            return EType.valueOf(type);
        } catch (IllegalArgumentException e) {
            throw RestError.TYPE_NOT_FOUND.get(type);
        }
    }

    public static List<EType> convertList(List<String> types) {
        return types.stream()
                .map(StringToEType::convert)
                .toList();
    }
}
