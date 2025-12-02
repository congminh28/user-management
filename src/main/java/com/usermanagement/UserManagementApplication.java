package com.usermanagement;

import com.usermanagement.entity.User;
import com.usermanagement.repository.UserRepository;
import com.usermanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class UserManagementApplication implements CommandLineRunner {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public static void main(String[] args) {
        SpringApplication.run(UserManagementApplication.class, args);
    }
    
    @Override
    public void run(String... args) throws Exception {
        // Create default admin user if not exists
        if (!userService.existsByEmail("admin@example.com")) {
            User admin = new User();
            admin.setName("Administrator");
            admin.setEmail("admin@example.com");
            admin.setPassword("admin123");
            userService.createUser(admin);
            System.out.println("Default admin user created: admin@example.com / admin123");
        }
    }
}

