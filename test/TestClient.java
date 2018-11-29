import java.io.IOException;
import java.net.*;

public class TestClient {
    public static void main(String[] args) throws IOException {
        InetAddress address = InetAddress.getByName("localhost");
        byte[] data = new String("hi").getBytes();
        DatagramPacket packet = new DatagramPacket(data, 0, data.length, address , 8089);
        DatagramSocket socket = new DatagramSocket();

        socket.send(packet);
    }
}
