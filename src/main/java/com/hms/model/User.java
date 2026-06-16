package com.hms.model;

public class User {
    public enum Role { ADMIN, DOCTOR, RECEPTIONIST }

    private int userId;
    private String username;
    private String passwordHash;
    private Role role;
    private String fullName;
    private String email;
    private boolean isActive;

    public User() {}
    public User(String username, String passwordHash, Role role, String fullName, String email) {
        this.username = username; this.passwordHash = passwordHash;
        this.role = role; this.fullName = fullName; this.email = email; this.isActive = true;
    }

    public int getUserId()                    { return userId; }
    public void setUserId(int id)             { this.userId = id; }
    public String getUsername()               { return username; }
    public void setUsername(String u)         { this.username = u; }
    public String getPasswordHash()           { return passwordHash; }
    public void setPasswordHash(String p)     { this.passwordHash = p; }
    public Role getRole()                     { return role; }
    public void setRole(Role r)               { this.role = r; }
    public String getFullName()               { return fullName; }
    public void setFullName(String n)         { this.fullName = n; }
    public String getEmail()                  { return email; }
    public void setEmail(String e)            { this.email = e; }
    public boolean isActive()                 { return isActive; }
    public void setActive(boolean a)          { this.isActive = a; }
}
