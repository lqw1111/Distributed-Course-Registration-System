package ReplicaHost1.DCRS;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;

public class UdpWokerThread implements Runnable{

    DatagramSocket socket = null;
    DatagramPacket packet = null;
    DCRSImpl servent = null;

    public UdpWokerThread(DatagramSocket socket, DatagramPacket packet, DCRSImpl servent) {
        this.socket = socket;
        this.packet = packet;
        this.servent = servent;
    }

    @Override
    public void run() {
        String info = null;
        InetAddress address = null;
        int port = 8800;
        byte[] data2 = null;
        DatagramPacket packet2 = null;
        try {
            info = new String(packet.getData(), 0, packet.getLength());
//            System.out.println("我是服务器，客户端说："+info);

            String[] command = info.split(" ");

            String result = "";

            switch(command[0]) {
                case "listCourseAvailability" :
                    List<String> courseList = servent.getLocalCourseList(command[1]);
                    for (String course :
                            courseList) {
                        result = result + course + " ";
                    }
                    break;
                case "enrolCourse" :
                    result = servent.enrolCourse(command[1],command[2],command[3]);
                    break;
                case "dropCourse" :
                    result = servent.dropLocalCourse(command[1],command[2],command[3]);
                    break;
                case "dropRemovedCourseFromStuCourList" :
                    result = servent.dropRemovedCourseFromStuCourList(command[1]);
                    break;
                case "checkDropAndEnroll" :
                    result = servent.checkDropAndEnroll(command[1], command[2],command[3],command[4]);
                    break;
                case "checkWhetherCanEnrollAndEnroll" :
                    result = servent.checkWhetherCanEnrollAndEnroll(command[1],command[2], command[3]);
                    break;
                case "checkEnroll" :
                    result = servent.checkEnroll(command[1],command[2],command[3],command[4]);
                    break;
                case "dropOldCourse" :
                    result = servent.dropOldCourse(command[1],command[2],command[3]);
                    break;
                default :
                    System.out.println("Invalid Command!");
            }

            address = packet.getAddress();
            port = packet.getPort();

            data2 = result.getBytes();
            packet2 = new DatagramPacket(data2, data2.length, address, port);
            socket.send(packet2);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //socket.close();不能关闭
    }
}
