package com.hms.model;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Appointment {
    public enum Status { SCHEDULED, COMPLETED, CANCELLED, NO_SHOW }
    private int appointmentId; private int patientId; private int doctorId;
    private LocalDate apptDate; private LocalTime apptTime; private String reason;
    private Status status; private String notes; private Integer bookedBy;
    private LocalDateTime bookedAt; private String patientName; private String doctorName; private String specialisation;

    public Appointment() {}
    public int getAppointmentId()                    { return appointmentId; }
    public void setAppointmentId(int id)             { this.appointmentId = id; }
    public int getPatientId()                        { return patientId; }
    public void setPatientId(int id)                 { this.patientId = id; }
    public int getDoctorId()                         { return doctorId; }
    public void setDoctorId(int id)                  { this.doctorId = id; }
    public LocalDate getApptDate()                   { return apptDate; }
    public void setApptDate(LocalDate d)             { this.apptDate = d; }
    public LocalTime getApptTime()                   { return apptTime; }
    public void setApptTime(LocalTime t)             { this.apptTime = t; }
    public String getReason()                        { return reason; }
    public void setReason(String r)                  { this.reason = r; }
    public Status getStatus()                        { return status; }
    public void setStatus(Status s)                  { this.status = s; }
    public String getNotes()                         { return notes; }
    public void setNotes(String n)                   { this.notes = n; }
    public Integer getBookedBy()                     { return bookedBy; }
    public void setBookedBy(Integer b)               { this.bookedBy = b; }
    public LocalDateTime getBookedAt()               { return bookedAt; }
    public void setBookedAt(LocalDateTime t)         { this.bookedAt = t; }
    public String getPatientName()                   { return patientName; }
    public void setPatientName(String n)             { this.patientName = n; }
    public String getDoctorName()                    { return doctorName; }
    public void setDoctorName(String n)              { this.doctorName = n; }
    public String getSpecialisation()                { return specialisation; }
    public void setSpecialisation(String s)          { this.specialisation = s; }
}
