package com.justbinary.config;

import com.justbinary.model.User;
import com.justbinary.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.findByEmail("admin@justbinary.com").isEmpty()) {
            User admin = new User();
            admin.setEmail("admin@justbinary.com");
            admin.setPassword(passwordEncoder.encode("Admin@1234"));
            admin.setFullName("Super Admin");
            admin.setRole("ADMIN");
            admin.setEnabled(true);
            userRepository.save(admin);
            System.out.println("✅ Admin account created!");
        }
    }
}