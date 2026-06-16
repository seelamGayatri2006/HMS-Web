package com.hms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class HmsUserDetailsService implements UserDetailsService {

    @Autowired
    private JdbcTemplate jdbc;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("=== Loading user: " + username + " ===");

        List<Map<String, Object>> rows = jdbc.queryForList(
            "SELECT * FROM users WHERE username = ? AND is_active = TRUE", username
        );

        if (rows.isEmpty()) {
            System.out.println("=== User NOT found: " + username + " ===");
            throw new UsernameNotFoundException("User not found: " + username);
        }

        Map<String, Object> user = rows.get(0);
        String storedHash = (String) user.get("password_hash");
        String role       = (String) user.get("role");

        System.out.println("=== Found user: " + username + " | role: " + role + " | hash length: " + storedHash.length() + " ===");

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role));

        return new org.springframework.security.core.userdetails.User(
            username,
            storedHash,
            true, true, true, true,
            authorities
        );
    }
}
