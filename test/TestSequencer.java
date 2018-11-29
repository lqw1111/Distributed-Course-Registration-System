import PortInfo.FEPort;
import PortInfo.SequencerPort;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class TestSequencer {

    public static void sendRequest(String deparment, String msg) throws IOException {
        InetAddress add = InetAddress.getByName("localhost");
        String ms = deparment + ":" + msg;
        byte[] data = ms.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(data, data.length, add, 8002);

        DatagramSocket socket = new DatagramSocket(FEPort.FE_PORT.FEPort);
//        DatagramSocket recvSocket = new DatagramSocket(FEPort.FE_PORT.FEPort);

        socket.send(sendPacket);
//
//        byte[] recvData = new byte[1024];
//        DatagramPacket recvPacket = new DatagramPacket(recvData, recvData.length);
//        recvSocket.receive(recvPacket);
//
//        String receiveMessage = new String(recvPacket.getData(), 0, recvPacket.getLength());
//
//        System.out.println(receiveMessage);
//
        socket.close();

//        packageMsgAndSendToFE(socket, msg.getFEHostAddress(), receiveMessage);
    }

    public static void main(String[] args) throws IOException {
        sendRequest("comp","listCourseAvailability fall");
    }
}
