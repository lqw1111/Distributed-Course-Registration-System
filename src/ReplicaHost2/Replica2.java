package ReplicaHost2;

import ReplicaHost2.DCRS.DCRSImpl;
import ReplicaHost2.DCRS.UdpServer;
import ReplicaHost2.Log.LoggerFormatter;
import ReplicaHost2.DCRS.DCRSWrong;
import java.io.IOException;
import java.net.*;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Replica2 {

    public boolean bug;
    public Logger logger;
    public DCRSImpl compServer;
    public DCRSImpl soenServer;
    public DCRSImpl inseServer;
    public DCRSWrong compWrong;
    public DCRSWrong soenWrong;
    public DCRSWrong inseWrong;

    public Replica2(Logger logger , DCRSImpl compServer, DCRSImpl soenServer, DCRSImpl inseServer, DCRSWrong compWrong, DCRSWrong soenWrong, DCRSWrong inseWrong){
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
        Thread thread = new Thread(new UdpServer(udpPort, department));
        thread.start();
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
        Logger compLogger = Logger.getLogger("comp.server2.log");
        configLogger("compServer2",compLogger);

        Logger soenLogger = Logger.getLogger("soen.server2.log");
        configLogger("soenServer2",soenLogger);

        Logger inseLogger = Logger.getLogger("inse.server2.log");
        configLogger("inseServer2",inseLogger);

        Logger replicaLogger = Logger.getLogger("replica2.log");
        configLogger("replica2", replicaLogger);

        DCRSImpl compServer = new DCRSImpl("comp",compLogger);

        DCRSImpl soenServer = new DCRSImpl("soen", soenLogger);

        DCRSImpl inseServer = new DCRSImpl("inse", inseLogger);

        DCRSWrong compWrong = new DCRSWrong();
        DCRSWrong soenWrong = new DCRSWrong();
        DCRSWrong inseWrong = new DCRSWrong();

        Replica2 replica2 = new Replica2(replicaLogger , compServer , soenServer , inseServer, compWrong, soenWrong, inseWrong);

        Runnable compTask = () -> {
            try {
                replica2.startUdpServer(5556, compServer);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        Runnable soenTask = () -> {
            try {
                replica2.startUdpServer(5557, soenServer);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        Runnable inseTask = () -> {
            try {
                replica2.startUdpServer(5558, inseServer);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        Runnable replicaTask = () -> {
            try {
                replica2.startReplica(2222);
            } catch (IOException e) {
                e.printStackTrace();
            }
        };

        Runnable softwareListener = () -> {
            try {
                replica2.startSoftFailPort(8882);
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
        replica2.reRunReplica(8082, 5002);

    }
}
