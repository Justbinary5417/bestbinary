package com.justbinary.dto;

import java.util.Objects;

public class RegisterRequest {

    private String fullName;
    private String email;
    private String password;
    private String phone;

    public RegisterRequest() {}

    public RegisterRequest(String fullName, String email, String password, String phone) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.phone = phone;
    }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RegisterRequest)) return false;
        RegisterRequest that = (RegisterRequest) o;
        return Objects.equals(email, that.email) && Objects.equals(fullName, that.fullName);
    }

    @Override
    public int hashCode() { return Objects.hash(fullName, email); }

    @Override
    public String toString() {
        return "RegisterRequest{fullName='" + fullName + "', email='" + email + "', phone='" + phone + "'}";
    }
}