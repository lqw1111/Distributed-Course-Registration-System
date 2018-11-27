package ReplicaHost3.DCRS;

import java.util.*;
import java.util.stream.Collectors;

public class Student {
    private Map<String, List<String>> courseRegistered;
    private String tag;

    public Student(String studentID) {
        this.courseRegistered = new HashMap<>();
        this.tag = studentID.substring(0, 4);

        courseRegistered.put("fall", new ArrayList<>());
        courseRegistered.put("winter", new ArrayList<>());
        courseRegistered.put("summer", new ArrayList<>());
    }

    public int getRemoteCourseNum(String semester) {
        List<String> list = courseRegistered.get(semester);
        return (int)list.stream().filter(course -> !course.substring(0, 4).equals(tag)).count();
    }

    String getCourseSemester(String oldCourseID) {
        for (Map.Entry<String, List<String>> stringListEntry : courseRegistered.entrySet()) {
            if (stringListEntry.getValue().contains(oldCourseID))
                return stringListEntry.getKey();
        }
        return null;
    }

    public int getRemoteCourseNumberIfSwap(String oldCourseID, String newCourseID) {
        String courseSemester = this.getCourseSemester(oldCourseID);
        if (courseSemester == null)
            return -1;
        int remoteCourseNum = this.getRemoteCourseNum(courseSemester);
        if (!oldCourseID.substring(0, 4).equals(tag))
            remoteCourseNum--;
        if (!newCourseID.substring(0, 4).equals(tag))
            remoteCourseNum++;
        return remoteCourseNum;
    }



    public boolean checkCourse(String courseID) {
        for (List<String> list : courseRegistered.values()) {
            if (list.contains(courseID)) return true;
        }
        return false;
    }

    public void swapCourse(String oldCourseID, String newCourseID) {
        String semester = this.getSemester(oldCourseID);
        synchronized (this) {
            courseRegistered.get(semester).remove(oldCourseID);
            courseRegistered.get(semester).add(newCourseID);
        }
    }

    public String getSemester(String courseID) {
        for (Map.Entry<String, List<String>> stringListEntry : courseRegistered.entrySet()) {
            if (stringListEntry.getValue().contains(courseID))
                return stringListEntry.getKey();
        }
        return "";
    }

    public boolean checkCourse(String courseID, String semester) {
        if (!courseRegistered.containsKey(semester))
            return false;
        List<String> courses = courseRegistered.get(semester);
        if (courses == null) return false;
        if (courses.size() == 0) return false;
        return courses.contains(courseID);

    }

    public int getRegistedNum(String semester) {
        return courseRegistered.get(semester).size();
    }

    public void registerCourse(String courseID, String semester) {
        synchronized (this) {
            courseRegistered.get(semester).add(courseID);
        }
    }

    public void drop(String courseID) {
        synchronized (this) {
            for (List<String> list : courseRegistered.values()) {
                list.remove(courseID);
            }
        }
    }

    public String[] getSchedule() {
        List<String> list = new ArrayList<>();
        for (Map.Entry<String, List<String>> stringListEntry : courseRegistered.entrySet()) {
            for(String str : stringListEntry.getValue()){
                list.add(str + "--" + stringListEntry.getKey());
            }
        }
        String[] result = list.toArray(new String[list.size()]);
        Arrays.sort(result);
        return result;
    }

}
