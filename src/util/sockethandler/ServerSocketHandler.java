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
		while (shouldRun) {
			try {
				Message msgIn = readMessage();
				Message msgOut = notifyListener(msgIn);

				writeMessage(msgOut);
				if (msgOut.getAction().equals("terminate")) {
					stop();
				}
			} catch (IOException e) {
				System.err.println("ServerSocketHandler error. Did connection reset?");
//				e.printStackTrace();
				System.exit(-1);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
    }
}