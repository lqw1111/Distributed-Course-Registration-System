package ReplicaHost3.DCRS;

import java.util.ArrayList;
import java.util.List;

public class Course {
    private String course_name;
    private int max_num;
    private List<String> student_registerred;

    public Course(String course_name, int max_num) {
        this.course_name = course_name;
        this.max_num = max_num;
        this.student_registerred = new ArrayList<>();
    }

    public boolean isStatusFull() {
        if(max_num > student_registerred.size()) return false;
        else return true;
    }

    public boolean requestLocalSwap(Course course, String studentID) {
        return course.getRequestLocationSwap(this, studentID);
    }

    public boolean getRequestLocationSwap(Course course, String studentID) {
        synchronized (this) {
            if (this.isStatusFull())
                return false;
            course.drop(studentID);
            return this.register(studentID);
        }
    }

    public void swapEnrol(String studentID) {
        this.student_registerred.add(studentID);
    }

    public boolean register(String studentID) {
        synchronized (this) {
            if (!isStatusFull()) {
                this.student_registerred.add(studentID);
                return true;
            }
        }
        return false;
    }

    public boolean drop(String studentID) {
        synchronized (this) {
            return this.student_registerred.remove(studentID);
        }
    }

    public void clear() {
        synchronized (this) {
            this.student_registerred.clear();
        }
    }

    public int getAvailableSpave() {
        return max_num - student_registerred.size();
    }

    public List<String> getStudent_registerred() {
        return student_registerred;
    }

    public String getCourse_name() {
        return course_name;
    }
}
