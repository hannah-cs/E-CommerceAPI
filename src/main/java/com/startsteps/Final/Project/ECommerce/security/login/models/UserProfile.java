package com.startsteps.Final.Project.ECommerce.security.login.models;

public class UserProfile {
    private Integer userId;
    private String username;
    private String email;
    private String name;

    public UserProfile(Integer userId, String username, String email, String name) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.name = name;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString(){
        return "User ID: "+userId+
                "\nUsername: "+username+
                "\nEmail: "+email+
                "\nName: "+name;
    }
}
