
package project;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class PatientDashboard extends JFrame {
    private User patient;//المريض الي داخل للوحه التحكم
    private DatabaseManager Db;//مدير لقاعده البيانات,بنطلب منه بيانات المواعيد

    public PatientDashboard(User patient, DatabaseManager Db) {
        this.patient = patient;
        this.Db = Db;
        //خصائص النفاذه ------------
        setTitle("pationt dashboard"); 
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
       //--------------------------------
       
        JTextArea appointmentsArea = new JTextArea();//نص يعرض مواعيد المريض
        appointmentsArea.setEditable(false);

        JButton viewAppointmentsBtn = new JButton("view apointmnts"); // الزر الي بيعرض المواحيد الموجوده
        /*هنا بنحدد وش راح يطلع اذا ضغط الزر(الحدث):
        بيعرض كل مواعيد المريض ياخذه من قاعده البيانات ويطلعه بالنافذه  
        بالسطرfor حلقه كل موعد خزنته بالمتغيرa ويشتغل عليه
        */
        viewAppointmentsBtn.addActionListener(e -> {
            ArrayList<Appointment> appointments = Db.getAppointmentsForPatient(patient.getId());
            appointmentsArea.setText("");//بيحذف اي شي قديم علشان يظهر المواعيد الحاليه الي طلبه المستخدم
            for (Appointment a : appointments) {
                String doctorName = Db.getDoctorNameById(a.getDoctorId());//اجيب اسم الدكتور _تبع الموعد المحدد
                appointmentsArea.append(
                    " appointment with Dr. " + doctorName + "\n" +
                    " date: " + a.getDate() + "\n" +
                    " time: " + a.getTime() + "\n" +
                    " status: " + a.getStatus() + "\n" +
                    " appointment ID: " + a.getAppointmentId() + "\n\n"
                );
            }
        });

        JButton bookAppointmentBtn = new JButton("Book apointment"); //حجز موعد
        /* اذا ضغطه المستخدم راح يكون (حدث):
        بيجيب اسماء الاطباء المسجله بالداتابيس عشان يقدر المستخدم يختار اسم الدكتور
        يساله عن التاريخ و الوقت يكتبه بشكل يدوي وحطينا مثال علشان يدخله بطريقه صح
        اذا الوقت كان فيه موعد يعيي ويقول غير الوقت و اذا كان متاح راح يحجز له
        */
        bookAppointmentBtn.addActionListener(e -> {
            ArrayList<User> doctors = Db.getAllDoctors();
// نتاكد اذا فيه اطباء 
            if (doctors.isEmpty()) {
                JOptionPane.showMessageDialog(this, "no doctor found.");
                return;
            }

            JComboBox<String> doctorBox = new JComboBox<>();//قائمه عشان المريض يختار الدكتور
            for (User doctor : doctors) {
                doctorBox.addItem(doctor.getName() );// اسم الدكتور
            }
//عرض نافذه اختيار الدكتور          اذا كنسل او قفل النافذه مراح يكمل
            int option = JOptionPane.showConfirmDialog(this, doctorBox, "select a doctor", JOptionPane.OK_CANCEL_OPTION);
// اذا وافق راح يكمل عمليه الحجز
            if (option == JOptionPane.OK_OPTION) {
                int selectedIndex = doctorBox.getSelectedIndex();
                String doctorId = doctors.get(selectedIndex).getId();
                 //ياخذ التاريخ و الوقت الي يبيه
                String date = JOptionPane.showInputDialog("enter the date (DD-MM-YYYY):");
                String time = JOptionPane.showInputDialog("enter the time (e.g., 14:30):");
                  //نتاكد انه ماخلاه فاضي ولا كنسل
                if (date != null && time != null && !date.isEmpty() && !time.isEmpty()) {
                    if (Db.bookAppointment(patient.getId(), doctorId, date, time)) {//بنحاول نحجز الموعد من قاعده البيانات اذا فيه موعد ماخوذ او لا
                        JOptionPane.showMessageDialog(this, "appointment booked succesfuly");
                    } else {
                        JOptionPane.showMessageDialog(this, "time not avilable, choose another time.");
                    }
                }
            }
        });
        JButton cancelAppointmentBtn = new JButton("cancel appointment");// زر الغاء الموعد
        cancelAppointmentBtn.addActionListener(e -> {
            //نافذه بتطلب من المريض يحط رقم الموعدالي وده يلغيه
            String appointmentId = JOptionPane.showInputDialog("enter the appointment ID you want to cancel:");
            // بنتاكد هل فعلاا كتب المريض شي او انه مهب فاضيي و اذا كنسله بيتجاهل العمليه
            if (appointmentId != null && !appointmentId.trim().isEmpty()) {
                boolean success = Db.cancelAppointmentById(appointmentId.trim());//بنتواصل مع قاعده البيانات باستخدام cancelAppointmentById
                if (success) {//اذا لقاه بيحذفه
                    JOptionPane.showMessageDialog(this, "apointment cancelled succesfuly.");
                } else {
                    JOptionPane.showMessageDialog(this, "failed to find or cancel the appointment.");
                }
            }
        });

       // زر البحث عن الطبيب
        JButton searchDoctorBtn = new JButton("search doctor"); 
        JTextField searchField = new JTextField(10);//المريض  يكتب اما اسم تخصص الدكتور او جزء من اسم الطبيب
        //مربع بيعرض لنا النتائج و مانقدر نكتب فيه يدويلا او نعدل عليه!!
        JTextArea searchResults = new JTextArea(5, 30);
        searchResults.setEditable(false);
        //(الحدث) اذا ضغط الزر بينفذ الي دتخله
        searchDoctorBtn.addActionListener(e -> {
            String keyword = searchField.getText().trim();// بياخذ النص الي كتبه المريض وبيشيل اي مسافه زيادهtrim 
            searchResults.setText("");// بفضي المربع من اي نتائج قديمه
            if (keyword.isEmpty()) {
                searchResults.setText("Please enter a keyword to search.");
                return;
            }
// هنا بيتواصل مع قاعده البيانات عن طريقsearchDoctorsByName وبيبحث عن الدكاتر حسب الاسم الي كتبه المريض
            ArrayList<User> doctors = Db.searchDoctorsByName(keyword);
            if (doctors.isEmpty()) {
                searchResults.setText("no matching doctors founded.");
            } else {
                for (User doc : doctors) {
                    searchResults.append(
                        "name: " + doc.getName() + "\n" +
                        "email: " + doc.getEmail() + "\n" //البحث يكون بالاسم -->هنا يطلع الايميل والاسم  للمعلومة فقط
                        
                    );
                }
            }
        });
//!!!! هنا راح نرتب الواجهه----------------------------------------------------
//-----------------الي تبع البحث
        JPanel searchPanel = new JPanel();
        searchPanel.add(new JLabel("specialty / doctor name:")); 
        searchPanel.add(searchField);
        searchPanel.add(searchDoctorBtn);
//-----------------الازرار حقات :عرض ,حجز,الغاء مواعيد
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.add(viewAppointmentsBtn);
        buttonsPanel.add(bookAppointmentBtn);
        buttonsPanel.add(cancelAppointmentBtn);
//-----------------هنا ترتيب كلللللل شي 
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(buttonsPanel, BorderLayout.NORTH);//الازرار لتصير فوق 
        mainPanel.add(new JScrollPane(appointmentsArea), BorderLayout.CENTER);//المواعيد بنتصير بالنص
        mainPanel.add(searchPanel, BorderLayout.SOUTH);// البحث بيصير تحت
//-----------------
        add(mainPanel, BorderLayout.CENTER);
        add(new JScrollPane(searchResults), BorderLayout.SOUTH); //  عرض نتائج البحث

        setVisible(true);
    }
}