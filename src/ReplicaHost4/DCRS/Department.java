//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ReplicaHost4.DCRS;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

class Department {
    public static final int MAX_ELECTIVE_COUSE_LIMIT = 2;
    public static final int MAX_LIMIT = 3;
    public static final int DEFAULT_CAPACITY = 2;
    private Map<String, Map<String, CourseRecord>> database;
    private DepartEnum name;

    Department(DepartEnum name) {
        Map<String, CourseRecord> fallCourseMap = new HashMap();
        Map<String, CourseRecord> wintCourseMap = new HashMap();
        Map<String, CourseRecord> summCourseMap = new HashMap();
        this.database = new HashMap();
        this.database.put("fall", fallCourseMap);
        this.database.put("winter", wintCourseMap);
        this.database.put("summer", summCourseMap);
        this.name = name;
    }

    String getName() {
        return this.name.getName();
    }

    String addCourse(String courseID, String semester) {
        Map<String, CourseRecord> courseMap = (Map)this.database.get(semester);
        if (courseMap.containsKey(courseID)) {
            return "The Course Have Already Added!";
        } else {
            CourseRecord initialRecord = new CourseRecord();
            courseMap.put(courseID, initialRecord);
            return "Add Successful";
        }
    }

    String removeCourse(String courseID, String semester) {
        Map<String, CourseRecord> courseMap = (Map)this.database.get(semester);
        if (courseMap.containsKey(courseID)) {
            CourseRecord saveRecod = (CourseRecord)courseMap.remove(courseID);
            return "Remove Successful";
        } else {
            return "The Course Doesn't Exist";
        }
    }

    String listCourseAvailability(String semester) {
        String result = "";
        Map<String, CourseRecord> semesterRecord = (Map)this.database.get(semester);

        String s;
        for(Iterator var4 = semesterRecord.keySet().iterator(); var4.hasNext(); result = result.concat(((CourseRecord)semesterRecord.get(s)).getAvailability() + " ")) {
            s = (String)var4.next();
            result = result.concat(s) + "--";
        }

        return result;
    }

    String enrolCourse(String studentID, String courseID, String semester) {
        String result = "";
        Map<String, CourseRecord> semesterRecord = (Map)this.database.get(semester);
        if (semesterRecord.containsKey(courseID)) {
            CourseRecord course = (CourseRecord)semesterRecord.get(courseID);
            if (course.getAvailability() >= 1 && !course.enrolled(studentID)) {
                course.enroll(studentID);
                result = courseID + " Enroll Successfully";
            } else if (course.enrolled(studentID)) {
                result = courseID + " Do not allow to enroll";
            } else if (course.getAvailability() < 1) {
                result = courseID + " Do not allow to enroll";
            }
        } else {
            result = "The Course does not Exist!";
        }

        return result;
    }

    String getClassSchedule(String studentID) {
        String result = "";
        Iterator var3 = this.database.keySet().iterator();

        while(var3.hasNext()) {
            String semester = (String)var3.next();
            Map<String, CourseRecord> record = (Map)this.database.get(semester);
            Iterator var6 = record.keySet().iterator();

            while(var6.hasNext()) {
                String s = (String)var6.next();
                if (((CourseRecord)record.get(s)).enrolled(studentID)) {
                    result = result.concat(s) + "--";
                    result = result.concat(semester) + " ";
                }
            }
        }

        return result;
    }

    String dropCourse(String studentID, String courseID) {
        String result = "Course Not Found In The Student Course List";
        Iterator var4 = this.database.keySet().iterator();

        while(var4.hasNext()) {
            String semester = (String)var4.next();
            Map<String, CourseRecord> record = (Map)this.database.get(semester);
            if (record.containsKey(courseID)) {
                CourseRecord courseRecord = (CourseRecord)record.get(courseID);
                if (courseRecord.enrolled(studentID)) {
                    courseRecord.drop(studentID);
                    result = "Drop Successful!";
                } else {
                    result = "Course Not Found In The Student Course List";
                }
            }
        }

        return result;
    }

    String getStudentRecord(String studentID, String semester) {
        Map<String, CourseRecord> record = (Map)this.database.get(semester);
        int result = 0;
        Iterator var5 = record.keySet().iterator();

        while(var5.hasNext()) {
            String s = (String)var5.next();
            CourseRecord c = (CourseRecord)record.get(s);
            if (c.enrolled(studentID)) {
                ++result;
            }
        }

        return Integer.toString(result);
    }

    String checkOldCourse(String studentID, String courseID) {
        String result = "null";
        boolean existCourse = false;
        String s = "fall";
        Iterator var6 = this.database.entrySet().iterator();

        while(var6.hasNext()) {
            Entry<String, Map<String, CourseRecord>> e = (Entry)var6.next();
            if (((Map)e.getValue()).containsKey(courseID)) {
                existCourse = true;
                s = (String)e.getKey();
            }
        }

        boolean enrolled = false;
        if (((Map)this.database.get(s)).containsKey(courseID)) {
            CourseRecord c = (CourseRecord)((Map)this.database.get(s)).get(courseID);
            if (c.enrolled(studentID)) {
                enrolled = true;
            }

            existCourse = true;
        } else {
            existCourse = false;
        }

        if (!existCourse) {
            result = String.format("oldcourse %s not exist.", courseID);
        } else if (!enrolled) {
            result = String.format("oldcourse %s not enrolled for %s.", courseID, studentID);
        } else {
            result = String.format("%s", s);
        }

        return result;
    }

    String checkNewCourse(String studentID, String courseID) {
        String result = "null";
        boolean existCourse = false;
        String s = "fall";
        Iterator var6 = this.database.entrySet().iterator();

        while(var6.hasNext()) {
            Entry<String, Map<String, CourseRecord>> e = (Entry)var6.next();
            if (((Map)e.getValue()).containsKey(courseID)) {
                existCourse = true;
                s = (String)e.getKey();
            }
        }

        boolean reenrolled = false;
        if (((Map)this.database.get(s)).containsKey(courseID)) {
            CourseRecord c = (CourseRecord)((Map)this.database.get(s)).get(courseID);
            if (c.enrolled(studentID)) {
                reenrolled = true;
            }

            existCourse = true;
        } else {
            existCourse = false;
        }

        if (!existCourse) {
            result = String.format("newcourse %s not exist.", courseID);
        } else if (reenrolled) {
            result = String.format("newcourse %s reenrolled for %s.", courseID, studentID);
        } else {
            result = String.format("%s", s);
        }

        return result;
    }

    String checkEnrolAvail(String courseID, String s) {
        return ((CourseRecord)((Map)this.database.get(s)).get(courseID)).enrolAvail() ? "True" : "False";
    }
}
