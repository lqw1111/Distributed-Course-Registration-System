import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class Client {
    public static void main(String[] args) {
        Runnable replicaTask = () -> {
            try {
                DatagramSocket socket = new DatagramSocket();
                String ms = "1";
                byte[] data = ms.getBytes();
                InetAddress address = InetAddress.getByName("localhost");
                DatagramPacket packet = new DatagramPacket(data,0,data.length,address,8001);
                socket.send(packet);

            } catch (SocketException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        };

        Thread t1 = new Thread(replicaTask);
        Thread t2 = new Thread(replicaTask);
        Thread t3 = new Thread(replicaTask);
        Thread t4 = new Thread(replicaTask);
        Thread t5 = new Thread(replicaTask);
        Thread t6 = new Thread(replicaTask);
        Thread t7 = new Thread(replicaTask);
        Thread t8 = new Thread(replicaTask);
        Thread t9 = new Thread(replicaTask);

        t1.start();
        t2.start();
        t3.start();
        t4.start();
        t5.start();
        t6.start();
        t7.start();
        t8.start();
        t9.start();
    }
}
