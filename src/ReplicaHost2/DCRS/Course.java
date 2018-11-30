package ReplicaHost2.DCRS;

import java.util.ArrayList;
import java.util.List;

public class Course {
    private String courseName;
    private String department;
    private int capacity;
    private ArrayList<String> studentList;
    private String semester;

    public Course(String courseName) {
        this.courseName = courseName;
        this.capacity = 2;
        studentList = new ArrayList<>();
    }

    public void resetCourse(){
        department = "";
        capacity = 0;
        studentList.clear();
        semester = "";
    }

    public String getDepartment() {
        department = courseName.substring(0, 4);
        return department;
    }

    public ArrayList<String> getStudentList() {
        return studentList;
    }

    public void setStudentList(ArrayList<String> studentList) {
        this.studentList = studentList;
    }

    public int getCapacity() {
        return capacity;
    }

    public void increaseCapacity() {
        this.capacity++;
    }

    public void decreaseCapacity() {
        this.capacity--;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }
}
