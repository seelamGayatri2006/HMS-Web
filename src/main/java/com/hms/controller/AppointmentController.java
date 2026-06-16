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
@RequestMapping("/appointments")
public class AppointmentController {

    @Autowired
    private JdbcTemplate jdbc;

    @GetMapping
    public String list(Model model) {
        var appts = jdbc.queryForList(
            "SELECT a.*,p.full_name AS patient_name,d.full_name AS doctor_name,d.specialisation " +
            "FROM appointments a JOIN patients p ON a.patient_id=p.patient_id " +
            "JOIN doctors d ON a.doctor_id=d.doctor_id ORDER BY a.appt_date DESC,a.appt_time"
        );
        model.addAttribute("appointments", appts);
        return "appointments";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("patients", jdbc.queryForList("SELECT patient_id,full_name FROM patients WHERE is_active=TRUE ORDER BY full_name"));
        model.addAttribute("doctors",  jdbc.queryForList("SELECT doctor_id,full_name,specialisation FROM doctors WHERE is_active=TRUE ORDER BY full_name"));
        return "appointment-form";
    }

    @PostMapping("/save")
    public String save(@RequestParam Map<String,String> p, RedirectAttributes ra) {
        try {
            // Check slot conflict
            int conflict = jdbc.queryForObject(
                "SELECT COUNT(*) FROM appointments WHERE doctor_id=? AND appt_date=? AND appt_time=? AND status='SCHEDULED'",
                Integer.class, Integer.parseInt(p.get("doctorId")), Date.valueOf(p.get("apptDate")), p.get("apptTime")+":00"
            );
            if (conflict > 0) { ra.addFlashAttribute("error","This time slot is already booked!"); return "redirect:/appointments/add"; }

            jdbc.update(
                "INSERT INTO appointments(patient_id,doctor_id,appt_date,appt_time,reason,status) VALUES(?,?,?,?,?,'SCHEDULED')",
                Integer.parseInt(p.get("patientId")), Integer.parseInt(p.get("doctorId")),
                Date.valueOf(p.get("apptDate")), p.get("apptTime")+":00", p.get("reason")
            );
            ra.addFlashAttribute("success","Appointment booked successfully!");
        } catch (Exception e) { ra.addFlashAttribute("error","Error: "+e.getMessage()); }
        return "redirect:/appointments";
    }

    @GetMapping("/cancel/{id}")
    public String cancel(@PathVariable int id, RedirectAttributes ra) {
        jdbc.update("UPDATE appointments SET status='CANCELLED' WHERE appointment_id=?", id);
        ra.addFlashAttribute("success","Appointment cancelled.");
        return "redirect:/appointments";
    }

    @GetMapping("/complete/{id}")
    public String complete(@PathVariable int id, RedirectAttributes ra) {
        jdbc.update("UPDATE appointments SET status='COMPLETED' WHERE appointment_id=?", id);
        ra.addFlashAttribute("success","Appointment marked as completed.");
        return "redirect:/appointments";
    }
}
