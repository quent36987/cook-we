package com.cookwe.domain.entity;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShoppingListDTO {
    private Long id;
    private String name;
    private String ownerUsername;
    private String createdAt;
}
