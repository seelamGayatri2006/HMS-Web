package com.hms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.*;

@Controller
public class ReportsAndUsersController {

    @Autowired private JdbcTemplate jdbc;
    @Autowired private PasswordEncoder passwordEncoder;

    // ---- Reports ----
    @GetMapping("/reports")
    public String reports(Model model) {
        int patients     = jdbc.queryForObject("SELECT COUNT(*) FROM patients WHERE is_active=TRUE", Integer.class);
        int doctors      = jdbc.queryForObject("SELECT COUNT(*) FROM doctors WHERE is_active=TRUE", Integer.class);
        int appointments = jdbc.queryForObject("SELECT COUNT(*) FROM appointments", Integer.class);
        Double revenue   = jdbc.queryForObject("SELECT COALESCE(SUM(paid_amount),0) FROM bills", Double.class);
        int scheduled    = jdbc.queryForObject("SELECT COUNT(*) FROM appointments WHERE status='SCHEDULED'", Integer.class);
        int completed    = jdbc.queryForObject("SELECT COUNT(*) FROM appointments WHERE status='COMPLETED'", Integer.class);
        int cancelled    = jdbc.queryForObject("SELECT COUNT(*) FROM appointments WHERE status='CANCELLED'", Integer.class);
        int paid         = jdbc.queryForObject("SELECT COUNT(*) FROM bills WHERE payment_status='PAID'", Integer.class);
        int pending      = jdbc.queryForObject("SELECT COUNT(*) FROM bills WHERE payment_status='PENDING'", Integer.class);
        int lowStock     = jdbc.queryForObject("SELECT COUNT(*) FROM medicines WHERE is_active=TRUE AND stock_quantity<=reorder_level", Integer.class);

        model.addAttribute("totalPatients", patients);
        model.addAttribute("totalDoctors", doctors);
        model.addAttribute("totalAppointments", appointments);
        model.addAttribute("totalRevenue", String.format("%.2f", revenue));
        model.addAttribute("scheduled", scheduled);
        model.addAttribute("completed", completed);
        model.addAttribute("cancelled", cancelled);
        model.addAttribute("paid", paid);
        model.addAttribute("pending", pending);
        model.addAttribute("lowStock", lowStock);
        return "reports";
    }

    // ---- Users (Admin only) ----
    @GetMapping("/users")
    public String users(Model model) {
        model.addAttribute("users", jdbc.queryForList("SELECT * FROM users WHERE is_active=TRUE ORDER BY role,full_name"));
        return "users";
    }

    @PostMapping("/users/save")
    public String saveUser(@RequestParam Map<String,String> p, RedirectAttributes ra) {
        try {
            String id = p.get("userId");
            if (id == null || id.isEmpty()) {
                String hash = passwordEncoder.encode(p.get("password"));
                jdbc.update(
                    "INSERT INTO users(username,password_hash,role,full_name,email) VALUES(?,?,?,?,?)",
                    p.get("username"), hash, p.get("role"), p.get("fullName"), p.get("email")
                );
                ra.addFlashAttribute("success","User '"+p.get("username")+"' created!");
            } else {
                jdbc.update(
                    "UPDATE users SET full_name=?,username=?,email=?,role=? WHERE user_id=?",
                    p.get("fullName"), p.get("username"), p.get("email"), p.get("role"), Integer.parseInt(id)
                );
                ra.addFlashAttribute("success","User updated!");
            }
        } catch (Exception e) { ra.addFlashAttribute("error","Error: "+e.getMessage()); }
        return "redirect:/users";
    }

    @PostMapping("/users/password/{id}")
    public String changePassword(@PathVariable int id, @RequestParam String newPassword, RedirectAttributes ra) {
        try {
            String hash = passwordEncoder.encode(newPassword);
            jdbc.update("UPDATE users SET password_hash=? WHERE user_id=?", hash, id);
            ra.addFlashAttribute("success","Password changed!");
        } catch (Exception e) { ra.addFlashAttribute("error","Error: "+e.getMessage()); }
        return "redirect:/users";
    }

    @GetMapping("/users/deactivate/{id}")
    public String deactivate(@PathVariable int id, RedirectAttributes ra) {
        jdbc.update("UPDATE users SET is_active=FALSE WHERE user_id=?", id);
        ra.addFlashAttribute("success","User deactivated.");
        return "redirect:/users";
    }
}
