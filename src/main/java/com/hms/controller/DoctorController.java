package com.hms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.sql.Time;
import java.util.*;

@Controller
@RequestMapping("/doctors")
public class DoctorController {

    @Autowired
    private JdbcTemplate jdbc;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("doctors", jdbc.queryForList("SELECT * FROM doctors WHERE is_active=TRUE ORDER BY full_name"));
        return "doctors";
    }

    @GetMapping("/add")
    public String addForm() { return "doctor-form"; }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable int id, Model model) {
        model.addAttribute("doctor", jdbc.queryForMap("SELECT * FROM doctors WHERE doctor_id=?", id));
        return "doctor-form";
    }

    @PostMapping("/save")
    public String save(@RequestParam Map<String,String> p, RedirectAttributes ra) {
        try {
            String id = p.get("doctorId");
            if (id == null || id.isEmpty()) {
                jdbc.update(
                    "INSERT INTO doctors(full_name,specialisation,qualification,phone,email,consultation_fee,available_days,available_from,available_to) VALUES(?,?,?,?,?,?,?,?,?)",
                    p.get("fullName"),p.get("specialisation"),p.get("qualification"),p.get("phone"),p.get("email"),
                    Double.parseDouble(p.get("consultationFee")),p.get("availableDays"),
                    p.get("availableFrom"),p.get("availableTo")
                );
                ra.addFlashAttribute("success","Doctor added!");
            } else {
                jdbc.update(
                    "UPDATE doctors SET full_name=?,specialisation=?,qualification=?,phone=?,email=?,consultation_fee=?,available_days=?,available_from=?,available_to=? WHERE doctor_id=?",
                    p.get("fullName"),p.get("specialisation"),p.get("qualification"),p.get("phone"),p.get("email"),
                    Double.parseDouble(p.get("consultationFee")),p.get("availableDays"),
                    p.get("availableFrom"),p.get("availableTo"),Integer.parseInt(id)
                );
                ra.addFlashAttribute("success","Doctor updated!");
            }
        } catch (Exception e) { ra.addFlashAttribute("error","Error: "+e.getMessage()); }
        return "redirect:/doctors";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable int id, RedirectAttributes ra) {
        jdbc.update("UPDATE doctors SET is_active=FALSE WHERE doctor_id=?", id);
        ra.addFlashAttribute("success","Doctor deactivated.");
        return "redirect:/doctors";
    }
}
