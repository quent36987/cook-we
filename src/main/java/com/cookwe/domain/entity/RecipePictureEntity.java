package com.cookwe.domain.entity;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@With
public class RecipePictureEntity {

    private Long id;
    private RecipeEntity recipe;
    private String imageUrl;
    private UserEntity user;
    private LocalDateTime createdAt;
}
