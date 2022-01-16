import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;

public class Server{
    private DatagramSocket datagramSocket;
    private ArrayList<Integer> clientPorts;
    private byte[] buffer;


    public Server(DatagramSocket datagramSocket) {
        this.datagramSocket = datagramSocket;
        clientPorts = new ArrayList<>();
        buffer = new byte[512];
    }

    public void run() {
        while (true) {
            try {
                buffer = new byte[512];
                DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
                datagramSocket.receive(datagramPacket);
                int port = datagramPacket.getPort();

                if (!clientPorts.contains(port)) {
                    clientPorts.add(port);
                }
                InetAddress address = InetAddress.getByName("localhost");
                String message = new String(datagramPacket.getData(), 0, datagramPacket.getLength());
                buffer = message.getBytes();
                for (int i = 0; i < clientPorts.size(); i++) {
                    int cl_port = clientPorts.get(i);
                    DatagramPacket sendDatagramPacket = new DatagramPacket(buffer, buffer.length, address, cl_port);
                    datagramSocket.send(sendDatagramPacket);
                }
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    public static void main(String[] args) throws SocketException {
        DatagramSocket datagramSocket = new DatagramSocket(2011);
        Server server = new Server(datagramSocket);
        server.run();
    }
}
