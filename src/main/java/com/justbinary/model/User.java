package com.justbinary.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import java.time.LocalDateTime;

@Document(collection = "users")
public class User {

    @Id
    private String id;

    @Indexed(unique = true)
    private String email;

    private String password;
    private String fullName;
    private String phone;
    private String role = "USER";
    private String accountType = "REAL"; // ← FIXED: was "DEMO", now "REAL"
    private boolean enabled = true;
    private LocalDateTime createdAt = LocalDateTime.now();

    public User() {}

    public String getId()                    { return id; }
    public String getEmail()                 { return email; }
    public String getPassword()              { return password; }
    public String getFullName()              { return fullName; }
    public String getPhone()                 { return phone; }
    public String getRole()                  { return role; }
    public String getAccountType()           { return accountType; }
    public boolean isEnabled()               { return enabled; }
    public LocalDateTime getCreatedAt()      { return createdAt; }

    public void setId(String id)                          { this.id = id; }
    public void setEmail(String email)                    { this.email = email; }
    public void setPassword(String password)              { this.password = password; }
    public void setFullName(String fullName)              { this.fullName = fullName; }
    public void setPhone(String phone)                    { this.phone = phone; }
    public void setRole(String role)                      { this.role = role; }
    public void setAccountType(String accountType)        { this.accountType = accountType; }
    public void setEnabled(boolean enabled)               { this.enabled = enabled; }
    public void setCreatedAt(LocalDateTime createdAt)     { this.createdAt = createdAt; }
}