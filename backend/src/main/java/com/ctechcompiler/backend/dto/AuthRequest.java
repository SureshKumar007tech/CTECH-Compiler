package com.ctechcompiler.backend.dto;

public class AuthRequest {
    private String username;
    private String password;
    private String name; // optional for register

    public AuthRequest() {}
    public String getUsername() { return username; }
    public void setUsername(String u) { username = u; }
    public String getPassword() { return password; }
    public void setPassword(String p) { password = p; }
    public String getName() { return name; }
    public void setName(String n) { name = n; }
}
