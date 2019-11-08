package util.sockethandler;

import util.HostAddressMessage;
import util.Message;
import util.MessageListener;

import java.io.IOException;
import java.net.Socket;

/**
 * This class is responsible for communicating with the client.
 * A new instance of this class is appointed to each client in an
 * independent thread.
 */
public class ClientSocketHandler extends SocketHandler {
	private String serverIp; // IP of the server side requested

	private boolean loopCommunication;

	private Message nextMsgOut;
	private Message lastMsgIn;

	public ClientSocketHandler(Socket socket, MessageListener listener) {
		this(socket, listener, false);
	}

	public ClientSocketHandler(Socket socket, MessageListener listener, boolean loopCommunication) {
		super(socket, listener);
		this.loopCommunication = loopCommunication;
		serverIp = socket.getInetAddress().getHostAddress();
	}

	public Message getNextMsgOut() {
		return nextMsgOut;
	}

	public void setNextMsgOut(Message nextMsgOut) {
		this.nextMsgOut = nextMsgOut;
	}

	public Message getLastMsgIn() {
		return lastMsgIn;
	}

	public void communicate() {
		if (loopCommunication) {
			while (shouldRun) {
				writeAndReadMsg();
			}
		} else {
			writeAndReadMsg();
		}
	}

	private void writeAndReadMsg() {
		try {
			Message msgOut = getNextMsgOut();
			writeMessage(msgOut);
			if (msgOut.getAction().equals("terminate")) {
				stop();
			}
			Message msgIn = readMessage();
			if (msgIn instanceof HostAddressMessage) {
				((HostAddressMessage) msgIn).setHostIP(serverIp);
			}
			lastMsgIn = msgIn;
			nextMsgOut = notifyListener(msgIn); // Let the listener handle the message but don't do anything with it

		} catch (IOException e) {
			System.err.println("Slave ServerSocketHandler error:");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}