package com.cookwe.presentation.controller;


import com.cookwe.data.model.ERole;
import com.cookwe.data.model.RoleModel;
import com.cookwe.data.model.UserModel;
import com.cookwe.data.repository.RoleRepository;
import com.cookwe.data.repository.UserRepository;
import com.cookwe.domain.entity.UserEntity;
import com.cookwe.domain.service.UserService;
import com.cookwe.presentation.request.LoginRequest;
import com.cookwe.presentation.request.SignupRequest;
import com.cookwe.presentation.response.MessageResponse;
import com.cookwe.presentation.response.UserDetailResponse;
import com.cookwe.presentation.response.UserResponse;
import com.cookwe.utils.converters.UserEntityToUserDetailResponse;
import com.cookwe.utils.errors.RestError;
import com.cookwe.utils.security.jwt.JwtUtils;
import com.cookwe.utils.security.services.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

//@CrossOrigin(origins = "*", maxAge = 3600)
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600, allowCredentials = "true")
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "Authentication operations")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/signin")
    @Operation(summary = "Authenticate a user")
    public ResponseEntity<UserResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .body(new UserResponse(userDetails.getUsername()));
    }

    @PostMapping("/signup")
    @Operation(summary = "Register a new user")
    public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (signUpRequest.getUsername().isEmpty() || signUpRequest.getEmail().isEmpty() || signUpRequest.getPassword().isEmpty()) {
            throw RestError.BAD_REQUEST.get();
        }

        if (Boolean.TRUE.equals(userRepository.existsByUsername(signUpRequest.getUsername()))) {
            throw RestError.USERNAME_ALREADY_EXISTS.get(signUpRequest.getUsername());
        }

        if (Boolean.TRUE.equals(userRepository.existsByEmail(signUpRequest.getEmail()))) {
            throw RestError.EMAIL_ALREADY_EXISTS.get(signUpRequest.getEmail());
        }

        // Create new user's account
        UserModel user = new UserModel(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        List<RoleModel> roles = new ArrayList<>();

        RoleModel userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        roles.add(userRole);

        user.setRoles(roles);
        userRepository.save(user);

        return this.authenticateUser(new LoginRequest(signUpRequest.getUsername(), signUpRequest.getPassword()));
    }

    @PostMapping("/signout")
    @Operation(summary = "Sign out a user")
    public ResponseEntity<MessageResponse> logoutUser() {
        ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new MessageResponse("User signed out successfully!"));
    }

    @GetMapping("/me")
    @Operation(summary = "Get the current user")
    public UserDetailResponse getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        UserEntity userEntity = userService.getUserByUsername(userDetails.getUsername());

        return UserEntityToUserDetailResponse.convert(userEntity);
    }
}
