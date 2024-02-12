package com.cookwe.presentation.response;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    public String firstName;
    public String lastName;
    public String username;
    public String email;
    public List<String> role;
}

