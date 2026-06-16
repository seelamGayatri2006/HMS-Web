package com.hms.model;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Patient {
    public enum Gender { MALE, FEMALE, OTHER }
    private int patientId; private String fullName; private LocalDate dob;
    private Gender gender; private String bloodGroup; private String phone;
    private String email; private String address; private String emergencyContact;
    private LocalDateTime registeredOn; private boolean isActive;

    public Patient() {}
    public int getPatientId()                        { return patientId; }
    public void setPatientId(int id)                 { this.patientId = id; }
    public String getFullName()                      { return fullName; }
    public void setFullName(String n)                { this.fullName = n; }
    public LocalDate getDob()                        { return dob; }
    public void setDob(LocalDate d)                  { this.dob = d; }
    public Gender getGender()                        { return gender; }
    public void setGender(Gender g)                  { this.gender = g; }
    public String getBloodGroup()                    { return bloodGroup; }
    public void setBloodGroup(String b)              { this.bloodGroup = b; }
    public String getPhone()                         { return phone; }
    public void setPhone(String p)                   { this.phone = p; }
    public String getEmail()                         { return email; }
    public void setEmail(String e)                   { this.email = e; }
    public String getAddress()                       { return address; }
    public void setAddress(String a)                 { this.address = a; }
    public String getEmergencyContact()              { return emergencyContact; }
    public void setEmergencyContact(String ec)       { this.emergencyContact = ec; }
    public LocalDateTime getRegisteredOn()           { return registeredOn; }
    public void setRegisteredOn(LocalDateTime t)     { this.registeredOn = t; }
    public boolean isActive()                        { return isActive; }
    public void setActive(boolean a)                 { this.isActive = a; }
    public int getAge() { return dob == null ? 0 : LocalDate.now().getYear() - dob.getYear(); }
}
