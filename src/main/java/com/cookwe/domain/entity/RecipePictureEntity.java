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
public class RecipePictureEntity {

    private Long id;
    private Long recipeId;
    private String imageUrl;
    private String userId;
    private LocalDateTime createdAt;
}
