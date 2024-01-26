package com.startsteps.Final.Project.ECommerce.security.login.payload.response;

import java.util.List;

public class UserInfoResponse {
    private Integer id;
    private String username;
    private String email;
    private final List<String> roles;

    public UserInfoResponse(Integer id, String username, String email, List<String> roles) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getRoles() {
        return roles;
    }
}
