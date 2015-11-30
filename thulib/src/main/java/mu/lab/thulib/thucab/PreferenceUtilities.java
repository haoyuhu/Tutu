package mu.lab.thulib.thucab;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import mu.lab.thulib.thucab.entity.StudentAccount;
import mu.lab.thulib.thucab.entity.StudentDetails;
import mu.lab.util.Log;

/**
 * Preference utilities
 * Created by coderhuhy on 15/11/11.
 */
public class PreferenceUtilities {

    private static final String LogTag = PreferenceUtilities.class.getSimpleName();

    private final static String THUCAB_PREFERENCE_KEY = "ThuCabPreferenceKey";
    private static SharedPreferences preferences;

    enum PreferenceKey {

        StudentId("student_id"), Password("password"),
        Name("username"), Department("department"),
        Phone("phone"), Email("email");
        String string;

        PreferenceKey(String string) {
            this.string = string;
        }

        public String toString() {
            return this.string;
        }

    }

    private PreferenceUtilities() {

    }

    public static void init(Context context) {
        init(context.getSharedPreferences(THUCAB_PREFERENCE_KEY, Context.MODE_PRIVATE));
    }

    public static void init(SharedPreferences pref) {
        preferences = pref;
    }

    static StudentAccount getStudentAccount() throws StudentAccountNotFoundError {
        return new StudentAccount(getStudentId(), getPassword());
    }

    protected static String getStudentId() throws StudentAccountNotFoundError {
        String studentId = preferences.getString(PreferenceKey.StudentId.toString(), "");
        if (!TextUtils.isEmpty(studentId)) {
            return studentId;
        } else {
            throw new StudentAccountNotFoundError("[thucab]cannot found student id...");
        }
    }

    protected static String getPassword() throws StudentAccountNotFoundError {
        String password = preferences.getString(PreferenceKey.Password.toString(), "");
        if (!TextUtils.isEmpty(password)) {
            return password;
        } else {
            throw new StudentAccountNotFoundError("[thucab]cannot found password...");
        }
    }

    static StudentDetails getStudentDetails() throws StudentAccountNotFoundError {
        String studentId = getStudentId();
        String name = getUsername();
        String department = getDepartment();
        String phone = getPhone();
        String email = getEmail();
        return new StudentDetails(studentId, name, phone, email, department);
    }

    protected static String getUsername() throws StudentAccountNotFoundError {
        String username = preferences.getString(PreferenceKey.Name.toString(), "");
        if (!TextUtils.isEmpty(username)) {
            return username;
        } else {
            throw new StudentAccountNotFoundError("[thucab]cannot found username...");
        }
    }

    protected static String getDepartment() throws StudentAccountNotFoundError {
        String department = preferences.getString(PreferenceKey.Department.toString(), "");
        if (!TextUtils.isEmpty(department)) {
            return department;
        } else {
            throw new StudentAccountNotFoundError("[thucab]cannot found department...");
        }
    }

    protected static String getPhone() {
        return preferences.getString(PreferenceKey.Phone.toString(), "");
    }

    protected static String getEmail() {
        return preferences.getString(PreferenceKey.Email.toString(), "");
    }

    static synchronized boolean saveStudenId(String studentId) {
        if (!TextUtils.isEmpty(studentId)) {
            preferences.edit().putString(PreferenceKey.StudentId.toString(), studentId).apply();
        } else {
            Log.i(LogTag, "student id is empty, cannot save to preference...");
            return false;
        }
        return true;
    }

    static synchronized boolean savePassword(String password) {
        if (!TextUtils.isEmpty(password)) {
            preferences.edit().putString(PreferenceKey.Password.toString(), password).apply();
        } else {
            Log.i(LogTag, "password is empty, cannot save to preference...");
            return false;
        }
        return true;
    }

    static synchronized boolean saveUsername(String name) {
        if(!TextUtils.isEmpty(name)) {
            preferences.edit().putString(PreferenceKey.Name.toString(), name).apply();
        } else {
            Log.i(LogTag, "name is empty, cannot save to preference...");
            return false;
        }
        return true;
    }

    static synchronized boolean saveDepartment(String department) {
        if (!TextUtils.isEmpty(department)) {
            preferences.edit().putString(PreferenceKey.Department.toString(), department).apply();
        } else {
            Log.i(LogTag, "department is empty, cannot save to preference...");
            return false;
        }
        return true;
    }

    static synchronized boolean savePhone(String phone) {
        if (phone != null) {
            preferences.edit().putString(PreferenceKey.Phone.toString(), phone).apply();
        } else {
            Log.i(LogTag, "phone is null, cannot save to preference...");
            return false;
        }
        return true;
    }

    static synchronized boolean saveEmail(String email) {
        if (email != null) {
            preferences.edit().putString(PreferenceKey.Email.toString(), email).apply();
        } else {
            Log.i(LogTag, "email is null, cannot save to preference...");
            return false;
        }
        return true;
    }

    /**
     * Clear user preferences
     */
    static synchronized void clear() {
        preferences.edit().clear().apply();
    }

    public static class StudentAccountNotFoundError extends Exception {

        private String message;

        public StudentAccountNotFoundError(String detailMessage) {
            super(detailMessage);
            this.message = detailMessage;
        }

        public String toString() {
            return String.format("StudentAccountNotFoundError: %s", this.message);
        }
    }

}
