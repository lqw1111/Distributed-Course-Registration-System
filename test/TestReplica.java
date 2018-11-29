
import org.junit.Test;

import java.io.IOException;
import java.net.*;

public class TestReplica {


    public static void sendRequest(String deparment, String msg) throws IOException {
        InetAddress address = InetAddress.getByName("localhost");
        String ms = deparment + ":" + msg;
        byte[] data = ms.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(data, data.length, address, 3333);

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
//        sendRequest("comp","addCourse comp3 fall");
//        sendRequest("comp","removeCourse comp1 fall");
//        sendRequest("comp", "listCourseAvailability fall");
        TestReplica testReplica = new TestReplica();
        testReplica.senario5();

//        sendRequest("comp","addCourse comp3 fall");
//        sendRequest("comp","addCourse comp3 fall");
    }

    public void senario1() throws IOException {
        sendRequest("comp","addCourse comp3 fall");
//        sendRequest("comp","removeCourse comp3 fall");
        sendRequest("comp", "listCourseAvailability fall");
    }

    public void senario2() throws IOException {
       sendRequest("comp","addCourse comp3 fall");
        sendRequest("comp","removeCourse comp1 fall");
        sendRequest("comp", "listCourseAvailability fall");
    }

    public void senario3() throws IOException {
        sendRequest("soen","addCourse soen1 fall");
        sendRequest("soen","addCourse soen2 fall");
        sendRequest("soen","addCourse soen3 fall");
        sendRequest("comp","enrolCourse comps1234 soen3 fall");
        sendRequest("comp","enrolCourse comps1234 soen2 fall");
        sendRequest("comp","enrolCourse comps1234 soen1 fall");
        sendRequest("soen", "listCourseAvailability fall");
    }
    public void senario4() throws IOException {
        sendRequest("comp","addCourse comp1 fall");
        sendRequest("comp","addCourse comp2 fall");
        sendRequest("comp","addCourse comp3 fall");
        sendRequest("comp","addCourse comp4 fall");
        sendRequest("comp","enrolCourse comps1234 comp1 fall");
        sendRequest("comp","enrolCourse comps1234 comp2 fall");
        sendRequest("comp","enrolCourse comps1234 comp3 fall");
        sendRequest("comp","enrolCourse comps1234 comp4 fall");
        sendRequest("soen", "listCourseAvailability fall");
    }
    public void senario5() throws IOException {
        sendRequest("comp","addCourse comp1 fall");
        sendRequest("comp","addCourse comp2 fall");
        sendRequest("comp","addCourse comp3 fall");
        sendRequest("soen","addCourse soen1 fall");
        sendRequest("soen","addCourse soen2 fall");
        sendRequest("soen","addCourse soen3 fall");
        sendRequest("comp","enrolCourse comps1234 comp1 fall");
        sendRequest("comp","swapCourse comps1234 soen1 comp1");
        sendRequest("soen", "listCourseAvailability fall");
    }
    public void senario6() throws IOException {
        sendRequest("comp","addCourse comp1 fall");
        sendRequest("comp","addCourse comp2 fall");
        sendRequest("comp","addCourse comp3 fall");
        sendRequest("soen","addCourse soen1 fall");
        sendRequest("soen","addCourse soen2 fall");
        sendRequest("soen","addCourse soen3 fall");
        sendRequest("comp","enrolCourse comps1234 comp1 fall");
        sendRequest("comp","swapCourse comps1234 comp2 comp1");
        sendRequest("soen", "listCourseAvailability fall");
    }
    public void senario7() throws IOException {
        sendRequest("inse","addCourse inse1 fall");
        sendRequest("inse","addCourse inse2 fall");
        sendRequest("inse","addCourse inse3 fall");
        sendRequest("soen","addCourse soen1 fall");
        sendRequest("soen","addCourse soen2 fall");
        sendRequest("soen","addCourse soen3 fall");
        sendRequest("comp","enrolCourse comps1234 inse1 fall");
        sendRequest("comp","swapCourse comps1234 soen1 inse1");
        sendRequest("soen", "listCourseAvailability fall");
    }
    public void senario8() throws IOException {
        sendRequest("inse","addCourse inse1 fall");
        sendRequest("inse","addCourse inse2 fall");
        sendRequest("inse","addCourse inse3 fall");
        sendRequest("soen","addCourse soen1 fall");
        sendRequest("soen","addCourse soen2 fall");
        sendRequest("soen","addCourse soen3 fall");
        sendRequest("comp","enrolCourse comps1234 soen2 fall");
        sendRequest("comp","swapCourse comps1234 soen1 soen2");
        sendRequest("soen", "listCourseAvailability fall");
    }
    public void senario9() throws IOException {
        sendRequest("comp","addCourse comp6231 winter");
        sendRequest("comp","addCourse comp6281 winter");
        sendRequest("inse","addCourse inse6320 winter");
        sendRequest("soen","addCourse soen6441 winter");
        sendRequest("soen","enrolCourse soens4444 inse6320 winter");
        sendRequest("soen","enrolCourse soens4444 comp6231 winter");
        sendRequest("soen","enrolCourse soens5555 comp6281 winter");
        sendRequest("soen","enrolCourse soens5555 soen6441 winter");

        sendRequest("soen", "listCourseAvailability winter");
        sendRequest("soen","enrolCourse soens1111 inse6320 winter");
        sendRequest("soen","enrolCourse soens1111 comp6231 winter");
        sendRequest("soen","enrolCourse soens1111 comp6281 winter");
        sendRequest("soen","enrolCourse soens1111 soen6441 winter");
        sendRequest("inse","enrolCourse inses1111 inse6320 winter");
        sendRequest("soen","getClassSchedule soens1111");
        sendRequest("inse","removeCourse inse6320 winter");
        sendRequest("soen", "listCourseAvailability winter");
        sendRequest("soen","getClassSchedule soens1111");
    }
}
