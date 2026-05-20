package com.justbinary.service;

import com.justbinary.dto.LoginRequest;
import com.justbinary.dto.RegisterRequest;

public interface AuthService {
    String register(RegisterRequest request);
    String login(LoginRequest request);
    String registerAdmin(RegisterRequest request);
    void changePassword(String email, String currentPassword, String newPassword);
}