package com.cookwe.domain.entity;

import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@With
public class RecipePictureEntity {
    private Long id;
    private String imageUrl;
    private UserEntity user;
    private Timestamp createdAt;
}
