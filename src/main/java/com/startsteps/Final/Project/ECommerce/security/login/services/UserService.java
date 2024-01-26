package com.startsteps.Final.Project.ECommerce.security.login.services;

import com.startsteps.Final.Project.ECommerce.security.login.models.ERole;
import com.startsteps.Final.Project.ECommerce.security.login.models.Role;
import com.startsteps.Final.Project.ECommerce.security.login.models.User;
import com.startsteps.Final.Project.ECommerce.security.login.repository.RoleRepository;
import com.startsteps.Final.Project.ECommerce.security.login.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

}
