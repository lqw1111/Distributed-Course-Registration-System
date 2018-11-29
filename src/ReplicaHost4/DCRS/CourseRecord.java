//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ReplicaHost4.DCRS;

import java.util.LinkedList;
import java.util.List;

class CourseRecord {
    private List<String> studentList = new LinkedList();
    int capacity;
    public static final int DEFAULT_CAPACITY = 2;

    CourseRecord(int capacity) {
        this.capacity = capacity;
    }

    CourseRecord() {
        this.capacity = 2;
    }

    int getAvailability() {
        return this.capacity - this.studentList.size();
    }

    void enroll(String studentID) {
        this.studentList.add(studentID);
    }

    boolean enrolled(String studentID) {
        return this.studentList.contains(studentID);
    }

    void drop(String studentID) {
        this.studentList.remove(studentID);
    }

    boolean enrolAvail() {
        return this.capacity - this.studentList.size() >= 1;
    }
}
