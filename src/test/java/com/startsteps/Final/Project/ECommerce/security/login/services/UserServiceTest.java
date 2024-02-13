package com.startsteps.Final.Project.ECommerce.security.login.services;

import com.startsteps.Final.Project.ECommerce.security.login.models.ERole;
import com.startsteps.Final.Project.ECommerce.security.login.models.Role;
import com.startsteps.Final.Project.ECommerce.security.login.models.User;
import com.startsteps.Final.Project.ECommerce.security.login.repository.RoleRepository;
import com.startsteps.Final.Project.ECommerce.security.login.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void setERoleAndRoles_ValidUserIdAndERole_CorrectRolesSet() {
        Integer userId = 1;
        ERole eRole = ERole.ADMIN;
        User user = new User();
        user.setUserId(userId);
        user.setERole(ERole.USER);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(roleRepository.findByName(eRole)).thenReturn(Optional.of(new Role(eRole)));

        userService.setERoleAndRoles(userId, eRole);

        assertEquals(eRole, user.getERole());
        assertEquals(1, user.getRoles().size());
        verify(userRepository, times(1)).save(user);
    }


    @Test
    void isAdmin_AdminUser_ReturnsTrue() {
        String username = "admin";
        User user = new User();
        user.setUsername(username);
        user.setERole(ERole.ADMIN);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        boolean result = userService.isAdmin(username);

        assertEquals(true, result);
    }

    @Test
    void makeAdmin_UserNotAdmin_MakesUserAdmin() {
        Integer userId = 1;
        User user = new User();
        user.setUserId(userId);
        user.setERole(ERole.USER);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.makeAdmin(userId);

        assertEquals(ERole.ADMIN, user.getERole());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void makeAdmin_UserAlreadyAdmin_DoesNotMakeUserAdmin() {
        Integer userId = 1;
        User user = new User();
        user.setUserId(userId);
        user.setERole(ERole.ADMIN);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.makeAdmin(userId);

        assertEquals(ERole.ADMIN, user.getERole());
        verify(userRepository, never()).save(user);
    }


    @Test
    void getUserProfile_UserProfileFound_ReturnsUserProfile() {
        String username = "testuser";
        User user = new User();
        user.setUserId(1);
        user.setName("Test User");
        user.setUsername(username);
        user.setEmail("test@example.com");
        Role role = new Role();
        role.setName(ERole.USER);
        user.setRoles(Collections.singleton(role));
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        ResponseEntity<?> responseEntity = userService.getUserProfile(username);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertTrue(responseEntity.getBody() instanceof Map);

        Map<String, Object> userProfile = (Map<String, Object>) responseEntity.getBody();
        assertEquals(user.getUserId(), userProfile.get("userId"));
        assertEquals(user.getName(), userProfile.get("name"));
        assertEquals(user.getUsername(), userProfile.get("username"));
        assertEquals(user.getEmail(), userProfile.get("email"));
        assertTrue(userProfile.containsKey("roles"));
        assertTrue(userProfile.get("roles") instanceof Set);
        assertEquals(Collections.singleton(ERole.USER.name()), userProfile.get("roles"));
    }
}