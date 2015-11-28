package mu.lab.thulib.thucab.entity;

/**
 * Created by coderhuhy on 15/11/11.
 */
public class StudentAccount {

    private String studentId;
    private String password;

    public StudentAccount(String studentId, String password) {
        this.studentId = studentId;
        this.password = password;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
