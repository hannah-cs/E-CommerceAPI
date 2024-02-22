package com.startsteps.Final.Project.ECommerce.security.login.models;

import java.util.HashSet;
import java.util.Set;

public class UserProfile {
    private Integer userId;
    private String username;
    private String email;
    private ERole eRole;

    public UserProfile(Integer userId, String username, String email, ERole eRole) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.eRole = eRole;
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

    public ERole geteRole() {
        return eRole;
    }

    public void seteRole(ERole eRole) {
        this.eRole = eRole;
    }

    @Override
    public String toString(){
        return "User ID: "+userId+
                "\nUsername: "+username+
                "\nEmail: "+email+
                "\nRoles: "+geteRole();
    }
}
