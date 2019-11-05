package slave;

import util.Message;
import util.MessageListener;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * This class is responsible for communicating with the client.
 * A new instance of this class is appointed to each client in an
 * independent thread.
 */
public class SocketHandler implements Runnable {

    private Socket socket;
    private ObjectInputStream socketIn;
    private ObjectOutputStream socketOut;

    private MessageListener listener;

    public SocketHandler(Socket socket, MessageListener listener) {
        try {
            socket = socket;
            socketOut = new ObjectOutputStream(socket.getOutputStream());
            socketIn = new ObjectInputStream(socket.getInputStream());

            printIPInfo();
        } catch (IOException e) {
            System.out.println("Slave SocketHandler: Create socketOut/socketIn failed");
            e.printStackTrace();
        }

	    this.listener = listener;
    }


    private Message notifyListener(Message msg) {
    	return listener.handleMessage(msg);
    }

    @Override
    public void run() {
//        createInputStream();
        communicate();
    }

    public void communicate() {
		while (true) {
			try {
				Message msgIn = readMessage();
				Message msgOut = notifyListener(msgIn);
				writeMessage(msgOut);
			} catch (IOException e) {
				System.err.println("Slave SocketHandler error:");
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}

		}
    }

    /**
     * Creates an input socket stream from server
     */
    public void createInputStream() {
        try {
            socketIn = new ObjectInputStream(socket.getInputStream());
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

    public void stop() throws IOException {
        socket.close();
    }

    private Message readMessage() throws IOException, ClassNotFoundException {
	    return (Message) socketIn.readObject();
    }

    private void writeMessage(Message msg) throws IOException {
    	writeObject(msg);
    }

    private void writeObject(Object obj) throws IOException {
        socketOut.writeObject(obj);
        socketOut.reset();
    }
}