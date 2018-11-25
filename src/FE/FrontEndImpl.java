package FE;

import PortInfo.AddressInfo;
import PortInfo.FEPort;
import PortInfo.Replica;
import PortInfo.SequencerPort;
import FrontEndCorba.FrontEndPOA;
import org.omg.CORBA.ORB;
import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class FrontEndImpl extends FrontEndPOA {

    private ORB orb;

    public void setORB(ORB orb_val) {
        orb = orb_val;
    }

    @Override
    public String addCourse(String courseId, String semester){
        //TODO:在此处的msg中添加department信息
        String department = courseId.substring(0,4);
        Map<String, String> resultSet = new HashMap<>();
        DatagramSocket socket = null;
        int count = 0;
        StringBuilder sb = new StringBuilder();

        try {
            socket = new DatagramSocket(FEPort.FE_PORT.FEPort);

            sendRequest(sb.append(department)
                    .append(":")
                    .append("addCourse")
                    .append(" ")
                    .append(courseId)
                    .append(" ")
                    .append(semester).toString());

            FE.Timer timer = new FE.Timer(socket, false);
            Thread thread = new Thread(timer);
            thread.start();
            //TODO:假设只发生一个failure
            while(count < 4 && !timer.timeout) {
                count = registerListener(socket, resultSet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (resultSet.size() < 4){
            tellRMCrash(resultSet);
        }
        return majority(resultSet);
    }

    @Override
    public String removeCourse(String courseId, String semester) {
       //TODO:在此处的msg中添加department信息
        String department = courseId.substring(0,4);
        Map<String, String> resultSet = new HashMap<>();
        DatagramSocket socket = null;
        int count = 0;
        StringBuilder sb = new StringBuilder();

        try {
            socket = new DatagramSocket(FEPort.FE_PORT.FEPort);

            sendRequest(sb.append(department)
                    .append(":")
                    .append("removeCourse")
                    .append(" ")
                    .append(courseId)
                    .append(" ")
                    .append(semester).toString());

            FE.Timer timer = new FE.Timer(socket,false);
            Thread thread = new Thread(timer);
            thread.start();
            //TODO:假设只发生一个failure
            while(count < 4 && !timer.timeout) {
                count = registerListener(socket, resultSet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (resultSet.size() < 4){
            tellRMCrash(resultSet);
        }
        return majority(resultSet);
    }

    @Override
    public String[] listCourseAvailability(String semester){
        //TODO:在此处的msg中添加department信息
        String department = "comp";
        Map<String, String> resultSet = new HashMap<>();
        DatagramSocket socket = null;
        int count = 0;
        StringBuilder sb = new StringBuilder();

        try {
            socket = new DatagramSocket(FEPort.FE_PORT.FEPort);
            sendRequest(sb.append(department)
                    .append(":")
                    .append("listCourseAvailability")
                    .append(" ")
                    .append(semester).toString());

            FE.Timer timer = new FE.Timer(socket,false);
            Thread thread = new Thread(timer);
            thread.start();
            //TODO:假设只发生一个failure
            while(count < 4 && !timer.timeout) {
                count = registerListener(socket, resultSet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (resultSet.size() < 4){
            tellRMCrash(resultSet);
        }
        return majority(resultSet).split(" ");
    }

    @Override
    public String enrolCourse(String studentId, String courseId, String semester){
        String department = studentId.substring(0,4);
        Map<String, String> resultSet = new HashMap<>();
        DatagramSocket socket = null;
        int count = 0;
        StringBuilder sb = new StringBuilder();

        try{
            socket = new DatagramSocket(FEPort.FE_PORT.FEPort);
            sendRequest(sb.append(department)
                    .append(":")
                    .append("enrolCourse")
                    .append(" ")
                    .append(studentId)
                    .append(" ")
                    .append(courseId)
                    .append(" ")
                    .append(semester).toString());

            FE.Timer timer = new FE.Timer(socket,false);
            Thread thread = new Thread(timer);
            thread.start();
            //TODO:假设只发生一个failure
            while(count < 4 && !timer.timeout) {
                count = registerListener(socket, resultSet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (resultSet.size() < 4){
            tellRMCrash(resultSet);
        }
        return majority(resultSet);
    }

    @Override
    public String dropCourse(String studentId, String courseId) {
        String department = studentId.substring(0,4);
        Map<String, String> resultSet = new HashMap<>();
        DatagramSocket socket = null;
        int count = 0;
        StringBuilder sb = new StringBuilder();

        try{
            socket = new DatagramSocket(FEPort.FE_PORT.FEPort);
            sendRequest(sb.append(department)
                    .append(":")
                    .append("dropCourse")
                    .append(" ")
                    .append(studentId)
                    .append(" ")
                    .append(courseId).toString());

            FE.Timer timer = new FE.Timer(socket,false);
            Thread thread = new Thread(timer);
            thread.start();
            //TODO:假设只发生一个failure
            while(count < 4 && !timer.timeout) {
                count = registerListener(socket, resultSet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (resultSet.size() < 4){
            tellRMCrash(resultSet);
        }
        return majority(resultSet);
    }

    @Override
    public String[] getClassSchedule(String studentId) {
        String department = studentId.substring(0,4);
        Map<String, String> resultSet = new HashMap<>();
        DatagramSocket socket = null;
        int count = 0;
        StringBuilder sb = new StringBuilder();

        try{
            socket = new DatagramSocket(FEPort.FE_PORT.FEPort);
            sendRequest(sb.append(department)
                    .append(":")
                    .append("getClassSchedule")
                    .append(" ")
                    .append(studentId).toString());

            FE.Timer timer = new FE.Timer(socket,false);
            Thread thread = new Thread(timer);
            thread.start();
            //TODO:假设只发生一个failure
            while(count < 4 && !timer.timeout) {
                count = registerListener(socket, resultSet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (resultSet.size() < 4){
            tellRMCrash(resultSet);
        }
        return majority(resultSet).split(" ");
    }

    @Override
    public String swapCourse(String studentID, String newCourseID, String oldCourseID) {
        String department = studentID.substring(0,4);
        Map<String, String> resultSet = new HashMap<>();
        DatagramSocket socket = null;
        int count = 0;
        StringBuilder sb = new StringBuilder();

        try{
            socket = new DatagramSocket(FEPort.FE_PORT.FEPort);
            sendRequest(sb.append(department)
                    .append(":")
                    .append("swapCourse")
                    .append(" ")
                    .append(studentID)
                    .append(" ")
                    .append(newCourseID)
                    .append(" ")
                    .append(oldCourseID).toString());

            FE.Timer timer = new FE.Timer(socket,false);
            Thread thread = new Thread(timer);
            thread.start();
            //TODO:假设只发生一个failure
            while(count < 4 && !timer.timeout) {
                count = registerListener(socket, resultSet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (resultSet.size() < 4){
            tellRMCrash(resultSet);
        }
        return majority(resultSet);
    }

    @Override
    public void shutdown() {

    }

    private void tellRMCrash(Map<String, String> resultSet) {
        if (!resultSet.containsKey("1")) {
            String msg = "1 " + Failure.ServerCrash;
            sendReq(msg);
        } else if (!resultSet.containsKey("2")) {
            String msg = "2 " + Failure.ServerCrash;
            sendReq(msg);
        } else if (!resultSet.containsKey("3")) {
            String msg = "3 " + Failure.ServerCrash;
            sendReq(msg);
        } else if (!resultSet.containsKey("4")) {
            String msg = "4 " + Failure.ServerCrash;
            sendReq(msg);
        }
    }

    private DatagramPacket packet(String rmAddress, byte[] data, int replica) throws UnknownHostException {
        InetAddress address = InetAddress.getByName(rmAddress);
        return new DatagramPacket(data,0, data.length, address, replica);
    }

    private void sendReq(String msg) {
        try {
            DatagramSocket socket = new DatagramSocket();
            byte[] data = msg.getBytes();
            multicastCrashMsg(socket,data);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    private void multicastCrashMsg(DatagramSocket socket, byte[] data){
        try {
            socket.send(packet(AddressInfo.ADDRESS_INFO.RM1address, data,Replica.REPLICA.replica1));
            socket.send(packet(AddressInfo.ADDRESS_INFO.RM2address, data,Replica.REPLICA.replica2 ));
            socket.send(packet(AddressInfo.ADDRESS_INFO.RM3address, data,Replica.REPLICA.replica3));
            socket.send(packet(AddressInfo.ADDRESS_INFO.RM4address, data,Replica.REPLICA.replica4));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private String majority(Map<String,String> resultSet) {
        Map<String,Integer> map = new HashMap<>();
        resultSet.forEach((k,v) -> {
            if (map.containsKey(v)){
                map.put(v, map.get(v) + 1);
            } else {
                map.put(v, 1);
            }
        });

        Integer vote = 0;
        String candidate = "";
        for (Map.Entry<String, Integer> entry :
                map.entrySet()) {
            if (entry.getValue() > vote){
                candidate = entry.getKey();
                vote = entry.getValue();
            }
        }

        findSoftwareFail(candidate, vote, resultSet);

        return candidate;
    }

    private void findSoftwareFail(String candidate, Integer vote, Map<String, String> resultSet) {
        if (vote == 4)
            return;
        String crashServerNum = null;
        for (Map.Entry<String, String> entry : resultSet.entrySet()){
            if (!entry.getValue().equals(candidate)){
                crashServerNum = entry.getKey();
            }
        }
        if (null != crashServerNum){
            sendToRM(crashServerNum);
        }
    }

    private void sendToRM(String crashServerNum) {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket();
            String msg = Failure.SoftWareFailure.toString();

            byte[] data = msg.getBytes();

            if (crashServerNum.equals("1")){
                InetAddress address = InetAddress.getByName(AddressInfo.ADDRESS_INFO.RM1address);
                DatagramPacket packet = new DatagramPacket(data, 0, data.length,address,Replica.REPLICA.replica1 );
                socket.send(packet);
            } else if (crashServerNum.equals("2")){
                InetAddress address = InetAddress.getByName(AddressInfo.ADDRESS_INFO.RM2address);
                DatagramPacket packet = new DatagramPacket(data, 0, data.length,address,Replica.REPLICA.replica2 );
                socket.send(packet);
            } else if (crashServerNum.equals("3")){
                InetAddress address = InetAddress.getByName(AddressInfo.ADDRESS_INFO.RM3address);
                DatagramPacket packet = new DatagramPacket(data, 0, data.length,address,Replica.REPLICA.replica3 );
                socket.send(packet);
            } else {
                InetAddress address = InetAddress.getByName(AddressInfo.ADDRESS_INFO.RM4address);
                DatagramPacket packet = new DatagramPacket(data, 0, data.length,address,Replica.REPLICA.replica4 );
                socket.send(packet);
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        socket.close();
    }



    public void sendRequest(String message) throws Exception {

        //TODO:Sequener Ip;
        InetAddress address = InetAddress.getByName("localhost");

        byte[] data = message.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(data, data.length, address, SequencerPort.SEQUENCER_PORT.sequencerPort);

        DatagramSocket socket = new DatagramSocket();
        socket.send(sendPacket);

    }

    private int registerListener(DatagramSocket socket,  Map<String,String> resultSet) {
        byte[] data = new byte[1024];
        DatagramPacket packet = new DatagramPacket(data, data.length);
        try {
            socket.receive(packet);
            String result = new String(packet.getData(), 0 , packet.getLength());

            String[] res = result.split(":");
            resultSet.put(res[0], res[1]);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultSet.size();
    }

    public void listen(int udpPort) throws IOException {
        DatagramSocket socket = new DatagramSocket(udpPort);
        DatagramPacket packet = null;
        byte[] data = null;

        while(true)
        {
            data = new byte[1024];
            packet = new DatagramPacket(data, data.length);
            socket.receive(packet);

            String receiveMessage = new String(packet.getData(), 0, packet.getLength());

        }
    }

}

enum Failure {
    SoftWareFailure,
    ServerCrash,
    BackUp,
}
