package Master;

import java.io.*;
import java.net.*;

import util.*;

/**
 * This class is responsible for communicating with the client.
 * A new instance of this class is appointed to each client in an
 * independent thread.
 */
public class SocketHandler {

    /**
     * The socket used for general input and output communication
     */
    private Socket aSocket;

    /**
     * The input socket used for reading in messages from slave and client
     */
    private ObjectInputStream socketIn;

    /**
     * the output socket used for sending out messages to slave and client
     */
    private ObjectOutputStream socketOut;


    public SocketHandler(Socket s) {
        try {
            aSocket = s;
            socketOut = new ObjectOutputStream(aSocket.getOutputStream());


            printIPInfo();
        } catch (IOException e) {
            System.out.println("ServerCommController: Create ServerCommController Error");
            e.printStackTrace();
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

    /**
     * Prints the IP information (current IP address)
     */
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


    /**
     * Closes the socket
     * @throws IOException
     */
    public void stop() throws IOException {
        aSocket.close();
    }

    /**
     * Writes the corresponding object to the output socket
     * @param obj The output object
     * @throws IOException
     */
    public void writeObject(Object obj) throws IOException {
        socketOut.writeObject(obj);
        socketOut.reset();
    }

    /**
     * Reads in the object from socket
     */
    public Message readObject() throws IOException, ClassNotFoundException {
        return (Message)socketIn.readObject();
    }

    /**
     * Closes out all the sockets
     * @throws IOException
     */
    public void closeSockets() throws IOException {
        socketIn.close();
        socketOut.close();
        aSocket.close();
    }
}