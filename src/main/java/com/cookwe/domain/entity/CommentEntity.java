package com.cookwe.domain.entity;

import lombok.*;

import java.time.LocalDateTime;

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
    private LocalDateTime createdAt;
}