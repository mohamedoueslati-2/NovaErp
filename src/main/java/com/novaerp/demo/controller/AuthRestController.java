package com.novaerp.demo.controller;

import com.novaerp.demo.config.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthRestController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    // ✅ LOGIN JWT
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> data) {

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        data.get("email"),
                        data.get("password")
                )
        );

        UserDetails user = (UserDetails) auth.getPrincipal();
        String token = jwtUtil.generateToken(user);

        return ResponseEntity.ok(Map.of("token", token));
    }

    // 🔐 TEST JWT
    @GetMapping("/profile")
    public ResponseEntity<?> profile(Authentication auth) {
        return ResponseEntity.ok(Map.of(
                "username", auth.getName(),
                "roles", auth.getAuthorities()
        ));
    }
}
