
import org.junit.Test;

import java.io.IOException;
import java.net.*;

public class TestReplica {


    public static void sendRequest(String deparment, String msg) throws IOException {
        InetAddress address = InetAddress.getByName("localhost");
        String ms = deparment + ":" + msg;
        byte[] data = ms.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(data, data.length, address, 2222);

        DatagramSocket socket = new DatagramSocket(2002);

        socket.send(sendPacket);

        byte[] recvData = new byte[1024];
        DatagramPacket recvPacket = new DatagramPacket(recvData, recvData.length);
        socket.receive(recvPacket);

        String receiveMessage = new String(recvPacket.getData(), 0, recvPacket.getLength());

        System.out.println(receiveMessage);

        socket.close();

//        packageMsgAndSendToFE(socket, msg.getFEHostAddress(), receiveMessage);
    }

    public static void main(String[] args) throws IOException {
        System.out.println("---1---");
        sendRequest("comp","addCourse comp3 fall");
//        sendRequestCompara("comp","addCourse comp3 fall");
//
//        System.out.println("---2---");
//        sendRequest("comp","removeCourse comp1 fall");

//        System.out.println("---3---");
//        sendRequest("comp", "listCourseAvailability fall");

        System.out.println("---4---");
        sendRequest("comp","enrolCourse comps1111 comp3 fall");
        sendRequest("comp", "listCourseAvailability fall");
        sendRequest("comp","getClassSchedule comps1111");

//        sendRequestCompara("comp","enrolCourse comps1111 comp3 fall");
//        sendRequestCompara("comp", "listCourseAvailability fall");
//        sendRequestCompara("comp","getClassSchedule comps1111");

//        System.out.println("---5---");
//        sendRequestCompara("comp","swapCourse comps1111 comp3 comp2");
//        sendRequestCompara("comp", "listCourseAvailability fall");
//        sendRequestCompara("comp","getClassSchedule comps1111");

//        sendRequest("comp","swapCourse comps1111 comp3 comp2");
//        sendRequest("comp", "listCourseAvailability fall");
//        sendRequest("comp","getClassSchedule comps1111");
//
        System.out.println("---6---");
        sendRequest("comp","dropCourse comps1111 comp3");
        sendRequest("comp", "listCourseAvailability fall");
        sendRequest("comp","getClassSchedule comps1111");

//        sendRequestCompara("comp","dropCourse comps1111 comp3");
//        sendRequestCompara("comp", "listCourseAvailability fall");
//        sendRequestCompara("comp","getClassSchedule comps1111");
    }

    public static void sendRequestCompara(String deparment, String msg) throws IOException {
        InetAddress address = InetAddress.getByName("localhost");
        String ms = deparment + ":" + msg;
        byte[] data = ms.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(data, data.length, address, 1111);

        DatagramSocket socket = new DatagramSocket(2001);

        socket.send(sendPacket);

        byte[] recvData = new byte[1024];
        DatagramPacket recvPacket = new DatagramPacket(recvData, recvData.length);
        socket.receive(recvPacket);

        String receiveMessage = new String(recvPacket.getData(), 0, recvPacket.getLength());

        System.out.println(receiveMessage);

        socket.close();

//        packageMsgAndSendToFE(socket, msg.getFEHostAddress(), receiveMessage);
    }

}
