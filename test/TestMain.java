
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class TestMain {
    public static void main(String[] args) throws IOException {
        DatagramSocket socket = new DatagramSocket(8089);
        byte[] data = new byte[1024];
        DatagramPacket packet = new DatagramPacket(data, data.length);
        while(true){
            socket.receive(packet);

            System.out.println(new String(packet.getData(), 0, packet.getLength()));
        }
    }
}
