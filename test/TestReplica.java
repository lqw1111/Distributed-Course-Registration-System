
import org.junit.Test;

import java.io.IOException;
import java.net.*;

public class TestReplica {


    public static void sendRequest(String deparment, String msg) throws IOException {
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

    public static void main(String[] args) throws IOException {
        sendRequest("comp","enrolCourse comps1111 comp2 fall");
//        sendRequest("comp","enrolCourse comps1111 inse1 fall");
//        sendRequest("comp","enrolCourse comps1111 comp1 fall");
//        sendRequest("comp","getClassSchedule comps1111");
//        sendRequest("comp","removeCourse comp1 fall");
//        sendRequest("comp", "listCourseAvailability fall");

//        sendRequest("comp","addCourse comp3 fall");
//        sendRequest("comp","addCourse comp3 fall");
    }

}
