package com.cookwe.domain.entity;

import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@With
public class RecipePictureDTO {
    private Long id;
    private String imageUrl;
    private String ownerUsername;
    private Timestamp createdAt;
}
