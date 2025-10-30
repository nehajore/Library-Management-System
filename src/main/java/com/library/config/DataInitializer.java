package com.library.config;

import com.library.entity.Admin;
import com.library.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Create default admin user if it doesn't exist
        Optional<Admin> existingAdmin = adminRepository.findByUsername("admin");
        if (existingAdmin.isEmpty()) {
            Admin admin = new Admin();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmail("admin@library.com");
            admin.setFirstName("Admin");
            admin.setLastName("User");
            admin.setRole("ADMIN");
            admin.setActive(true);
            adminRepository.save(admin);
            System.out.println("Default admin user created: username=admin, password=admin123");
        }
    }
}

