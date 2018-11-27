package ReplicaHost3.DCRS;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UdpWokerThread implements Runnable{

    DatagramSocket socket = null;
    DatagramPacket packet = null;
    DCRSImpl service = null;

    public UdpWokerThread(DatagramSocket socket, DatagramPacket packet, DCRSImpl service) {
        this.socket = socket;
        this.packet = packet;
        this.service = service;
    }

    @Override
    public void run() {
        String info = null;
        InetAddress address = null;
        int port = 8800;
        byte[] data2 = null;
        DatagramPacket packet2 = null;
        try {
            info = new String(packet.getData(), 0, packet.getLength());
//            System.out.println("我是服务器，客户端说："+info);

            String[] command = info.split("\\s+");

            String result = scenarios(command);

//            switch(command[0]) {
//                case "listCourseAvailability" :
//                    List<String> courseList = servent.getLocalCourseList(command[1]);
//                    for (String course :
//                            courseList) {
//                        result = result + course + " ";
//                    }
//                    break;
//                case "enrolCourse" :
//                    result = servent.enrolCourse(command[1],command[2],command[3]);
//                    break;
//                case "dropCourse" :
//                    result = servent.dropLocalCourse(command[1],command[2],command[3]);
//                    break;
//                case "dropRemovedCourseFromStuCourList" :
//                    result = servent.dropRemovedCourseFromStuCourList(command[1]);
//                    break;
//                case "checkDropAndEnroll" :
//                    result = servent.checkDropAndEnroll(command[1], command[2],command[3],command[4]);
//                    break;
//                case "checkWhetherCanEnrollAndEnroll" :
//                    result = servent.checkWhetherCanEnrollAndEnroll(command[1],command[2], command[3]);
//                    break;
//                case "checkEnroll" :
//                    result = servent.checkEnroll(command[1],command[2],command[3],command[4]);
//                    break;
//                case "dropOldCourse" :
//                    result = servent.dropOldCourse(command[1],command[2],command[3]);
//                    break;
//                default :
//                    System.out.println("Invalid Command!");
//            }

            address = packet.getAddress();
            port = packet.getPort();

            data2 = result.getBytes();
            packet2 = new DatagramPacket(data2, data2.length, address, port);
            socket.send(packet2);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //socket.close();不能关闭
    }

    public String scenarios(String[] req) {
        if (req[0].equals("show_available_courses"))
            return this.service.listCourseAvailabilityInLocal(req[1]);
        else if (req[0].equals("enrol"))
            return this.service.enrolCourse(req[1], req[2], req[3]);
        else if (req[0].equals("drop"))
            return this.service.dropCourse(req[1], req[2]);
        else if (req[0].equals("remove_student_course"))
            this.service.removeStudentCourse(req);
        else if (req[0].equals("swap"))
            return this.service.swapCourseDropRequest(req[1], req[2], req[3]);
        else if (req[0].equals("swap_enrol"))
            return this.service.swapCourseEnrolRequest(req[1], req[2], req[3]);
        else if (req[0].equals("swap_drop"))
            return this.service.swapCourseDropReply(req[1], req[2]);
        return "failure";
    }
}
