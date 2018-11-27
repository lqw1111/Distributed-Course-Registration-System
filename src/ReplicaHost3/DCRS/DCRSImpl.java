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
        if (course == null) return "THE_COURSE_HAS_BEEN_MOVED";
        if(course.drop(studentID))
            return "SUCCESS";
        else return "THE_COURSE_HAS_BEEN_MOVED";
    }

    public String swapCourseEnrolRequest(String studentID, String newCourseID, String oldCourseID) {
        // check if the course if full or not
        Course course = this.searchCourse(newCourseID);
        synchronized (course) {
            if (course.isStatusFull())
                return "THE_COURSE_IS_FULL";
            String[] param = {"swap_drop", studentID,  oldCourseID};
            String res = send(dataProcess(param), oldCourseID.substring(0, 4));
            if (res.equals("SUCCESS"))
                course.swapEnrol(studentID);
            return res;
        }
    }

    public String swapCourseDropRequest(String studentID, String newCourseID, String oldCourseID) {
        // check is the new course in the local server
        if (checkDepartment(newCourseID)) {
            // check is the two courses in the same semester
            if (!checkIsInSameSemester(newCourseID, oldCourseID)) return "NOT_IN_THE_SAME_SEMESTER";
            // run local course exchange, if success return SUCCESS
            if(!swapLocalCourse(studentID, newCourseID, oldCourseID))
               return "THE_COURSE_" + newCourseID.toUpperCase() + "_IS_FULL";
            return "SUCCESS";
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
            return "DON'T_HAVE_THE_SWAPPED_COURSE";
        if (student.checkCourse(newCourseID))
            return "THE_SWAPPED_COURSE_IS_ALREADY_REGISTERED";
        int numberIfSwap = student.getRemoteCourseNumberIfSwap(oldCourseID, newCourseID);
        if (numberIfSwap > 2)
            return "OTHER_DEPARTMENT_COURSE_WILL_BE_FULL_IF_SWAP";
        if (numberIfSwap == -1)
            return "TWO_COURSES_ARE_NOT_IN_THE_SAME_SEMESTER";
        if (checkDepartment(newCourseID) && checkDepartment(oldCourseID)) {
            if (!checkIsInSameSemester(newCourseID, oldCourseID))
                return "NOT_IN_THE_SAME_SEMESTER";
            if(!swapLocalCourse(studentID, newCourseID, oldCourseID))
                return "THE_COURSE_" + newCourseID.toUpperCase() + "_IS_FULL";
            student.swapCourse(oldCourseID, newCourseID);
            return "SUCCESS";
        }
        String[] param = {"swap", studentID, newCourseID, oldCourseID};
        String res = this.send(dataProcess(param), oldCourseID.substring(0, 4));
        if (res.equals("SUCCESS")) {
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
        if (!courseID.substring(0, 4).equals(this.departmentTag))
            return "You Are Not Authorized To Add The Course ";
        if (!checkCourse(courseID, semester)) {
            synchronized (this) {
                this.courseSchedule.get(semester).put(courseID, new Course(courseID, 2));
            }
            return "Add Successful";
        }
        return "The Course Have Already Added!";
    }

    public String removeCourse(String courseID, String semester) {
        if (!courseID.substring(0, 4).equals(this.departmentTag))
            return "You Are Not Authorized To Add The Course ";
        if (!checkCourse(courseID, semester)) {
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
            list.add(this.send(dataProcess(param), key));
        }
        return (String[]) list.toArray();
    }

    public String listCourseAvailabilityInLocal(String semester) {
        Map<String, Course> courseMap = courseSchedule.get(semester);
        List<Course> list = new ArrayList<>();
        for (Course course : courseMap.values()) {
            list.add(course);
        }
        return list.stream().map((a) -> a.getCourse_name() + "--" + a.getAvailableSpave()).collect(Collectors.joining(" "));
    }

    public String getClassSchedule(String studentID) {
        if (!students.containsKey(studentID)) {
            students.put(studentID, new Student(studentID));
            return "YOU_HAVE_NOT_COURSE_REGISTED";
        }
        return students.get(studentID).getSchedule();
    }

    public String enrolCourse(String studentID, String courseID, String semester)  {
        if (!checkDepartment(studentID)) {
            if(this.courseSchedule.get(semester).get(courseID).register(studentID)) return "SUCCESS";
            else return "COURSE_IS_FULL";
        }
        if (!students.containsKey(studentID)) {
            students.put(studentID, new Student(studentID));
        }
        Student student = students.get(studentID);

        if (student.checkCourse(courseID, semester)) {
            return "THE_COURSE_IS_ALREADY_REGISTERED";
        }
        int registedNum = student.getRegistedNum(semester);
        if (registedNum == 3) {
            return "EXCESS_MAX_NUM_OF_COURSE_IN_THIS_SESSION";
        }
        if (!checkDepartment(courseID)) {
            int remoteCourseNum = student.getRemoteCourseNum(semester);
            if (remoteCourseNum == 2) {
                return "EXCESS_MAX_NUM_OTHER_DEPARTMENT_COURSE";
            }
            String[] param = {"enrol", studentID, courseID, semester};
            String res = this.send(dataProcess(param), courseID.substring(0, 4));
            if (res.equals("SUCCESS")) {
                student.registerCourse(courseID, semester);
                return courseID + " Enroll Successfully";
            }
            return res;
        }

        if(!this.courseSchedule.get(semester).get(courseID).register(studentID)) {
            return "COURSE_IS_FULL";
        }

        student.registerCourse(courseID, semester);
        return "SUCCESS";
    }

    public String dropCourse(String studentID, String courseID) {
        Course course = this.searchCourse(courseID);
        if (!checkDepartment(studentID)) {
            if (course == null) return "SYSTEM_DOES_NOT_HAVE_THE_COURSE";
            if(course.drop(studentID))
                return "SUCCESS";
             else return "DID_NOT_REGISTED_THE_COURSE";
        }
        if (!students.containsKey(studentID)) {
            students.put(studentID, new Student(studentID));
            return "DID_NOT_REGISTED_THE_COURSE";
        }
        Student student = students.get(studentID);
        if (!checkDepartment(courseID)) {
            String[] param = {"drop", studentID, courseID};
            String res = this.send(dataProcess(param), courseID.substring(0, 4));
            if (res.equals("SUCCESS")) {
                student.drop(courseID);
            }
            return res;
        }
        if (course == null) return "SYSTEM_DOES_NOT_HAVE_THE_COURSE";
        if (course.drop(studentID)) {
            student.drop(courseID);
            return "SUCCESS";
        } else return "DID_NOT_REGISTED_THE_COURSE";
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

