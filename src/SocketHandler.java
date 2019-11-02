import java.io.*;
import java.net.*;
import java.util.ArrayList;

/**
 * This class is responsible for communicating with the client.
 * A new instance of this class is appointed to each client in an
 * independent thread.
 */
public class SocketHandler implements Runnable {

    private Socket aSocket;
    private ObjectInputStream socketIn;
    private ObjectOutputStream socketOut;
    private ServerController serverController;

    public SocketHandler(Socket s, ServerController serverController) {
        try {
            aSocket = s;
            setServerController(serverController);

            socketOut = new ObjectOutputStream(aSocket.getOutputStream());


            printIPInfo();
        } catch (IOException e) {
            System.out.println("ServerCommController: Create ServerCommController Error");
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        createInputStream();
        communicate();
    }

    public void communicate() {
        ArrayList<String> responses = new ArrayList<String>();
        try {
            socketOut.writeObject("First Questions");
            responses.add((String)socketIn.readObject());
            socketOut.writeObject("Second Questions");
            responses.add((String)socketIn.readObject());
            socketOut.writeObject("Third Questions");
            responses.add((String)socketIn.readObject());
            socketOut.writeObject("Forth Questions");
            responses.add((String)socketIn.readObject());
            for(int i = 0; i < responses.size(); i++) {
                System.out.println("i " + responses.get(i));
            }
        } catch (Exception e) {
        }


    }

    /**
     * Creates an input socket stream from server
     */
    public void createInputStream() {
        try {
            socketIn = new ObjectInputStream(aSocket.getInputStream());
        } catch (IOException e) {
            System.out.println("Error creating server output stream");
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

    //GETTERS AND SETTERS
    public void setServerController(ServerController sc) {
        serverController = sc; // 2-way association
    }

    public void stop() throws IOException {
        aSocket.close();
    }
}