package com.hms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.sql.Date;
import java.util.*;

@Controller
@RequestMapping("/patients")
public class PatientController {

    @Autowired
    private JdbcTemplate jdbc;

    @GetMapping
    public String list(@RequestParam(required=false) String search, Model model) {
        List<Map<String,Object>> patients;
        if (search != null && !search.isEmpty()) {
            patients = jdbc.queryForList(
                "SELECT * FROM patients WHERE is_active=TRUE AND (full_name LIKE ? OR phone LIKE ?) ORDER BY full_name",
                "%"+search+"%", "%"+search+"%"
            );
        } else {
            patients = jdbc.queryForList(
                "SELECT * FROM patients WHERE is_active=TRUE ORDER BY full_name"
            );
        }
        model.addAttribute("patients", patients);
        model.addAttribute("search", search);
        return "patients";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        // Send null so template knows it's Add mode
        model.addAttribute("patient", null);
        return "patient-form";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable int id, Model model) {
        Map<String,Object> p = jdbc.queryForMap(
            "SELECT * FROM patients WHERE patient_id=?", id
        );
        model.addAttribute("patient", p);
        return "patient-form";
    }

    @PostMapping("/save")
    public String save(@RequestParam Map<String,String> params, RedirectAttributes ra) {
        try {
            String id = params.get("patientId");
            String dob = params.get("dob");
            Date dobDate = (dob != null && !dob.isEmpty()) ? Date.valueOf(dob) : null;

            if (id == null || id.isEmpty()) {
                jdbc.update(
                    "INSERT INTO patients (full_name,dob,gender,blood_group,phone,email,address,emergency_contact) VALUES(?,?,?,?,?,?,?,?)",
                    params.get("fullName"), dobDate,
                    params.get("gender"), params.get("bloodGroup"),
                    params.get("phone"), params.get("email"),
                    params.get("address"), params.get("emergencyContact")
                );
                ra.addFlashAttribute("success", "Patient added successfully!");
            } else {
                jdbc.update(
                    "UPDATE patients SET full_name=?,dob=?,gender=?,blood_group=?,phone=?,email=?,address=?,emergency_contact=? WHERE patient_id=?",
                    params.get("fullName"), dobDate,
                    params.get("gender"), params.get("bloodGroup"),
                    params.get("phone"), params.get("email"),
                    params.get("address"), params.get("emergencyContact"),
                    Integer.parseInt(id)
                );
                ra.addFlashAttribute("success", "Patient updated successfully!");
            }
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/patients";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable int id, RedirectAttributes ra) {
        jdbc.update("UPDATE patients SET is_active=FALSE WHERE patient_id=?", id);
        ra.addFlashAttribute("success", "Patient deleted.");
        return "redirect:/patients";
    }
}
