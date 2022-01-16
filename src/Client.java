import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.Scanner;

class Sender implements Runnable{
    DatagramSocket datagramSocket;
    private final int port = 2011;
    private final String userName;
    private byte [] buffer;

    public Sender(DatagramSocket datagramSocket, String userName) {
        this.datagramSocket = datagramSocket;
        this.userName = userName;
        buffer = new byte[512];
    }

    public void sendMessage(String message) throws IOException {
        buffer = new byte[512];
        buffer = message.getBytes();
        InetAddress address = InetAddress.getByName("localhost");
        //System.out.println(buffer.length);
        DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length, address, port);
        datagramSocket.send(datagramPacket);
    }

    @Override
    public void run() {
        boolean connected = false;
        do{
            try {
                sendMessage(userName + " has been connected to the server");
                connected = true;
            }catch (IOException e){
                e.printStackTrace();
            }
        }while (!connected);
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

        while (true){
            try {
                String message = userName + ": " + input.readLine();
                sendMessage(message);
            }
            catch (IOException e) {
                e.printStackTrace();
                System.out.println();
            }
        }
    }
}
class Receiver implements Runnable{

    DatagramSocket datagramSocket;
    byte [] buffer;

    public Receiver(DatagramSocket datagramSocket) {
        this.datagramSocket = datagramSocket;
        buffer = new byte[512];
    }

    @Override
    public void run() {
        while (true){
            try {
                buffer = new byte[512];
                DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
                datagramSocket.receive(datagramPacket);
                String receivedMessage = new String(datagramPacket.getData(), 0, datagramPacket.getLength());
                //System.out.println(receivedMessage.length());
                System.out.println(receivedMessage);
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}

class ChatClient{
    public static void main(String[] args) throws SocketException {
        DatagramSocket datagramSocket = new DatagramSocket();
        Receiver receiver = new Receiver(datagramSocket);
        System.out.println("Type your name: ");
        Scanner scanner = new Scanner(System.in);
        String userName = scanner.nextLine();
        Sender sender = new Sender(datagramSocket, userName);
        Thread receiverThread = new Thread(receiver);
        Thread senderThread = new Thread(sender);
        receiverThread.start();
        senderThread.start();
    }
}
