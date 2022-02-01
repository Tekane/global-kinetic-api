package com.example.globalkineticapi.controller;

import com.example.globalkineticapi.dto.LogOutRequest;
import com.example.globalkineticapi.dto.LoginRequest;
import com.example.globalkineticapi.dto.LoginResponse;
import com.example.globalkineticapi.event.OnUserLogoutSuccessEvent;
import com.example.globalkineticapi.model.User;
import com.example.globalkineticapi.security.JwtProvider;
import com.example.globalkineticapi.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.jwk.JwkTokenStore;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private AuthenticationManager authenticationManager;

    private PasswordEncoder encoder;

    private JwtProvider jwtProvider;

    private UserService userService;

    private ApplicationEventPublisher applicationEventPublisher;

    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, PasswordEncoder encoder, JwtProvider jwtProvider, ApplicationEventPublisher applicationEventPublisher, UserDetailsServiceImpl userDetailsService, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.encoder = encoder;
        this.jwtProvider = jwtProvider;
        this.applicationEventPublisher = applicationEventPublisher;
        this.userDetailsService = userDetailsService;
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody User signUpRequest) {

        if (userService.existsByUsername(signUpRequest.getUsername())) {
            return new ResponseEntity<String>("Fail -> Email is already in use!",
                    HttpStatus.BAD_REQUEST);
        }
        // Creating user's account
        userService.createUser(signUpRequest);
        return ResponseEntity.ok("User registered successfully");

    }

    @PostMapping(value = "/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest userRequestBody) {
        User user = (User) userDetailsService.loadUserByUsername(userRequestBody.getUsername());
        if (user.getActive()) {
            Authentication authentication = getAuthentication(user);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwtToken = jwtProvider.generateJwtToken(user);
            return ResponseEntity.ok(new LoginResponse(String.valueOf(user.getId()), jwtToken));
        }

        return ResponseEntity.badRequest().body(new LoginResponse(null, "User has been deactivated/locked !!"));
    }

    @PostMapping(value = "/logout/{id}")
    public ResponseEntity<Void> logoutUser(@CurrentUser User currentUser, @RequestBody LogOutRequest logOutRequest, @PathVariable("id") Long id) {
        Authentication authentication = getAuthentication(currentUser);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        OnUserLogoutSuccessEvent logoutSuccessEvent = new
                OnUserLogoutSuccessEvent(currentUser.getUsername(), logOutRequest.getToken(), logOutRequest);
        applicationEventPublisher.publishEvent(logoutSuccessEvent);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private Authentication getAuthentication(User user) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                user.getUsername(),
                user.getPassword()));
        return authentication;
    }


}
