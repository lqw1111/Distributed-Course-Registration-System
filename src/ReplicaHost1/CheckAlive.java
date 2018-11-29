package ReplicaHost1;

import PortInfo.AddressInfo;
import PortInfo.AlivePort;
import PortInfo.Replica;
import PortInfo.Replication;

import java.io.IOException;
import java.net.*;
import java.util.Timer;
import java.util.TimerTask;

public class CheckAlive implements Runnable{
    String crashServerNum;

    public CheckAlive(String crashServerNum){
        this.crashServerNum = crashServerNum;
    }
    @Override
    public void run() {
        switch (crashServerNum){
            case "1" :
                checkEcho(AddressInfo.ADDRESS_INFO.RM1address,Replication.REPLICATION.Replica1, AlivePort.ALIVE_PORT.RM1);
                break;
            case "2" :
                checkEcho(AddressInfo.ADDRESS_INFO.RM2address,Replication.REPLICATION.Replica2, AlivePort.ALIVE_PORT.RM2);
                break;
            case "3" :
                checkEcho(AddressInfo.ADDRESS_INFO.RM3address,Replication.REPLICATION.Replica3, AlivePort.ALIVE_PORT.RM3);
                break;
            case "4" :
                checkEcho(AddressInfo.ADDRESS_INFO.RM4address,Replication.REPLICATION.Replica4, AlivePort.ALIVE_PORT.RM4);
                break;
            default:
                System.out.println("Invaild Number");
        }
    }

    private void checkEcho(String RMaddress, int replica, int alivePort){
        try {
            InetAddress address = InetAddress.getByName(RMaddress);
            DatagramSocket socket = new DatagramSocket();
            byte[] data = "Hi".getBytes();
            DatagramPacket packet = new DatagramPacket(data, 0 ,data.length, address, replica);
            socket.send(packet);

            byte[] data2 = new byte[1024];
            DatagramPacket packet1 = new DatagramPacket(data2,data2.length);

            ReplicaHost1.Timer timer = new ReplicaHost1.Timer(socket);
            Thread thread = new Thread(timer);
            thread.start();

            socket.receive(packet1);


        } catch (SocketException e) {
//            e.printStackTrace();
            //Get Timeout Exception
            TellRM(RMaddress, alivePort);

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void TellRM(String RMaddress , int port) {
        try {
            InetAddress address = InetAddress.getByName(RMaddress);
            DatagramSocket socket = new DatagramSocket();
            byte[] data = Failure.ServerCrash.toString().getBytes();
            DatagramPacket packet = new DatagramPacket(data, 0 ,data.length, address, port);
            socket.send(packet);

            socket.close();

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
