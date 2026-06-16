package com.hms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.sql.PreparedStatement;
import java.util.*;

@Controller
@RequestMapping("/billing")
public class BillingController {

    @Autowired
    private JdbcTemplate jdbc;

    @GetMapping
    public String list(Model model) {
        var bills = jdbc.queryForList(
            "SELECT b.*,p.full_name AS patient_name FROM bills b " +
            "JOIN patients p ON b.patient_id=p.patient_id ORDER BY b.bill_date DESC"
        );
        model.addAttribute("bills", bills);
        return "billing";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("patients", jdbc.queryForList(
            "SELECT patient_id,full_name FROM patients WHERE is_active=TRUE ORDER BY full_name"
        ));
        return "bill-form";
    }

    @PostMapping("/save")
    public String save(@RequestParam Map<String,String> p,
                       @RequestParam List<String> descriptions,
                       @RequestParam List<String> categories,
                       @RequestParam List<Integer> quantities,
                       @RequestParam List<Double> unitPrices,
                       RedirectAttributes ra) {
        try {
            final int patientId = Integer.parseInt(p.get("patientId"));
            final double discount = Double.parseDouble(p.getOrDefault("discount","0"));

            double sub = 0;
            for (int i = 0; i < quantities.size(); i++) sub += quantities.get(i) * unitPrices.get(i);
            final double subtotal    = sub;
            final double tax         = Math.round(subtotal * 0.18 * 100.0) / 100.0;
            final double total       = Math.round((subtotal + tax - discount) * 100.0) / 100.0;

            KeyHolder key = new GeneratedKeyHolder();
            jdbc.update(con -> {
                PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO bills(patient_id,subtotal,discount,tax,total_amount,payment_status,payment_method) " +
                    "VALUES(?,?,?,?,?,'PENDING','CASH')",
                    new String[]{"bill_id"}
                );
                ps.setInt(1, patientId);
                ps.setDouble(2, subtotal);
                ps.setDouble(3, discount);
                ps.setDouble(4, tax);
                ps.setDouble(5, total);
                return ps;
            }, key);

            int billId = key.getKey().intValue();
            for (int i = 0; i < descriptions.size(); i++) {
                if (!descriptions.get(i).trim().isEmpty()) {
                    jdbc.update(
                        "INSERT INTO bill_items(bill_id,description,category,quantity,unit_price) VALUES(?,?,?,?,?)",
                        billId, descriptions.get(i), categories.get(i), quantities.get(i), unitPrices.get(i)
                    );
                }
            }
            ra.addFlashAttribute("success", "Bill #" + billId + " generated! Total: ₹" + total);
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/billing";
    }

    @GetMapping("/view/{id}")
    public String view(@PathVariable int id, Model model) {
        var bill  = jdbc.queryForMap(
            "SELECT b.*,p.full_name AS patient_name FROM bills b " +
            "JOIN patients p ON b.patient_id=p.patient_id WHERE b.bill_id=?", id
        );
        var items = jdbc.queryForList("SELECT * FROM bill_items WHERE bill_id=?", id);
        model.addAttribute("bill", bill);
        model.addAttribute("items", items);
        return "bill-view";
    }

    @PostMapping("/pay/{id}")
    public String recordPayment(@PathVariable int id,
                                @RequestParam double amount,
                                @RequestParam String method,
                                RedirectAttributes ra) {
        try {
            jdbc.update(
                "UPDATE bills SET paid_amount=paid_amount+?, payment_method=?, " +
                "payment_status=CASE WHEN paid_amount+? >= total_amount THEN 'PAID' ELSE 'PARTIAL' END " +
                "WHERE bill_id=?",
                amount, method, amount, id
            );
            ra.addFlashAttribute("success", "Payment of ₹" + amount + " recorded!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/billing/view/" + id;
    }
}
