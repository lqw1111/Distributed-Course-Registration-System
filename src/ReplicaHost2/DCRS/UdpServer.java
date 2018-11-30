package ReplicaHost2.DCRS;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.rmi.RemoteException;
import java.util.logging.Logger;

public class UdpServer implements Runnable {
    private int portNumber;
    private DCRSImpl dcrsImpl;
    private boolean stop = true;
    private DatagramSocket socket = null;
//    private Logger logger;

    public UdpServer(int portNumber, DCRSImpl dcrsImpl) {
        this.portNumber = portNumber;
        this.dcrsImpl = dcrsImpl;
    }

    @Override
    public void run() {
        try {
            socket = new DatagramSocket(portNumber);
            byte[] receiveBuffer = new byte[1024];
            byte[] replyBuffer = null;

            while (stop) {
                DatagramPacket request = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                socket.receive(request);
                String message = new String(request.getData(), 0, request.getLength()).split("\n")[0];
//                logger.info("Server Recv Message :" + message);
//                String opRecord = "Data received via udp ==> " + message;
//                System.out.println(opRecord);
                String opArgs[] = message.split(" ");
//                for (String opArg : opArgs) {
//                    System.out.println("opArg ==> " + opArg);
//                }
                String operation = opArgs[0];
                switch (operation){
                    case ("listCourseAvailability"):
                        String result = "";
                        String[] availability = dcrsImpl.localCourseAvailability(opArgs[1]);
                        if (availability.length == 0) {
                            replyBuffer = ("none\n").getBytes();
                        } else {
                            for (String a : availability) {
                                result = result + a;
                            }
                            replyBuffer = (result + "\n").getBytes();
                        }
                        break;
                    case("enrolCourse"):
                        String enrolResult = dcrsImpl.enrolLocalCourse(opArgs[1], opArgs[2], opArgs[3]);
                        replyBuffer = (enrolResult + "\n").getBytes();
                        break;
                    case("dropCourse"):
                        String dropResult = dcrsImpl.dropLocalCourse(opArgs[1], opArgs[2], opArgs[3]);
                        replyBuffer = (dropResult + "\n").getBytes();
                        break;
                    case("getSemester"):
                        String semester = dcrsImpl.getLocalSemester(opArgs[1]);
                        replyBuffer = (semester + "\n").getBytes();
                        break;
                    case("checkAvailability"):
                        String swapAvailability = dcrsImpl.checkLocalAvailbility(opArgs[1]);
                        replyBuffer = (swapAvailability + "\n").getBytes();
                        break;
                    case("removeCourse"):
                        String removeResult = dcrsImpl.removeLocalCourse(opArgs[1], opArgs[2], opArgs[3]);
                        replyBuffer = (removeResult + "\n").getBytes();
                        break;
                    default:
                        replyBuffer = "1".getBytes();
                }
                DatagramPacket reply = new DatagramPacket(replyBuffer,0, replyBuffer.length, request.getAddress(), request.getPort());
                socket.send(reply);
            }

        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
