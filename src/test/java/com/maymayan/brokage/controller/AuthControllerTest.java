package com.maymayan.brokage.controller;

import com.maymayan.brokage.dto.AuthRequest;
import com.maymayan.brokage.dto.UserJwtTokenModel;
import com.maymayan.brokage.logic.CustomerService;
import com.maymayan.brokage.logic.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private CustomerService userDetailsService;
    @Mock
    private AuthenticationManager authenticationManager;


    @Test
    void testLogin_success() {
        AuthRequest req = new AuthRequest();
        req.setUsername("user");
        req.setPassword("pass");
        var userToReturn = new User("user", "pass", List.of(new SimpleGrantedAuthority("user")));
        when(userDetailsService.loadUserByUsername("user")).thenReturn(userToReturn);
        when(jwtUtil.generateToken(userToReturn)).thenReturn("abc");
        var response = authController.login(req);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("abc", ((UserJwtTokenModel) response.getBody()).getToken());
    }

}
