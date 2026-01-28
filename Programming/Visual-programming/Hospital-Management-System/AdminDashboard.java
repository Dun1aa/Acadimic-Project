
package project;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;//تخزن المواعيد

public class AdminDashboard extends JFrame {
    private User admin;// عشان يحفظ معلومات المشرف الحالي
    private DatabaseManager Db;//مدير لقاعده البيانات,بنطلب منه بيانات المواعيد

    public AdminDashboard(User admin, DatabaseManager Db) {
        this.admin = admin;
        this.Db = Db;
        //خصائص النفاذه ------------
        setTitle("admin dashboard"); 
        setSize(800, 600);//كبرت الواجهة شوي عشان الازرار الجديدة
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        //-------------------------------------------
        JTextArea reportArea = new JTextArea();// يسوي مربع نص يعرض فيه التقارير
        reportArea.setEditable(false);// ونقفل خاصيه الكتابه عشان المستخدم بس يقرا مايقدر يعدل

        JButton viewReportsBtn = new JButton("view reports"); //يعرض تقرير به كل تفاصيل المواعيد الحاليه
        // حدث
        viewReportsBtn.addActionListener(e -> {
            try {
                ArrayList<Appointment> appointments = Db.getAllAppointments();
                reportArea.setText("");
                if (appointments.isEmpty()) {//اذا مابه مواعيد تطلع رساله انه مافيه مواعيد
                    reportArea.setText("no appointments available."); 
                } else {// اذا فيه راح يمر على المواعيد واحد واحد
                    for (Appointment a : appointments) {//كل موعد بنجيب اسم الدكتور و المريض عن طريق الاي دي حقهم
                        String doctorName = Db.getDoctorNameById(a.getDoctorId());
                        String patientName = Db.getPatientNameById(a.getPatientId());
                        reportArea.append(
                            " appointment ID: " + a.getAppointmentId() + "\n" +
                            "️ doctor: Dr. " + doctorName + "\n" +
                            " patient: " + patientName + "\n" +
                            " date: " + a.getDate() + "\n" +
                            " time: " + a.getTime() + "\n" +
                            " status: " + a.getStatus() + "\n\n"
                        );
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error loading appointments.");
            }
        });

        // زر اضافه دكتور
        JButton addDoctorBtn = new JButton("add doctor");
        addDoctorBtn.addActionListener(e -> {
            String name = JOptionPane.showInputDialog("enter doctor name:");
            if (name != null && !name.trim().isEmpty()) {
                boolean success = Db.addDoctor(name.trim());
                if (success)
                    JOptionPane.showMessageDialog(this, "doctor added successfully.");
                else
                    JOptionPane.showMessageDialog(this, "failed to add doctor.");
            }
        });

        // زر حذف دكتور
        JButton deleteDoctorBtn = new JButton("delete doctor");
        deleteDoctorBtn.addActionListener(e -> {
            String id = JOptionPane.showInputDialog("enter doctor id to delete:");
            if (id != null && !id.trim().isEmpty()) {
                boolean success = Db.deleteDoctorById(id.trim());
                if (success)
                    JOptionPane.showMessageDialog(this, "doctor deleted successfully.");
                else
                    JOptionPane.showMessageDialog(this, "failed to delete doctor.");
            }
        });

        // زر اضافه مريض
        JButton addPatientBtn = new JButton("add patient");
        addPatientBtn.addActionListener(e -> {
            String name = JOptionPane.showInputDialog("enter patient name:");
            if (name != null && !name.trim().isEmpty()) {
                boolean success = Db.addPatient(name.trim());
                if (success)
                    JOptionPane.showMessageDialog(this, "patient added successfully.");
                else
                    JOptionPane.showMessageDialog(this, "failed to add patient.");
            }
        });
        
// زر حذف مريض
        JButton deletePatientBtn = new JButton("delete patient");
        deletePatientBtn.addActionListener(e -> {
            String id = JOptionPane.showInputDialog("enter patient id to delete:");
            if (id != null && !id.trim().isEmpty()) {
                boolean success = Db.deletePatientById(id.trim());
                if (success)
                    JOptionPane.showMessageDialog(this, "patient deleted successfully.");
                else
                    JOptionPane.showMessageDialog(this, "failed to delete patient.");
            }
        });

        // زر تغييرخالة موعد
        JButton updateStatusBtn = new JButton("update apointment status");
        updateStatusBtn.addActionListener(e -> {
            String id = JOptionPane.showInputDialog("enter appointment id:");
            if (id != null && !id.trim().isEmpty()) {
                String status = JOptionPane.showInputDialog("enter new status (pending, confirmed, cancelled):");
                if (status != null && !status.trim().isEmpty()) {
                    boolean success = Db.updateAppointmentStatus(id.trim(), status.trim());
                    if (success)
                        JOptionPane.showMessageDialog(this, "status updated successfully.");
                    else
                        JOptionPane.showMessageDialog(this, "failed to update status.");
                }
            }
        });

        //!!! ترتيب العناصر بالواجهه------------------------------
        JPanel topPanel = new JPanel();//بنسوي لوحه عشان نرتب الازرار
        topPanel.setLayout(new GridLayout(2, 3, 5, 5));//نرتب الازرار 2 صفوف و3 اعمده

        topPanel.add(viewReportsBtn);
        topPanel.add(addDoctorBtn);
        topPanel.add(deleteDoctorBtn);
        topPanel.add(addPatientBtn);
        topPanel.add(deletePatientBtn);
        topPanel.add(updateStatusBtn);

        add(topPanel, BorderLayout.NORTH);
        /*بنحط مساحه النص بالنص 
        ويلفها  ScrollPane
        عشان لو زادت المواعيد يصير فيه شريط تمرير
        */
        add(new JScrollPane(reportArea), BorderLayout.CENTER);
        setVisible(true);
    }
}
