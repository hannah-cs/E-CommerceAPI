package com.startsteps.Final.Project.ECommerce.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.startsteps.Final.Project.ECommerce.Config.JwtTokenProvider;

import javax.naming.AuthenticationException;

@Service
public class AuthService {
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public AuthService(UserService userService, JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public String login(Integer userId, String password) throws AuthenticationException {
        boolean isAuthenticated = userService.authenticateUser(userId, password);
        if (isAuthenticated){
            return jwtTokenProvider.generateToken(userId);
        } else {
            throw new AuthenticationException("Invalid credentials");
        }
    }
}
