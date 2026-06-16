package com.hms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@Controller
public class DashboardController {

    @Autowired
    private JdbcTemplate jdbc;

    @GetMapping("/login")
    public String login() { return "login"; }

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {
        // Stats
        int patients     = jdbc.queryForObject("SELECT COUNT(*) FROM patients WHERE is_active=TRUE", Integer.class);
        int doctors      = jdbc.queryForObject("SELECT COUNT(*) FROM doctors WHERE is_active=TRUE", Integer.class);
        int appointments = jdbc.queryForObject("SELECT COUNT(*) FROM appointments", Integer.class);
        int rooms        = jdbc.queryForObject("SELECT COUNT(*) FROM rooms WHERE status='AVAILABLE'", Integer.class);
        Double revenue   = jdbc.queryForObject("SELECT COALESCE(SUM(paid_amount),0) FROM bills", Double.class);

        model.addAttribute("totalPatients", patients);
        model.addAttribute("totalDoctors", doctors);
        model.addAttribute("totalAppointments", appointments);
        model.addAttribute("availableRooms", rooms);
        model.addAttribute("totalRevenue", String.format("%.0f", revenue));

        // Today's appointments
        var todayAppts = jdbc.queryForList(
            "SELECT a.appointment_id, p.full_name as patient_name, d.full_name as doctor_name, " +
            "a.appt_time, a.status FROM appointments a " +
            "JOIN patients p ON a.patient_id=p.patient_id " +
            "JOIN doctors d ON a.doctor_id=d.doctor_id " +
            "WHERE a.appt_date=CURDATE() ORDER BY a.appt_time"
        );
        model.addAttribute("todayAppointments", todayAppts);

        return "dashboard";
    }
}
