package com.startsteps.Final.Project.ECommerce.Controller;

import com.startsteps.Final.Project.ECommerce.Models.User.User;
import com.startsteps.Final.Project.ECommerce.Models.User.UserRole;
import com.startsteps.Final.Project.ECommerce.Service.AuthService;
import com.startsteps.Final.Project.ECommerce.Service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/register") public class RegistrationController {
    private final UserService userService;
    private final AuthService authService;

    public RegistrationController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @PostMapping
    public ResponseEntity<String> registerUser(@RequestBody User newUser){
        if (newUser.getUserRole() == UserRole.USER) {
            userService.createUser(newUser);
            return new ResponseEntity<>("User created successfully", HttpStatus.CREATED);
        } else if (newUser.getUserRole() == UserRole.ADMIN){
            // TODO: admin validation
            return new ResponseEntity<>("Admin user created successfully", HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>("Invalid user role", HttpStatus.BAD_REQUEST);
        }
    }
}
