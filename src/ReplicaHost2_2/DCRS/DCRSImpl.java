package ReplicaHost2.DCRS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class DCRSImpl {
    private String department;
    private Logger logger;
    private ConcurrentHashMap<String, ConcurrentHashMap<String, Course>> serverMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Student> studentMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, String> courseMap = new ConcurrentHashMap<>(); // <CourseId, Semester>

    public DCRSImpl(String department, Logger logger){
        this.department = department;
        this.logger = logger;

        ConcurrentHashMap<String, ReplicaHost2.DCRS.Course> fallCourse = new ConcurrentHashMap<>();
        serverMap.put("fall", fallCourse);

        ConcurrentHashMap<String, ReplicaHost2.DCRS.Course> winterCourse = new ConcurrentHashMap<>();
        serverMap.put("winter", winterCourse);

        ConcurrentHashMap<String, ReplicaHost2.DCRS.Course> summerCourse = new ConcurrentHashMap<>();
        serverMap.put("summer", summerCourse);

        ReplicaHost2.DCRS.Student student1 = new ReplicaHost2.DCRS.Student(department + "s1111");
        ReplicaHost2.DCRS.Student student2 = new ReplicaHost2.DCRS.Student(department + "s2222");
        ReplicaHost2.DCRS.Student student3 = new ReplicaHost2.DCRS.Student(department + "s3333");
        ReplicaHost2.DCRS.Student student4 = new ReplicaHost2.DCRS.Student(department + "s4444");
        ReplicaHost2.DCRS.Student student5 = new ReplicaHost2.DCRS.Student(department + "s5555");
        ReplicaHost2.DCRS.Student student6 = new ReplicaHost2.DCRS.Student(department + "s6666");
        ReplicaHost2.DCRS.Student student7 = new ReplicaHost2.DCRS.Student(department + "s7777");
        ReplicaHost2.DCRS.Student student8 = new ReplicaHost2.DCRS.Student(department + "s8888");
        ReplicaHost2.DCRS.Student student9 = new ReplicaHost2.DCRS.Student(department + "s9999");
        ReplicaHost2.DCRS.Student student10 = new ReplicaHost2.DCRS.Student(department + "s1010");

        studentMap.put(department + "s1111",student1);
        studentMap.put(department + "s2222",student2);
        studentMap.put(department + "s3333",student3);
        studentMap.put(department + "s4444",student4);
        studentMap.put(department + "s5555",student5);
        studentMap.put(department + "s6666",student6);
        studentMap.put(department + "s7777",student7);
        studentMap.put(department + "s8888",student8);
        studentMap.put(department + "s9999",student9);
        studentMap.put(department + "s1010",student10);

        if (department.equals("comp")){
            addCourse("comp1","fall");
            addCourse("comp2","fall");
        }
        if (department.equals("soen")){
            addCourse("soen1","fall");
            addCourse("soen2","fall");
        }
        if (department.equals("inse")){
            addCourse("inse1","fall");
            addCourse("inse2","fall");
        }
    }

    public String addCourse(String courseId, String semester){
        if (serverMap.get(semester) == null ) return "The Semester Does Not Exist! Please Check The Semester";
        String result = "Add successful";
        if (!departmentCheck(courseId)) {
            logger.info("Add Course:" + courseId + " " + semester + ":" + "Not Authorized");
            result = "You Are Not Authorized To Add The Course ";
            return result;
        }

        ConcurrentHashMap<String, Course> semesterMap = serverMap.get(semester);
        if (semesterMap.containsKey(courseId)) {
            result = "The Course Have Already Added!";
            return result;
        } else {
            synchronized (semesterMap) {
                Course course = new Course(courseId);
                course.setSemester(semester);
                courseMap.put(courseId, semester);
                semesterMap.put(courseId, course);
                serverMap.put(semester, semesterMap);
            }
        }
        logger.info("Add Course:" + courseId + " " + semester + ":" + result);
        return result;
    }

    public String removeCourse(String courseId, String semester) {
        if (serverMap.get(semester) == null ) return "The Semester Does Not Exist! Please Check The Semester";
        String result = "Remove Successful";
        if (!departmentCheck(courseId)) {
            logger.info("Remove Course:" + courseId + " " + semester + ":" + " Not Authorized");
            result = "You Are Not Authorized To Delete The Course ";
            return result;
        }

        ConcurrentHashMap<String, Course> semesterMap = serverMap.get(semester);
        if (!semesterMap.containsKey(courseId)) {
            logger.info("Remove Course:" + courseId + " " + semester + ":" + "The Course Doesn't Exist");
            result = "The Course Doesn't Exist";
            return result;
        } else {
            synchronized (semesterMap) {
                ArrayList<String> studentList = semesterMap.get(courseId).getStudentList();
                Course course = semesterMap.get(courseId);
                semesterMap.remove(courseId);
                serverMap.put(semester, semesterMap);
                courseMap.remove(courseId, semester);
                for (String studentId : studentList) {
                    Student student = studentMap.get(studentId);
                    removeStudentCourse(student, semester, courseId);
                    if (!course.getDepartment().equals(student.getMajor())) {
                        student.decreaseOtherCount();
                        student.decreaseCount(semester);
                    } else {
                        student.decreaseCount(semester);
                    }
                }
                course.resetCourse();
            }
        }
        logger.info("Remove Course:" + courseId + " " + semester + ":" + " Remove Successful");
        return result;
    }

    public String[] listCourseAvailability(String semester) {
        logger.info("List Course Availability :" + semester);
        String[] localAvailability = localCourseAvailability(semester);
        String[] standardLA = new String[localAvailability.length];
        for (int i = 0; i < localAvailability.length; i++) {
            standardLA[i] = localAvailability[i].trim();
        }

        String message = "listCourseAvailability " + semester;
        String reply = groupSend(message);
        String[] replyArray = reply.split("\\s+");

        ArrayList<String> availableList = new ArrayList<>();
        for (String sA : standardLA) {
            availableList.add(sA);
        }
        for (String rA : replyArray) {
            availableList.add(rA);
        }

        String[] result = new String[availableList.size()];
        for (String aL : availableList) {
            int index = availableList.indexOf(aL);
            result[index] = aL;
        }
        Arrays.sort(result);
        return result;
    }

    public String[] localCourseAvailability(String semester) {
        ConcurrentHashMap<String, Course> semesterMap = serverMap.get(semester);
        ArrayList<Course> courseList = new ArrayList<>(semesterMap.values());
        String[] result = new String[courseList.size()];
        for (Course course : courseList) {
            int index = courseList.indexOf(course);
            String course_capacity = course.getCourseName() + "--" + course.getCapacity() + " ";
            result[index] = course_capacity;
        }
        return result;
    }

    public String enrolCourse(String studentId, String courseId, String semester) {
        String result = courseId + " Enroll Successfully";
        courseId = courseId.toLowerCase();
        studentId = studentId.toLowerCase();
        String courseDepartment = getDepartment(courseId);

        Student student;
        if (studentMap.keySet().contains(studentId)) {
            student = studentMap.get(studentId);
        } else {
            student = new Student(studentId);
            studentMap.put(studentId, student);
        }

        if (student.getCourseCount(semester) >= 3) {
            result = "Do not allow to enroll";
            return result;
        }

        if (courseDepartment.equals(getDepartment(studentId))) {
            result = enrolLocalCourse(studentId, courseId, semester);
            if (result.split(",")[0].equals("false")) {
                return result.split(",")[1];
            } else {
                int count = student.getCourseCount(semester) + 1;
                student.setCourseCount(semester, count);
                HashMap<String, ArrayList<String>> enrolledCourse = student.getEnrolledCourse();
                ArrayList<String> courses = enrolledCourse.get(semester);
                courses.add(courseId);
                student.setEnrolledCourse(semester, courses);
                studentMap.put(studentId, student);
                result = result.split(",")[1];
            }
        } else {
            if (student.getOtherCount() >= 2) {
                result = courseId + " Do not allow to enroll";
                return result;
            }

            int receiverPort = getUDPPort(courseDepartment);
            String message = "enrolCourse," + studentId + "," + courseId + "," + semester;
            String reply = UdpClient.request(message, receiverPort);
            String spReply[] = reply.split(",");
            if (spReply[0].equals("false")) {
                result = spReply[1];
                return result;
            } else {
                int count = student.getCourseCount(semester) + 1;
                student.setCourseCount(semester, count);
                student.increaseOtherCount();
                HashMap<String, ArrayList<String>> enrolledCourse = student.getEnrolledCourse();
                ArrayList<String> courses = enrolledCourse.get(semester);
                courses.add(courseId);
                student.setEnrolledCourse(semester, courses);
                studentMap.put(studentId, student);
                result = result.split(",")[1];
            }
        }
        logger.info("Enroll Course :" + studentId + " " + courseId + " " + semester + ":" + result);
        return result;
    }

    public String dropCourse(String studentId, String courseId) {
        String result = "Drop Successful!";
        courseId = courseId.toLowerCase();
        studentId = studentId.toLowerCase();
        String courseDepartment = getDepartment(courseId);

        Student student;
        if (studentMap.keySet().contains(studentId)) {
            student = studentMap.get(studentId);
        } else {
            student = new Student(studentId);
            studentMap.put(studentId, student);
        }

        String semester = courseMap.get(courseId);
        if (courseDepartment.equals(getDepartment(studentId))) {
            result = dropLocalCourse(studentId, courseId, semester);
            if (result.split(",")[0].equals("false")) {
                return result.split(",")[1];
            } else {
                int count = student.getCourseCount(semester) - 1;
                student.setCourseCount(semester, count);
                HashMap<String, ArrayList<String>> enrolledCourse = student.getEnrolledCourse();
                ArrayList<String> courses = enrolledCourse.get(semester);
                courses.remove(courseId);
                student.setEnrolledCourse(semester, courses);
                studentMap.put(studentId, student);
                result = result.split(",")[1];
            }
        } else {
            int receiverPort = getUDPPort(courseDepartment);
            String message = "dropCourse," + studentId + "," + courseId + "," + semester;
            String reply = UdpClient.request(message, receiverPort);
            String spReply[] = reply.split(",");
            if (spReply[0].equals("false")) {
                result = spReply[1];
                return result;
            } else {
                int count = student.getCourseCount(semester) - 1;
                student.setCourseCount(semester, count);
                student.decreaseOtherCount();
                HashMap<String, ArrayList<String>> enrolledCourse = student.getEnrolledCourse();
                ArrayList<String> courses = enrolledCourse.get(semester);
                courses.remove(courseId);
                student.setEnrolledCourse(semester, courses);
                studentMap.put(studentId, student);
                result = result.split(",")[1];
            }
        }
        logger.info("Drop Course :" + studentId + " " + courseId + ":" + result);
        return result;
    }

    public String enrolLocalCourse (String studentId, String courseId, String semester) {
        String result = "true," + courseId + " Enroll Successfully";

        ConcurrentHashMap<String, Course> semesterMap = serverMap.get(semester);
        if (!semesterMap.containsKey(courseId)) {
            result = "false,The Course does not Exist!";
            return result;
        }

        Course course = semesterMap.get(courseId);
        ArrayList<String> studentList = course.getStudentList();

        if (course.getCapacity() <= 0) {
            result = "false," + courseId + " Do not allow to enroll";
            return result;
        }

        if (studentList.contains(studentId)) {
            result = "false," + courseId + " Do not allow to enroll";
            return result;
        }

        course.decreaseCapacity();
        studentList.add(studentId);
        course.setStudentList(studentList);
        synchronized (semesterMap) {
            semesterMap.put(courseId, course);
            serverMap.put(semester, semesterMap);
        }
        return result;
    }

    public String dropLocalCourse (String studentId, String courseId, String semester) {
        String result = "true,Drop Successful!";

        ConcurrentHashMap<String, Course> semesterMap = serverMap.get(semester);

        if (!semesterMap.containsKey(courseId)) {
            result = "false,Drop Fail";
            return result;
        }

        Course course = semesterMap.get(courseId);
        ArrayList<String> studentList = course.getStudentList();

        if (!studentList.contains(studentId)){
            result = "false,Course Not Found In The Student Course List";
            return result;
        }

        course.increaseCapacity();
        studentList.remove(studentId);
        course.setStudentList(studentList);
        synchronized (semesterMap) {
            semesterMap.put(courseId, course);
            serverMap.put(semester, semesterMap);
        }
        return result;
    }

    public String swapCourse (String studentId, String newCourseId, String oldCourseId) {
        String result = "Swap Course Successful";
        newCourseId = newCourseId.toLowerCase();
        oldCourseId = oldCourseId.toLowerCase();
        studentId = studentId.toLowerCase();

        Student student;
        if (studentMap.keySet().contains(studentId)) {
            student = studentMap.get(studentId);
        } else {
            student = new Student(studentId);
            studentMap.put(studentId, student);
        }

        String oldSemester = getSemester(oldCourseId);
        String newSemester = getSemester(newCourseId);

        if (!oldSemester.equals(newSemester)) {
            logger.info("Swap Course:" + oldCourseId + "->" + newCourseId + ":" + " Swap Fail");
            result = "Swap Fail";
            return result;
        }

        HashMap<String, ArrayList<String>> enrolledCourse = student.getEnrolledCourse();
//        for (String s : enrolledCourse.keySet()) {
//            System.out.println("enrolled course ==>" + s + enrolledCourse.get(s).toString());
//        }

        ArrayList<String> enrolledList = enrolledCourse.get(oldSemester);
//        System.out.println(enrolledList.toString());

        for (String courseName : enrolledList) {
//            System.out.println("courseName ==> " + courseName);
//            System.out.println("oldid ==> " + oldCourseId);
//            System.out.println(courseName.equals(oldCourseId));
            if (!courseName.equals(oldCourseId)) {
                logger.info("Swap Course:" + oldCourseId + "->" + newCourseId + ":" + " Swap Fail");
                result= "Swap Fail";
                return result;
            }
        }

        if (checkAvailability(newCourseId).trim().equals("false")) {
            logger.info("Swap Course:" + oldCourseId + "->" + newCourseId + ":" + " Swap Fail");
            result = "Swap Fail";
            return result;
        }


        dropCourse(studentId, oldCourseId);
        enrolCourse(studentId, newCourseId, newSemester);

        logger.info("Swap Course:" + oldCourseId + "->" + newCourseId + ":" + " Swap Successful");
        return result;
    }

    public String checkLocalAvailbility(String courseId) {
        for (ConcurrentHashMap<String, Course> semesterMap : serverMap.values()) {
            for (String courseName : semesterMap.keySet()) {
                if (courseName.equals(courseId)) {
                    Course course = semesterMap.get(courseName);
                    if (course.getCapacity() == 0) {
                        return "false";
                    } else {
                        return "true";
                    }
                }
            }
        }
        return "false";
    }

    public String getLocalSemester(String courseId) {
        String result = "null";
        for (String semester : serverMap.keySet()) {
            ConcurrentHashMap<String, Course> semesterMap = serverMap.get(semester);
            for (String courseName : semesterMap.keySet()) {
                if (courseName.equals(courseId)) {
                    result = semester;
                    return result;
                }
            }
        }
        return result;
    }

    public String[] getClassSchedule(String studentId) {
        logger.info("Get Class Schedule :" + studentId);
        Student student;
        if (studentMap.keySet().contains(studentId)) {
            student = studentMap.get(studentId);
        } else {
            student = new Student(studentId);
            studentMap.put(studentId, student);
        }

        HashMap<String, ArrayList<String>> classSchedule = student.getEnrolledCourse();
        ArrayList<String> classes = new ArrayList<>();
        for (String semes : classSchedule.keySet()) {
            if (classSchedule.get(semes).size() == 0) {
                continue;
            } else {
                ArrayList<String> semesterClasses = classSchedule.get(semes);
                for (String semesterClass : semesterClasses) {
                    classes.add(semesterClass + "--" + semes);
                }
            }
        }

        String[] result = new String[classes.size()];
        int index = 0;
        for (String c : classes) {
            result[index] = c;
            index++;
        }
        Arrays.sort(result);
        return result;
    }

    private void removeStudentCourse(Student student, String semester, String courseId){
        HashMap<String, ArrayList<String>> enrolledMap = student.getEnrolledCourse();
        ArrayList<String> enrolledList = enrolledMap.get(semester);
        enrolledList.remove(courseId);
        student.setEnrolledCourse(semester, enrolledList);
    }

    private Boolean departmentCheck(String courseId) {
        String courseDepart = getDepartment(courseId);
        if (!courseDepart.equals(this.department)) {
            return false;
        }
        return true;
    }

    private String groupSend(String message) {
        String reply = "";
        if (this.department.equals("comp")){
            String reply1 = UdpClient.request(message , Config.UDP_SOEN_PORT);
            String reply2 = UdpClient.request(message , Config.UDP_INSE_PORT);
            reply = reply1 + reply2;
        } else if(this.department.equals("soen")){
            String reply1 = UdpClient.request(message , Config.UDP_COMP_PORT);
            String reply2 = UdpClient.request(message , Config.UDP_INSE_PORT);
            reply = reply1 + reply2;
        } else if(this.department.equals("inse")){
            String reply1 = UdpClient.request(message , Config.UDP_COMP_PORT);
            String reply2 = UdpClient.request(message , Config.UDP_SOEN_PORT);
            reply = reply1 + reply2;
        }

        return reply;
    }

    private String getDepartment(String id) {
        String result = id.substring(0, 4);
        return result;
    }

    private int getUDPPort(String department) {
        int result = 0;
        if (department.equals("comp")) {
            result = Config.UDP_COMP_PORT;
        } else if (department.equals("soen")) {
            result = Config.UDP_SOEN_PORT;
        } else if (department.equals("inse")) {
            result = Config.UDP_INSE_PORT;
        }
        return result;
    }

    private String getSemester(String courseId) {
        if (departmentCheck(courseId)) {
            return getLocalSemester(courseId);
        } else {
            String courseDepartment = getDepartment(courseId);
            int receiverPort = getUDPPort(courseDepartment);
            String message = "getSemester," + courseId;
            String reply = UdpClient.request(message, receiverPort).trim();
            return reply;
        }
    }

    private String checkAvailability(String courseId) {
        if (departmentCheck(courseId)) {
            return checkLocalAvailbility(courseId);
        } else {
            String courseDepartment = getDepartment(courseId);
            int receiverPort = getUDPPort(courseDepartment);
            String message = "checkAvailability," + courseId;
            String reply = UdpClient.request(message, receiverPort);
            return reply;
        }
    }
}
