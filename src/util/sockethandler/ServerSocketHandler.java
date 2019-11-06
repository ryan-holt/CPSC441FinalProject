package util.sockethandler;

import util.Message;
import util.MessageListener;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * This class is responsible for communicating with the client.
 * A new instance of this class is appointed to each client in an
 * independent thread.
 */
public class ServerSocketHandler extends SocketHandler {
    public ServerSocketHandler(Socket socket, MessageListener listener) {
    	super(socket, listener);
    }

    public void communicate() {
	    System.out.println("!!! communicate() started, shouldRun: " + shouldRun); // FIXME delete
		while (shouldRun) {
			System.out.println("!!! communicate() inside shouldRun loop"); // FIXME delete
			try {
				System.out.println("!!! communicate(), waiting for message in"); // FIXME DELETE
				Message msgIn = readMessage();
				System.out.println("!!! communicate(), message received!"); // FIXME delete
				Message msgOut = notifyListener(msgIn);

				writeMessage(msgOut);
				if (msgOut.getAction().equals("terminate")) {
					stop();
				}
			} catch (IOException e) {
				System.err.println("Slave ServerSocketHandler error:");
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
    }
}