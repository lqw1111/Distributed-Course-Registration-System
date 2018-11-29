package ReplicaHost1;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;

public class Timer implements Runnable {

    DatagramSocket socket;

    public Timer(DatagramSocket socket){
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(5000);
            this.socket.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
