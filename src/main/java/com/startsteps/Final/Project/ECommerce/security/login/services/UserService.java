package com.startsteps.Final.Project.ECommerce.security.login.services;

import com.startsteps.Final.Project.ECommerce.security.login.models.ERole;
import com.startsteps.Final.Project.ECommerce.security.login.models.Role;
import com.startsteps.Final.Project.ECommerce.security.login.models.User;
import com.startsteps.Final.Project.ECommerce.security.login.models.UserProfile;
import com.startsteps.Final.Project.ECommerce.security.login.repository.RoleRepository;
import com.startsteps.Final.Project.ECommerce.security.login.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    public void setERoleAndRoles(Integer userId, ERole eRole) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            user.setERole(eRole);
            Role correspondingRole = roleRepository.findByName(eRole).orElse(null);
            if (correspondingRole != null) {
                user.getRoles().add(correspondingRole);
            }
            userRepository.save(user);
        }
    }

    public boolean isAdmin(String username){
        User user = userRepository.findByUsername(username).orElse(null);
        return user != null && user.getERole().equals(ERole.ADMIN);
    }

    public void makeAdmin(Integer userId){
        User user = userRepository.findById(userId).orElse(null);
        if (!user.getERole().equals(ERole.ADMIN)){
            setERoleAndRoles(userId, ERole.ADMIN);
        }
    }

    public ResponseEntity<?> getUserProfile(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            UserProfile userProfile = new UserProfile(user.getUserId(), user.getUsername(), user.getEmail(), user.getName());
            return ResponseEntity.ok().body("User profile. \n"+userProfile.toString());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found");
        }
    }

}
