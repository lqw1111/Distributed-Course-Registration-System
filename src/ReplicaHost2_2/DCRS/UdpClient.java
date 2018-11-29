package ReplicaHost2.DCRS;

import java.io.IOException;
import java.net.*;

public class UdpClient{
    public static String request(String message, int portNumber){
        String result = "";
        DatagramSocket socket;
        try {
            socket = new DatagramSocket();
            InetAddress host = InetAddress.getLocalHost();
            int udpPort = portNumber;
            byte[] requestByte = (message + "\n").getBytes();

            DatagramPacket request = new DatagramPacket(requestByte, requestByte.length, host, udpPort);
            socket.send(request);

            byte[] buffer = new byte[1024];
            DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
            socket.receive(reply);
//            System.out.println("client reply ==> " + reply.getData());
            result = new String(reply.getData(),0, reply.getLength()).split("\n")[0] + "\n";
//            System.out.println("client result ==> " + result);

            socket.close();

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
