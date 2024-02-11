package com.cookwe.presentation.response;

import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
public class RecipeResponse {
    public Long id;

    public String name;

    public Long time;

    public String user_id;

    public LocalDateTime createdAt;
}
