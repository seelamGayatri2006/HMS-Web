package com.hms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.*;

@Controller
@RequestMapping("/rooms")
public class RoomController {

    @Autowired
    private JdbcTemplate jdbc;

    @GetMapping
    public String list(Model model) {
        var rooms = jdbc.queryForList(
            "SELECT r.*,p.full_name AS patient_name FROM rooms r " +
            "LEFT JOIN patients p ON r.current_patient_id=p.patient_id ORDER BY r.room_number"
        );
        model.addAttribute("rooms", rooms);
        model.addAttribute("availableCount", jdbc.queryForObject("SELECT COUNT(*) FROM rooms WHERE status='AVAILABLE'", Integer.class));
        model.addAttribute("occupiedCount",  jdbc.queryForObject("SELECT COUNT(*) FROM rooms WHERE status='OCCUPIED'", Integer.class));
        return "rooms";
    }

    @GetMapping("/add")
    public String addForm() { return "room-form"; }

    @PostMapping("/add")
    public String addRoom(@RequestParam Map<String,String> p, RedirectAttributes ra) {
        try {
            jdbc.update(
                "INSERT INTO rooms(room_number,room_type,floor,price_per_day) VALUES(?,?,?,?)",
                p.get("roomNumber"),p.get("roomType"),Integer.parseInt(p.get("floor")),Double.parseDouble(p.get("pricePerDay"))
            );
            ra.addFlashAttribute("success","Room added!");
        } catch (Exception e) { ra.addFlashAttribute("error","Error: "+e.getMessage()); }
        return "redirect:/rooms";
    }

    @GetMapping("/assign/{id}")
    public String assignForm(@PathVariable int id, Model model) {
        model.addAttribute("room", jdbc.queryForMap("SELECT * FROM rooms WHERE room_id=?", id));
        model.addAttribute("patients", jdbc.queryForList("SELECT patient_id,full_name FROM patients WHERE is_active=TRUE ORDER BY full_name"));
        return "room-assign";
    }

    @PostMapping("/assign/{id}")
    public String assign(@PathVariable int id, @RequestParam int patientId, RedirectAttributes ra) {
        try {
            jdbc.update("UPDATE rooms SET status='OCCUPIED',current_patient_id=?,admitted_at=NOW() WHERE room_id=?", patientId, id);
            ra.addFlashAttribute("success","Patient assigned to room!");
        } catch (Exception e) { ra.addFlashAttribute("error","Error: "+e.getMessage()); }
        return "redirect:/rooms";
    }

    @GetMapping("/discharge/{id}")
    public String discharge(@PathVariable int id, RedirectAttributes ra) {
        jdbc.update("UPDATE rooms SET status='AVAILABLE',current_patient_id=NULL,admitted_at=NULL WHERE room_id=?", id);
        ra.addFlashAttribute("success","Patient discharged!");
        return "redirect:/rooms";
    }
}
