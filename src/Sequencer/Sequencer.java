package Sequencer;

import PortInfo.Replica;
import PortInfo.SequencerPort;
import java.io.IOException;
import java.net.*;

public class Sequencer {

    private Integer sequenceNumber;

    public Sequencer() {
        this.sequenceNumber = 0;
    }

    public void listen(int udpPort) throws IOException {
        DatagramSocket socket = new DatagramSocket(udpPort);
        DatagramPacket packet = null;
        byte[] data = null;
        int count = 0;
        while(true)
        {
            data = new byte[1024];
            packet = new DatagramPacket(data, data.length);
            socket.receive(packet);

            String FEIpAddress = packet.getAddress().getHostAddress();
            String receiveMessage = new String(packet.getData(), 0, packet.getLength());

            packetAndSendMessage(FEIpAddress, receiveMessage, socket);

            count++;
            System.out.println("Server Connected：" + count);
            InetAddress address = packet.getAddress();
            System.out.println("Server IP："+address.getHostAddress());

        }
    }

    private void multicastMessage(String packageMessage, DatagramSocket socket) throws IOException {

        //TODO：可能不是本地的localhost，需要修改为replica主机的ip
        InetAddress address = InetAddress.getByName("localhost");

        byte[] data = packageMessage.getBytes();
        DatagramPacket sendPacket1 = new DatagramPacket(data, data.length, address, Replica.REPLICA.replica1);
        DatagramPacket sendPacket2 = new DatagramPacket(data, data.length, address, Replica.REPLICA.replica2);
        DatagramPacket sendPacket3 = new DatagramPacket(data, data.length, address, Replica.REPLICA.replica3);
        DatagramPacket sendPacket4 = new DatagramPacket(data, data.length, address, Replica.REPLICA.replica4);

        socket.send(sendPacket1);
        socket.send(sendPacket2);
        socket.send(sendPacket3);
        socket.send(sendPacket4);
    }

    private void packetAndSendMessage(String FEIpAddress, String receiveMessage, DatagramSocket socket) throws IOException {
        synchronized (this.sequenceNumber){
            //1.packet the message, give a sequence number, sequence number ++
            String sendMessage = this.sequenceNumber.toString() + ":" + FEIpAddress + ":" + receiveMessage;
            this.sequenceNumber ++;

            //2.multicast the message to replica
            multicastMessage(sendMessage, socket);
        }
    }

    public static void main(String[] args) throws IOException {
        Sequencer sequencer = new Sequencer();
        sequencer.listen(SequencerPort.SEQUENCER_PORT.sequencerPort);
    }
}

