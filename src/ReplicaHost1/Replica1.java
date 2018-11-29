package ReplicaHost1;

import ReplicaHost1.DCRS.DCRSImpl;
import ReplicaHost1.DCRS.DCRSWrong;
import ReplicaHost1.DCRS.UdpWokerThread;
import ReplicaHost1.Log.LoggerFormatter;
import java.io.IOException;
import java.net.*;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Replica1 {

    public boolean bugFree;
    public Logger logger;
    public DCRSImpl compServer;
    public DCRSImpl soenServer;
    public DCRSImpl inseServer;
    public DCRSWrong compWrong;
    public DCRSWrong soenWrong;
    public DCRSWrong inseWrong;

    public Replica1(Logger logger , DCRSImpl compServer, DCRSImpl soenServer, DCRSImpl inseServer, DCRSWrong compWrong, DCRSWrong soenWrong, DCRSWrong inseWrong){
        this.logger = logger;
        this.compServer = compServer;
        this.soenServer = soenServer;
        this.inseServer = inseServer;
        this.compWrong = compWrong;
        this.soenWrong = soenWrong;
        this.inseWrong = inseWrong;
        this.bugFree = true;
    }

    private void reRunReplica(int RepBackPort, int rmBackPort){
        //TODO:请求RM的backUpQ，得到history message, rerun the message
        try {
            InetAddress address = InetAddress.getByName("localhost");
            DatagramSocket socket = new DatagramSocket(RepBackPort);
            byte[] data = Failure.BackUp.toString().getBytes();
            DatagramPacket packet = new DatagramPacket(data,0 ,data.length,address, rmBackPort);
            socket.send(packet);

            while (true){
                byte[] buffer = new byte[1024];
                DatagramPacket packet1 = new DatagramPacket(buffer, buffer.length);

                socket.receive(packet1);
                String msg = new String(packet1.getData(), 0 , packet1.getLength());
                String department = msg.split(":")[0];

                Thread thread = new Thread(new BackUpThread(socket, packet1, getDepartment(department)));
                thread.start();
            }

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startReplica(int replica1Port) throws IOException {
        DatagramSocket socket = new DatagramSocket(replica1Port);
        DatagramPacket packet = null;
        byte[] data = null;
        logger.info(" Replica Server Start");
        while(true)
        {
            data = new byte[1024];
            packet = new DatagramPacket(data, data.length);
            socket.receive(packet);
            logger.info("Replica Recv Message :" + new String(packet.getData(), 0, packet.getLength()));

            String recvMsg = new String(packet.getData(), 0 , packet.getLength());
            String department = recvMsg.split(":")[0];

            Thread thread = new Thread(new WorkerThread(socket, packet, getDepartment(department), getWrongDepartment(department), bugFree));
            thread.start();
        }
    }

    private DCRSImpl getDepartment(String department) {
        if (department.equals("comp"))
            return this.compServer;
        else if(department.equals("soen"))
            return this.soenServer;
        else
            return this.inseServer;
    }

    private DCRSWrong getWrongDepartment(String department) {
        if (department.equals("comp"))
            return this.compWrong;
        else if(department.equals("soen"))
            return this.soenWrong;
        else
            return this.inseWrong;
    }

    public static void configLogger(String department , Logger logger) throws IOException {
        logger.setLevel(Level.ALL);
        FileHandler compFileHandler = new FileHandler(department + ".log");
        compFileHandler.setFormatter(new LoggerFormatter());
        logger.addHandler(compFileHandler);
    }

    public void startUdpServer(int udpPort, DCRSImpl department) throws IOException {
        DatagramSocket socket = new DatagramSocket(udpPort);
        DatagramPacket packet = null;
        byte[] data = null;

        logger.info(" Upd Server Start");
        while(true)
        {
            data = new byte[1024];
            packet = new DatagramPacket(data, data.length);
            socket.receive(packet);

            logger.info("Server Recv Message :" + new String(packet.getData(), 0, packet.getLength()));

            Thread thread = new Thread(new UdpWokerThread(socket, packet, department));
            thread.start();

        }
    }

    public void startSoftFailPort(int port) throws IOException {
        DatagramSocket socket = new DatagramSocket(port);
        byte[] data = new byte[1024];
        DatagramPacket packet = new DatagramPacket(data, data.length);
        while (true){
            socket.receive(packet);
            this.bugFree = true;
        }
    }

    public static void main(String[] args) throws IOException {
        Logger compLogger = Logger.getLogger("comp.server1.log");
        configLogger("compServer1",compLogger);

        Logger soenLogger = Logger.getLogger("soen.server1.log");
        configLogger("soenServer1",soenLogger);

        Logger inseLogger = Logger.getLogger("inse.server1.log");
        configLogger("inseServer1",inseLogger);

        Logger replicaLogger = Logger.getLogger("replica1.log");
        configLogger("replica1", replicaLogger);

        DCRSImpl compServer = new DCRSImpl("comp",compLogger);

        DCRSImpl soenServer = new DCRSImpl("soen", soenLogger);

        DCRSImpl inseServer = new DCRSImpl("inse", inseLogger);

        DCRSWrong compWrong = new DCRSWrong();
        DCRSWrong soenWrong = new DCRSWrong();
        DCRSWrong inseWrong = new DCRSWrong();

        Replica1 replica1 = new Replica1(replicaLogger , compServer , soenServer , inseServer, compWrong, soenWrong, inseWrong);

        Runnable compTask = () -> {
            try {
                replica1.startUdpServer(1112, compServer);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        Runnable soenTask = () -> {
            try {
                replica1.startUdpServer(2223, soenServer);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        Runnable inseTask = () -> {
            try {
                replica1.startUdpServer(3334, inseServer);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        Runnable replicaTask = () -> {
            try {
                replica1.startReplica(1111);
            } catch (IOException e) {
                e.printStackTrace();
            }
        };

        Runnable softwareListener = () -> {
            try {
                replica1.startSoftFailPort(8881);
            } catch (IOException e) {
                e.printStackTrace();
            }
        };

        Thread t1 = new Thread(compTask);
        Thread t2 = new Thread(soenTask);
        Thread t3 = new Thread(inseTask);
        Thread t4 = new Thread(replicaTask);
        Thread t5 = new Thread(softwareListener);

        t1.start();
        t2.start();
        t3.start();
        t4.start();
        t5.start();
        replica1.reRunReplica(8081, 5001);

    }
}
