package com.cookwe.domain.entity;

import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@With
@AllArgsConstructor
@NoArgsConstructor
public class CommentDTO {
    private Long id;
    private String text;
    private String ownerUsername;
    private Long recipeId;
    private Timestamp createdAt;
}