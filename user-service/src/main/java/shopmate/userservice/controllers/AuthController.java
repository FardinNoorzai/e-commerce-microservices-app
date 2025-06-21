package shopmate.userservice.controllers;


import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import shopmate.userservice.dto.AuthRequest;
import shopmate.userservice.dto.LoginResponse;
import shopmate.userservice.models.User;
import shopmate.userservice.repositories.UserRepository;
import shopmate.userservice.services.CustomUserDetailsService;
import shopmate.userservice.utils.JwtUtil;

import java.util.Map;


@RestController
@RequestMapping("/api/users/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    CustomUserDetailsService customUserDetailsService;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserRepository userRepository, PasswordEncoder passwordEncoder, CustomUserDetailsService customUserDetailsService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.customUserDetailsService = customUserDetailsService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, LoginResponse>> login(@RequestBody AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );
        UserDetails user = (UserDetails) authentication.getPrincipal();
        User user1 = customUserDetailsService.findByUsername(user.getUsername());
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setFirstName(user1.getFirstName());
        loginResponse.setLastName(user1.getLastName());
        loginResponse.setUsername(user.getUsername());
        loginResponse.setToken(jwtUtil.generateToken(user));
        return ResponseEntity.ok(Map.of("user", loginResponse));
    }

    @PostMapping("me")
    public ResponseEntity<Map<String,LoginResponse>> me(Authentication authentication) {
        User user = customUserDetailsService.findByUsername(authentication.getName());
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setFirstName(user.getFirstName());
        loginResponse.setLastName(user.getLastName());
        loginResponse.setUsername(user.getUsername());
        loginResponse.setToken(jwtUtil.generateToken(user));
        return ResponseEntity.ok(Map.of("user", loginResponse));
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody AuthRequest request) {
        if (userRepository.findByUsername(request.username()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists");
        }
        User user = new User();
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setRole("ROLE_USER");
        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/validate")
    public ResponseEntity<String> validate(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        if (jwtUtil.validateToken(token)) {
            return ResponseEntity.ok("true");
        }
        return ResponseEntity.badRequest().body("false");
    }
}