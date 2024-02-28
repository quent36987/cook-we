package com.cookwe.domain.entity;

import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@With
@AllArgsConstructor
@NoArgsConstructor
public class CommentEntity {

    private Long id;
    private String text;
    private UserEntity user;
    private Long recipeId;
    private Timestamp createdAt;
}