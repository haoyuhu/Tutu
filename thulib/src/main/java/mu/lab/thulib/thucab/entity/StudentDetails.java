package mu.lab.thulib.thucab.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Student details
 * Created by coderhuhy on 15/11/12.
 */
public class StudentDetails {

    @SerializedName("id")
    private String studentId;
    private String name;
    private String phone;
    private String email;
    @SerializedName("dept")
    private String department;

    public StudentDetails(String studentId, String name, String phone, String email, String department) {
        this.studentId = studentId;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.department = department;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String toString() {
        return "".concat("Name: " + name + " ")
            .concat("Student Id: " + studentId + " ")
            .concat("Phone: " + phone + " ")
            .concat("email: " + email + " ")
            .concat("Department: " + department);
    }

}
