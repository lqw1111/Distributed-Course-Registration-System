package ReplicaHost1;

import ReplicaHost1.DCRS.DCRSImpl;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;
import java.util.List;

public class WorkerThread implements Runnable {

    DatagramPacket packet;
    DatagramSocket socket;
    DCRSImpl department;

    public WorkerThread(DatagramSocket socket, DatagramPacket packet , DCRSImpl dcrs) {
        this.department = dcrs;
        this.packet = packet;
        this.socket = socket;
    }

    @Override
    public void run() {
        InetAddress address = null;
        String message = new String(packet.getData(), 0 , packet.getLength());
        String[] ms = message.split(":");
        String info = ms[1];
        int port = 8800;
        byte[] data2 = null;
        DatagramPacket packet2 = null;

        try {

            String[] command = info.split(" ");

            String result = "";

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
