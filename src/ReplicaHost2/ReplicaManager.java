//package ReplicaHost2;
//
//import PortInfo.FEPort;
//import PortInfo.Replica;
//import ReplicaHost2.Log.LoggerFormatter;
//
//import java.io.IOException;
//import java.net.DatagramPacket;
//import java.net.DatagramSocket;
//import java.net.InetAddress;
//import java.net.SocketException;
//import java.util.Comparator;
//import java.util.PriorityQueue;
//import java.util.Queue;
//import java.util.logging.FileHandler;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
//
//public class ReplicaManager {
//
//    public Logger logger;
//    public String ReplicaId;
//    public Integer SeqNumber;
//    public Integer DeliveryNum;
//    public Queue<Message> holdBackQueue;
//    public Queue<Message> deliveryQueue;
//    public Queue<Message> backUpQueue;
//
//    public ReplicaManager(Logger logger) throws IOException {
//        Comparator<Message> comparator = new Comparator<Message>() {
//            @Override
//            public int compare(Message o1, Message o2) {
//                return (Integer.valueOf(o1.getSeqId()) - Integer.valueOf(o2.getSeqId()));
//            }
//        };
//        this.logger = logger;
//        ReplicaId = "1";
//        DeliveryNum = 0;
//        SeqNumber = 0;
//        holdBackQueue = new PriorityQueue<Message>(comparator);
//        deliveryQueue = new PriorityQueue<Message>(comparator);
//        backUpQueue = new PriorityQueue<Message>(comparator);
//    }
//
//
//    public void startRMListener(int RMPort) throws Exception {
//        DatagramSocket socket = new DatagramSocket(RMPort);
//        DatagramPacket packet = null;
//        byte[] data = null;
//
//        while(true)
//        {
//            data = new byte[1024];
//            packet = new DatagramPacket(data, data.length);
//            socket.receive(packet);
//
//            String receiveMessage = new String(packet.getData(), 0, packet.getLength());
//
//
//            if (receiveMessage.equals(Failure.SoftWareFailure)){
//
//                //TODO:log software failure
//                logger.info("RM_" + ReplicaId + " SoftWareFailure");
//
//            } else if (receiveMessage.indexOf(Failure.ServerCrash.toString()) != -1){
//
//                //TODO:log server crash
////                logger.info("RM_" + ReplicaId + " Crash");
//                //check whether crash
//
//                String crashServerNum = receiveMessage.split(" ")[0];
//
//                if (crashServerNum != this.ReplicaId){
//                    Thread checkThread = new Thread(new CheckAlive(crashServerNum));
//                    checkThread.start();
//                }
//
//            } else {
//                Message recvMsg = parseToMessage(receiveMessage);
//                moveToHoldBackQueue(recvMsg);
//            }
//        }
//    }
//
//    private void moveToHoldBackQueue(Message recvMsg) throws IOException {
//        if(!holdBackQueue.contains(recvMsg)){
//            this.holdBackQueue.offer(recvMsg);
//            moveToDeliveryQueue();
//        } else {
////            reject();
//        }
//    }
//
//    private void moveToDeliveryQueue() throws IOException {
//        Message message = this.holdBackQueue.peek();
//        if (message == null) return;
//        if (Integer.valueOf(message.getSeqId()) == this.SeqNumber && !this.deliveryQueue.contains(message)){
//            Message msg = this.holdBackQueue.poll();
//            this.deliveryQueue.offer(msg);
//            this.SeqNumber ++;
//            executeMessage();
//            moveToDeliveryQueue();
//        }
//    }
//
//    private void executeMessage() throws IOException {
//        Message msg = this.deliveryQueue.peek();
//        if(msg != null && SeqNumber >= DeliveryNum && Integer.valueOf(msg.getSeqId()) == DeliveryNum){
//            //执行message
//            msg = this.deliveryQueue.poll();
//
//            sendToReplicaAndReturnRes(msg);
//
//            backUpQueue.offer(msg);
//            DeliveryNum ++;
//
//            //继续检查是否还有可以执行的message
//            executeMessage();
//        }
//    }
//
//    private void sendToReplicaAndReturnRes (Message msg) throws IOException {
//        InetAddress address = InetAddress.getByName("localhost");
//        String ms = msg.getDepartment() + ":" + msg.getMessage();
//        byte[] data = ms.getBytes();
//        DatagramPacket sendPacket = new DatagramPacket(data, data.length, address, ReplicaPort.REPLICA_PORT.port);
//
//        DatagramSocket socket = new DatagramSocket(2001);
//
//        socket.send(sendPacket);
//
//        byte[] recvData = new byte[1024];
//        DatagramPacket recvPacket = new DatagramPacket(recvData, recvData.length);
//
//        Timer timer = new Timer(socket);
//        Thread thread = new Thread(timer);
//        thread.start();
//        try{
//            socket.receive(recvPacket);
//        } catch (SocketException e) {
//            logger.info("TIME OUT:Replica_" + ReplicaId + " Not Reply");
//        }
//
//        String receiveMessage = new String(recvPacket.getData(), 0, recvPacket.getLength());
//
//        packageMsgAndSendToFE(socket, msg.getFEHostAddress(), receiveMessage);
//    }
//
//    private void packageMsgAndSendToFE (DatagramSocket socket, String feHostAddress, String receiveMessage) throws IOException {
//        InetAddress address = InetAddress.getByName(feHostAddress);
//        String msg = this.ReplicaId + ":" + receiveMessage;
//        byte[] data = msg.getBytes();
//        DatagramPacket sendPacket = new DatagramPacket(data, data.length, address , FEPort.FE_PORT.FEPort);
//
//        socket.send(sendPacket);
//        socket.close();
//    }
//
//    private Message parseToMessage(String receiveMessage) {
//        String[] message = receiveMessage.split(":");
//        String seqId = message[0];
//        String FEHostAddress = message[1];
//        String department = message[2];
//        String msg = message[3];
//        return new Message(seqId,FEHostAddress,department,msg);
//    }
//
//    private void listenCrash(int port){
//        try {
//            DatagramSocket socket = new DatagramSocket(port);
//            byte[] data = new byte[1024];
//            DatagramPacket packet = new DatagramPacket(data, data.length);
//
//            int count = 0;
//            while (count < 3){
//                socket.receive(packet);
//                count ++;
//            }
//
//            this.logger.info("Replica_" + ReplicaId + " Crash");
//
//        } catch (SocketException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void listenBackUp(int backUpPort){
//        try {
//            DatagramSocket socket = new DatagramSocket(backUpPort);
//            byte[] data = new byte[1024];
//            DatagramPacket packet = new DatagramPacket(data, data.length);
//
//            while (true) {
//                socket.receive(packet);
//                String msg = new String(packet.getData(),0 , packet.getLength());
//
//                Comparator<Message> comparator = new Comparator<Message>() {
//                    @Override
//                    public int compare(Message o1, Message o2) {
//                        return (Integer.valueOf(o1.getSeqId()) - Integer.valueOf(o2.getSeqId()));
//                    }
//
//                };
//                Queue<Message> q = new PriorityQueue<>(comparator);
//
//                if (msg.equals(Failure.BackUp)){
//                    while (!backUpQueue.isEmpty()){
//
//                        Message message = backUpQueue.poll();
//                        q.offer(message);
//
//                        String ms = message.getDepartment() + ":" + message.getMessage();
//
//                        byte[] senddata = ms.getBytes();
//                        DatagramPacket sendPacket = new DatagramPacket(senddata, senddata.length, packet.getAddress(), packet.getPort());
//
//                        socket.send(sendPacket);
//
//                        byte[] buffer = new byte[1024];
//                        DatagramPacket packet1 = new DatagramPacket(buffer, buffer.length);
//
//                        socket.receive(packet1);
//
//                    }
//                }
//
//                logger.info("BackUp End");
//                backUpQueue = q;
//            }
//
//
//        } catch (SocketException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static void main(String[] args) throws Exception {
//        Logger replicaLogger = Logger.getLogger("RM_1.log");
//        replicaLogger.setLevel(Level.ALL);
//
//        //Replica2
//        FileHandler FileHandler = new FileHandler("RM_1" + ".log");
//        FileHandler.setFormatter(new LoggerFormatter());
//        replicaLogger.addHandler(FileHandler);
//
//        ReplicaManager RM = new ReplicaManager(replicaLogger);
//        //打开监听comp,soen,inse server的端口
//
//        Runnable RMListenerTask = () -> {
//            try {
//                RM.startRMListener(Replica.REPLICA.replica1);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        };
//
//        Runnable CrashListener = () -> {
//            RM.listenCrash(7001);
//        };
//
//        Runnable BackUpListener = () -> {
//            RM.listenBackUp(5001);
//        };
//
//        Thread t1 = new Thread(RMListenerTask);
//        Thread t2 = new Thread(CrashListener);
//        Thread t3 = new Thread(BackUpListener);
//
//        t1.start();
//        t2.start();
//        t3.start();
//    }
//
//}
//
//enum ReplicaPort{
//    REPLICA_PORT;
//    final int port = 1111;
//    final int backUpPort = 8081;
//}