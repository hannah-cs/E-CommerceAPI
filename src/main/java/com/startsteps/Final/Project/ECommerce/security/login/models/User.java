package com.startsteps.Final.Project.ECommerce.security.login.models;

import com.startsteps.Final.Project.ECommerce.Models.User.UserRole;
import com.startsteps.Final.Project.ECommerce.security.login.models.ERole;
import com.startsteps.Final.Project.ECommerce.security.login.models.Role;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "userId"),
                @UniqueConstraint(columnNames = "email")
        })

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer userId;
    private String email;
    private String name;
    private String password;
    List<String> allOrders;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(  name = "user_roles",
            joinColumns = @JoinColumn(name = "userId"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();
    @Enumerated(EnumType.STRING)
    private ERole userRole;


    public User() {
    }

    public User(String email, String name, String password, ERole eRole) {
        this.email = email;
        this.name = name;
        this.password = password;
        Role role = new Role(eRole);
        this.roles.add(role);
        this.userRole = eRole;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getAllOrders() {
        return allOrders;
    }

    public void setAllOrders(List<String> allOrders) {
        this.allOrders = allOrders;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public ERole getUserRole() {
        return userRole;
    }

    public void setUserRole(ERole userRole) {
        this.userRole = userRole;
    }
}
