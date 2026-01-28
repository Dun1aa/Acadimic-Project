
package project;

import javax.swing.*;
import java.awt.*;
import java.util.UUID; //مكتبه لانشا ارقام عشوائيه فريده

public class LoginForm extends JFrame {

    private DatabaseManager db = new DatabaseManager(); // obj for data base
// constr
    public LoginForm() {
        setTitle("Hospital System"); 
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();

        //  نافذه تسجيل دخول
        JPanel loginPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        JTextField loginEmail= new JTextField();
        JPasswordField loginPassword = new JPasswordField();
        JButton loginBtn = new JButton("logn");

        loginPanel.add(new JLabel("email or phone number")); 
        loginPanel.add(loginEmail);
        loginPanel.add(new JLabel("pasword:")); 
        loginPanel.add(loginPassword);
        loginPanel.add(new JLabel());
        loginPanel.add(loginBtn);

        loginBtn.addActionListener(e -> {
            String email = loginEmail.getText();
            String password = new String(loginPassword.getPassword());

            try {
                User user = db.login(email, password);
                if (user != null) { 
                    JOptionPane.showMessageDialog(this, "Logn sccessful"); //اذا لقاه باقاعده مسجل قبل
                    dispose(); // إغلاق النافذه تسجيل الدخول

                    String role = user.getRole().toLowerCase();
                    if (role.equals("patient")) // اذا مريض 
                        new PatientDashboard(user, db); //تفتح نافذه المريض
                    else if (role.equals("doctor")) //اذ دكتور 
                        new DoctorDashboard(user, db); //تفتح نافذخ الدكتور
                    else
                        new AdminDashboard(user, db); // غيره يكون مشرف 
                } else {
                    JOptionPane.showMessageDialog(this, "Incrrect lgin data");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, " error occurred during login", "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        //نافذه تسجيل
        JPanel signupPanel = new JPanel(new GridLayout(7, 2, 5, 5));
        JTextField signName = new JTextField();
        JTextField signEmailPhone = new JTextField();
        JPasswordField signPass = new JPasswordField();
        JTextField signPhone = new JTextField();
        JComboBox<String> signRole = new JComboBox<>(new String[]{"patient", "doctor", "admin"}); 
        JButton registerBtn = new JButton("register"); 

        signupPanel.add(new JLabel("name")); 
        signupPanel.add(signName);
        signupPanel.add(new JLabel("email")); 
        signupPanel.add(signEmailPhone);
        signupPanel.add(new JLabel("password")); 
        signupPanel.add(signPass);
        signupPanel.add(new JLabel("phone")); 
        signupPanel.add(signPhone);
        signupPanel.add(new JLabel("role")); 
        signupPanel.add(signRole);
        signupPanel.add(new JLabel());
        signupPanel.add(registerBtn);

        registerBtn.addActionListener(e -> {
            String id = UUID.randomUUID().toString();
            String name = signName.getText();
            String emailOrPhone = signEmailPhone.getText();
            String password = new String(signPass.getPassword());
            String phone = signPhone.getText();

            String selectedRole = (String) signRole.getSelectedItem();
            String role = "";
            if (selectedRole.equals("patient"))
                role = "patient";
            else if (selectedRole.equals("doctor"))
                role = "doctor";
            else
                role = "admin";
            
            // نتحقق من البريد 
            if (!isValidEmail(emailOrPhone)) {
                JOptionPane.showMessageDialog(this, "please enter valid email");
                return;
            }

            // نتحفف من قوه كلمه السر
            if (!isStrongPassword(password)) {
                JOptionPane.showMessageDialog(this, "week password. must be at least 8 character.");
                return;
            }
            // نتحقق من وجوده قبل او لا
            if (db.checkUserExists(emailOrPhone)) {
                JOptionPane.showMessageDialog(this, "Account already exists.");
            } else {
                User newUser = new User(id, name, emailOrPhone, password, phone, role);

             // يتم التسجيل  اذا كان جديد
                if (db.addUser(newUser)) {
                    JOptionPane.showMessageDialog(this, "registration sccessful");
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, " error occurred during registration");
                }
            }
        });

        tabs.addTab("login", loginPanel); 
        tabs.addTab("register", signupPanel); 

        add(tabs);
        setVisible(true);
    }

    public boolean isValidEmail(String email) { //نتحقق من الاميل
        return email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    }

    public boolean isStrongPassword(String password) {//نتحقق من كلمه السر
        return password.length() >= 8;
    }
}