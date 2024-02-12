package com.cookwe.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommentEntity {

    private Long id;
    private String text;
    private String userId;
    private Long recipeId;
    private LocalDateTime createdAt;
}