package ReplicaHost2.DCRS;

import ReplicaHost2.DCRS.Certification;

import java.util.ArrayList;
import java.util.HashMap;

public class Student extends Certification {
    private HashMap<String, Integer> courseCount = new HashMap<>();
    private int otherCount;
    private HashMap<String, ArrayList<String>> enrolledCourse = new HashMap<>();

    public Student(String clientId) {
        super(clientId);
        courseCount.put("fall", 0);
        courseCount.put("winter", 0);
        courseCount.put("summer", 0);
        enrolledCourse.put("fall", new ArrayList<>());
        enrolledCourse.put("winter", new ArrayList<>());
        enrolledCourse.put("summer", new ArrayList<>());
        otherCount = 0;
    }

    public HashMap<String, ArrayList<String>> getEnrolledCourse() {
        return enrolledCourse;
    }

    public void setEnrolledCourse(String semester, ArrayList<String> enrolledCourse) {
        this.enrolledCourse.put(semester, enrolledCourse);
    }

    public int getCourseCount(String semester) {
        int count = courseCount.get(semester);
        return count;
    }

    public void setCourseCount(String semester, int count) {
        this.courseCount.put(semester, count);
    }

    public void setOtherCount(int otherCount) {
        this.otherCount = otherCount;
    }

    public void decreaseCount(String semester) {
        int count = courseCount.get(semester);
        courseCount.put(semester, count - 1);
    }

    public void increaseCount(String semester) {
        int count = courseCount.get(semester);
        courseCount.put(semester, count + 1);
    }

    public int getOtherCount() {
        return otherCount;
    }

    public void increaseOtherCount() {
        this.otherCount++;
    }

    public void decreaseOtherCount() {
        this.otherCount--;
    }
}
