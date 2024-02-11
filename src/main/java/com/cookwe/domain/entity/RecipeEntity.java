package com.cookwe.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@With
public class RecipeEntity {

    public Long id;

    public String name;

    public String time;

    public String user_id;

    public LocalDateTime createdAt;
}
