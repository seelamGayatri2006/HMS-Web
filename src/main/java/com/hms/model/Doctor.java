package com.hms.model;
import java.time.LocalTime;

public class Doctor {
    private int doctorId; private Integer userId; private String fullName;
    private String specialisation; private String qualification; private String phone;
    private String email; private double consultationFee; private String availableDays;
    private LocalTime availableFrom; private LocalTime availableTo; private boolean isActive;

    public Doctor() {}
    public int getDoctorId()                         { return doctorId; }
    public void setDoctorId(int id)                  { this.doctorId = id; }
    public Integer getUserId()                       { return userId; }
    public void setUserId(Integer id)                { this.userId = id; }
    public String getFullName()                      { return fullName; }
    public void setFullName(String n)                { this.fullName = n; }
    public String getSpecialisation()                { return specialisation; }
    public void setSpecialisation(String s)          { this.specialisation = s; }
    public String getQualification()                 { return qualification; }
    public void setQualification(String q)           { this.qualification = q; }
    public String getPhone()                         { return phone; }
    public void setPhone(String p)                   { this.phone = p; }
    public String getEmail()                         { return email; }
    public void setEmail(String e)                   { this.email = e; }
    public double getConsultationFee()               { return consultationFee; }
    public void setConsultationFee(double f)         { this.consultationFee = f; }
    public String getAvailableDays()                 { return availableDays; }
    public void setAvailableDays(String d)           { this.availableDays = d; }
    public LocalTime getAvailableFrom()              { return availableFrom; }
    public void setAvailableFrom(LocalTime t)        { this.availableFrom = t; }
    public LocalTime getAvailableTo()                { return availableTo; }
    public void setAvailableTo(LocalTime t)          { this.availableTo = t; }
    public boolean isActive()                        { return isActive; }
    public void setActive(boolean a)                 { this.isActive = a; }
}
