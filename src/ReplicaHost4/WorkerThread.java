package ReplicaHost4;

import ReplicaHost4.DCRS.DCRSImpl;
import ReplicaHost4.DCRS.DCRSWrong;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;

public class WorkerThread implements Runnable {

    DatagramPacket packet;
    DatagramSocket socket;
    DCRSImpl department;
    DCRSWrong dcrsWrong;
    boolean bug;

    public WorkerThread(DatagramSocket socket, DatagramPacket packet , DCRSImpl dcrs, DCRSWrong dcrsWrong, boolean bug) {
        this.department = dcrs;
        this.packet = packet;
        this.socket = socket;
        this.dcrsWrong = dcrsWrong;
        this.bug = bug;
    }

    @Override
    public void run() {
        InetAddress address = null;
        String message = new String(packet.getData(), 0 , packet.getLength());
        String info = null;
        if (!message.equals("Hi")){
            String[] ms = message.split(":");
            info = ms[1];
        } else {
            info = "Hi";
        }

        int port = 8800;
        byte[] data2 = null;
        DatagramPacket packet2 = null;


        try {

            String[] command = info.split(" ");

            String result = "";

            if (!bug){
                switch(command[0]) {
                    case "addCourse" :
                        result = dcrsWrong.addCourse(command[1],command[2]);
                        department.addCourse(command[1], command[2]);
                        break;
                    case "removeCourse" :
                        result = dcrsWrong.removeCourse(command[1],command[2]);
                        department.removeCourse(command[1],command[2]);
                        break;
                    case "listCourseAvailability" :
                        department.listCourseAvailability(command[1]);
                        String[] res = dcrsWrong.listCourseAvailability(command[1]);
                        StringBuilder r = new StringBuilder();
                        Arrays.stream(res).forEach(record -> r.append(record).append(" "));
                        result = r.toString().trim();
                        break;
                    case "enrolCourse" :
                        department.enrolCourse(command[1], command[2], command[3]);
                        result = dcrsWrong.enrolCourse(command[1], command[2], command[3]);
                        break;
                    case "dropCourse" :
                        department.dropCourse(command[1], command[2]);
                        result = dcrsWrong.dropCourse(command[1], command[2]);
                        break;
                    case "getClassSchedule" :
                        StringBuilder sb = new StringBuilder();
                        department.getClassSchedule(command[1]);
                        Arrays.stream(dcrsWrong.getClassSchedule(command[1])).forEach(record -> sb.append(record).append(" "));
                        result = sb.toString().trim();
                        break;
                    case "swapCourse" :
                        department.swapCourse(command[1],command[2],command[3]);
                        result = dcrsWrong.swapCourse(command[1],command[2],command[3]);
                        break;
                    case "Hi" :
                        ReplyEcho(this.packet);
                        break;
                    default :
                        System.out.println("Invalid Command!");
                }
            }else {
                switch(command[0]) {
                    case "addCourse" :
                        result = department.addCourse(command[1], command[2]);
                        break;
                    case "removeCourse" :
                        result = department.removeCourse(command[1],command[2]);
                        break;
                    case "listCourseAvailability" :
                        String[] res = department.listCourseAvailability(command[1]);
                        StringBuilder r = new StringBuilder();
                        Arrays.stream(res).forEach(record -> r.append(record).append(" "));
                        result = r.toString().trim();
                        break;
                    case "enrolCourse" :
                        result = department.enrolCourse(command[1], command[2], command[3]);
                        break;
                    case "dropCourse" :
                        result = department.dropCourse(command[1], command[2]);
                        break;
                    case "getClassSchedule" :
                        StringBuilder sb = new StringBuilder();
                        Arrays.stream(department.getClassSchedule(command[1])).forEach(record -> sb.append(record).append(" "));
                        result = sb.toString().trim();
                        break;
                    case "swapCourse" :
                        result = department.swapCourse(command[1],command[2],command[3]);
                        break;
                    case "Hi" :
                        ReplyEcho(this.packet);
                        break;
                    default :
                        System.out.println("Invalid Command!");
                }
            }

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

    private void ReplyEcho(DatagramPacket packet) {
        try {
            DatagramSocket socket = new DatagramSocket();
            byte[] reply = "Hi".getBytes();
            DatagramPacket packet1 = new DatagramPacket(reply, 0, reply.length, packet.getAddress(), packet.getPort());
            socket.send(packet1);
            socket.close();

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
