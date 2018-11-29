package ReplicaHost3.DCRS;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DCRSWrong {

    public String department;
    public ConcurrentHashMap<String, ConcurrentHashMap<String, Course>> compCourseDatabase = new ConcurrentHashMap<String, ConcurrentHashMap<String, Course>>();

    //studentId -> student
    public Map<String, Student> studentEnrollDatabase = new ConcurrentHashMap<String,Student>();


    public DCRSWrong(){ }

    public String addCourse(String courseId, String semester) {
        return "Fail";
    }

    public String removeCourse(String courseId, String semester) {
        return "Fail";
    }

    public String[] listCourseAvailability(String semester) {
        List<String> courseList = new ArrayList<>();

        courseList.add("comp1--2");

        return translateStringArray(courseList);
    }

    private String[] translateStringArray(List<String> courseList) {
        String[] res = new String[courseList.size()];
        for(int i = 0 ; i < courseList.size() ; i ++){
            res[i] = courseList.get(i);
        }
        return res;
    }


    public String enrolCourse(String studentId, String courseId, String semester) {
        return "Fail";
    }

    public String dropCourse(String studentId, String courseId) {
        return "Fail";
    }

    public String[] getClassSchedule(String studentId) {

        List<String> res = new ArrayList<>();
        res.add(new String("comp1--fall"));

        return translateStringArray(res);
    }

    public String swapCourse(String studentID, String newCourseID, String oldCourseID) {
        return "Fail";
    }

}
