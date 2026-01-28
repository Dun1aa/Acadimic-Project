
package project;

import java.sql.*; //مكتبه للقاعده الببانات
import java.time.LocalDateTime; //مكتبه للوقت والتاريخ الحالي  
import java.time.format.DateTimeFormatter;//مكتبه لتنسيق الوقت والتاريخ
import java.time.format.DateTimeParseException;//مكتبه لاخطاء التحويل الوقت التاريخ

public class Appointment {
//data
    private String appointmentId;
    private String patientId;
    private String doctorId;
    private String date;
    private String time;
    private String status;
//constr
    public Appointment(String appointmentId, String patientId, String doctorId, String date, String time, String status) {
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.date = date;
        this.time = time;
        this.status = status;
    }
//matod
//get
    public String getAppointmentId() { return appointmentId; }
    public String getPatientId() { return patientId; }
    public String getDoctorId() { return doctorId; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getStatus() { return status; }
    
//set لتحديث حاله الموعد
    public void setStatus(String status) { this.status = status; }

    //نتحقق من توفر الموعد 
    public static boolean isTimeSlotAvailable(String doctorId, String datetime) {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:hospital.db");
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM appointments WHERE doctor_id = ? AND appointment_time = ?")) {
            stmt.setString(1, doctorId);
            stmt.setString(2, datetime);
            ResultSet rs = stmt.executeQuery();
            return !rs.next(); 
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
 // نتحقق منن تارخ و وقت الموعد
    public static boolean isFutureDate(String date, String time) { 
        try {
            String fullDateTime = date + "T" + time; //  2025-09-20   15:20 بهذ الشكل يكتب مثلا
            LocalDateTime selected = LocalDateTime.parse(fullDateTime);
            return selected.isAfter(LocalDateTime.now());
        } catch (DateTimeParseException e) {
            e.printStackTrace();
        }
        return false;
    }
}