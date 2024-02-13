package com.cookwe.presentation.request;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@With
public class CreateCommentRequest {
    public String text;
}
