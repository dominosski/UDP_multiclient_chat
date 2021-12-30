import javax.xml.crypto.Data;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.Scanner;

//public class Client{
//    private DatagramSocket datagramSocket;
//    private InetAddress inetAddress;
//    private byte[] buffer = new byte[256];
//
//    public Client(DatagramSocket datagramSocket, InetAddress inetAddress) {
//        this.datagramSocket = datagramSocket;
//        this.inetAddress = inetAddress;
//    }
//
////    public void run(){
////        Scanner scanner = new Scanner(System.in);
////        while (true){
////            try {
////                DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
////                datagramSocket.receive(datagramPacket);
////                String messageFromServer = new String(datagramPacket.getData(), 0, datagramPacket.getLength());
////                System.out.println("Server: " + messageFromServer);
////                String message = scanner.nextLine();
////                buffer = message.getBytes();
////                DatagramPacket sendDatagram = new DatagramPacket(buffer, buffer.length, inetAddress, 2011);
////                datagramSocket.send(sendDatagram);
////            }catch (IOException e){
////                e.printStackTrace();
////                break;
////            }
////        }
////    }
//
//
////    public static void main(String[] args) throws SocketException, UnknownHostException {
////        DatagramSocket datagramSocket = new DatagramSocket();
////        InetAddress inetAddress = InetAddress.getByName("localhost");
////        Client client = new Client(datagramSocket, inetAddress);
////        client.run();
////        System.out.println("Send packets: ");
////    }
//}
class MessageSender implements Runnable{
    DatagramSocket datagramSocket;
    private final int port = 2011;

    public MessageSender(DatagramSocket datagramSocket) {
        this.datagramSocket = datagramSocket;
    }

    public void sendMessage(String message) throws IOException {
        byte [] buffer = message.getBytes();
        InetAddress address = InetAddress.getByName("localhost");
        System.out.println(buffer.length);
        DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length, address, port);
        datagramSocket.send(datagramPacket);
    }

    @Override
    public void run() {
        boolean connected = false;
        do{
            try {
                sendMessage("HelloThereAll");
                connected = true;
            }catch (IOException e){
                e.printStackTrace();
            }
        }while (!connected);
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

        while (true){
            try {
                while(!input.ready()){
                    Thread.sleep(100);
                }
                sendMessage(input.readLine());
            }catch (IOException | InterruptedException e){
                e.printStackTrace();
            }
        }
    }
}
class MessageReceiver implements Runnable{

    DatagramSocket datagramSocket;
    byte buffer[];

    public MessageReceiver(DatagramSocket datagramSocket) {
        this.datagramSocket = datagramSocket;
        buffer = new byte[1024];
    }

    @Override
    public void run() {
        while (true){
            try {
                buffer = new byte[1024];
                DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
                datagramSocket.receive(datagramPacket);
                String receivedMessage = new String(datagramPacket.getData(), 0, datagramPacket.getLength());
                System.out.println(receivedMessage);
            }catch (IOException e){
                System.out.println(e);
            }
        }
    }
}

class ChatClient{
    public static void main(String[] args) throws SocketException {
        DatagramSocket datagramSocket = new DatagramSocket();
        MessageReceiver messageReceiver = new MessageReceiver(datagramSocket);
        MessageSender messageSender = new MessageSender(datagramSocket);
        Thread receiver = new Thread(messageReceiver);
        Thread sender = new Thread(messageSender);
        receiver.start();
        sender.start();
    }
}
