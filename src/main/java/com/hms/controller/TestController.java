package com.hms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
public class TestController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/generate-hash")
    public String generateHash(@RequestParam String password) {
        String hash = passwordEncoder.encode(password);
        System.out.println("Hash for '" + password + "': " + hash);
        return "Hash: " + hash;
    }
}
