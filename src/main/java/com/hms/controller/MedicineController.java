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
@RequestMapping("/medicines")
public class MedicineController {

    @Autowired
    private JdbcTemplate jdbc;

    @GetMapping
    public String list(@RequestParam(required=false) String filter, Model model) {
        List<Map<String,Object>> medicines;
        if ("low".equals(filter)) {
            medicines = jdbc.queryForList("SELECT * FROM medicines WHERE is_active=TRUE AND stock_quantity<=reorder_level ORDER BY stock_quantity");
        } else {
            medicines = jdbc.queryForList("SELECT * FROM medicines WHERE is_active=TRUE ORDER BY name");
        }
        model.addAttribute("medicines", medicines);
        model.addAttribute("filter", filter);
        return "medicines";
    }

    @GetMapping("/add")
    public String addForm() { return "medicine-form"; }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable int id, Model model) {
        model.addAttribute("medicine", jdbc.queryForMap("SELECT * FROM medicines WHERE medicine_id=?", id));
        return "medicine-form";
    }

    @PostMapping("/save")
    public String save(@RequestParam Map<String,String> p, RedirectAttributes ra) {
        try {
            String id = p.get("medicineId");
            Date expiry = p.get("expiryDate").isEmpty() ? null : Date.valueOf(p.get("expiryDate"));
            if (id == null || id.isEmpty()) {
                jdbc.update(
                    "INSERT INTO medicines(name,category,manufacturer,unit_price,stock_quantity,reorder_level,expiry_date) VALUES(?,?,?,?,?,?,?)",
                    p.get("name"),p.get("category"),p.get("manufacturer"),
                    Double.parseDouble(p.get("unitPrice")),Integer.parseInt(p.get("stockQuantity")),
                    Integer.parseInt(p.get("reorderLevel")),expiry
                );
                ra.addFlashAttribute("success","Medicine added!");
            } else {
                jdbc.update(
                    "UPDATE medicines SET name=?,category=?,manufacturer=?,unit_price=?,stock_quantity=?,reorder_level=?,expiry_date=? WHERE medicine_id=?",
                    p.get("name"),p.get("category"),p.get("manufacturer"),
                    Double.parseDouble(p.get("unitPrice")),Integer.parseInt(p.get("stockQuantity")),
                    Integer.parseInt(p.get("reorderLevel")),expiry,Integer.parseInt(id)
                );
                ra.addFlashAttribute("success","Medicine updated!");
            }
        } catch (Exception e) { ra.addFlashAttribute("error","Error: "+e.getMessage()); }
        return "redirect:/medicines";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable int id, RedirectAttributes ra) {
        jdbc.update("UPDATE medicines SET is_active=FALSE WHERE medicine_id=?", id);
        ra.addFlashAttribute("success","Medicine removed.");
        return "redirect:/medicines";
    }
}
