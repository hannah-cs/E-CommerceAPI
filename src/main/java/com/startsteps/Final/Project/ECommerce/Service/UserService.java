package com.startsteps.Final.Project.ECommerce.Service;

import com.startsteps.Final.Project.ECommerce.Models.User.User;
import com.startsteps.Final.Project.ECommerce.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public User getUserById(Integer id){
        Optional<User> user = userRepository.findById(id);
        return user.get();
    }

    public void createUser(User newUser){
        userRepository.save(newUser);
    }

    public boolean authenticateUser(Integer userId, String password) {
        Optional<User> userOptional =  userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return user.getPassword().equals(password);
        }
        return false;
    }

}
