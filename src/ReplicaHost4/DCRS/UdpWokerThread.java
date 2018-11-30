//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ReplicaHost4.DCRS;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UdpWokerThread implements Runnable {
    DatagramSocket socket = null;
    DatagramPacket packet = null;
    DCRSImpl servent = null;

    public UdpWokerThread(DatagramSocket socket, DatagramPacket packet, DCRSImpl servent) {
        this.socket = socket;
        this.packet = packet;
        this.servent = servent;
    }

    public void run() {
        String info = null;
        InetAddress address = null;
        int port = 8800;
        byte[] data2 = null;
        DatagramPacket packet2 = null;

        try {
            info = new String(this.packet.getData(), 0, this.packet.getLength());
            String[] command = info.split(" ");
            String result = "";

            switch(command[0]) {
                case DCRSImpl.REQUEST_ENROLL:
                    result = this.servent.database.enrolCourse(command[1], command[2], command[3]);;
                    break;
                case DCRSImpl.REQUEST_RECORD:
                    result = this.servent.database.getClassSchedule(command[1]);
                    break;
                case DCRSImpl.REQUEST_ENROLLED_COUNT:
                    result = this.servent.database.getStudentRecord(command[1], command[2]);
                    break;
                case DCRSImpl.REQUEST_DROP:
                    result = this.servent.database.dropCourse(command[1], command[2]);
                    break;
                case DCRSImpl.REQUEST_OLDCOURSE_STATUS:
                    result = this.servent.database.checkOldCourse(command[1], command[2]);
                    break;
                case DCRSImpl.REQUEST_AVAILABILITY:
                    result = this.servent.database.listCourseAvailability(command[1]);
                    break;
                case DCRSImpl.REQUEST_ENROL_AVAIL:
                    result = this.servent.database.checkEnrolAvail(command[1], command[2]);
                    break;
                case DCRSImpl.REQUEST_NEWCOURSE_STATUS:
                    result = this.servent.database.checkNewCourse(command[1], command[2]);
            }

            address = this.packet.getAddress();
            port = this.packet.getPort();

            data2 = result.getBytes();
            packet2 = new DatagramPacket(data2, data2.length, address, port);
            socket.send(packet2);
        } catch (IOException var10) {
            var10.printStackTrace();
        } catch (Exception var11) {
            var11.printStackTrace();
        }

    }
}
