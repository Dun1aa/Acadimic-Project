
package project;

public class User {//كلاس للمستخدمين (مريض,دكتور,المشرف)
    private String id;
    private String name;
    private String email;
    private String password;
    private String phone;
    private String role; //دور المستخدم (مريض,دكتور,المشرف)
    //constructor 
    public User(String id, String name, String email, String password, String phone, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.role = role;
    }
    //getter للبياناتت
    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getPhone() { return phone; }
    public String getRole() { return role; }
    //setter  لتعديل (الاسم,الايميل,باسوورد,رقم)\\id +role =ثابت مايتغيرون ابد
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setPhone(String phone) { this.phone = phone; }
}