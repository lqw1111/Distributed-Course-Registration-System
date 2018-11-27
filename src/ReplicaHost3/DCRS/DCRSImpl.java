package ReplicaHost3.DCRS;

import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class DCRSImpl {
    private Map<String, Map<String, Course>> courseSchedule;
    private Map<String, Student> students;
    private String departmentTag;
    public Logger logger;

    public DCRSImpl(String departmentTag, Logger logger) {
        this.departmentTag = departmentTag;
        this.courseSchedule = new HashMap<>();
        this.students = new HashMap<>();
        this.courseSchedule.put("fall", new HashMap<>());
        this.courseSchedule.put("winter", new HashMap<>());
        this.courseSchedule.put("summer", new HashMap<>());
        this.logger = logger;
    }

    public String swapCourseDropReply(String studentID,String oldCourseID) {
        Course course = this.searchCourse(oldCourseID);
        if (course == null) return "Swap Fail";
        if(course.drop(studentID))
            return "Swap Course Successful";
        else return "Swap Fail";
    }

    public String swapCourseEnrolRequest(String studentID, String newCourseID, String oldCourseID) {
        // check if the course if full or not
        Course course = this.searchCourse(newCourseID);
        synchronized (course) {
            if (course.isStatusFull())
                return "Swap Fail";
            String[] param = {"swap_drop", studentID,  oldCourseID};
            String res = send(dataProcess(param), oldCourseID.substring(0, 4));
            if (res.contains("Successful"))
                course.swapEnrol(studentID);
            return res;
        }
    }

    public String swapCourseDropRequest(String studentID, String newCourseID, String oldCourseID) {
        // check is the new course in the local server
        if (checkDepartment(newCourseID)) {
            // check is the two courses in the same semester
            if (!checkIsInSameSemester(newCourseID, oldCourseID)) return "Swap Fail";
            // run local course exchange, if success return SUCCESS
            if(!swapLocalCourse(studentID, newCourseID, oldCourseID))
               return "Swap Fail";
            return "Swap Course Successful";
        }
        String[] param = {"swap_enrol", studentID, newCourseID, oldCourseID};
        String res = this.send(dataProcess(param), newCourseID.substring(0, 4));
        return res;
}

    private boolean swapLocalCourse(String studentID, String newCourseID, String oldCourseID) {
        Course newCourse = this.searchCourse(newCourseID);
        Course oldCourse = this.searchCourse(oldCourseID);
        if (!oldCourse.requestLocalSwap(newCourse, studentID))
            return false;
        return true;
    }

    public String swapCourse(String studentID, String newCourseID, String oldCourseID) {
        Student student = students.get(studentID);
        if(student == null)
            students.put(studentID, new Student(studentID));
        if (!student.checkCourse(oldCourseID))
            return "Swap Fail";
        if (student.checkCourse(newCourseID))
            return "Swap Fail";
        int numberIfSwap = student.getRemoteCourseNumberIfSwap(oldCourseID, newCourseID);
        if (numberIfSwap > 2)
            return "Swap Fail";
        if (numberIfSwap == -1)
            return "Swap Fail";
        if (checkDepartment(newCourseID) && checkDepartment(oldCourseID)) {
            if (!checkIsInSameSemester(newCourseID, oldCourseID))
                return "Swap Fail";
            if(!swapLocalCourse(studentID, newCourseID, oldCourseID))
                return "Swap Fail";
            student.swapCourse(oldCourseID, newCourseID);
            return "Swap Course Successful";
        }
        String[] param = {"swap", studentID, newCourseID, oldCourseID};
        String res = this.send(dataProcess(param), oldCourseID.substring(0, 4));
        if (res.contains("Successful")) {
            student.swapCourse(oldCourseID, newCourseID);
        }
        return res;
    }

    private boolean checkIsInSameSemester(String newCourseID, String oldCourseID) {
        String oldSemester = this.getSemester(oldCourseID);
        String newSemester = this.getSemester(newCourseID);
        if (oldSemester.equals(newSemester)) {
            return true;
        }
        return false;
    }

    public String addCourse(String courseID, String semester) {
        if (!courseID.substring(0, 4).equals(this.departmentTag)) {
            logger.info("Add Course:" + courseID + " " + semester + ":" + "Not Authorized");
            return "You Are Not Authorized To Add The Course ";
        }
        if (!checkCourse(courseID, semester)) {
            synchronized (this) {
                this.courseSchedule.get(semester).put(courseID, new Course(courseID, 2));
            }
            logger.info("Add Course:" + courseID + " " + semester + ":" + "Add Successful");
            return "Add Successful";
        }
        logger.info("Add Course:" + courseID + " " + semester + ":" + "The Course Have Already Added!");
        return "The Course Have Already Added!";
    }

    public String removeCourse(String courseID, String semester) {
        if (!courseID.substring(0, 4).equals(this.departmentTag)) {
            logger.info("Remove Course:" + courseID + " " + semester + ":" + " Not Authorized");
            return "You Are Not Authorized To Add The Course ";
        }
        if (!checkCourse(courseID, semester)) {
            logger.info("Remove Course:" + courseID + " " + semester + ":" + "The Course Doesn't Exist");
            return "The Course Doesn't Exist";
        }
        List<String> student_registerred = this.courseSchedule.get(semester).get(courseID).getStudent_registerred();
        for (String key : DepartmentToPort.map.keySet()) {
            for (String studentID : student_registerred) {
                List<String> studentIDs = new ArrayList<>();
                studentIDs.add("remove_student_course");
                studentIDs.add(courseID);
                if (studentID.contains(key)) {
                    studentIDs.add(studentID);
                }
                this.send(dataProcess(studentIDs.toArray(new String[studentIDs.size()])), key);
            }
        }
        this.courseSchedule.get(semester).get(courseID).clear();
        synchronized (this) {
            this.courseSchedule.get(semester).remove(courseID);
        }
        logger.info("Remove Course:" + courseID + " " + semester + ":" + " Remove Successful");
        return "Remove Successful";
    }

    public void removeStudentCourse(String[] params) {
        for (int i = 2; i < params.length; i++) {
            if (students.containsKey(params[i])) {
                students.get(params[i]).drop(params[1]);
            }
        }
    }

    public String[] listCourseAvailability(String semester) {
        List<String> list = new ArrayList<>();
        for (String key : DepartmentToPort.map.keySet()) {
            String[] param = {"show_available_courses", semester};
            String[] units = this.send(dataProcess(param), key).split(" ");
            for (String unit : units) {
                list.add(unit);
            }
        }
        String[] result = list.toArray(new String[list.size()]);
        Arrays.sort(result);
        return result;
    }

    public String listCourseAvailabilityInLocal(String semester) {
        Map<String, Course> courseMap = courseSchedule.get(semester);
        List<Course> list = new ArrayList<>();
        for (Course course : courseMap.values()) {
            list.add(course);
        }
        return list.stream().map((a) -> a.getCourse_name() + "--" + a.getAvailableSpave()).collect(Collectors.joining(" "));
    }

    public String[] getClassSchedule(String studentID) {
        if (!students.containsKey(studentID)) {
            students.put(studentID, new Student(studentID));
            return null;
        }
        logger.info("Get Class Schedule :" + studentID);
        return students.get(studentID).getSchedule();
    }

    public String enrolCourse(String studentID, String courseID, String semester)  {
        if (!checkDepartment(studentID)) {
            if(this.courseSchedule.get(semester).get(courseID).register(studentID)) {
                logger.info("Enroll Course :" + studentID + " " + courseID + " " + semester + ":" + (courseID + " Enroll Successfully"));
                return (courseID + " Enroll Successfully");
            }
            else {
                logger.info("Enroll Course :" + studentID + " " + courseID + " " + semester + ":" + "Do not allow to enroll");
                return "Do not allow to enroll";
            }
        }
        if (!students.containsKey(studentID)) {
            students.put(studentID, new Student(studentID));
        }
        Student student = students.get(studentID);

        if (student.checkCourse(courseID, semester)) {
            logger.info("Enroll Course :" + studentID + " " + courseID + " " + semester + ":" + (courseID + " Do not allow to enroll"));
            return (courseID + " Do not allow to enroll");
        }
        int registedNum = student.getRegistedNum(semester);
        if (registedNum == 3) {
            logger.info("Enroll Course :" + studentID + " " + courseID + " " + semester + ":" + (courseID + " Do not allow to enroll"));
            return (courseID + " Do not allow to enroll");
        }
        if (!checkDepartment(courseID)) {
            int remoteCourseNum = student.getRemoteCourseNum(semester);
            if (remoteCourseNum == 2) {
                logger.info("Enroll Course :" + studentID + " " + courseID + " " + semester + ":" + "Do not allow to enroll");
                return "Do not allow to enroll";
            }
            String[] param = {"enrol", studentID, courseID, semester};
            String res = this.send(dataProcess(param), courseID.substring(0, 4));
            if (res.contains("Successfully")) {
                student.registerCourse(courseID, semester);
            }
            logger.info("Enroll Course :" + studentID + " " + courseID + " " + semester + ":" + res);
            return res;
        }

        if(!this.courseSchedule.get(semester).get(courseID).register(studentID)) {
            logger.info("Enroll Course :" + studentID + " " + courseID + " " + semester + ":" + (courseID + " Do not allow to enroll"));
            return (courseID + " Do not allow to enroll");
        }

        student.registerCourse(courseID, semester);
        logger.info("Enroll Course :" + studentID + " " + courseID + " " + semester + ":" + (courseID + " Enroll Successfully"));
        return (courseID + " Enroll Successfully");
    }

    public String dropCourse(String studentID, String courseID) {
        Course course = this.searchCourse(courseID);
        if (!checkDepartment(studentID)) {
//            if (course == null) return "SYSTEM_DOES_NOT_HAVE_THE_COURSE";
            if(course.drop(studentID))
                return "Drop Successful!";
             else return "Course Not Found In The Student Course List";
        }
        if (!students.containsKey(studentID)) {
            students.put(studentID, new Student(studentID));
            return "Course Not Found In The Student Course List";
        }
        Student student = students.get(studentID);
        if (!checkDepartment(courseID)) {
            String[] param = {"drop", studentID, courseID};
            String res = this.send(dataProcess(param), courseID.substring(0, 4));
            if (res.contains("Successful")) {
                student.drop(courseID);
            }
            return res;
        }
//        if (course == null) return "SYSTEM_DOES_NOT_HAVE_THE_COURSE";
        if (course.drop(studentID)) {
            student.drop(courseID);
            return "Drop Successful!";
        } else return "Drop Fail";
    }

    private boolean checkCourse(String courseID, String semester) {
        return courseSchedule.get(semester).get(courseID) != null;
    }

    private boolean checkDepartment(String ID) {
        return departmentTag.equals(ID.substring(0, 4));
    }

    public Course searchCourse(String courseID) {
        Course course = null;
        for (Map<String, Course> stringCourseMap : courseSchedule.values()) {
           course = stringCourseMap.get(courseID);
           if (course != null) {
               return course;
           }
        }
        return course;
    }

    private String getSemester(String courseID) {
        for (Map.Entry<String, Map<String, Course>> stringMapEntry : courseSchedule.entrySet()) {
            if (stringMapEntry.getValue().containsKey(courseID))
                return stringMapEntry.getKey();
        }
       return null;
    }

    private String dataProcess(String[] data) {
        return Arrays.stream(data).collect( Collectors.joining( " " ) );
    }

    public String send(String data, String dest) {
        DatagramSocket aSocket = null;
        String reply = null;
        this.logger.info("\nUDP AT PORT " + this.departmentTag + " SEND REQUEST TO " + DepartmentToPort.map.get(dest) + ":\n"  + data.toUpperCase());
        byte [] message = data.getBytes();
        try {
            aSocket = new DatagramSocket();
            DatagramPacket packet = new DatagramPacket(message, message.length, InetAddress.getByName("localhost"), DepartmentToPort.map.get(dest));
            aSocket.send(packet);
            byte data2[] = new byte[1024];
            DatagramPacket packet2 = new DatagramPacket(data2, data2.length);
            aSocket.receive(packet2);
            reply = new String(data2, 0, packet2.getLength());
            System.out.println("Reply: " + reply);
            aSocket.close();
        }catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        this.logger.info("\nUDP AT PORT " + this.departmentTag + " GET REPLY FROM " + DepartmentToPort.map.get(dest) + ":\n" + reply.toUpperCase());
        return reply;
    }
}

