import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class Server {
    public static void main(String[] args) throws IOException, InterruptedException {
        DatagramSocket socket = new DatagramSocket(8001);
        byte[] data = new byte[1024];
        DatagramPacket packet = new DatagramPacket(data,data.length);

        while (true){
            socket.receive(packet);

            Thread.sleep(1000);
            String rec = new String(packet.getData(),0, packet.getLength());

            System.out.println(rec);
        }

    }
}
