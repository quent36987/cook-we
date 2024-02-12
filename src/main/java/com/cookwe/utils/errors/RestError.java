package com.cookwe.utils.errors;

public enum RestError {
    POST_NOT_FOUND(404, "Post with ID %s not found"),
    BAD_REQUEST(400, "Invalid request"),
    INTERNAL_SERVER_ERROR(500, "Internal server error"),
    MISSING_FIELD(400, "missing field: %s"), // FIXME
    MISSING_USER_ID(400, "User id (X-user-id header) can not be null."),

    USERNAME_ALREADY_EXISTS(400, "Username %s already exists"),
    EMAIL_ALREADY_EXISTS(400, "Email %d already exists"),
    RECIPE_NOT_FOUND(404, "Recipe with ID %s not found"),
    USER_NOT_FOUND(404, "User not found"),
    ROLE_NOT_FOUND(404, "Role with ID %s not found");

    private final int code;
    private final String message;

    RestError(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public ErrorCode get(Object... args) {
        return new ErrorCode(code, message, args);
    }
}
