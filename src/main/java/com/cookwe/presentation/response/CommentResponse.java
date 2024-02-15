package com.cookwe.presentation.response;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@With
public class CommentResponse {
    public Long id;
    public Long recipeId;
    public String text;
    public UserResponse user;
    public LocalDateTime createdAt;
}
