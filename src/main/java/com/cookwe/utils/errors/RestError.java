package com.cookwe.utils.errors;

public enum RestError {
    // NON TROUVÉ
    NOT_FOUND_MESSAGE(404, "Non trouvé : %s"),
    POST_NOT_FOUND(404, "Post avec l'ID %s non trouvé"),
    ROLE_NOT_FOUND(404, "Rôle avec l'ID %s non trouvé"),
    UNIT_NOT_FOUND(400, "Unité %s non trouvée"),
    RECIPE_NOT_FOUND(404, "Recette avec l'ID %d non trouvée"),
    COMMENT_NOT_FOUND(404, "Commentaire avec l'ID %d non trouvé"),
    USER_NOT_FOUND(404, "Utilisateur non trouvé"),
    SEASON_NOT_FOUND(404, "Saison %s non trouvée"),
    TYPE_NOT_FOUND(404, "Type %s non trouvé"),
    PICTURE_NOT_FOUND(404, "Image non trouvée"),
    SHOPPING_LIST_NOT_FOUND(404, "Liste de courses avec l'ID %d non trouvée"),

    // NON AUTORISÉ
    FORBIDDEN(403, "Interdit"),
    FORBIDDEN_MESSAGE(403, "Interdit : %s"),

    // CHAMP MANQUANT ET MAUVAISE REQUÊTE
    BAD_REQUEST(400, "Requête invalide"),
    BAD_REQUEST_MESSAGE(400, "Requête invalide : %s"),
    INVALID_FIELD(400, "Champ invalide : %s"),
    RECIPE_ALREADY_FAVORITE(400, "Recette %s est déjà dans vos favoris"),
    RECIPE_NOT_FAVORITE(400, "Recette %s n'est pas dans vos favoris"),
    MISSING_FIELD(400, "Champ manquant : %s"),
    MISSING_USER_ID(400, "L'ID utilisateur (en-tête X-user-id) ne peut pas être nul."),
    USERNAME_ALREADY_EXISTS(400, "Nom d'utilisateur %s existe déjà"),
    EMAIL_ALREADY_EXISTS(400, "Email %s existe déjà"),
    ROLE_ALREADY_EXISTS(400, "Rôle %s existe déjà"),
    ROLE_NOT_PRESENT(400, "Rôle %s non présent"),
    FILE_CANT_BE_SAVE(400, "Le fichier ne peut pas être enregistré"),
    FILE_CANT_BE_DELETED(400, "Le fichier ne peut pas être supprimé"),

    // ERREUR INTERNE DU SERVEUR
    INTERNAL_SERVER_ERROR(500, "Erreur interne du serveur"),


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
