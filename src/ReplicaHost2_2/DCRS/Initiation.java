//package ReplicaHost2.DCRS;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.concurrent.ConcurrentHashMap;
//
//public class Initiation {
//    private Course course;
//    private Student student;
//    private ConcurrentHashMap<String, HashMap<String, Course>> compMap = new ConcurrentHashMap<>();
//    private ConcurrentHashMap<String, HashMap<String, Course>> soenMap = new ConcurrentHashMap<>();
//    private ConcurrentHashMap<String, HashMap<String, Course>> inseMap = new ConcurrentHashMap<>();
//
//    private ConcurrentHashMap<String, Student> compStudent = new ConcurrentHashMap<>();
//    private ConcurrentHashMap<String, Student> soenStudent = new ConcurrentHashMap<>();
//    private ConcurrentHashMap<String, Student> inseStudent = new ConcurrentHashMap<>();
//
//    public ConcurrentHashMap<String, HashMap<String, Course>> cInit(){
//
//        // ---------------- init serverMap -----------------------
//
//        HashMap<String, Course> fallMap = new HashMap<>();
//        HashMap<String, Course> winterMap = new HashMap<>();
//        HashMap<String, Course> summerMap = new HashMap<>();
//
//        ArrayList<String> fallStudents = new ArrayList<>();
//        ArrayList<String> winterStudents = new ArrayList<>();
//        ArrayList<String> summerStudents = new ArrayList<>();
//
//        course = new Course("comp6231");
//        fallStudents.add("comps1111");
//        fallStudents.add("soens1111");
//        course.setStudentList(fallStudents);
//        fallMap.put("comp6231", course);
//        compMap.put("fall", fallMap);
//
//        course = new Course("comp6521");
//        winterStudents.add("comps1111");
//        winterStudents.add("inses1111");
//        course.setStudentList(winterStudents);
//        winterMap.put("comp6521", course);
//        compMap.put("winter", winterMap);
//
//        course = new Course("comp6961");
//        summerStudents.add("comps1111");
//        course.setStudentList(summerStudents);
//        summerMap.put("comp6961", course);
//        compMap.put("summer", summerMap);
//
//        // --------------- init comps1111 ----------------
//
//        ArrayList<String> enrolledCourseFall = new ArrayList();
//        ArrayList<String> enrolledCourseWinter = new ArrayList();
//        ArrayList<String> enrolledCourseSummer = new ArrayList();
//
//        student = new Student("comps1111");
//
//        enrolledCourseFall.add("comp6231");
//        enrolledCourseFall.add("soen6441");
//        enrolledCourseFall.add("inse6230");
//        student.setEnrolledCourse("fall", enrolledCourseFall);
//
//        enrolledCourseWinter.add("comp6521");
//        student.setEnrolledCourse("winter", enrolledCourseWinter);
//
//        enrolledCourseSummer.add("comp6961");
//        student.setEnrolledCourse("summer", enrolledCourseSummer);
//
//        student.setCourseCount("fall", 3);
//        student.setCourseCount("winter", 1);
//        student.setCourseCount("summer", 1);
//        student.setOtherCount(2);
//        compStudent.put("comps1111", student);
//
//        return compMap;
//    }
//
//    public ConcurrentHashMap<String, HashMap<String, Course>> sInit(){
//
//        // ---------------- init serverMap -----------------------
//
//        HashMap<String, Course> fallMap = new HashMap<>();
//        HashMap<String, Course> winterMap = new HashMap<>();
//        HashMap<String, Course> summerMap = new HashMap<>();
//
//        ArrayList<String> fallStudents = new ArrayList<>();
//        ArrayList<String> winterStudents = new ArrayList<>();
//        ArrayList<String> summerStudents = new ArrayList<>();
//
//        course = new Course("soen6441");
//        fallStudents.add("comps1111");
//        fallStudents.add("soens1111");
//        course.setStudentList(fallStudents);
//        fallMap.put("soen6441", course);
//        soenMap.put("fall", fallMap);
//
//        course = new Course("soen6471");
//        winterStudents.add("soens1111");
//        course.setStudentList(winterStudents);
//        winterMap.put("soen6471", course);
//        soenMap.put("winter", winterMap);
//
//        course = new Course("soen6481");
//        summerStudents.add("soens1111");
//        course.setStudentList(summerStudents);
//        summerMap.put("soen6481", course);
//        soenMap.put("summer", summerMap);
//
//        // --------------- init soens1111 ----------------
//
//        ArrayList<String> enrolledCourseFall = new ArrayList();
//        ArrayList<String> enrolledCourseWinter = new ArrayList();
//        ArrayList<String> enrolledCourseSummer = new ArrayList();
//
//        student = new Student("soens1111");
//
//        enrolledCourseFall.add("comp6231");
//        enrolledCourseFall.add("soen6441");
//        student.setEnrolledCourse("fall", enrolledCourseFall);
//
//        enrolledCourseWinter.add("soen6471");
//        enrolledCourseWinter.add("inse6210");
//        student.setEnrolledCourse("winter", enrolledCourseWinter);
//
//        enrolledCourseSummer.add("soen6481");
//        student.setEnrolledCourse("summer", enrolledCourseSummer);
//
//        student.setCourseCount("fall", 2);
//        student.setCourseCount("winter", 2);
//        student.setCourseCount("summer", 1);
//        student.setOtherCount(2);
//        soenStudent.put("soens1111", student);
//
//        return soenMap;
//    }
//
//    public ConcurrentHashMap<String, HashMap<String, Course>> iInit(){
//
//        // ---------------- init serverMap -----------------------
//
//        HashMap<String, Course> fallMap = new HashMap<>();
//        HashMap<String, Course> winterMap = new HashMap<>();
//        HashMap<String, Course> summerMap = new HashMap<>();
//
//        ArrayList<String> fallStudents = new ArrayList<>();
//        ArrayList<String> winterStudents = new ArrayList<>();
//        ArrayList<String> summerStudents = new ArrayList<>();
//
//        course = new Course("inse6230");
//        fallStudents.add("comps1111");
//        fallStudents.add("inses1111");
//        course.setStudentList(fallStudents);
//        fallMap.put("inse6230", course);
//        inseMap.put("fall", fallMap);
//
//        course = new Course("inse6210");
//        winterStudents.add("soens1111");
//        winterStudents.add("inses1111");
//        course.setStudentList(winterStudents);
//        winterMap.put("inse6210", course);
//        inseMap.put("winter", winterMap);
//
//        course = new Course("inse6630");
//        summerStudents.add("inses1111");
//        course.setStudentList(summerStudents);
//        summerMap.put("inse6630", course);
//        inseMap.put("summer", summerMap);
//
//        // --------------- init inses1111 ----------------
//
//        ArrayList<String> enrolledCourseFall = new ArrayList();
//        ArrayList<String> enrolledCourseWinter = new ArrayList();
//        ArrayList<String> enrolledCourseSummer = new ArrayList();
//
//        student = new Student("inses1111");
//
//        enrolledCourseFall.add("inse6230");
//        student.setEnrolledCourse("fall", enrolledCourseFall);
//
//        enrolledCourseWinter.add("inse6210");
//        enrolledCourseWinter.add("comp6521");
//        student.setEnrolledCourse("winter", enrolledCourseWinter);
//
//        enrolledCourseSummer.add("inse6630");
//        student.setEnrolledCourse("summer", enrolledCourseSummer);
//
//        student.setCourseCount("fall", 1);
//        student.setCourseCount("winter", 2);
//        student.setCourseCount("summer", 1);
//        student.setOtherCount(1);
//        inseStudent.put("inses1111", student);
//
//        return inseMap;
//    }
//
//    public ConcurrentHashMap<String, Student> getStudentMap(String department) {
//        if (department.equals("comp")) {
//            return compStudent;
//        } else if (department.equals("soen")) {
//            return soenStudent;
//        } else if (department.equals("inse")) {
//            return inseStudent;
//        }
//        return null;
//    }
//}
