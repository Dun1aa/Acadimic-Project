
package project;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class DoctorDashboard extends JFrame {
    private User doctor;//يحفظ معلومات الدكتور الحالي
    private DatabaseManager Db;//مدير لقاعده البيانات,بنطلب منه بيانات المواعيد
// الدكتور يقدر يشوف ويلغي ويعيد جدوله الموعد
    public DoctorDashboard(User doctor, DatabaseManager Db) {
        this.doctor = doctor;
        this.Db = Db;
  //خصائص النفاذه ------------
        setTitle("doctor dashboard"); 
        setSize(600, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
//-----------------------------------------
        JTextArea appointmentsArea = new JTextArea();// تعرض مواعيد الدكتور
        appointmentsArea.setEditable(false);// خليناه مايمدي يعدل عليه بشكل يدوي

        JButton viewAppointmentsBtn = new JButton("view apointment"); // زر عرض المواعيد
        /* نفس الي سويناه مع المريض لكن هنا بيعرض معلومات المريض 
        يعرض معلومات كل موعد (اسم المريض + التاريخ + الوقت + الحالة + رقم الموعد)
        */
        viewAppointmentsBtn.addActionListener(e -> {
            appointmentsArea.setText("");
            ArrayList<Appointment> appointments = Db.getAppointmentsForDoctor(doctor.getId());
            for (Appointment a : appointments) {
                String patientName = Db.getPatientNameById(a.getPatientId());
                appointmentsArea.append(
                    " patient: " + patientName + "\n" +
                    " date: " + a.getDate() + "\n" +
                    " time: " + a.getTime() + "\n" +
                    " status: " + a.getStatus() + "\n" +
                    " appointment ID: " + a.getAppointmentId() + "\n\n"
                );
            }
        });

       
        JButton cancelBtn = new JButton("cancel apointment"); //زر الغاء الموعد
        cancelBtn.addActionListener(e -> {
            String id = JOptionPane.showInputDialog("enter the appointment ID to cancel:");// يطلب من الدكتور يدخل رقم الموعد الي بيلغيه
            if (id != null && !id.trim().isEmpty()) {//اذا موجود بيلغيه
                boolean success = Db.cancelAppointmentById(id.trim());
                if (success)
                    JOptionPane.showMessageDialog(this, "apointment canceled successfuly.");
                else
                    JOptionPane.showMessageDialog(this, "apointment not found.");
            }
        });

      
        JButton rescheduleBtn = new JButton("reschedule appointment"); // زر حق اعاده الجدوله
        rescheduleBtn.addActionListener(e -> {
            String id = JOptionPane.showInputDialog("enter the appointment ID to reschedule:");//بياخذ من الدكتور رقم الموعد
            if (id != null && !id.trim().isEmpty()) {//اذا موجود راح يطلب منه وقت وتاريخ جديد
                String newDate = JOptionPane.showInputDialog("enter the new date (e.g., DD-MM-YYYY):");
                String newTime = JOptionPane.showInputDialog("enter the new time (e.g., 15:00):");
                if (newDate != null && newTime != null && !newDate.isEmpty() && !newTime.isEmpty()) {// بيتاكد ان الدكتور دخل البيانات المطلوبه
                    boolean success = Db.updateAppointmentTime(id.trim(), newDate, newTime);
                    if (success)
                        JOptionPane.showMessageDialog(this, "appointment updated successfully.");
                    else
                        JOptionPane.showMessageDialog(this, "failed update appointment.");
                }
            }
        });
//!!! ترتيب العناصر بالواجهه------------------------------
        //الازرار بالشريط ال يفوق  
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.add(viewAppointmentsBtn);
        buttonsPanel.add(cancelBtn);
        buttonsPanel.add(rescheduleBtn);
        //الازرار فوق و المنطقه الي بتعرض المواعيد بتصير بالوسط
        add(buttonsPanel, BorderLayout.NORTH);
        add(new JScrollPane(appointmentsArea), BorderLayout.CENTER);

        setVisible(true);
    }
}