
package project;

import javax.swing.*;

public class MainApp {
    public static void main(String[] args) {
        // البرنامج يشغل واجهة تسجيل الدخول 
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());//يغير شكل الفورم لشكل الجهاز
        } catch (Exception e) {//يطبع رسالة لو فشل  
            System.out.println("Failed to apply system ."); 
        }

        SwingUtilities.invokeLater(() -> {// طريقة تشغيل الواجهات على طريقة  Swing
            new LoginForm();//هنا حق تسجيل الدخول
        });
    }
}
