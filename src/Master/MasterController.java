package Master;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class is responsible for managing the thread pool as well as
 * the ServerCommunicationController. Everytime, a new client connects,
 * this class makes an instance of the ServerCommunicationController
 * for the client in a new thread hi
 */
public class MasterController {

    private static final int PORT = 9000;
    private ServerSocket serverSocket;
    private ExecutorService pool;

    public MasterController() {
        try {
            serverSocket = new ServerSocket(PORT);
            pool = Executors.newFixedThreadPool(10);
            System.out.println("Server is running");
            printIPInfo();
            System.out.println("********");
        } catch (IOException e) {
            System.out.println("ServerController: Create a new socket error");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        MasterController myServer = new MasterController();
        myServer.communicateWithClient();
    }

    public void communicateWithClient() {
        try {
            while (true) {
                SocketHandler scc = new SocketHandler(serverSocket.accept(), this);

                System.out.println("New Client Connected");

                pool.execute(scc);
            }
        } catch (IOException e) {
            System.out.println("ServerController: CommunicateWithClient error");
            e.printStackTrace();
        }
    }

    public void printIPInfo() {
        InetAddress ip;
        try {
            ip = InetAddress.getLocalHost();
            System.out.println("You current IP address: " + ip);
        } catch (UnknownHostException e) {
            System.out.println("IP Print error");
            e.printStackTrace();
        }
    }

}