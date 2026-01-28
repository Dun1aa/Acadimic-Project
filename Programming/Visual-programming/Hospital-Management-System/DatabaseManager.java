

package project; 

import java.sql.*; //مكتبه للقاعده الببانات
import java.util.ArrayList; //  مكتبه لتخزين بيانات كثيره 
import java.io.File; //  مكتبه للتعامل مع ملفات

public class DatabaseManager { 
    private Connection conn; // var لحفظ الاتصال بقاعد البيانات
//constr
    public DatabaseManager() {  
        try {
            System.out.println(" Database file: " + new java.io.File("hospital.db").getAbsolutePath());
            String dbPath = "C:/Users/Asus/OneDrive/Desktop/mysqlite/hospital.db"; // الباث
            conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath); // اتصال بالقاعده
            System.out.println(" Database path (inside project folder): " + dbPath);
            if (new File(dbPath).exists()) { // نتحقق من ملف قاعدتنا موجود
                
            } else {
                // ملف قاعدتنا مو موجود  
            }

            createTablesIfNeeded(); // ننشئ الجداول
        } catch (SQLException e) { // مسك اي خطا بالتصال
            System.out.println("error connecting to database " + e.getMessage()); // طباعه الخطأ
        }
    }
//mathod
    public String getDoctorNameById(String doctorId) { //تجيب اسم الدكتور حسب الاي دي
        try {
            String query = "SELECT name FROM users WHERE id = ? AND role = 'doctor'"; // استعلام SQL
            PreparedStatement ps = conn.prepareStatement(query); // تجهيز الاستعلام
            ps.setString(1, doctorId); 
            ResultSet rs = ps.executeQuery(); // تنفيذ الاستعلام
            
            if (rs.next()) { //اذا فيه نتجيه رجع اسم الطبيب
                return rs.getString("name");  
            }
        } catch (SQLException e) {
            System.out.println("error retrieving doctor name: " + e.getMessage()); // طباعه الخطأ
        }
        return "Unknown"; // اعطاء قيمه افتراضيه اذا مالقيناه
    }

    public String getPatientNameById(String patientId) { // تجيب اسم المريض حسب الاي دي
        try {
            String query = "SELECT name FROM users WHERE id = ? AND role = 'patient'";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, patientId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) { 
                return rs.getString("name");
            }
        } catch (SQLException e) {
            System.out.println("error retreving patient name: " + e.getMessage());
        }
        return "Unknown";
    }

    public ArrayList<Appointment> getAppointmentsForDoctor(String doctorId) { // تجيب مواعيد دكتور معين
        ArrayList<Appointment> appointments = new ArrayList<>();//قائمه للمواعيد
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM appointments WHERE doctorId = ?");
            ps.setString(1, doctorId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {  //طالما يوجد مواعيد
                appointments.add(new Appointment( // اضافه كل موعد
                        rs.getString("appointmentId"),
                        rs.getString("patientId"),
                        rs.getString("doctorId"),
                        rs.getString("date"),
                        rs.getString("time"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            System.out.println("error retrieving doctor appointments: " + e.getMessage());
        }
        return appointments;
    }

    public ArrayList<Appointment> getAllAppointments() { //  تجيب جميع المواعيد
        ArrayList<Appointment> appointments = new ArrayList<>();
        try {
            String query = "SELECT * FROM appointments"; //استعلام لجميع المواعيد
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                appointments.add(new Appointment(
                    rs.getString("appointmentId"),
                    rs.getString("patientId"),
                    rs.getString("doctorId"),
                    rs.getString("date"),
                    rs.getString("time"),
                    rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving all appointments: " + e.getMessage());
        }
        return appointments;
    }

    public boolean updateAppointmentTime(String appointmentId, String newDate, String newTime) { // دالة لتحديث موعد
        try {
            String query = "UPDATE appointments SET date = ?, time = ? WHERE appointmentId = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, newDate);
            ps.setString(2, newTime);
            ps.setString(3, appointmentId);
            int updated = ps.executeUpdate();
            return updated > 0; // عدد الصفوف تاثرت فانه صار تحديث
        } catch (SQLException e) {
            System.out.println("error updating apontment: " + e.getMessage());
        }
        return false;
    }

    public void createTablesIfNeeded() { //  انشاء الجداول إذا مب موجوده
        try {
            Statement stmt = conn.createStatement(); 
            String createUsersTable = "CREATE TABLE IF NOT EXISTS users (" +
                    "id TEXT PRIMARY KEY, " +
                    "name TEXT, " +
                    "email TEXT, " +
                    "password TEXT, " +
                    "phone TEXT UNIQUE, " +
                    "role TEXT)";
            
            String createAppointmentsTable = "CREATE TABLE IF NOT EXISTS appointments (" +
                    "appointmentId TEXT PRIMARY KEY, " +
                    "patientId TEXT, " +
                    "doctorId TEXT, " +
                    "date TEXT, " +
                    "time TEXT, " +
                    "status TEXT CHECK(status IN ('pending', 'confirmed', 'cancelled')))";
            stmt.execute(createUsersTable);
            stmt.execute(createAppointmentsTable);
        } catch (SQLException e) {
            System.out.println("error creating tabls: " + e.getMessage());
        }
    }

    public boolean checkUserExists(String emailOrPhone) { //  نتحقق من وجود المستخدم بالبريد او رقمه
        try {
            String query = "SELECT * FROM users WHERE email = ? OR phone = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, emailOrPhone);
            ps.setString(2, emailOrPhone);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.out.println("error chcking user existense" + e.getMessage());
            return false;
        }
    }

    public boolean addUser(User user) { //  لائضافه مستخدم جديد
        try {
            String query = "INSERT INTO users (id, name, email, password, phone, role) VALUES (?, ?, ?, ?, ?, ?)"; //اماكن فاضيه?
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, user.getId());
            ps.setString(2, user.getName());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getPassword());
            ps.setString(5, user.getPhone());
            ps.setString(6, user.getRole());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("error adding user " + e.getMessage());
            return false;
        }
    }

    public User login(String emailOrPhone, String password) { //  لتسجيل الدخول
        try {
            String query = "SELECT * FROM users WHERE (email = ? OR phone = ?) AND password = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, emailOrPhone);
            ps.setString(2, emailOrPhone);
            ps.setString(3, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("phone"),
                        rs.getString("role")
                );
            }
        } catch (SQLException e) {
            System.out.println("error during login " + e.getMessage());
        }
        return null;
    }

    public ArrayList<User> getAllDoctors() { // دالة تجيب جميع الدكاتره
        ArrayList<User> doctors = new ArrayList<>();
        try {
            String query = "SELECT * FROM users WHERE role = 'doctor'"; //يختار بس الدكاتره 
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                doctors.add(new User(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("phone"),
                        rs.getString("role")
                ));
            }
        } catch (SQLException e) {
            System.out.println("error retrieving doctors list " + e.getMessage());
        }
        return doctors;
    }
    
    public ArrayList<Appointment> getAppointmentsForPatient(String patientId) { //كل المواعيد لمريض معين 
        ArrayList<Appointment> appointments = new ArrayList<>();
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM appointments WHERE patientId = ?");
            ps.setString(1, patientId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                appointments.add(new Appointment(
                        rs.getString("appointmentId"),
                        rs.getString("patientId"),
                        rs.getString("doctorId"),
                        rs.getString("date"),
                        rs.getString("time"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            System.out.println("error retrieving patient appointment: " + e.getMessage());
        }
        return appointments;
    }

    public boolean isAppointmentAvailable(String doctorId, String date, String time) { //نتحقق من توفر الموعد    
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM appointments WHERE doctorId = ? AND date = ? AND time = ?");
            ps.setString(1, doctorId);
            ps.setString(2, date);
            ps.setString(3, time);
            ResultSet rs = ps.executeQuery();
            return !rs.next();
        } catch (SQLException e) {
            System.out.println("error checking appointment availabil " + e.getMessage());
        return false; // في حالةالخطا  نعتبر الموعد غير متاح
        }
    }

    public boolean bookAppointment(String patientId, String doctorId, String date, String time) { // نحجز موعد
        if (isAppointmentAvailable(doctorId, date, time)) {
            try {
            String appointmentId = generateAppointmentId(); // انشاء اي دي للموعد
                String query = "INSERT INTO appointments (appointmentId, patientId, doctorId, date, time, status) VALUES (?, ?, ?, ?, ?, 'confirmed')";
                PreparedStatement ps = conn.prepareStatement(query);
                ps.setString(1, appointmentId);
                ps.setString(2, patientId);
                ps.setString(3, doctorId);
                ps.setString(4, date);
                ps.setString(5, time);
                ps.executeUpdate();
                System.out.println("appointment succesfully saved to database"); //تم الحجز
                return true;
            } catch (SQLException e) {
                System.out.println("error booking apointmnt"); 
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    private String generateAppointmentId() {  //  لانشاء اي دي  للموعد باستخدام الوقت الحالي
        return "APPT-" + System.currentTimeMillis();
    }

    public boolean cancelAppointmentById(String appointmentId) { // لالغاء الموعد حسب الاي دي
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM appointments WHERE appointmentId = ?")) {
            ps.setString(1, appointmentId);
            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            System.out.println("error deleting appointment: " + e.getMessage());
            return false;
        }
    }

    public int getAppointmentStats() { //   لحساب عدد المواعيد كلهم
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS total FROM appointments")) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.out.println("error counting appointments " + e.getMessage());
        }
        return 0;
    }
public ArrayList<User> searchDoctorsByName(String keyword){ 

    ArrayList<User> results = new ArrayList<>(); // انشاء  قائمه لنواتج
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE role = 'doctor' AND name LIKE ?")) {
            ps.setString(1, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                results.add(new User(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("phone"),
                        rs.getString("role")
                ));
            }
        } catch (SQLException e) {
            System.out.println("error searching doctors: " + e.getMessage());
        }
        return results;
    }

// ----قانكشنز لل  Admin ---

// إضافة دكتور بالاسم بس
public boolean addDoctor(String name) {
    try {
        String id = "DOC-" + System.currentTimeMillis(); // id فريد
        String query = "INSERT INTO users (id, name, email, password, phone, role) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setString(1, id);
        ps.setString(2, name);
        ps.setString(3, ""); // بيخلي البريد فاضي
        ps.setString(4, ""); // passفاضي
        ps.setString(5, id); // phone = id عشان يكون فريد وما يعطي خطأ
        ps.setString(6, "doctor"); // roll
        ps.executeUpdate();
        return true;
    } catch (SQLException e) {
        System.out.println("error adding doctor: " + e.getMessage());
        return false;
    }
}

// إضافة مريض بالاسم بسس
public boolean addPatient(String name) {
    try {
        String id = "PAT-" + System.currentTimeMillis(); //id فريدد
        String query = "INSERT INTO users (id, name, email, password, phone, role) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setString(1, id);
        ps.setString(2, name);
        ps.setString(3, ""); 
        ps.setString(4, ""); 
        ps.setString(5, id); 
        ps.setString(6, "patient"); // roll
        ps.executeUpdate();
        return true;
    } catch (SQLException e) {
        System.out.println("error adding patient: " + e.getMessage());
        return false;
    }
}


// ---- حذف دكتور عن طريق id----
public boolean deleteDoctorById(String id) {
    try {
        String query = "DELETE FROM users WHERE id = ? AND role = 'doctor'";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setString(1, id);
        int affected = ps.executeUpdate();
        return affected > 0;
    } catch (SQLException e) {
        System.out.println("error deleting doctor: " + e.getMessage());
        return false;
    }
}

// ---- حذف مريض عن طريق id ----
public boolean deletePatientById(String id) {
    try {
        String query = "DELETE FROM users WHERE id = ? AND role = 'patient'";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setString(1, id);
        int affected = ps.executeUpdate();
        return affected > 0;
    } catch (SQLException e) {
        System.out.println("error deleting patient: " + e.getMessage());
        return false;
    }
}
// ---- تعديل حالة الموعد ----(pending, confirmed, cancelled)
public boolean updateAppointmentStatus(String appointmentId, String status) {
    try {
        String query = "UPDATE appointments SET status = ? WHERE appointmentId = ?";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setString(1, status);
        ps.setString(2, appointmentId);
        int affected = ps.executeUpdate();
        return affected > 0;
    } catch (SQLException e) {
        System.out.println("error updating appointment status: " + e.getMessage());
        return false;
    }
}
}

