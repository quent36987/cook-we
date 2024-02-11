package com.cookwe.utils.errors;

import lombok.Getter;

@Getter
public class ErrorCode extends RuntimeException {
    private final int code;

    public ErrorCode(int code, String message, Object... args) {
        super(String.format(message, args));
        this.code = code;
    }
}
