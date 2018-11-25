package ReplicaHost3.DCRS;

import java.util.List;

public class Student {
    public String studentId;
    public List<Course> studentEnrollCourseList;

    public Student(String studentId , List<Course> studentEnrollCourseList) {
        this.studentId = studentId;
        this.studentEnrollCourseList = studentEnrollCourseList;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public List<Course> getStudentEnrollCourseList() {
        return studentEnrollCourseList;
    }

    public void setStudentEnrollCourseList(List<Course> studentEnrollCourseList) {
        this.studentEnrollCourseList = studentEnrollCourseList;
    }
}
