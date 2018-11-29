package ReplicaHost4;

import ReplicaHost4.DCRS.DCRSImpl;
import ReplicaHost4.DCRS.DCRSWrong;
import ReplicaHost4.DCRS.UdpWokerThread;
import ReplicaHost4.Log.LoggerFormatter;
import java.io.IOException;
import java.net.*;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Replica4 {

    public boolean bug;
    public Logger logger;
    public DCRSImpl compServer;
    public DCRSImpl soenServer;
    public DCRSImpl inseServer;
    public DCRSWrong compWrong;
    public DCRSWrong soenWrong;
    public DCRSWrong inseWrong;

    public Replica4(Logger logger , DCRSImpl compServer, DCRSImpl soenServer, DCRSImpl inseServer, DCRSWrong compWrong, DCRSWrong soenWrong, DCRSWrong inseWrong){
        this.logger = logger;
        this.compServer = compServer;
        this.soenServer = soenServer;
        this.inseServer = inseServer;
        this.compWrong = compWrong;
        this.soenWrong = soenWrong;
        this.inseWrong = inseWrong;
        this.bug = true;
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

            Thread thread = new Thread(new WorkerThread(socket, packet, getDepartment(department), getWrongDepartment(department), bug));
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
            this.bug = true;
        }
    }

    public static void main(String[] args) throws IOException {
        Logger compLogger = Logger.getLogger("comp.server4.log");
        configLogger("compServer4",compLogger);

        Logger soenLogger = Logger.getLogger("soen.server4.log");
        configLogger("soenServer4",soenLogger);

        Logger inseLogger = Logger.getLogger("inse.server4.log");
        configLogger("inseServer4",inseLogger);

        Logger replicaLogger = Logger.getLogger("replica4.log");
        configLogger("replica4", replicaLogger);

        DCRSImpl compServer = new DCRSImpl("comp",compLogger);

        DCRSImpl soenServer = new DCRSImpl("soen", soenLogger);

        DCRSImpl inseServer = new DCRSImpl("inse", inseLogger);

        DCRSWrong compWrong = new DCRSWrong();
        DCRSWrong soenWrong = new DCRSWrong();
        DCRSWrong inseWrong = new DCRSWrong();

        Replica4 replica4 = new Replica4(replicaLogger , compServer , soenServer , inseServer, compWrong, soenWrong, inseWrong);

        Runnable compTask = () -> {
            try {
                replica4.startUdpServer(7777, compServer);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        Runnable soenTask = () -> {
            try {
                replica4.startUdpServer(7778, soenServer);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        Runnable inseTask = () -> {
            try {
                replica4.startUdpServer(7779, inseServer);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        Runnable replicaTask = () -> {
            try {
                replica4.startReplica(4444);
            } catch (IOException e) {
                e.printStackTrace();
            }
        };

        Runnable softwareListener = () -> {
            try {
                replica4.startSoftFailPort(8884);
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
        replica4.reRunReplica(8084, 5004);

    }
}

