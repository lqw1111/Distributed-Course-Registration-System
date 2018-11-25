package Client;

import FrontEndCorba.FrontEnd;
import ReplicaHost1.DCRS.certificateIdentity;
import ReplicaHost1.Log.LoggerFormatter;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AdvisorClient extends Client{
    public String advisortId;

    public FrontEnd Login(){

        System.out.println("Input Your Id:");
        Scanner sc = new Scanner(System.in);
        String cmd = sc.nextLine();

        advisortId = cmd;

        certificateIdentity certificateIdentity = parseStatus(cmd);
        if (certificateIdentity.getStatus().equals("a")){

            FrontEnd servent = connect("frontEnd");
            return servent;
        } else {
            System.out.println("It's Not An Advisor Account");
            return null;
        }
    }

    public void startAdvisorClient(FrontEnd frontEnd , String AdvisorId) throws RemoteException {
        Logger logger = Logger.getLogger("client.log");
        logger.setLevel(Level.ALL);

        FileHandler fileHandler = null;
        try {
            fileHandler = new FileHandler(AdvisorId + ".log");
        } catch (IOException e) {
            e.printStackTrace();
        }
        fileHandler.setFormatter(new LoggerFormatter());
        logger.addHandler(fileHandler);
        logger.info(AdvisorId + "  Login Successful");

        Scanner sc = new Scanner(System.in);
        String cmd = "";

        while(true){
            System.out.println("Input your operate: \n           " +
                    "1.Add Course\n           " +
                    "2.Remove Course\n           " +
                    "3.List Course Availability\n           " +
                    "4.Enroll Course\n           " +
                    "5.Drop Course\n           " +
                    "6.Get Class Schedule\n           " +
                    "7.Swap Course");
            cmd = sc.nextLine();

            String result = "";
            String courseId = "";
            String studendId = "";

            switch(cmd) {
                //add
                case "1" :
                    System.out.println("Input The Course Id: ");
                    courseId = sc.nextLine();
                    System.out.println("Input Semester: ");
                    String semester = sc.nextLine();
                    result = frontEnd.addCourse(courseId,semester);
                    logger.info("Operation: " + result);
                    break;
                //remove
                case "2" :
                    System.out.println("Input The Course Id: ");
                    courseId = sc.nextLine();
                    System.out.println("Input The Semester : ");
                    semester = sc.nextLine();
                    result = frontEnd.removeCourse(courseId,semester);
                    logger.info("Operation: " + result);
                    break;
                //listCourseAvailability
                case "3" :
                    System.out.println("Input the semester");
                    semester = sc.nextLine();
                    String[] courseAvailability = frontEnd.listCourseAvailability(semester);
                    System.out.println("Print Course List: ");
                    for (String course : courseAvailability){
                        System.out.println("              " + course);
                    }
                    logger.info("list Course Availability");
                    break;
                //enrolCourse
                case "4" :
                    System.out.println("Input The Student Id: ");
                    studendId = sc.nextLine();
                    System.out.println("Input The Course Id : ");
                    courseId = sc.nextLine();
                    System.out.println("Input The Semester : ");
                    semester = sc.nextLine();
                    result = frontEnd.enrolCourse(studendId,courseId,semester);
                    logger.info("Operation: " + result);
                    break;
                //dropCourse
                case "5" :
                    System.out.println("Input The Student Id: ");
                    studendId = sc.nextLine();
                    System.out.println("Input The Course Id : ");
                    courseId = sc.nextLine();
                    result = frontEnd.dropCourse(studendId,courseId);
                    logger.info("Operation: " + result);
                    break;
                //getClassSchedule
                case "6" :
                    System.out.println("Input The Student Id: ");
                    studendId = sc.nextLine();
                    String[] classSchedule = frontEnd.getClassSchedule(studendId);
                    if (classSchedule == null){
                        logger.info("The Student Does Not Exist! Please Contact With Advisor!");
                    }else {
                        System.out.println("Student " + studendId + ":");
                        for (String course :
                                classSchedule) {
                            System.out.println("                      " + course);
                        }
                        logger.info("Operation: get class schedule");
                    }
                    break;
                case "7" :
                    System.out.println("Input The Student Id: ");
                    studendId = sc.nextLine();
                    System.out.println("Input The New Course Id: ");
                    String newCourseId = sc.nextLine();
                    System.out.println("Input The Old Course Id: ");
                    String oldCourseId = sc.nextLine();
                    result = frontEnd.swapCourse(studendId, newCourseId, oldCourseId);
                    logger.info("Operation: " + result);
                    break;
                default :
                    System.out.println("Invalid Command!");
            }

        }
    }

    public static void main(String[] args) throws RemoteException{
        AdvisorClient advisorClient = new AdvisorClient();
        FrontEnd servent = advisorClient.Login();
        if (servent != null){
            advisorClient.startAdvisorClient(servent, advisorClient.advisortId);
        }
    }
}
