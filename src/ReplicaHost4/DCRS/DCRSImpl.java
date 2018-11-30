package ReplicaHost4.DCRS;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DCRSImpl {
    public static final String REQUEST_AVAILABILITY = "REQUEST_AVAILABILITY";
    public static final String REQUEST_ENROLLED_COUNT = "REQUEST_ENROLLED_COUNT";
    public static final String REQUEST_ENROLL = "REQUEST_ENROLL";
    public static final String REQUEST_DROP = "REQUEST_DROP";
    public static final String REQUEST_RECORD = "REQUEST_RECORD";
    public static final String REQUEST_NEWCOURSE_STATUS = "REQUEST_NEWCOURSE_STATUS";
    public static final String REQUEST_OLDCOURSE_STATUS = "REQUEST_OLDCOURSE_STATUS";
    public static final String REQUEST_ENROL_AVAIL = "REQUEST_ENROL_AVAIL";
    public Logger log;
    public Department database;
    private DepartEnum departName;
    public static Lock lock = new ReentrantLock();

    public DCRSImpl(String department, Logger logger) {
        if (department.equals("comp")) {
            this.departName = DepartEnum.COMP;
        } else if (department.equals("soen")) {
            this.departName = DepartEnum.SOEN;
        } else {
            this.departName = DepartEnum.INSE;
        }

        this.database = new Department(this.departName);
        this.log = logger;
        this.addCourse(department+"1", "fall");
        this.addCourse(department+"2", "fall");
    }

    public String addCourse(String courseID, String semester) {
        String result;
        if (!semester.equals("fall") && !semester.equals("winter") && !semester.equals("summer")) {
            result = "The Semester Does Not Exist! Please Check The Semester";
        } else if (courseID.contains(this.database.getName())) {
            Department var4 = this.database;
            synchronized(this.database) {
                result = this.database.addCourse(courseID, semester);
                this.log.info("Add Course:" + courseID + " " + semester + ":" + result);
            }
        } else {
            this.log.info("Add Course:" + courseID + " " + semester + ":Not Authorized");
            result = "You Are Not Authorized To Add The Course ";
        }

        return result;
    }

    public String removeCourse(String courseID, String semester) {
        if (!semester.equals("fall") && !semester.equals("winter") && !semester.equals("summer")) {
            return "The Semester Does Not Exist! Please Check The Semester";
        } else if (courseID.contains(this.database.getName())) {
            synchronized(this) {
                String res = this.database.removeCourse(courseID, semester);
                if (res.equals("Remove Successful")) {
                    this.log.info("Remove Course:" + courseID + " " + semester + ": Remove Successful");
                } else {
                    this.log.info("Remove Course:" + courseID + " " + semester + ":The Course Doesn't Exist");
                }

                return res;
            }
        } else {
            this.log.info("Remove Course:" + courseID + " " + semester + ": Not Authorized");
            return "You Are Not Authorized To Delete The Course ";
        }
    }

    public String[] listCourseAvailability(String semester) {
        this.log.info("List Course Availability :" + semester);
        String result = this.database.listCourseAvailability(semester);
        StringBuilder builder = new StringBuilder(result);
        List<DepartEnum> departList = (List)Stream.of(DepartEnum.values()).collect(Collectors.toList());
        Iterator var5 = departList.iterator();

        while(var5.hasNext()) {
            DepartEnum d = (DepartEnum)var5.next();
            if (!d.getName().equals(this.database.getName())) {
                try {
                    String sendMessage = "REQUEST_AVAILABILITY ";
                    sendMessage = sendMessage + semester;
                    String receiveMessage = this.send(sendMessage, d.getPort());
                    builder.append(receiveMessage);
                } catch (IOException var9) {
                    var9.printStackTrace();
                    System.out.println("UDP exception.");
                }
            }
        }

        result = builder.toString();
        String[] res = result.split(" ");
        Arrays.sort(res);
        return res;
    }

    public String enrolCourse(String studentID, String courseID, String semester) {
        lock.lock();
        String result = "";
        int elevtiveCourseCount = 0;
        int mandatoryCourseCount = Integer.parseInt(this.database.getStudentRecord(studentID, semester));
        List<DepartEnum> departList = (List)Stream.of(DepartEnum.values()).collect(Collectors.toList());
        Iterator var8 = departList.iterator();

        while(var8.hasNext()) {
            DepartEnum d = (DepartEnum)var8.next();
            if (!d.getName().equals(this.database.getName())) {
                try {
                    String sendMessage = "REQUEST_ENROLLED_COUNT ";
                    sendMessage = sendMessage + studentID + " " + semester;
                    String receive = this.send(sendMessage, d.getPort());
                    elevtiveCourseCount += Integer.parseInt(receive);
                } catch (SocketException var14) {
                    var14.printStackTrace();
                    System.out.println("UDP exception");
                }
            }
        }

        if (courseID.contains(this.database.getName())) {
            if (mandatoryCourseCount + elevtiveCourseCount < 3) {
                result = this.database.enrolCourse(studentID, courseID, semester);
            } else {
                result = courseID + " Do not allow to enroll";
            }
        } else if (mandatoryCourseCount + elevtiveCourseCount < 3 && elevtiveCourseCount < 2) {
            String resendMessage = "REQUEST_ENROLL ";
            List<DepartEnum> departEnumList = (List)Stream.of(DepartEnum.values()).collect(Collectors.toList());
            Iterator var17 = departEnumList.iterator();

            while(var17.hasNext()) {
                DepartEnum departEnum = (DepartEnum)var17.next();
                if (courseID.contains(departEnum.getName())) {
                    try {
                        resendMessage = resendMessage + studentID + " " + courseID + " " + semester;
                        result = this.send(resendMessage, departEnum.getPort());
                    } catch (SocketException var13) {
                        var13.printStackTrace();
                        System.out.println("UDP exception");
                    }
                }
            }
        } else {
            result = courseID+" Do not allow to enroll";
        }

        this.log.info("Enroll Course :" + studentID + " " + courseID + " " + semester + ":" + result);
        lock.unlock();
        return result;
    }

    public String[] getClassSchedule(String studentID) {
        this.log.info("Get Class Schedule :" + studentID);
        String result = this.database.getClassSchedule(studentID);
        StringBuilder builder = new StringBuilder(result);
        List<DepartEnum> departList = (List)Stream.of(DepartEnum.values()).collect(Collectors.toList());
        Iterator var5 = departList.iterator();

        while(var5.hasNext()) {
            DepartEnum d = (DepartEnum)var5.next();
            if (!d.getName().equals(this.database.getName())) {
                try {
                    String sendMessage = "REQUEST_RECORD ";
                    sendMessage = sendMessage + studentID;
                    String receiveMessage = this.send(sendMessage, d.getPort());
                    builder.append(receiveMessage);
                } catch (IOException var9) {
                    var9.printStackTrace();
                    System.out.println("UDP exception.");
                }
            }
        }

        result = builder.toString();
        String[] res = result.split(" ");
        Arrays.sort(res);
        return res;
    }

    public String dropCourse(String studentID, String courseID) {
        lock.lock();
        String result = "Failed.";
        if (courseID.contains(this.database.getName())) {
            result = this.database.dropCourse(studentID, courseID);
        } else {
            String sendMessage = "REQUEST_DROP ";
            List<DepartEnum> departList = (List)Stream.of(DepartEnum.values()).collect(Collectors.toList());
            Iterator var6 = departList.iterator();

            while(var6.hasNext()) {
                DepartEnum departEnum = (DepartEnum)var6.next();
                if (courseID.contains(departEnum.getName())) {
                    try {
                        sendMessage = sendMessage + studentID + " " + courseID;
                        result = this.send(sendMessage, departEnum.getPort());
                    } catch (SocketException var9) {
                        var9.printStackTrace();
                        System.out.println("UDP exception");
                    }
                }
            }
        }

        this.log.info("Drop Course :" + studentID + " " + courseID + ":" + result);
        lock.unlock();
        return result;
    }

    public String swapCourse(String studentID, String newCourseID, String oldCourseID) {
        lock.lock();
        String result = "null";
        String studentDepart = studentID.substring(0, 4);
        String AcourseDepart = oldCourseID.substring(0, 4);
        String BcourseDepart = newCourseID.substring(0, 4);
        String oldStatus;
        String newStatus;
        if (studentDepart.equals(AcourseDepart) && studentDepart.equals(BcourseDepart)) {
            oldStatus = this.database.checkOldCourse(studentID, oldCourseID);
            newStatus = this.database.checkNewCourse(studentID, newCourseID);
            if (Pattern.matches("(fall|winter|summer)", oldStatus) && Pattern.matches("(fall|winter|summer)", newStatus)) {
                if (oldStatus.equals(newStatus)) {
                    if (this.database.checkEnrolAvail(newCourseID, newStatus).equals("True")) {
                        this.database.enrolCourse(studentID, newCourseID, newStatus);
                        this.database.dropCourse(studentID, oldCourseID);
                        this.log.info("Swap Course:" + oldCourseID + "->" + newCourseID + ": Swap Successful");
                        result = "Swap Course Successful!";
                    } else {
                        this.log.info("Swap Course:" + oldCourseID + "->" + newCourseID + ": Swap Fail");
                        result = "Swap Fail!";
                    }
                } else {
                    this.log.info("Swap Course:" + oldCourseID + "->" + newCourseID + ": Swap Fail");
                    result = "Swap Fail!";
                }
            } else {
                this.log.info("Swap Course:" + oldCourseID + "->" + newCourseID + ": Swap Fail");
                result = "Swap Fail!";
            }
        } else {
            DepartEnum newcourseDep;
            String dropSendMsg;
            String checkEnrolAvail;

            String sendEnrolMsg;

            String sendMessage;
            if (studentDepart.equals(AcourseDepart) && !studentDepart.equals(BcourseDepart)) {
                oldStatus = this.database.checkOldCourse(studentID, oldCourseID);
                newStatus = "null";
                sendMessage = "REQUEST_NEWCOURSE_STATUS " + studentID + " " + newCourseID;
                newcourseDep = DepartEnum.getInstance(newCourseID.substring(0, 4));
                newStatus = this.serverToServer(newcourseDep, sendMessage);
                if (Pattern.matches("(fall|winter|summer)", oldStatus) && Pattern.matches("(fall|winter|summer)", newStatus)) {
                    if (oldStatus.equals(newStatus)) {
                        dropSendMsg = "REQUEST_ENROLLED_COUNT " + studentID + " " + newStatus;
                        int elevtiveCourseCount = 0;
                        List<DepartEnum> departList = (List)Stream.of(DepartEnum.values()).collect(Collectors.toList());
                        Iterator var24 = departList.iterator();

                        while(var24.hasNext()) {
                            DepartEnum d = (DepartEnum)var24.next();
                            if (!d.getName().equals(this.database.getName())) {
                                sendEnrolMsg = this.serverToServer(d, dropSendMsg);
                                elevtiveCourseCount += Integer.parseInt(sendEnrolMsg);
                            }
                        }

                        if (elevtiveCourseCount >= 2) {
                            this.log.info("Swap Course:" + oldCourseID + "->" + newCourseID + ": Swap Fail");
                            result = "Swap Fail!";
                        } else {
                            checkEnrolAvail = "REQUEST_ENROL_AVAIL " + newCourseID + " " + newStatus;
                            dropSendMsg = this.serverToServer(newcourseDep, checkEnrolAvail);
                            if (dropSendMsg.equals("True")) {
                                this.database.dropCourse(studentID, oldCourseID);
                                sendEnrolMsg = "REQUEST_ENROLL " + studentID + " " + newCourseID + " " + newStatus;
                                this.serverToServer(newcourseDep, sendEnrolMsg);
                                this.log.info("Swap Course:" + oldCourseID + "->" + newCourseID + ": Swap Successful");
                                result = "Swap Course Successful!";
                            } else {
                                this.log.info("Swap Course:" + oldCourseID + "->" + newCourseID + ": Swap Fail");
                                result = "Swap Fail!";
                            }
                        }
                    } else {
                        this.log.info("Swap Course:" + oldCourseID + "->" + newCourseID + ": Swap Fail");
                        result = "Swap Fail!";
                    }
                } else {
                    this.log.info("Swap Course:" + oldCourseID + "->" + newCourseID + ": Swap Fail");
                    result = "Swap Fail!";
                }
            } else if (studentDepart.equals(BcourseDepart) && !studentDepart.equals(AcourseDepart)) {
                oldStatus = this.database.checkNewCourse(studentID, newCourseID);
                newStatus = "null";
                sendMessage = "REQUEST_OLDCOURSE_STATUS " + studentID + " " + oldCourseID;
                newcourseDep = DepartEnum.getInstance(oldCourseID.substring(0, 4));
                newStatus = this.serverToServer(newcourseDep, sendMessage);
                if (Pattern.matches("(fall|winter|summer)", newStatus) && Pattern.matches("(fall|winter|summer)", oldStatus)) {
                    if (newStatus.equals(oldStatus)) {
                        if (this.database.checkEnrolAvail(newCourseID, oldStatus).equals("True")) {
                            dropSendMsg = "REQUEST_DROP " + studentID + " " + oldCourseID;
                            this.serverToServer(newcourseDep, dropSendMsg);
                            this.database.enrolCourse(studentID, newCourseID, oldStatus);
                            this.log.info("Swap Course:" + oldCourseID + "->" + newCourseID + ": Swap Successful");
                            result = "Swap Course Successful!";
                        } else {
                            this.log.info("Swap Course:" + oldCourseID + "->" + newCourseID + ": Swap Fail");
                            result = "Swap Fail!";
                        }
                    } else {
                        this.log.info("Swap Course:" + oldCourseID + "->" + newCourseID + ": Swap Fail");
                        result = "Swap Fail!";
                    }
                } else {
                    this.log.info("Swap Course:" + oldCourseID + "->" + newCourseID + ": Swap Fail");
                    result = "Swap Fail!";
                }
            } else {
                DepartEnum oldcourseDep;
                String sendEnrolAvailMsg;

                if (!studentDepart.equals(AcourseDepart) && AcourseDepart.equals(BcourseDepart)) {
                    oldcourseDep = DepartEnum.getInstance(newCourseID.substring(0, 4));
                    String sendNewStatusMsg = "REQUEST_NEWCOURSE_STATUS " + studentID + " " + newCourseID;
                    newStatus = this.serverToServer(oldcourseDep, sendNewStatusMsg);
                    dropSendMsg = "REQUEST_OLDCOURSE_STATUS " + studentID + " " + oldCourseID;
                    oldStatus = this.serverToServer(oldcourseDep, dropSendMsg);
                    if (Pattern.matches("(fall|winter|summer)", oldStatus) && Pattern.matches("(fall|winter|summer)", newStatus)) {
                        if (oldStatus.equals(newStatus)) {
                            sendEnrolAvailMsg = "REQUEST_ENROL_AVAIL " + newCourseID + " " + newStatus;
                            sendEnrolAvailMsg = this.serverToServer(oldcourseDep, sendEnrolAvailMsg);
                            if (sendEnrolAvailMsg.equals("True")) {
                                checkEnrolAvail = "REQUEST_DROP " + studentID + " " + oldCourseID;
                                this.serverToServer(oldcourseDep, checkEnrolAvail);
                                sendEnrolMsg = "REQUEST_ENROLL " + studentID + " " + newCourseID + " " + newStatus;
                                this.serverToServer(oldcourseDep, sendEnrolMsg);
                                this.log.info("Swap Course:" + oldCourseID + "->" + newCourseID + ": Swap Successful");
                                result = "Swap Course Successful!";
                            } else {
                                this.log.info("Swap Course:" + oldCourseID + "->" + newCourseID + ": Swap Fail");
                                result = "Swap Fail!";
                            }
                        } else {
                            this.log.info("Swap Course:" + oldCourseID + "->" + newCourseID + ": Swap Fail");
                            result = "Swap Fail!";
                        }
                    } else {
                        this.log.info("Swap Course:" + oldCourseID + "->" + newCourseID + ": Swap Fail");
                        result = "Swap Fail!";
                    }
                } else if (!studentDepart.equals(AcourseDepart) && !AcourseDepart.equals(BcourseDepart)) {
                    oldStatus = "null";
                    newStatus = "null";
                    oldcourseDep = DepartEnum.getInstance(oldCourseID.substring(0, 4));
                    newcourseDep = DepartEnum.getInstance(newCourseID.substring(0, 4));
                    dropSendMsg = "REQUEST_NEWCOURSE_STATUS " + studentID + " " + newCourseID;
                    newStatus = this.serverToServer(newcourseDep, dropSendMsg);
                    sendEnrolAvailMsg = "REQUEST_OLDCOURSE_STATUS " + studentID + " " + oldCourseID;
                    oldStatus = this.serverToServer(oldcourseDep, sendEnrolAvailMsg);
                    if (Pattern.matches("(fall|winter|summer)", oldStatus) && Pattern.matches("(fall|winter|summer)", newStatus)) {
                        if (oldStatus.equals(newStatus)) {
                            sendEnrolAvailMsg = "REQUEST_ENROL_AVAIL " + newCourseID + " " + newStatus;
                            checkEnrolAvail = this.serverToServer(newcourseDep, sendEnrolAvailMsg);
                            if (checkEnrolAvail.equals("True")) {
                                dropSendMsg = "REQUEST_DROP " + studentID + " " + oldCourseID;
                                this.serverToServer(oldcourseDep, dropSendMsg);
                                sendEnrolMsg = "REQUEST_ENROLL " + studentID + " " + newCourseID + " " + newStatus;
                                this.serverToServer(newcourseDep, sendEnrolMsg);
                                this.log.info("Swap Course:" + oldCourseID + "->" + newCourseID + ": Swap Successful");
                                result = "Swap Course Successful!";
                            } else {
                                this.log.info("Swap Course:" + oldCourseID + "->" + newCourseID + ": Swap Fail");
                                result = "Swap Fail!";
                            }
                        } else {
                            this.log.info("Swap Course:" + oldCourseID + "->" + newCourseID + ": Swap Fail");
                            result = "Swap Fail!";
                        }
                    } else {
                        this.log.info("Swap Course:" + oldCourseID + "->" + newCourseID + ": Swap Fail");
                        result = "Swap Fail!";
                    }
                }
            }
        }

        lock.unlock();
        return result;
    }

    private String send(String content, int dest) throws SocketException {
        String reply = null;
        byte[] message = content.getBytes();

        try {
            DatagramSocket aSocket = new DatagramSocket();
            DatagramPacket packet = new DatagramPacket(message, message.length, InetAddress.getByName("localhost"), dest);
            aSocket.send(packet);
            byte[] data2 = new byte[1024];
            DatagramPacket packet2 = new DatagramPacket(data2, data2.length);
            aSocket.receive(packet2);
            reply = new String(data2, 0, packet2.getLength());
            aSocket.close();
        } catch (UnknownHostException var9) {
            var9.printStackTrace();
        } catch (SocketException var10) {
            var10.printStackTrace();
        } catch (IOException var11) {
            var11.printStackTrace();
        }

        return reply;
    }

    public String serverToServer(DepartEnum destination, String sendmsg) {
        String result = "null";

        try {
            result = this.send(sendmsg, destination.getPort());
        } catch (SocketException var5) {
            var5.printStackTrace();
        }

        return result;
    }
}
