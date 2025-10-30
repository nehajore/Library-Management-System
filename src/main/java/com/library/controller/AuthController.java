package com.library.controller;

import com.library.dto.LoginRequest;
import com.library.dto.LoginResponse;
import com.library.dto.UserRegistrationDto;
import com.library.entity.User;
import com.library.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@Valid @RequestBody UserRegistrationDto registrationDto) {
        User user = authService.registerUser(registrationDto);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PostMapping("/login/user")
    public ResponseEntity<LoginResponse> loginUser(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse response = authService.loginUser(loginRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login/admin")
    public ResponseEntity<LoginResponse> loginAdmin(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse response = authService.loginAdmin(loginRequest);
        return ResponseEntity.ok(response);
    }
}

