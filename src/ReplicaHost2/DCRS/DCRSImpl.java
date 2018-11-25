package ReplicaHost2.DCRS;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;


public class DCRSImpl {

    public Logger logger;
    public String department;
    public ConcurrentHashMap<String, ConcurrentHashMap<String, Course>> compCourseDatabase = new ConcurrentHashMap<String, ConcurrentHashMap<String, Course>>();

    //studentId -> student
    public Map<String, Student> studentEnrollDatabase = new ConcurrentHashMap<String,Student>();


    public DCRSImpl(String department, Logger logger){
        this.department = department;
        this.logger = logger;

        ConcurrentHashMap<String, Course> fallCourse = new ConcurrentHashMap<String, Course>();
        compCourseDatabase.put("fall", fallCourse);

        ConcurrentHashMap<String, Course> winterCourse = new ConcurrentHashMap<String, Course>();
        compCourseDatabase.put("winter", winterCourse);

        ConcurrentHashMap<String, Course> summerCourse = new ConcurrentHashMap<String, Course>();
        compCourseDatabase.put("summer", summerCourse);

        Student student1 = new Student(department + "s1111",new ArrayList<Course>());
        Student student2 = new Student(department + "s2222",new ArrayList<Course>());
        Student student3 = new Student(department + "s3333",new ArrayList<Course>());
        Student student4 = new Student(department + "s4444",new ArrayList<Course>());
        Student student5 = new Student(department + "s5555",new ArrayList<Course>());
        Student student6 = new Student(department + "s6666",new ArrayList<Course>());
        Student student7 = new Student(department + "s7777",new ArrayList<Course>());
        Student student8 = new Student(department + "s8888",new ArrayList<Course>());
        Student student9 = new Student(department + "s9999",new ArrayList<Course>());
        Student student10 = new Student(department + "s1010",new ArrayList<Course>());


        studentEnrollDatabase.put(department + "s2222",student2);
        studentEnrollDatabase.put(department + "s3333",student3);
        studentEnrollDatabase.put(department + "s4444",student4);
        studentEnrollDatabase.put(department + "s5555",student5);
        studentEnrollDatabase.put(department + "s6666",student6);
        studentEnrollDatabase.put(department + "s7777",student7);
        studentEnrollDatabase.put(department + "s8888",student8);
        studentEnrollDatabase.put(department + "s9999",student9);
        studentEnrollDatabase.put(department + "s1010",student10);

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

    public String addCourse(String courseId, String semester) {
        if (compCourseDatabase.get(semester) == null ) return "The Semester Does Not Exist! Please Check The Semester";
        if (courseId.substring(0,4).equals(this.department)){
            String result = "";
            Course newCourse = new Course(courseId , semester);
            ConcurrentHashMap<String, Course> courseIdCourseMap = compCourseDatabase.get(semester);

            if (courseIdCourseMap.containsKey(courseId)){
                result = "The Course Have Already Added!";
            } else {
                synchronized (courseIdCourseMap){
                    courseIdCourseMap.put(courseId, newCourse);
                    compCourseDatabase.put(semester,courseIdCourseMap);
                    result = "Add Successful";
                }
            }
            logger.info("Add Course:" + courseId + " " + semester + ":" + result);

            return result;
        } else {
            logger.info("Add Course:" + courseId + " " + semester + ":" + "Not Authorized");
            return "You Are Not Authorized To Add The Course ";
        }
    }

    public String removeCourse(String courseId, String semester) {
        if (compCourseDatabase.get(semester) == null ) return "The Semester Does Not Exist! Please Check The Semester";

        if (!courseId.substring(0,4).equals(this.department)){
            logger.info("Remove Course:" + courseId + " " + semester + ":" + " Not Authorized");
            return "You Are Not Authorized To Delete The Course ";
        } else{
            ConcurrentHashMap<String, Course> courseList = compCourseDatabase.get(semester);

            //delete the course from course list
            synchronized (courseList){
                if (courseList.containsKey(courseId)){
                    courseList.remove(courseId);

                    //drop the course from all the student who enroll the course
                    String department = courseId.substring(0,4);
                    dropRemovedCourseFromStuCourList(courseId);
                    try {
                        notifyOtherDepartment(courseId);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    logger.info("Remove Course:" + courseId + " " + semester + ":" + " Remove Successful");
                    return "Remove Successful";
                } else{
                    logger.info("Remove Course:" + courseId + " " + semester + ":" + "The Course Doesn't Exist");
                    return "The Course Doesn't Exist";
                }
            }
        }
    }

    public String[] listCourseAvailability(String semester) {
        logger.info("List Course Availability :" + semester);

        List<String> courseList = getLocalCourseList(semester);

        try {
            String courseAvailibleList = "";
            String message = "listCourseAvailability " + semester;
            if (this.department.equals("comp")){
                courseAvailibleList = getRemoteCourseList(message,2223, message,3334);

            } else if(this.department.equals("inse")){
                courseAvailibleList = getRemoteCourseList(message,2223, message,1112);

            } else if(this.department.equals("soen")){
                courseAvailibleList = getRemoteCourseList(message,1112, message,3334);
            }

            String[] courses = courseAvailibleList.split(" ");
            for (String course :
                    courses) {
                if (!course.equals("")){
                    courseList.add(course);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

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
        String result = "";
        if (studentEnrollDatabase.get(studentId) == null && this.department.equals(studentId.substring(0,4))) {
            return "The Student Does Not Exist! Please Contact With Advisor!";
        }

        String department = courseId.substring(0,4);
        if (department.equals(this.department)){
            if (compCourseDatabase.get(semester).containsKey(courseId)){

                Course course = compCourseDatabase.get(semester).get(courseId);
                synchronized (course){
                    if(allowToEnroll(studentId, courseId, semester) &&
                            course.getEnrollNumber() < course.getCapacity()){
                        Student student = studentEnrollDatabase.get(studentId);

                        //if the student belongs to depart,it is a local operate,otherwise it is a remote operate
                        course.getStudentList().add(studentId);
                        course.setEnrollNumber(course.getEnrollNumber() + 1);
                        if (studentId.substring(0, 4).equals(this.department)) {
                            student.getStudentEnrollCourseList().add(course);
                        }

                        result = (courseId + " Enroll Successfully");
                    } else {
                        result = (courseId + " Do not allow to enroll");
                    }
                }

            } else {
                result = "The Course does not Exist!";
            }


        } else {
            if(enrollInOtherDepartment(studentId, semester, department)){
                int port = getPort(department);

                try {
                    result = sendMessage("enrolCourse " + studentId + " " + courseId + " " + semester, port);
                    if (result.contains("Successfully")){
                        Course course = new Course(courseId,semester);
                        Student student = studentEnrollDatabase.get(studentId);
                        student.getStudentEnrollCourseList().add(course);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else{
                result = "Do not allow to enroll";
            }

        }
        logger.info("Enroll Course :" + studentId + " " + courseId + " " + semester + ":" + result);
        return result;
    }

    public String dropCourse(String studentId, String courseId) {

        if (studentEnrollDatabase.get(studentId) == null && this.department.equals(studentId.substring(0,4))) {
            return "The Student Does Not Exist! Please Contact With Advisor!";
        }

        boolean findTargetCourse = false;
        String result = "";
        String department = courseId.substring(0,4);

        Student student = studentEnrollDatabase.get(studentId);
        List<Course> courseList = student.getStudentEnrollCourseList();
        String semester = "";
        List<Course> removeCourses = new ArrayList<Course>();
        for (Course course: courseList){
            if (course.getCourseName().equals(courseId)){
                findTargetCourse = true;
                semester = course.getSemester();
                removeCourses.add(course);
            }
        }

        if (findTargetCourse){
            synchronized (student){
                courseList.removeAll(removeCourses);
                if (department.equals(this.department)){
                    result = dropLocalCourse(studentId, courseId, semester);
                } else{
                    String message = "dropCourse " + studentId + " " + courseId + " " + semester;
                    int port = getPort(department);
                    try {
                        result = sendMessage(message, port);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        } else {
            result = "Course Not Found In The Student Course List";
        }

        logger.info("Drop Course :" + studentId + " " + courseId + ":" + result);

        return result;
    }

    public String[] getClassSchedule(String studentId) {
        if (studentEnrollDatabase.get(studentId) == null) return null;

        logger.info("Get Class Schedule :" + studentId);
        List<Course> courses = studentEnrollDatabase.get(studentId).getStudentEnrollCourseList();
        List<String> res = new ArrayList<>();

        for (Course course :
                courses) {
            String str = course.getCourseName() + "--" + course.getSemester();
            res.add(str);
        }

        return translateStringArray(res);
    }

    public String swapCourse(String studentID, String newCourseID, String oldCourseID) {
        String result = "";
        Student student = studentEnrollDatabase.get(studentID);
        String semester = findSemester(student,oldCourseID);
        if(checkStudentAllowEnrol(studentID,newCourseID,oldCourseID) && checkLocalStuListHaveOldCourseAndNoNewCourse(studentID,oldCourseID,newCourseID)){
            result = checkEnroll(studentID, newCourseID, oldCourseID, semester);
            if (result.contains("Successful")){
                Course newCourse = new Course(newCourseID, findSemester(student, oldCourseID));
                List<Course> oldCourse = new ArrayList<>();
                List<Course> courses = studentEnrollDatabase.get(studentID).getStudentEnrollCourseList();
                for (Course course : courses) {
                    if (course.getCourseName().equals(oldCourseID)) {
                        oldCourse.add(course);
                    }

                }
                if (student.getStudentEnrollCourseList().removeAll(oldCourse) && student.getStudentEnrollCourseList().add(newCourse)){
                    logger.info("Swap Course:" + oldCourseID + "->" + newCourseID + ":" + " Swap Successful");
                    return "Swap Course Successful";
                } else {
                    logger.info("Swap Course:" + oldCourseID + "->" + newCourseID + ":" + " Swap Fail");
                    return "Swap Fail";
                }

            }else {
                logger.info("Swap Course:" + oldCourseID + "->" + newCourseID + ":" + " Swap Fail");
                return "Swap Fail!";
            }
        } else {
            logger.info("Swap Course:" + oldCourseID + "->" + newCourseID + ":" + " Do Not Allow Swap Course!");
            return "Do Not Allow Swap Course!";
        }
    }

    public String checkEnroll(String studentID, String newCourseID, String oldCourseID , String semester) {
        String result = "";
        String newCourseDepartment = newCourseID.substring(0,4);
        if (newCourseDepartment.equals(this.department)){

            Course newCourse = compCourseDatabase.get(semester).get(newCourseID);

            if (newCourse != null){
                synchronized (newCourse) {
                    if (newCourse.getCapacity() - newCourse.getEnrollNumber() > 0){

                        String dropResult = dropOldCourse(studentID, oldCourseID, semester);

                        if (dropResult.contains("Successful")){
                            if (newCourse.getStudentList().add(studentID)){

                                newCourse.setEnrollNumber(newCourse.getEnrollNumber() + 1);
                                return "Successful";

                            }else{
                                return "Fail";
                            }
                        }else{
                            return "Fail";
                        }
                    } else{
                        return "Fail";
                    }
                }
            } else {
                return "Fail";
            }
        } else {
            String message = "checkEnroll " + studentID + " " + newCourseID + " " + oldCourseID + " " + semester;
            try {
                result = sendMessage(message, getPort(newCourseDepartment));
                return result;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public String dropOldCourse(String studentID, String oldCourseID, String semester) {
        String result = "";
        String oldCourseDepartment = oldCourseID.substring(0,4);
        if (oldCourseDepartment.equals(this.department)){

            result = dropLocalCourse(studentID, oldCourseID, semester);
            return result;

        } else {
            String message = "dropOldCourse " + studentID + " " + oldCourseID + " " + semester;
            try {
                result = sendMessage(message, getPort(oldCourseDepartment));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }
    }

    private boolean checkStudentAllowEnrol(String studentId,String newCourseId, String oldCourseId) {
        int localCourse = 0;
        int otherDepartmentCourse = 0;
        if (newCourseId.substring(0,4).equals(studentId.substring(0,4))){
            localCourse ++;
        } else {
            otherDepartmentCourse ++;
        }
        if(oldCourseId.substring(0,4).equals(studentId.substring(0,4))){
            localCourse --;
        }else {
            otherDepartmentCourse --;
        }
        Student student = studentEnrollDatabase.get(studentId);
        if (student != null ){
            List<Course> courses = student.getStudentEnrollCourseList();
            for(Course course : courses){
                if(course.getCourseName().substring(0,4).equals(studentId.substring(0,4))){
                    localCourse ++;
                } else {
                    otherDepartmentCourse ++;
                }
            }
            if(localCourse + otherDepartmentCourse > 3){
                return false;
            } else if(otherDepartmentCourse > 2){
                return false;
            } else if( localCourse > 3){
                return false;
            } else {
                return true;
            }
        }else {
            return false;
        }
    }

    public String checkWhetherCanEnrollAndEnroll(String studentId , String newCourseId ,String semester){
        String result = "";
        //本地或者远程两种情况
        String department = newCourseId.substring(0,4);
        if(this.department.equals(department)){
            //本地
            //查看是否允许enroll
            Course course = compCourseDatabase.get(semester).get(newCourseId);

            synchronized (course){
                if (course != null ){
                    if (course.getCapacity() - course.getEnrollNumber() > 0){
                        //执行enroll操作
                        course.setEnrollNumber(course.getEnrollNumber() + 1);
                        course.getStudentList().add(studentId);

                        result = "Successful";
                        return result;
                    } else {
                        return "Swap Fail";
                    }
                }else {
                    return "Swap Fail";
                }
            }
        } else {
            String message = "checkWhetherCanEnrollAndEnroll " + studentId + " " + newCourseId  + " " + semester;
            try{
                result = sendMessage(message, getPort(department));
            } catch (Exception e){
                e.printStackTrace();
            }
            return result;
        }

    }

    private boolean checkLocalStuListHaveOldCourseAndNoNewCourse(String studentId, String oldCourseId, String newCourseId ) {
        boolean findOldCourse = false;
        boolean findNewCourse = false;
        Student student = studentEnrollDatabase.get(studentId);
        List<Course> courses = student.getStudentEnrollCourseList();
        for (Course course :
                courses) {
            if (course.getCourseName().equals(oldCourseId)){
                findOldCourse = true;
            }
            if (course.getCourseName().equals(newCourseId)){
                findNewCourse = true;
            }
        }
        return (findOldCourse && !findNewCourse);
    }

    private String findSemester(Student student, String oldCourseId){
        String semester = "";
        List<Course> courses = student.getStudentEnrollCourseList();
        for (Course course :
                courses) {
            if (course.getCourseName().equals(oldCourseId)){
                semester = course.getSemester();
            }
        }
        return semester;
    }

    public String checkDropAndEnroll(String studentId, String newCourseID, String oldCourseId ,String semester) {
        String result = "Swap Course Fail!";
        String department = oldCourseId.substring(0,4);
        if(this.department.equals(department)){
            Course course = compCourseDatabase.get(semester).get(oldCourseId);
            List<String> studentNameList = course.getStudentList();

            boolean findStuName = false;
            for (int i = 0; i < studentNameList.size(); i++) {
                if (studentNameList.get(i).equals(studentId)){
                    findStuName = true;
                }
            }

            if (findStuName){
                result = checkWhetherCanEnrollAndEnroll(studentId, newCourseID, semester);

                if (result.contains("Successful")){
                    if(dropLocalCourse(studentId,oldCourseId,semester).contains("Successful")){
                        result = "Swap Course Successful";
                        return result;
                    } else {
                        return "Swap Course Fail!";
                    }

                } else {
                    return "Swap Course Fail!";
                }

            }else {
                result = "Swap Course Fail!";
                return result;
            }

        }else {
            //远程
            String message = "checkDropAndEnroll " + studentId + " " + newCourseID + " " + oldCourseId + " " + semester;
            try{
                result = sendMessage(message, getPort(department));
            } catch (Exception e){
                e.printStackTrace();
            }

        }
        return result;
    }

    private void notifyOtherDepartment(String courseId) throws Exception {
        String message = "dropRemovedCourseFromStuCourList " + courseId;
        if (this.department.equals("comp")){
            sendMessage(message , getPort("inse"));
            sendMessage(message , getPort("soen"));
        } else if(this.department.equals("inse")){
            sendMessage(message , getPort("comp"));
            sendMessage(message , getPort("soen"));
        } else if(this.department.equals("soen")){
            sendMessage(message , getPort("comp"));
            sendMessage(message , getPort("inse"));
        }
    }

    public String dropRemovedCourseFromStuCourList(String courseId){
        List<String> studentList = new ArrayList<>();
        synchronized (studentEnrollDatabase){
            for (Map.Entry<String,Student> studentEntry:
                    studentEnrollDatabase.entrySet()) {
                List<Course> courseList = studentEntry.getValue().getStudentEnrollCourseList();

                for (int i = 0; i < courseList.size(); i++) {
                    if(courseList.get(i).getCourseName().equals(courseId)){
                        studentList.add(studentEntry.getKey());
                        studentEntry.getValue().getStudentEnrollCourseList().remove(i);
                    }
                }
            }
        }
        return "Remove Successful";
    }

    protected List<String> getLocalCourseList(String semester){
        List<String> courseList = new ArrayList<>();

        //get local data
        ConcurrentHashMap<String, Course> courseMap = compCourseDatabase.get(semester);

        if (courseMap == null) {
            return courseList;
        }

        for (Map.Entry<String, Course> entry: courseMap.entrySet()){
            Course course = entry.getValue();
//            if (course.getCapacity() - course.getEnrollNumber() > 0){
            if (course.getCapacity() - course.getEnrollNumber() >= 0){
                courseList.add(entry.getKey() + "--"+ (entry.getValue().getCapacity() - entry.getValue().getEnrollNumber()));
            }
        }

        return courseList;
    }

    private String getRemoteCourseList(String message1 , int port1, String message2 , int port2) throws Exception {
        String receive1 = sendMessage(message1, port1);
        String receive2 = sendMessage(message2, port2);
        return receive1 + receive2;
    }

    private boolean enrollInOtherDepartment(String studentId, String semester, String department) {
        int courseNum = 0;
        Student student = studentEnrollDatabase.get(studentId);
        if (student == null){
            return false;
        } else{
            List<Course> courses = student.getStudentEnrollCourseList();
            for (Course course : courses){
                if (course.getSemester().equals(semester) && !course.getCourseName().substring(0,4).equals(this.department)){
                    courseNum = courseNum + 1;
                }
            }
            return (courseNum < 2);
        }
    }

    private boolean allowToEnroll(String studentId,String courseId, String semester) {
        if (!studentId.substring(0,4).equals(this.department) && !compCourseDatabase.get(semester).get(courseId).getStudentList().contains(studentId)) return true;
        if (compCourseDatabase.get(semester).get(courseId).getStudentList().contains(studentId)) return false;
        int courseNum = 0;
        Student student = studentEnrollDatabase.get(studentId);
        List<Course> courses = student.getStudentEnrollCourseList();
        for (Course course :
                courses) {
            if (course.getSemester().equals(semester)){
                courseNum = courseNum + 1;
            }
        }
        return (courseNum < 3);
    }

    public String dropLocalCourse(String studentId, String courseId, String semester) {
        String result = "";
        ConcurrentHashMap<String, Course> courseMap = compCourseDatabase.get(semester);
        Course course = courseMap.get(courseId);
        List<String> studentIdList = course.getStudentList();

        if (studentIdList.contains(studentId)){
            synchronized (course){
                if(studentIdList.remove(studentId)){
                    course.setEnrollNumber(course.getEnrollNumber() - 1);
                    result = "Drop Successful!";
                } else{
                    result = "Drop Fail";
                }
            }
        } else {
            result = "Drop Successful!";
        }

        return result;
    }

    private int getPort(String department){
        int port;
        if (department.equals("comp")){
            port = 1112;
        } else if(department.equals("soen")){
            port = 2223;
        } else {
            port = 3334;
        }
        return port;
    }


    private String sendMessage(String message, int port) throws Exception{
        logger.info("Client Send Request :" + message);
        InetAddress address = InetAddress.getByName("localhost");

        byte[] data = message.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(data, data.length, address, port);

        DatagramSocket socket = new DatagramSocket();

        socket.send(sendPacket);

        byte[] receiveData = new byte[1024];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        socket.receive(receivePacket);
        String info = new String(receiveData, 0, receivePacket.getLength());
        logger.info("Client Recv Response :" + info);
        socket.close();
        return info;
    }

}
