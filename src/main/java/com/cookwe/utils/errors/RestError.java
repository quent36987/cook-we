package com.cookwe.utils.errors;

public enum RestError {
    // NOT FOUND
    NOT_FOUND_MESSAGE(404, "Not found: %s"),
    POST_NOT_FOUND(404, "Post with ID %s not found"),
    ROLE_NOT_FOUND(404, "Role with ID %s not found"),
    UNIT_NOT_FOUND(400, "Unit %s not found"),
    RECIPE_NOT_FOUND(404, "Recipe with ID %d not found"),
    COMMENT_NOT_FOUND(404, "Comment with ID %d not found"),
    USER_NOT_FOUND(404, "User not found"),
    SEASON_NOT_FOUND(404, "Season %s not found"),

    // UNAUTHORIZED
    FORBIDDEN(403, "Forbidden"),
    FORBIDDEN_MESSAGE(403, "Forbidden: %s"),

    // MISSING FIELD AND BAD REQUEST
    BAD_REQUEST(400, "Invalid request"),
    RECIPE_ALREADY_FAVORITE(400, "Recipe %s is already in your favorites"),
    RECIPE_NOT_FAVORITE(400, "Recipe %s is not in your favorites"),
    MISSING_FIELD(400, "missing field: %s"), // FIXME
    MISSING_USER_ID(400, "User id (X-user-id header) can not be null."),
    USERNAME_ALREADY_EXISTS(400, "Username %s already exists"),
    EMAIL_ALREADY_EXISTS(400, "Email %s already exists"),

    // INTERNAL SERVER ERROR
    INTERNAL_SERVER_ERROR(500, "Internal server error"),

    ;
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
