package com.library.service;

import com.library.dto.LoginRequest;
import com.library.dto.LoginResponse;
import com.library.dto.UserRegistrationDto;
import com.library.entity.Admin;
import com.library.entity.User;
import com.library.exception.BadRequestException;
import com.library.exception.UnauthorizedException;
import com.library.repository.AdminRepository;
import com.library.repository.UserRepository;
import com.library.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    public LoginResponse loginUser(LoginRequest loginRequest) {
        User user = userRepository.findByUsername(loginRequest.getUsername())
            .orElseThrow(() -> new UnauthorizedException("Invalid username or password"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid username or password");
        }

        if (!user.isActive()) {
            throw new UnauthorizedException("Account is deactivated");
        }

        String token = jwtTokenUtil.generateToken(user.getUsername(), user.getRole());
        
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        
        return response;
    }

    public LoginResponse loginAdmin(LoginRequest loginRequest) {
        Admin admin = adminRepository.findByUsername(loginRequest.getUsername())
            .orElseThrow(() -> new UnauthorizedException("Invalid username or password"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), admin.getPassword())) {
            throw new UnauthorizedException("Invalid username or password");
        }

        if (!admin.isActive()) {
            throw new UnauthorizedException("Account is deactivated");
        }

        String token = jwtTokenUtil.generateToken(admin.getUsername(), admin.getRole());
        
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setId(admin.getId());
        response.setUsername(admin.getUsername());
        response.setEmail(admin.getEmail());
        response.setRole(admin.getRole());
        
        return response;
    }

    public User registerUser(UserRegistrationDto registrationDto) {
        if (userRepository.existsByUsername(registrationDto.getUsername())) {
            throw new BadRequestException("Username already exists");
        }

        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new BadRequestException("Email already exists");
        }

        User user = new User();
        user.setUsername(registrationDto.getUsername());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setEmail(registrationDto.getEmail());
        user.setFirstName(registrationDto.getFirstName());
        user.setLastName(registrationDto.getLastName());
        user.setPhoneNumber(registrationDto.getPhoneNumber());
        user.setRole("USER");
        user.setActive(true);

        return userRepository.save(user);
    }
}

