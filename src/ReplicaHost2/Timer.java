package ReplicaHost2;

import java.net.DatagramSocket;

public class Timer implements Runnable {

    DatagramSocket socket;

    public Timer(DatagramSocket socket){
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(2000);
            this.socket.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
