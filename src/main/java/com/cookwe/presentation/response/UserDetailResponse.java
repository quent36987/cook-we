package com.cookwe.presentation.response;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

@AllArgsConstructor
@NoArgsConstructor
@With
public class UserDetailResponse {
    public String firstName;
    public String lastName;
    public String username;
    public String email;
}
