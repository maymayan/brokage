package com.maymayan.brokage.controller;

import com.maymayan.brokage.dto.AuthRequest;
import com.maymayan.brokage.dto.UserJwtTokenModel;
import com.maymayan.brokage.logic.CustomerService;
import com.maymayan.brokage.logic.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomerService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        final String jwt = jwtUtil.generateToken(userDetails);
        String role = null;
        if (userDetails.getAuthorities() != null && !userDetails.getAuthorities().isEmpty()) {
            role = userDetails.getAuthorities().stream().toList().get(0).getAuthority();
        }

        return ResponseEntity.ok(new UserJwtTokenModel(jwt, role));
    }
}


