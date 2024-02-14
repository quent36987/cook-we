package com.cookwe.presentation.request;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@With
public class UpdateUserRequest {
    public String firstName;
    public String lastName;
}
